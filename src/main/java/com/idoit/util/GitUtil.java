package com.idoit.util;

import com.google.common.collect.Lists;
import com.idoit.context.UserContext;
import com.intellij.dvcs.push.ui.VcsPushDialog;
import com.intellij.ide.actions.SaveAllAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ChangeListManagerImpl;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vcs.changes.actions.RefreshAction;
import git4idea.GitLocalBranch;
import git4idea.GitReference;
import git4idea.branch.GitBranchUtil;
import git4idea.branch.GitBrancher;
import git4idea.branch.GitNewBranchOptions;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitLineHandler;
import git4idea.history.GitHistoryUtils;
import git4idea.push.GitPushSource;
import git4idea.repo.GitRepository;
import git4idea.ui.branch.GitBranchActionsUtilKt;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitUtil {

    private static final Pattern LESSON_BRANCH_PATTERN = Pattern.compile("block\\d{1,2}_lesson\\d{1,2}_.+$");

    /**
     * 1. Create a branch name: block{number}_lesson{number}_{userName}
     * 2. Save all changed/added files for commit
     * 3. Commit current changes but not push to not run pipelines each time when a branch changes
     * 4. Refresh git state in idea to display changed state of files
     * 5. Create a new branch if there's no local branch with such a name
     * 6. Check out the branch (will be absolutely fresh for now. In future would be nice to add unshelving old changes)
     */
    public static void checkoutLessonBranch(AnActionEvent event, String lessonBranch) {
        doInRepo(event, (project, repository) -> {
            String localBranch = createLocalBranchName(lessonBranch);
            commitAndRefreshView(event, repository);
            getExistingLocalBranch(repository, localBranch).ifPresentOrElse(
                    existingBranch -> checkoutExistingBranch(project, repository, existingBranch.getName()),
                    () -> createAndCheckoutBranch(project, repository, lessonBranch, localBranch));
            refreshView(event);
        });
    }

    public static void checkoutSolutionBranch(AnActionEvent event) {
        doInRepo(event, (project, repository) -> {
            commitAndRefreshView(event, repository);
            String solutionBranch = getSolutionLessonBranchName(repository);
            checkoutExistingBranch(project, repository, solutionBranch);
            refreshView(event);
        });
    }

    /**
     * 1. Commit current changes
     * 2. Find template lesson branch
     * 3. Checkout to the template branch
     * 4. Delete local user's branch
     * 5. Create a new local user's branch
     */
    public static void resetLessonBranch(AnActionEvent event) {
        doInRepo(event, (project, repository) -> {
            commitAndRefreshView(event, repository);
            String lessonBranch = getTemplateLessonBranchName(repository);
            String usersBranch = repository.getCurrentBranchName();
            GitBrancher brancher = GitBrancher.getInstance(project);
            List<GitRepository> repositories = Collections.singletonList(repository);
            brancher.checkout(lessonBranch, false, repositories, () -> brancher.deleteBranches(
                    Collections.singletonMap(usersBranch, repositories),
                    () -> createAndCheckoutBranch(project, repository, lessonBranch, usersBranch)
            ));
            refreshView(event);
        });
    }

    public static void pushLessonBranch(AnActionEvent event) {
        doInRepo(event, (project, repository) -> {
            commitAndRefreshView(event, repository);
            List<GitRepository> repositories = Collections.singletonList(repository);
            Optional.ofNullable(repository.getCurrentBranch())
                    .ifPresent(currentBranch -> new VcsPushDialog(
                            project,
                            repositories,
                            repositories,
                            null,
                            GitPushSource.create(currentBranch)
                    ).push(false));
            refreshView(event);
        });
    }

    public static boolean areThereChanges(AnActionEvent event) {
        Project project = event.getDataContext().getData(DataKey.create("project"));
        return Optional.ofNullable(project)
                .map(GitBranchUtil::getCurrentRepository)
                .map(repository -> {
                    ChangeListManagerImpl changeListManager = (ChangeListManagerImpl) ChangeListManager.getInstance(repository.getProject());
                    return !changeListManager.getDefaultChangeList().getChanges().isEmpty();
                }).orElse(false);
    }

    public static String getCurrentBranch(AnActionEvent event) {
        Project project = event.getDataContext().getData(DataKey.create("project"));
        return Optional.ofNullable(project)
                .map(GitBranchUtil::getCurrentRepository)
                .map(GitRepository::getCurrentBranch)
                .map(GitReference::getName)
                .orElse(null);
    }

    public static String getTemplateLessonBranchName(AnActionEvent event) {
        Project project = event.getDataContext().getData(DataKey.create("project"));
        return Optional.ofNullable(project)
                .map(GitBranchUtil::getCurrentRepository)
                .map(GitUtil::getTemplateLessonBranchName)
                .orElse(null);
    }

    private static String getTemplateLessonBranchName(GitRepository repository) {
        return getLessonBranchName(repository, "template");
    }

    private static String getSolutionLessonBranchName(GitRepository repository) {
        return getLessonBranchName(repository, "solution");
    }

    private static String getLessonBranchName(GitRepository repository, String type) {
        return Optional.ofNullable(repository.getCurrentBranchName())
                .map(currentBranch -> {
                    Matcher matcher = LESSON_BRANCH_PATTERN.matcher(currentBranch);
                    return matcher.matches() ? currentBranch.substring(0, currentBranch.lastIndexOf("_") + 1) + type : null;
                })
                .orElse(null);
    }

    private static void commitAndRefreshView(AnActionEvent event, GitRepository repository) {
        new SaveAllAction().actionPerformed(event);
        commitCurrentChanges(repository);
        new RefreshAction().actionPerformed(event);
    }

    private static void refreshView(AnActionEvent event) {
        new SaveAllAction().actionPerformed(event);
        new RefreshAction().actionPerformed(event);
    }

    private static void doInRepo(AnActionEvent event, BiConsumer<Project, GitRepository> projectRepoConsumer) {
        Project project = event.getDataContext().getData(DataKey.create("project"));
        Optional.ofNullable(project)
                .map(GitBranchUtil::getCurrentRepository)
                .ifPresent(repository -> projectRepoConsumer.accept(project, repository));
    }

    private static String createLocalBranchName(String lessonBranch) {
        return lessonBranch.replaceAll("template", UserContext.auth.getLogin());
    }

    private static Optional<GitLocalBranch> getExistingLocalBranch(GitRepository repository, String branch) {
        return repository.getBranches().getLocalBranches().stream()
                .filter(b -> b.getFullName().contains(branch))
                .findFirst();
    }

    private static void commitCurrentChanges(GitRepository repository) {
        Git git = Git.getInstance();
        ChangeListManagerImpl changeListManager = (ChangeListManagerImpl) ChangeListManager.getInstance(repository.getProject());
        LocalChangeList defaultChangeList = changeListManager.getDefaultChangeList();
        if (!defaultChangeList.getChanges().isEmpty()) {
            addChangesToStage(git, repository, defaultChangeList);
            commitChanges(git, repository);
        }
    }

    private static void addChangesToStage(Git git, GitRepository repository, LocalChangeList defaultChangeList) {
        GitLineHandler addHandler = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.ADD);
        defaultChangeList.getChanges().stream()
                .map(change -> change.getBeforeRevision().getFile().getIOFile())
                .forEach(addHandler::addAbsoluteFile);
        git.runCommand(addHandler);
    }

    private static void commitChanges(Git git, GitRepository repository) {
        GitLineHandler commitHandler = new GitLineHandler(repository.getProject(), repository.getRoot(), GitCommand.COMMIT);
        commitHandler.addParameters("-m", generateUserCommitMessage());
        git.runCommand(commitHandler);
    }

    private static void createAndCheckoutBranch(Project project, GitRepository repository, String lessonBranch, String localBranch) {
        getRootBranch(repository, lessonBranch).ifPresent(branch -> {
            try {
                String lastCommitHash = getLastCommitHash(project, repository, branch.getName());
                GitNewBranchOptions branchOptions = new GitNewBranchOptions(localBranch, true, false, true);
                GitBranchActionsUtilKt.checkoutOrReset(project, Lists.newArrayList(repository), lastCommitHash, branchOptions);
            } catch (VcsException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    private static void checkoutExistingBranch(Project project, GitRepository repository, String branch) {
        try {
            String lastCommitHash = getLastCommitHash(project, repository, branch);
            GitBranchActionsUtilKt.checkout(project, Lists.newArrayList(repository), lastCommitHash, branch, false);
        } catch (VcsException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String getLastCommitHash(Project project, GitRepository repository, String branchName) throws VcsException {
        return GitHistoryUtils.history(project, repository.getRoot(), branchName).get(0).getId().asString();
    }

    private static Optional<GitLocalBranch> getRootBranch(GitRepository repository, String lessonBranch) {
        return repository.getBranches().getLocalBranches().stream()
                .filter(b -> b.getFullName().contains(lessonBranch))
                .findFirst();
    }

    private static String generateUserCommitMessage() {
        return UserContext.auth.getLogin() + '-' + UUID.randomUUID();
    }
}

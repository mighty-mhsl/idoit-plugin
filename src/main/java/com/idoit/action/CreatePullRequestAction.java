package com.idoit.action;

import com.idoit.action.rules.OwnBranchAction;
import com.idoit.bean.StudentProgress;
import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.idoit.util.WebUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class CreatePullRequestAction extends OwnBranchAction {

    @Override
    protected void performActionOnOwnedBranch(AnActionEvent event) throws Exception {
        String currentBranch = GitUtil.getCurrentBranch(event);
        String templateBranch = GitUtil.getTemplateLessonBranchName(event);
        StudentProgress currentProgress = WebUtil.getCurrentProgress();
        if (currentProgress.getPullNumber() != 0) {
            WebUtil.closePullRequest(currentProgress.getPullNumber());
        }
        currentProgress = WebUtil.createPullRequest(currentBranch, templateBranch);
        showPullInfoMessage(currentProgress);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "pull_request", getClass());
    }

    private void showPullInfoMessage(StudentProgress currentProgress) {
        String pullUrl = currentProgress.getPullUrl().replaceAll("api\\.", "");
        int response = Messages.showDialog("Pull request successfully created", "Pull Request Info",
                new String[]{"OK", "Check on GitHub"}, 0, null);
        if (response == 1) {
            BrowserUtil.browse(pullUrl);
        }
    }
}

package com.idoit.action;

import com.idoit.bean.TestRun;
import com.idoit.util.ActionUtil;
import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.idoit.util.WebUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TestRunAction extends AnAction {

    private static final String STATISTICS_MESSAGE_FORMAT = "Tests passed: %d\nTests failed: %d";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ActionUtil.runSafe(() -> {
            boolean changed = GitUtil.areThereChanges(event);
            GitUtil.pushLessonBranch(event);
            Optional.ofNullable(GitUtil.getCurrentBranch(event))
                    .ifPresent(branch -> ActionUtil.runSafe(() -> {
                        TestRun testRun = WebUtil.createOrUpdateStatistics(branch, changed);
                        if (changed) {
                            Thread.sleep(30_000); //TODO: bad. Replace with background task that is run in 30 sec afterwards
                        }
                        testRun = WebUtil.getJobStatus(testRun.getJobId());
                        if (testRun.getFailed() == 0) {
                            WebUtil.progressWithBlock();
                        }
                        showTestsInfoMessage(testRun);
                    }));
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "run_tests", getClass());
    }

    private void showTestsInfoMessage(TestRun testRun) {
        String message = String.format(STATISTICS_MESSAGE_FORMAT, testRun.getPassed(), testRun.getFailed());
        int response = Messages.showDialog(message, "Statistics", new String[]{"OK", "Check on CI"},
                0, null);
        if (response == 1) {
            BrowserUtil.browse(testRun.getCiLink());
        }
    }
}
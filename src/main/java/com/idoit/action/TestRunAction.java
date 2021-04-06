package com.idoit.action;

import com.idoit.bean.TestRun;
import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.idoit.util.WebUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TestRunAction extends AnAction {

    private static final String STATISTICS_MESSAGE_FORMAT = "Tests passed: %d\nTests failed: %d\nCheck results on UI: %s";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        boolean changed = GitUtil.areThereChanges(event);
        GitUtil.pushLessonBranch(event);
        Optional.ofNullable(GitUtil.getCurrentBranch(event))
                .ifPresent(branch -> {
                    try {
                        TestRun testRun = WebUtil.createOrUpdateStatistics(branch, changed);
                        if (changed) {
                            Thread.sleep(30_000);
                        }
                        testRun = WebUtil.getJobStatus(testRun.getJobId());
                        String message = String.format(STATISTICS_MESSAGE_FORMAT, testRun.getPassed(), testRun.getFailed(), testRun.getCiLink());
                        Messages.showInfoMessage(message, "Statistics");
                        if (testRun.getFailed() == 0) {
                            WebUtil.progressWithBlock();
                        }
                    } catch (Exception e) {
                        Messages.showErrorDialog(e.getMessage(), "Error");
                    }
                });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "run_tests", getClass());
    }
}
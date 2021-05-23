package com.idoit.action;

import com.idoit.action.rules.OwnBranchAction;
import com.idoit.bean.TestRun;
import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.idoit.util.WebUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class TestRunAction extends OwnBranchAction {

    private static final String STATISTICS_MESSAGE_FORMAT = "Tests passed: %d\nTests failed: %d";

    @Override
    protected void performActionOnOwnedBranch(AnActionEvent event) throws Exception {
        boolean changed = GitUtil.areThereChanges(event);
        GitUtil.pushLessonBranch(event);
        String currentBranch = GitUtil.getCurrentBranch(event);
        if (currentBranch != null && !currentBranch.contains("solution")) {
            if (changed) {
                Thread.sleep(30_000); //TODO: bad. Replace with background task that is run in 30 sec afterwards
            }
            TestRun testRun = WebUtil.createOrUpdateStatistics(currentBranch, changed);
            testRun = WebUtil.getJobStatus(testRun.getJobId());
            if (testRun.getFailed() == 0) {
                WebUtil.progressWithBlock();
            }
            showTestsInfoMessage(testRun);
        } else {
            Messages.showErrorDialog("Cannot run tests for solution! Please, switch to your own lesson and try again.", "Error");
        }
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
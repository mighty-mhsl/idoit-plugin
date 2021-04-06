package com.idoit.action;

import com.idoit.bean.StudentProgress;
import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.idoit.util.WebUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class CreatePullRequestAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            String currentBranch = GitUtil.getCurrentBranch(event);
            if (!currentBranch.contains("template")) {
                String templateBranch = GitUtil.getTemplateLessonBranchName(event);
                StudentProgress currentProgress = WebUtil.getCurrentProgress();
                if (currentProgress.getPullNumber() != 0) {
                    WebUtil.closePullRequest(currentProgress.getPullNumber());
                }
                currentProgress = WebUtil.createPullRequest(currentBranch, templateBranch);
                String pullUrl = currentProgress.getPullUrl().replaceAll("api\\.", "");
                Messages.showInfoMessage(String.format("Pull request URL: %s", pullUrl), "Pull Request Info");
            }
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "pull_request", getClass());
    }
}

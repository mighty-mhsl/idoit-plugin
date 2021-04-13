package com.idoit.action;

import com.idoit.util.ActionUtil;
import com.idoit.util.GitUtil;
import com.idoit.util.WebUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class LoginAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        ActionUtil.runSafe(() -> {
            String login = Messages.showInputDialog("Login: ", "Login", null);
            String password = Messages.showPasswordDialog("Password: ", "Login");
            WebUtil.login(login, password);
            String templateBranch = GitUtil.getTemplateLessonBranchName(event);
            WebUtil.fetchBranchInfo(templateBranch);
        });
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}

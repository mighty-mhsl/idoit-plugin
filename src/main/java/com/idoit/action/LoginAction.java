package com.idoit.action;

import com.idoit.action.rules.AbstractAction;
import com.idoit.util.GitUtil;
import com.idoit.util.WebUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class LoginAction extends AbstractAction {

    @Override
    public void performAction(@NotNull AnActionEvent event) throws Exception {
        String login = Messages.showInputDialog("Login: ", "Login", null);
        String password = Messages.showPasswordDialog("Password: ", "Login");
        WebUtil.login(login, password);
        String templateBranch = GitUtil.getTemplateLessonBranchName(event);
        if (templateBranch != null) {
            WebUtil.fetchBranchInfo(templateBranch);
        }
        Messages.showInfoMessage("You successfully logged in to idoit!", "Login Succeeded");
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}

package com.idoit.action.rules;

import com.idoit.util.GitUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public abstract class OwnBranchAction extends AuthorizedAction {

    @Override
    protected void performAuthorizedAction(AnActionEvent event) throws Exception {
        if (GitUtil.isUsersOwnBranch(event)) {
            performActionOnOwnedBranch(event);
        } else {
            Messages.showErrorDialog("You're trying to take an action against not your solution.\n" +
                    "Please, switch to your own lesson solution and try again", "Error");
        }
    }

    protected abstract void performActionOnOwnedBranch(AnActionEvent event) throws Exception;
}

package com.idoit.action;

import com.idoit.context.UserContext;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public abstract class AuthorizedAction extends AbstractAction {

    @Override
    protected void performAction(AnActionEvent event) throws Exception {
        if (UserContext.auth != null) {
            performAuthorizedAction(event);
        } else {
            Messages.showErrorDialog("Please log in to idoit before taking any other actions\n" +
                    "Click Tools -> Login idoit", "Error");
        }
    }

    protected abstract void performAuthorizedAction(AnActionEvent event) throws Exception;
}

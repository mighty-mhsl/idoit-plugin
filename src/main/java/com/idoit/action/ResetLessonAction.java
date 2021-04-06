package com.idoit.action;

import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ResetLessonAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            int answer = Messages.showDialog("Do you really want to reset lesson progress (all the changes will be lost)?",
                    "Are You Sure?", new String[]{"Yes", "No"}, 0, null);
            if (answer == 0) {
                GitUtil.resetLessonBranch(event);
            }
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "reset", getClass());
    }
}

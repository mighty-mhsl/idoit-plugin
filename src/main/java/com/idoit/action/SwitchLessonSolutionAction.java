package com.idoit.action;

import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class SwitchLessonSolutionAction extends AuthorizedAction {

    @Override
    public void performAuthorizedAction(@NotNull AnActionEvent event) {
        String currentBranch = GitUtil.getCurrentBranch(event);
        if (currentBranch.contains("solution")) {
            GitUtil.checkoutLessonBranch(event, currentBranch.replaceAll("solution", "template"));
        } else {
            GitUtil.checkoutSolutionBranch(event);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "solution", getClass());
    }
}

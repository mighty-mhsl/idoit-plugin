package com.idoit.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;

public class IconUtil {

    public static void updateActionIcon(AnActionEvent event, String iconName, Class<?> actionClass) {
        String iconPath;
        if (UIUtil.isUnderDarcula()) {
            iconPath = String.format("/icons/%s_dark.png", iconName);
        } else {
            iconPath = String.format("/icons/%s.png", iconName);
        }
        event.getPresentation().setIcon(IconLoader.getIcon(iconPath, actionClass));
    }
}

package com.idoit.util;

import com.idoit.safe.SafeRunnable;
import com.intellij.openapi.ui.Messages;

public class ActionUtil {

    public static void runSafe(SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Error");
        }
    }
}

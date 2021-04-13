package com.idoit.util;

import com.idoit.safe.SafeRunnable;
import com.intellij.openapi.ui.Messages;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ActionUtil {

    public static void runSafe(SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Messages.showErrorDialog(e.getMessage() + '\n' + sw, "Error");
        }
    }
}

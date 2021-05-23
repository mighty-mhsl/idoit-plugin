package com.idoit.action.rules;

import com.idoit.bean.ErrorReport;
import com.idoit.context.UserContext;
import com.idoit.util.WebUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class AbstractAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        try {
            performAction(event);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            int option = Messages.showDialog(e.getMessage() + '\n' + sw, "Error",
                    new String[]{"Send Report", "OK"}, 0, null);
            if (option == 0) {
                sendErrorReport(e, sw);
            }
        }
    }

    protected abstract void performAction(AnActionEvent event) throws Exception;

    private void sendErrorReport(Exception e, StringWriter sw) {
        ErrorReport errorReport = new ErrorReport();
        if (UserContext.lessonId == 0) {
            errorReport.setLessonId(1); //let them to point to the first one, as only started
        } else {
            errorReport.setLessonId(UserContext.lessonId);
        }
        errorReport.setMessage(e.getMessage());
        errorReport.setStackTrace(sw.toString());
        try {
            WebUtil.sendErrorReport(errorReport);
        } catch (Exception exception) {
            Messages.showErrorDialog("Error while sending an error report. Ooooh... \n" + exception.getMessage(), "Error");
        }
    }
}

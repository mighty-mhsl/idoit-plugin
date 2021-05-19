package com.idoit.action;

import com.idoit.bean.Block;
import com.idoit.bean.Lesson;
import com.idoit.context.UserContext;
import com.idoit.util.GitUtil;
import com.idoit.util.IconUtil;
import com.idoit.util.WebUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ChooseLessonAction extends AbstractAction {

    @Override
    public void performAction(@NotNull AnActionEvent event) throws Exception {
        long blockId = chooseBlock();
        if (blockId != 0) {
            String lessonBranch = chooseLesson(blockId);
            if (lessonBranch != null) {
                GitUtil.checkoutLessonBranch(event, lessonBranch);
            }
        }
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        IconUtil.updateActionIcon(e, "lesson_menu", getClass());
    }

    private long chooseBlock() throws IOException, InterruptedException {
        List<Block> blocks = WebUtil.getAvailableBlocks();
        String[] blockNames = getBlocksNames(blocks);
        int blockNumber = Messages.showDialog("Please, choose a block you want to work on:",
                "Available Blocks", blockNames, 0, null);
        return blocks.stream()
                .filter(block -> block.getOrderNumber() == blockNumber + 1)
                .findFirst()
                .map(block -> {
                    UserContext.blockId = block.getId();
                    return block.getId();
                }).orElse(0L);
    }

    private String chooseLesson(long blockId) throws IOException, InterruptedException {
        List<Lesson> lessons = WebUtil.getAvailableLessons(blockId);
        String[] lessonNames = getLessonsNames(lessons);
        int lessonNumber = Messages.showDialog("Please, choose a lesson you want to work on: ",
                "Available Lessons", lessonNames, 0, null);
        return lessons.stream()
                .filter(lesson -> lesson.getOrderNumber() == lessonNumber + 1)
                .findFirst().map(lesson -> {
                    UserContext.lessonId = lesson.getId();
                    return lesson.getBranchName();
                }).orElse(null);
    }

    private String[] getBlocksNames(List<Block> blocks) {
        return blocks.stream()
                .sorted(Comparator.comparingInt(Block::getOrderNumber))
                .map(Block::getName)
                .toArray(String[]::new);
    }

    private String[] getLessonsNames(List<Lesson> lessons) {
        return lessons.stream()
                .sorted(Comparator.comparingInt(Lesson::getOrderNumber))
                .map(Lesson::getName)
                .toArray(String[]::new);
    }
}

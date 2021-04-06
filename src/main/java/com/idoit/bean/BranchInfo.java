package com.idoit.bean;

public class BranchInfo {
    private long lessonId;
    private long blockId;

    public BranchInfo() {
    }

    public BranchInfo(long lessonId, long blockId) {
        this.lessonId = lessonId;
        this.blockId = blockId;
    }

    public long getLessonId() {
        return lessonId;
    }

    public void setLessonId(long lessonId) {
        this.lessonId = lessonId;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }
}

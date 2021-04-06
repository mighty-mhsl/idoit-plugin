package com.idoit.bean;

public class StudentProgress {
    private long id;
    private long courseId;
    private long blockId;
    private int currentLesson;
    private Integer pullNumber;
    private String pullUrl;

    public StudentProgress() {
    }

    public StudentProgress(long id, long courseId, long blockId, int currentLesson, int pullNumber, String pullUrl) {
        this.id = id;
        this.courseId = courseId;
        this.blockId = blockId;
        this.currentLesson = currentLesson;
        this.pullNumber = pullNumber;
        this.pullUrl = pullUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public int getCurrentLesson() {
        return currentLesson;
    }

    public void setCurrentLesson(int currentLesson) {
        this.currentLesson = currentLesson;
    }

    public int getPullNumber() {
        return pullNumber;
    }

    public void setPullNumber(int pullNumber) {
        this.pullNumber = pullNumber;
    }

    public String getPullUrl() {
        return pullUrl;
    }

    public void setPullUrl(String pullUrl) {
        this.pullUrl = pullUrl;
    }
}

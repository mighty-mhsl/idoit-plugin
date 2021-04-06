package com.idoit.bean;

public class TestRun {
    private long id;
    private int passed;
    private int failed;
    private int jobId;
    private String ciLink;

    public TestRun() {
    }

    public TestRun(long id, int passed, int failed, int jobId, String ciLink) {
        this.id = id;
        this.passed = passed;
        this.failed = failed;
        this.jobId = jobId;
        this.ciLink = ciLink;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getCiLink() {
        return ciLink;
    }

    public void setCiLink(String ciLink) {
        this.ciLink = ciLink;
    }
}

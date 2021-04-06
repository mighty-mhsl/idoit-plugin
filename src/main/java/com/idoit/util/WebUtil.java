package com.idoit.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idoit.context.HttpContext;
import com.idoit.context.UserContext;
import com.idoit.bean.AuthResponse;
import com.idoit.bean.Block;
import com.idoit.bean.BranchInfo;
import com.idoit.bean.Lesson;
import com.idoit.bean.StudentProgress;
import com.idoit.bean.TestRun;
import com.idoit.json.JsonBodyHandler;
import com.intellij.openapi.ui.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class WebUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String API_HOST;

    static {
        String host = "";
        try (InputStream is = WebUtil.class.getClassLoader().getResourceAsStream("settings.properties")) {
            Properties properties = new Properties();
            properties.load(is);
            host = properties.getProperty("api.host");
        } catch (IOException e) {
            Messages.showErrorDialog(e.getMessage(), "Error");
        }
        API_HOST = host;
    }

    public static List<Block> getAvailableBlocks() throws IOException, InterruptedException {
        HttpRequest request = prepareBlocksHttpRequest();
        return getAvailableBlocks(request);
    }

    public static List<Lesson> getAvailableLessons(long blockId) throws IOException, InterruptedException {
        HttpRequest request = prepareLessonsHttpRequest(blockId);
        return getAvailableLessons(request);
    }

    public static TestRun createOrUpdateStatistics(String branchName, boolean changed) throws IOException, InterruptedException {
        HttpRequest request = prepareStatisticsHttpRequest(branchName, changed);
        return sendTestRun(request);
    }

    public static TestRun getJobStatus(int jobId) throws IOException, InterruptedException {
        HttpRequest request = prepareGetJobStatusRequest(jobId);
        return sendTestRun(request);
    }

    public static void fetchBranchInfo(String templateBranch) throws IOException, InterruptedException {
        HttpRequest request = prepareBranchInfoRequest(templateBranch);
        BranchInfo branchInfo = sendBranchInfo(request);
        UserContext.lessonId = branchInfo.getLessonId();
        UserContext.blockId = branchInfo.getBlockId();
    }

    public static void login(String login, String password) throws IOException, InterruptedException {
        HttpRequest request = prepareLoginRequest(login, password);
        performLogin(request);
    }

    public static void progressWithBlock() throws IOException, InterruptedException {
        HttpRequest request = prepareProgressWithBlockRequest();
        sendStudentProgress(request);
    }

    public static StudentProgress getCurrentProgress() throws IOException, InterruptedException {
        HttpRequest request = prepareGetCurrentProgressRequest();
        return sendStudentProgress(request);
    }

    public static StudentProgress createPullRequest(String lessonBranch, String templateBranch) throws IOException, InterruptedException {
        HttpRequest request = prepareCreatePullRequest(lessonBranch, templateBranch);
        return sendStudentProgress(request);
    }

    public static void closePullRequest(int pullNumber) throws IOException, InterruptedException {
        HttpRequest request = prepareClosePullRequest(pullNumber);
        sendStudentProgress(request);
    }

    private static void performLogin(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<AuthResponse> response = HttpContext.HTTP_CLIENT
                .send(request, new JsonBodyHandler<>(AuthResponse.class));
        UserContext.auth = response.body();
        UserContext.auth.setSessionId(response.headers().firstValue("JSESSIONID").orElse(null));
    }

    private static HttpRequest prepareGetCurrentProgressRequest() {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/block/%d/progress", UserContext.blockId)))
                .header("Authorization", "Bearer " + UserContext.auth.getToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private static HttpRequest prepareClosePullRequest(int pullNumber) {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/pulls/%d/close", pullNumber)))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private static HttpRequest prepareCreatePullRequest(String lessonBranch, String templateBranch) {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/%s/%s/pull", lessonBranch, templateBranch)))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private static HttpRequest prepareProgressWithBlockRequest() {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/block/%d/progress", UserContext.lessonId)))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private static HttpRequest prepareLoginRequest(String login, String password) {
        String body = String.format("{\"login\":\"%s\", \"password\":\"%s\"}", login, password);
        return HttpRequest.newBuilder(URI.create(getApiUrl("/all/login")))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private static HttpRequest prepareBranchInfoRequest(String templateBranch) {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/%s/info", templateBranch)))
                .header("Authorization", "Bearer " + UserContext.auth.getToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private static HttpRequest prepareGetJobStatusRequest(int jobId) {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/%d/status", jobId)))
                .header("Authorization", "Bearer " + UserContext.auth.getToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private static HttpRequest prepareStatisticsHttpRequest(String branchName, boolean changed) {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/test/run/%d/%s/%s", UserContext.lessonId, branchName, changed)))
                .header("Authorization", "Bearer " + UserContext.auth.getToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private static HttpRequest prepareBlocksHttpRequest() {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/1/blocks")))
                .header("Authorization", "Bearer " + UserContext.auth.getToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private static HttpRequest prepareLessonsHttpRequest(long blockId) {
        return HttpRequest.newBuilder(URI.create(getApiUrl("/plugin/%d/lessons", blockId)))
                .header("Authorization", "Bearer " + UserContext.auth.getToken())
                .header("Content-Type", "application/json")
                .GET()
                .build();
    }

    private static String getApiUrl(String format, Object... args) {
        return String.format(API_HOST + format, args);
    }

    private static List<Block> getAvailableBlocks(HttpRequest request) throws IOException, InterruptedException {
        List list = send(request);
        return MAPPER.convertValue(list, new TypeReference<List<Block>>() {});
    }

    private static List<Lesson> getAvailableLessons(HttpRequest request) throws IOException, InterruptedException {
        List list = send(request);
        return MAPPER.convertValue(list, new TypeReference<List<Lesson>>() {});
    }

    private static List send(HttpRequest request) throws IOException, InterruptedException {
        return HttpContext.HTTP_CLIENT
                .send(request, new JsonBodyHandler<>(List.class))
                .body();
    }

    private static TestRun sendTestRun(HttpRequest request) throws IOException, InterruptedException {
        return HttpContext.HTTP_CLIENT
                .send(request, new JsonBodyHandler<>(TestRun.class))
                .body();
    }

    private static BranchInfo sendBranchInfo(HttpRequest request) throws IOException, InterruptedException {
        return HttpContext.HTTP_CLIENT
                .send(request, new JsonBodyHandler<>(BranchInfo.class))
                .body();
    }

    private static StudentProgress sendStudentProgress(HttpRequest request) throws IOException, InterruptedException {
        return HttpContext.HTTP_CLIENT
                .send(request, new JsonBodyHandler<>(StudentProgress.class))
                .body();
    }
}

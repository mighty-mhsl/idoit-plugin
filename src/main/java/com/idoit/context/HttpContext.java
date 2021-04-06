package com.idoit.context;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.http.HttpClient;

public class HttpContext {
    public static final HttpClient HTTP_CLIENT;

    static {
        CookieHandler.setDefault(new CookieManager());
        HTTP_CLIENT = HttpClient.newBuilder()
                .cookieHandler(CookieHandler.getDefault())
                .build();
    }
}

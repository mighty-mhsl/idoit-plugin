package com.idoit.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private Class<T> clazz;

    public JsonBodyHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        return asJSON(clazz);
    }

    public static <T> HttpResponse.BodySubscriber<T> asJSON(Class<T> targetType) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(
                upstream,
                (String body) -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readValue(body, targetType);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
    }
}
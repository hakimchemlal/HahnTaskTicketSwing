package com.hahn.client.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyCookieJar implements CookieJar {
    private final Map<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        // Store cookies by the domain (i.e., without specific path)
        String domain = url.host();
        cookieStore.put(domain, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        // Retrieve cookies by domain for any URL within the same domain
        String domain = url.host();
        List<Cookie> cookies = cookieStore.get(domain);
        return cookies != null ? cookies : java.util.Collections.emptyList();
    }
}


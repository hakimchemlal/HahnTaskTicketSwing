package com.hahn.client.http;

import okhttp3.OkHttpClient;

public class OkHttpClientWithCookies {
    private static OkHttpClient client;

    // Private constructor to prevent instantiation
    private OkHttpClientWithCookies() {}

    // Singleton method to get the OkHttpClient instance
    public static OkHttpClient getClient() {
        if (client == null) {
            synchronized (OkHttpClientWithCookies.class) {
                if (client == null) {
                    client = new OkHttpClient.Builder()
                            .cookieJar(new MyCookieJar()) // Set custom CookieJar
                            .build();
                }
            }
        }
        return client;
    }
}


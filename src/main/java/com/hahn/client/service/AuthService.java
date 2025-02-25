package com.hahn.client.service;

import com.google.gson.Gson;
import com.hahn.client.http.OkHttpClientWithCookies;
import com.hahn.client.model.AuthenticatedUserResponse;
import okhttp3.*;

import java.io.IOException;

public class AuthService {

    private static final String API_URL = "http://localhost:8080";
    private final OkHttpClient client;
    private final Gson gson;

    public AuthService() {
        this.client = OkHttpClientWithCookies.getClient();
        this.gson = new Gson();
    }

    public AuthenticatedUserResponse getUserInfo() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/api/user/me")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            return gson.fromJson(response.body().string(), AuthenticatedUserResponse.class);
        } else {
            throw new IOException("Failed to fetch user info: " + response.message());
        }
    }

    public boolean login(String username, String password) throws IOException {
        FormBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(API_URL + "/login")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    public boolean logout() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/logout")
                .post(RequestBody.create(null, new byte[0]))
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }
}

package org.bluesky.apollo.core;

import lombok.Data;
import okhttp3.*;

import java.io.IOException;

@Data
public class ApolloClient {
    private final OkHttpClient client = new OkHttpClient();
    private final String portalUrl;
    private final String token; // already without "Bearer " or include as you like

    public ApolloClient(String portalUrl, String token) {
        this.portalUrl = portalUrl;
        this.token = token;
    }

    private Request.Builder baseBuilder(String path) {
        return new Request.Builder()
                .url(portalUrl + path)
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");
    }

    public String post(String path, String jsonBody) throws IOException {
        RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
        Request req = baseBuilder(path).post(body).build();
        try (Response resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) throw new IOException("http code: " + resp.code() + ", body: " + resp.body().string());
            return resp.body() == null ? "" : resp.body().string();
        }
    }

    public String get(String path) throws IOException { /* similar */ }
    public String put(String path, String jsonBody) throws IOException { /* similar */ }
    public String delete(String path) throws IOException { /* similar */ }
}

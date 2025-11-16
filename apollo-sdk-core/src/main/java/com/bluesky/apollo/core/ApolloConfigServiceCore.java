package com.bluesky.apollo.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ApolloConfigServiceCore {
    private final ApolloClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApolloConfigServiceCore(ApolloClient client) {
        this.client = client;
    }

    public boolean createOrUpdateItem(String appId, String env, String cluster, String namespace,
                                      String key, String value, String comment, String operator) {
        String path = String.format("/openapi/v1/apps/%s/envs/%s/clusters/%s/namespaces/%s/items",
                appId, env, cluster, namespace);
        Map<String,Object> body = Map.of(
                "key", key, "value", value, "comment", comment, "dataChangeCreatedBy", operator
        );
        try {
            String json = mapper.writeValueAsString(body);
            client.post(path, json);
            return true;
        } catch (Exception e) {
            // log
            return false;
        }
    }

    public boolean publishNamespace(...) { /* call /releases ... */ }
    public boolean publishSingle(...) { /* combine createOrUpdate + publish */ }
    // getItem, deleteItem, listNamespaceItems ...
}

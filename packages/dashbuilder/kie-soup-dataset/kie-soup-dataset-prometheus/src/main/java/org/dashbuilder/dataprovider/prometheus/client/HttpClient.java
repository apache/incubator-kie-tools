/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataprovider.prometheus.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class HttpClient {

    private static HttpClient INSTANCE;

    private HttpClient() {
        // do nothing
    }

    public String doGet(String url) {
        return doGet(url, null, null);
    }

    public String doGet(String url, String username, String password) {
        try {
            URLConnection connection = new URL(url).openConnection();
            if (username != null && !username.trim().isEmpty()) {
                addAuth(connection, username, password);
            }
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            try (BufferedReader br = new BufferedReader(in)) {
                return br.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error performing HTTP Request: " + e.getMessage(), e);
        }
    }

    private void addAuth(URLConnection connection, String username, String password) {
        String userpass = username + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        connection.setRequestProperty("Authorization", basicAuth);
    }

    public static HttpClient get() {
        if (INSTANCE == null) {
            INSTANCE = new HttpClient();
        }
        return INSTANCE;
    }

}

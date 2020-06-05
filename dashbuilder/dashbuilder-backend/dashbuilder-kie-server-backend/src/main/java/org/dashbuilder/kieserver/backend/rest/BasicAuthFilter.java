/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver.backend.rest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.resteasy.util.Base64;

public class BasicAuthFilter implements ClientRequestFilter {

    private final String user;
    private final String password;

    public BasicAuthFilter(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public void filter(ClientRequestContext ctx) throws IOException {
        final String basicAuthToken = getEncodedToken();
        ctx.getHeaders().add(HttpHeaders.AUTHORIZATION, basicAuthToken);

    }

    private String getEncodedToken() {
        String token = this.user + ":" + this.password;
        return "Basic " + Base64.encodeBytes(token.getBytes(StandardCharsets.UTF_8));
    }
}
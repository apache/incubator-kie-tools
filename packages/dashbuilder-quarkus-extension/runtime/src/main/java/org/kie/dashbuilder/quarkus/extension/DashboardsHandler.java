/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dashbuilder.quarkus.extension;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

public class DashboardsHandler implements Handler<RoutingContext> {

    private String basePath;
    private Map<String, String> dashboards;

    public DashboardsHandler() {
    }

    public DashboardsHandler(String basePath, Map<String, String> dashboardsBuildItem) {
        this.basePath = basePath;
        this.dashboards = dashboardsBuildItem;
    }

    @Override
    public void handle(RoutingContext event) {

        var request = event.request();
        var response = event.response();

        var name = retrieveName(request.uri());

        var dashboard = dashboards.get(name);

        if (request.method() != HttpMethod.GET) {
            response.setStatusCode(405);
            response.end();
        } else if (dashboard != null) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
            response.setStatusCode(200);
            response.end(dashboard);
        } else {
            response.setStatusCode(404);
            response.end();
        }
    }

    String retrieveName(String uri) {
        var index = uri.indexOf(this.basePath) + this.basePath.length() + 1;
        if (index < uri.length()) {
            var name = uri.substring(index, uri.length());
            try {
                return URLDecoder.decode(name, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                return name;
            }
        }
        return null;
    }

}

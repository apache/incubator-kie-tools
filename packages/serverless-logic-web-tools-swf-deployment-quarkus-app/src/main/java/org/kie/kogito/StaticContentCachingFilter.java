/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito;

import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.vertx.http.runtime.filters.Filters;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class StaticContentCachingFilter {

    private static final String MAX_AGE = "max-age=86400"; // 1 day in seconds
    private static final String STATIC_FILE_EXTENSIONS_REGEX = ".*\\.(css|js|html)";

    void registerFilters(@Observes Filters filters) {
        filters.register(staticContentCachingHandler(), 10);
    }

    private Handler<RoutingContext> staticContentCachingHandler() {
        return event -> {
            String path = event.normalizedPath();
            if (Pattern.matches(STATIC_FILE_EXTENSIONS_REGEX, path)) {
                event.response().headers().set(HttpHeaders.CACHE_CONTROL, MAX_AGE);
            }
            event.next();
        };
    }
}

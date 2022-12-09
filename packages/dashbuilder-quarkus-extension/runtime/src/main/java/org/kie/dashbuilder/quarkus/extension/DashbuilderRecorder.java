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

import java.util.List;
import java.util.Map;

import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.http.runtime.devmode.FileSystemStaticHandler;
import io.quarkus.vertx.http.runtime.webjar.WebJarStaticHandler;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class DashbuilderRecorder {

    public Handler<RoutingContext> dashbuilderWebAppHandler(String dashbuilderFinalDestination, String dashbuilderPath,
            List<FileSystemStaticHandler.StaticWebRootConfiguration> webRootConfigurations,
            ShutdownContext shutdownContext) {

        var handler = new WebJarStaticHandler(dashbuilderFinalDestination, dashbuilderPath,
                webRootConfigurations);
        shutdownContext.addShutdownTask(new ShutdownContext.CloseRunnable(handler));
        return handler;
    }

    public Handler<RoutingContext> dashboardsHandler(String dashboardsWebContext, Map<String, String> dashboards) {
        return new DashboardsHandler(dashboardsWebContext, dashboards);

    }
}
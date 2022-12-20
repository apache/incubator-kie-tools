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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ApplicationArchivesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.HotDeploymentWatchedFileBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.maven.dependency.GACT;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.webjar.WebJarBuildItem;
import io.quarkus.vertx.http.deployment.webjar.WebJarResultsBuildItem;
import io.quarkus.vertx.http.deployment.webjar.WebJarResourcesFilter.FilterResult;

public class DashbuilderProcessor {

    private final static Logger log = Logger.getLogger(DashbuilderProcessor.class);

    private static final GACT DASHBUILDER_UI_WEBJAR_ARTIFACT_KEY = new GACT("org.kie.dashbuilder",
            "quarkus-dashbuilder-ui", null,
            "jar");

    private static final String FEATURE = "dashbuilder";

    private static final String SETUP_FILE = "setup.js";

    private static final String DASHBUILDER_STATIC_PATH = "META-INF/resources/dashbuilder";

    private static final String DASHBOARDS_WEB_CONTEXT = "__dashboard";

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep()
    public void scanDashboards(ApplicationArchivesBuildItem applicationArchives,
            BuildProducer<HotDeploymentWatchedFileBuildItem> hotDeploymentWatchedFiles,
            DashbuilderConfig dashbuilderConfig,
            BuildProducer<DashboardsBuildItem> dashboardsProducer) {

        var dashboardsBuildItem = new DashboardsBuildItem();
        var watchList = new ArrayList<String>();
        var dashboards = dashbuilderConfig.dashboards;

        if (dashboards.isEmpty()) {
            applicationArchives.getRootArchive().accept(t -> {
                t.walk(visit -> {
                    var path = visit.getPath();
                    if (isDashboard(path.toString())) {
                        var relativePath = visit.getRelativePath("/");
                        watchList.add(relativePath);
                        try {
                            var name = getDashboardName(path);
                            var content = Files.readString(path);
                            dashboardsBuildItem.register(name, content);
                        } catch (IOException e) {
                            log.errorv("Not able to load dashboard file {}: {}", relativePath, e.getMessage());
                            log.debug(e);
                        }
                    }
                });
            });
            int n = dashboardsBuildItem.list().size();
            log.info("Found " + n + " dashboard" + (n == 1 ? "" : "s"));
        } else {
            for (var db : dashboards.get()) {
                var name = getDashboardName(Paths.get(db));
                var content = readDashboardFromClasspath(db);
                if (content != null) {
                    watchList.add(db);
                    dashboardsBuildItem.register(name, content);
                    log.info("Registered " + db);
                } else {
                    log.warn("Not able to load " + db);
                }

            }
        }

        dashboardsProducer.produce(dashboardsBuildItem);
        watchList.forEach(
                db -> hotDeploymentWatchedFiles.produce(new HotDeploymentWatchedFileBuildItem(db)));
    }

    @BuildStep
    public void buildDashbuilderWebApp(
            DashboardsBuildItem dashboardsBuildItem,
            DashbuilderConfig dashbuilderConfig,
            BuildProducer<WebJarBuildItem> webJarBuildProducer)
            throws Exception {

        var dashboards = dashboardsBuildItem.list();
        var buildSetupJs = buildSetupJs(dashboards).getBytes();

        webJarBuildProducer.produce(
                WebJarBuildItem.builder().artifactKey(DASHBUILDER_UI_WEBJAR_ARTIFACT_KEY)
                        .root(DASHBUILDER_STATIC_PATH)
                        .filter((fileName, file) -> fileName.equals(SETUP_FILE)
                                ? new FilterResult(new ByteArrayInputStream(buildSetupJs), true)
                                : new FilterResult(file, false))
                        .build());
    }

    @BuildStep
    @Record(value = ExecutionTime.RUNTIME_INIT)
    public void registerDashbuilderHandler(
            DashbuilderRecorder dashbuilderRecorder,
            DashboardsBuildItem dashboardsBuildItem,
            BuildProducer<RouteBuildItem> routes,
            NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            WebJarResultsBuildItem webJarResultsBuildItem,
            LaunchModeBuildItem launchMode,
            DashbuilderConfig dashbuilderConfig,
            ShutdownContextBuildItem shutdownContext) {

        var result = webJarResultsBuildItem
                .byArtifactKey(DASHBUILDER_UI_WEBJAR_ARTIFACT_KEY);
        if (result == null) {
            return;
        }

        var dashbuilderWebAppPath = nonApplicationRootPathBuildItem.resolvePath(dashbuilderConfig.path);

        var webAppHandler = dashbuilderRecorder.dashbuilderWebAppHandler(result.getFinalDestination(),
                dashbuilderWebAppPath,
                result.getWebRootConfigurations(), shutdownContext);

        var dashboardsHandler = dashbuilderRecorder.dashboardsHandler(DASHBOARDS_WEB_CONTEXT,
                dashboardsBuildItem.getDashboards());

        var dashboardsContext = dashbuilderConfig.path + "/" + DASHBOARDS_WEB_CONTEXT;

        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .route(dashbuilderConfig.path)
                .displayOnNotFoundPage("Dashbuilder Web App")
                .routeConfigKey("quarkus.dashbuilder.path")
                .handler(webAppHandler)
                .build());

        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .route(dashbuilderConfig.path + "*")
                .handler(webAppHandler)
                .build());

        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .route(dashboardsContext)
                .handler(dashboardsHandler)
                .build());

        routes.produce(nonApplicationRootPathBuildItem.routeBuilder()
                .route(dashboardsContext + "*")
                .handler(dashboardsHandler)
                .build());
    }

    boolean isDashboard(String path) {
        return path.endsWith(".dash.yaml") ||
                path.endsWith(".dash.yml") ||
                path.endsWith(".dash.json");
    }

    String buildSetupJs(Set<String> dashboardsSet) {
        var dashboardsJsArray = dashboardsSet.stream()
                .map(p -> "'" + p + "'")
                .collect(Collectors.joining(",", "[", "]"));
        return "dashbuilder = {" +
                "   \"mode\": \"CLIENT\",\n" +
                "   \"path\": \"" + DASHBOARDS_WEB_CONTEXT + "\",\n" +
                "   \"dashboards\": " + dashboardsJsArray + "\n"
                + "}";
    }

    String getDashboardName(Path dashboard) {
        var fileName = dashboard.toFile().getName();
        var dotIndex = fileName.indexOf(".");
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

    String readDashboardFromClasspath(String db) {
        var resource = db;
        if (!db.startsWith("/")) {
            resource = "/" + resource;
        }
        var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (is != null) {
            try {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.errorv("Not able to read {}: {}", db, e.getMessage());
                log.debug(e);
            }
        }
        return null;
    }
}
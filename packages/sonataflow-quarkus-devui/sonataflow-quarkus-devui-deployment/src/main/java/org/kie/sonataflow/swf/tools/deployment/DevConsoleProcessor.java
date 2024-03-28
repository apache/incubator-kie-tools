/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.sonataflow.swf.tools.deployment;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import io.quarkus.deployment.Capabilities;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import org.kie.sonataflow.swf.tools.runtime.config.DevUIStaticArtifactsRecorder;

import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ConfigurationBuildItem;
import io.quarkus.deployment.builditem.LaunchModeBuildItem;
import io.quarkus.deployment.builditem.LiveReloadBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.management.ManagementInterfaceBuildTimeConfig;
import org.kie.sonataflow.swf.tools.runtime.rpc.SonataFlowQuarkusExtensionJsonRPCService;

public class DevConsoleProcessor {

    private static final String STATIC_RESOURCES_PATH = "dev-static/";
    private static final String BASE_RELATIVE_URL = "/q/dev-ui/org.apache.kie.sonataflow.sonataflow-quarkus-devui";

    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    public void deployStaticResources(final DevUIStaticArtifactsRecorder devUIStaticArtifactsRecorder,
            final CurateOutcomeBuildItem curateOutcomeBuildItem,
            final LiveReloadBuildItem liveReloadBuildItem,
            final LaunchModeBuildItem launchMode,
            final ShutdownContextBuildItem shutdownContext,
            final BuildProducer<RouteBuildItem> routeBuildItemBuildProducer) throws IOException {
        ResolvedDependency devConsoleResourcesArtifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem,
                "org.apache.kie.sonataflow",
                "sonataflow-quarkus-devui-deployment");

        Path devConsoleStaticResourcesDeploymentPath = WebJarUtil.copyResourcesForDevOrTest(
                liveReloadBuildItem,
                curateOutcomeBuildItem,
                launchMode,
                devConsoleResourcesArtifact,
                STATIC_RESOURCES_PATH,
                true);

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(BASE_RELATIVE_URL + "/resources/*")
                .handler(devUIStaticArtifactsRecorder.handler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(BASE_RELATIVE_URL + "/*")
                .handler(devUIStaticArtifactsRecorder.handler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public JsonRPCProvidersBuildItem createJsonRPCServiceForJBPMDevUi() {
        return new JsonRPCProvidersBuildItem(SonataFlowQuarkusExtensionJsonRPCService.class);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public CardPageBuildItem pages(
            final NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            final ManagementInterfaceBuildTimeConfig managementInterfaceBuildTimeConfig,
            final LaunchModeBuildItem launchModeBuildItem,
            final ConfigurationBuildItem configurationBuildItem,
            final List<SystemPropertyBuildItem> systemPropertyBuildItems,
            final Capabilities capabilities) {

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();

        String uiPath = nonApplicationRootPathBuildItem.resolveManagementPath(BASE_RELATIVE_URL,
                                                                              managementInterfaceBuildTimeConfig, launchModeBuildItem, true);

        String openapiPath = getProperty(configurationBuildItem, systemPropertyBuildItems, "quarkus.smallrye-openapi.path");
        String devUIUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.dev-ui.url");
        String dataIndexUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.data-index.url");

        cardPageBuildItem.addBuildTimeData("extensionBasePath", uiPath);
        cardPageBuildItem.addBuildTimeData("openapiPath", openapiPath);
        cardPageBuildItem.addBuildTimeData("devUIUrl", devUIUrl);
        cardPageBuildItem.addBuildTimeData("dataIndexUrl", dataIndexUrl);

        cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                                          .componentLink("qwc-sonataflow-quarkus-devui.js")
                                          .metadata("page", "Workflows")
                                          .title("Workflows")
                                          .icon("font-awesome-solid:diagram-project")
                                          .dynamicLabelJsonRPCMethodName("queryWorkflowsCount"));

        cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                                          .componentLink("qwc-sonataflow-quarkus-devui.js")
                                          .metadata("page", "Monitoring")
                                          .title("Monitoring")
                                          .icon("font-awesome-solid:chart-simple"));

        return cardPageBuildItem;
    }

    private static String getProperty(ConfigurationBuildItem configurationBuildItem,
            List<SystemPropertyBuildItem> systemPropertyBuildItems,
            String propertyKey) {

        String propertyValue = configurationBuildItem
                .getReadResult()
                .getAllBuildTimeValues()
                .get(propertyKey);

        if (propertyValue == null) {
            propertyValue = configurationBuildItem
                    .getReadResult()
                    .getBuildTimeRunTimeValues()
                    .get(propertyKey);
        } else {
            return propertyValue;
        }

        if (propertyValue == null) {
            propertyValue = configurationBuildItem
                    .getReadResult()
                    .getRunTimeDefaultValues()
                    .get(propertyKey);
        }

        if (propertyValue != null) {
            return propertyValue;
        }

        return systemPropertyBuildItems.stream().filter(property -> property.getKey().equals(propertyKey))
                .findAny()
                .map(SystemPropertyBuildItem::getValue).orElse(null);
    }
}

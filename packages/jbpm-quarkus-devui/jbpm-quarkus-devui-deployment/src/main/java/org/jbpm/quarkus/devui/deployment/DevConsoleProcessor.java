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
package org.jbpm.quarkus.devui.deployment;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoDataIndexServiceAvailableBuildItem;
import org.jbpm.quarkus.devui.deployment.data.UserInfo;
import org.jbpm.quarkus.devui.runtime.config.DevConsoleRuntimeConfig;
import org.jbpm.quarkus.devui.runtime.config.DevUIStaticArtifactsRecorder;
import org.jbpm.quarkus.devui.runtime.rpc.JBPMDevuiJsonRPCService;

import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.*;
import io.quarkus.deployment.pkg.builditem.CurateOutcomeBuildItem;
import io.quarkus.deployment.util.WebJarUtil;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.runtime.management.ManagementInterfaceBuildTimeConfig;

public class DevConsoleProcessor {

    private static final String STATIC_RESOURCES_PATH = "dev-static/";
    private static final String BASE_RELATIVE_URL = "dev-ui/org.jbpm.jbpm-quarkus-devui";
    private static final String NON_APPLICATION_BASE_RELATIVE_URL = "/q/" + BASE_RELATIVE_URL;
    private static final String DATA_INDEX_CAPABILITY = "org.kie.kogito.data-index";

    @SuppressWarnings("unused")
    @BuildStep(onlyIf = IsDevelopment.class)
    @Record(ExecutionTime.RUNTIME_INIT)
    public void deployStaticResources(final DevUIStaticArtifactsRecorder devUIStaticArtifactsRecorder,
            final CurateOutcomeBuildItem curateOutcomeBuildItem,
            final LiveReloadBuildItem liveReloadBuildItem,
            final LaunchModeBuildItem launchMode,
            final ShutdownContextBuildItem shutdownContext,
            final BuildProducer<RouteBuildItem> routeBuildItemBuildProducer) throws IOException {
        ResolvedDependency devConsoleResourcesArtifact = WebJarUtil.getAppArtifact(curateOutcomeBuildItem,
                "org.jbpm",
                "jbpm-quarkus-devui-deployment");

        Path devConsoleStaticResourcesDeploymentPath = WebJarUtil.copyResourcesForDevOrTest(
                liveReloadBuildItem,
                curateOutcomeBuildItem,
                launchMode,
                devConsoleResourcesArtifact,
                STATIC_RESOURCES_PATH,
                true);

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(NON_APPLICATION_BASE_RELATIVE_URL + "/resources/*")
                .handler(devUIStaticArtifactsRecorder.handler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());

        routeBuildItemBuildProducer.produce(new RouteBuildItem.Builder()
                .route(NON_APPLICATION_BASE_RELATIVE_URL + "/*")
                .handler(devUIStaticArtifactsRecorder.handler(devConsoleStaticResourcesDeploymentPath.toString(),
                        shutdownContext))
                .build());
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public JsonRPCProvidersBuildItem createJsonRPCServiceForJBPMDevUi() {
        return new JsonRPCProvidersBuildItem(JBPMDevuiJsonRPCService.class);
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    public CardPageBuildItem pages(
            final NonApplicationRootPathBuildItem nonApplicationRootPathBuildItem,
            final DevConsoleRuntimeConfig devConsoleRuntimeConfig,
            final ManagementInterfaceBuildTimeConfig managementInterfaceBuildTimeConfig,
            final LaunchModeBuildItem launchModeBuildItem,
            final ConfigurationBuildItem configurationBuildItem,
            final List<SystemPropertyBuildItem> systemPropertyBuildItems,
            final Optional<KogitoDataIndexServiceAvailableBuildItem> dataIndexServiceAvailableBuildItem,
            final Capabilities capabilities) {

        if (dataIndexServiceAvailableBuildItem.isEmpty() && !capabilities.isPresent(DATA_INDEX_CAPABILITY)) {
            return null;
        }

        String uiPath = nonApplicationRootPathBuildItem.resolveManagementPath(BASE_RELATIVE_URL,
                managementInterfaceBuildTimeConfig, launchModeBuildItem, true);

        String devUIUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.dev-ui.url");
        String dataIndexUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.dataindex.http.url");
        String trustyServiceUrl = getProperty(configurationBuildItem, systemPropertyBuildItems, "kogito.trusty.http.url");
        String quarkusHttpHost = ConfigProvider.getConfig().getValue("quarkus.http.host", String.class);
        String quarkusHttpPort = ConfigProvider.getConfig().getValue("quarkus.http.port", String.class);

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();

        cardPageBuildItem.addBuildTimeData("quarkusHttpHost", quarkusHttpHost);
        cardPageBuildItem.addBuildTimeData("quarkusHttpPort", quarkusHttpPort);
        cardPageBuildItem.addBuildTimeData("quarkusAppRootPath", nonApplicationRootPathBuildItem.getNormalizedHttpRootPath());
        cardPageBuildItem.addBuildTimeData("extensionBasePath", uiPath);
        cardPageBuildItem.addBuildTimeData("devUIUrl", devUIUrl);
        cardPageBuildItem.addBuildTimeData("dataIndexUrl", dataIndexUrl);
        cardPageBuildItem.addBuildTimeData("isTracingEnabled", false);
        cardPageBuildItem.addBuildTimeData("userData", readUsersInfo(devConsoleRuntimeConfig));

        cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                .componentLink("qwc-jbpm-quarkus-devui.js")
                .metadata("page", "Processes")
                .title("Process Instances")
                .icon("font-awesome-solid:diagram-project")
                .dynamicLabelJsonRPCMethodName("queryProcessInstancesCount"));

        cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                .componentLink("qwc-jbpm-quarkus-devui.js")
                .metadata("page", "TaskInbox")
                .title("Tasks")
                .icon("font-awesome-solid:bars-progress")
                .dynamicLabelJsonRPCMethodName("queryTasksCount"));

        cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                .componentLink("qwc-jbpm-quarkus-devui.js")
                .metadata("page", "JobsManagement")
                .title("Jobs")
                .icon("font-awesome-solid:clock")
                .dynamicLabelJsonRPCMethodName("queryJobsCount"));

        cardPageBuildItem.addPage(Page.webComponentPageBuilder()
                .componentLink("qwc-jbpm-quarkus-devui.js")
                .metadata("page", "Forms")
                .title("Forms")
                .icon("font-awesome-solid:table-cells")
                .dynamicLabelJsonRPCMethodName("getFormsCount"));

        return cardPageBuildItem;
    }

    private Collection<UserInfo> readUsersInfo(DevConsoleRuntimeConfig devConsoleRuntimeConfig) {
        if (devConsoleRuntimeConfig.userConfigByUser.isEmpty()) {
            return Collections.emptyList();
        }

        return devConsoleRuntimeConfig.userConfigByUser.entrySet().stream()
                .map(entry -> new UserInfo(entry.getKey(), entry.getValue().groups))
                .collect(Collectors.toList());
    }

    private static String getProperty(ConfigurationBuildItem configurationBuildItem,
            List<SystemPropertyBuildItem> systemPropertyBuildItems, String propertyKey) {

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

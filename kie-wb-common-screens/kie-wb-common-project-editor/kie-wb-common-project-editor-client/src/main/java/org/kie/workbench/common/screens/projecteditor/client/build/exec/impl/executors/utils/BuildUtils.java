/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.impl.executors.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.guvnor.common.services.project.model.GAV;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.kie.workbench.common.screens.server.management.model.MergeMode;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;

public class BuildUtils {

    public static Set<String> extractExistingContainers(final ServerTemplate serverTemplate) {
        final Collection<ContainerSpec> containersSpec = serverTemplate.getContainersSpec();

        return containersSpec
                .stream()
                .map(ContainerSpecKey::getId)
                .collect(Collectors.toSet());
    }

    public static ContainerSpec makeContainerSpec(BuildExecutionContext context, final Map<String, String> parameters) {
        GAV gav = context.getModule().getPom().getGav();

        final ReleaseId releaseId = new ReleaseId(gav.getGroupId(), gav.getArtifactId(), gav.getVersion());

        ServerTemplate serverTemplate = context.getServerTemplate();

        final KieContainerStatus status = KieContainerStatus.STOPPED;
        final ServerTemplateKey serverTemplateKey = new ServerTemplateKey(serverTemplate.getId(), serverTemplate.getId());

        return new ContainerSpec(context.getContainerId(),
                                 context.getContainerAlias(),
                                 serverTemplateKey,
                                 releaseId,
                                 status,
                                 makeConfigs(serverTemplate, parameters));
    }

    private static Map<Capability, ContainerConfig> makeConfigs(final ServerTemplate serverTemplate,
                                                                final Map<String, String> parameters) {
        final Map<Capability, ContainerConfig> configs = new HashMap<>();

        if (hasProcessCapability(serverTemplate)) {
            configs.put(Capability.PROCESS, makeProcessConfig(parameters));
        }

        configs.put(Capability.RULE, makeRuleConfig());

        return configs;
    }

    private static RuleConfig makeRuleConfig() {
        return new RuleConfig(null, KieScannerStatus.STOPPED);
    }

    private static ProcessConfig makeProcessConfig(final Map<String, String> parameters) {
        String strategy = parameters.getOrDefault("RuntimeStrategy", RuntimeStrategy.SINGLETON.name());
        return new ProcessConfig(strategy, "", "", MergeMode.MERGE_COLLECTIONS.name());
    }

    private static boolean hasProcessCapability(final ServerTemplate serverTemplate) {
        final List<String> capabilities = serverTemplate.getCapabilities();
        final String process = Capability.PROCESS.name();

        return capabilities.contains(process);
    }
}

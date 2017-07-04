/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.build;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentScreenPopupViewImpl;
import org.kie.workbench.common.screens.projecteditor.client.editor.DeploymentScreenPopupViewImpl.ValidateExistingContainerCallback;
import org.uberfire.mvp.ParameterizedCommand;

class DeploymentPopupBuilder {

    private final BuildExecutor buildExecutor;

    DeploymentPopupBuilder(final BuildExecutor buildExecutor) {
        this.buildExecutor = buildExecutor;
    }

    DeploymentScreenPopupViewImpl buildDeployWithMultipleServerTemplates(final Collection<ServerTemplate> serverTemplates,
                                                                         final ParameterizedCommand<DeploymentScreenPopupViewImpl> onSuccess) {

        return setViewFields(onSuccess,
                             multipleServerTemplatesValidation(serverTemplates));
    }

    DeploymentScreenPopupViewImpl buildDeployWithOneServerTemplate(final ServerTemplate serverTemplate,
                                                                   final ParameterizedCommand<DeploymentScreenPopupViewImpl> onSuccess) {

        return setViewFields(onSuccess,
                             singleServerTemplatesValidation(serverTemplate));
    }

    ValidateExistingContainerCallback multipleServerTemplatesValidation(final Collection<ServerTemplate> serverTemplates) {
        final Map<String, ServerTemplate> serverTemplateByIds = serverTemplateByIds(serverTemplates);
        final Map<String, Set<String>> containerNamesByServerTemplateIds = containerNamesByServerTemplateIds(serverTemplateByIds);
        final TreeSet<String> orderedServerTemplateIds = orderedServerTemplateIds(serverTemplateByIds);

        view().addServerTemplates(orderedServerTemplateIds);

        return containerName -> {
            final Set<String> containers = containerNamesByServerTemplateIds.get(view().getServerTemplate());

            return containers
                    .stream()
                    .anyMatch(s -> Objects.equals(s,
                                                  containerName));
        };
    }

    ValidateExistingContainerCallback singleServerTemplatesValidation(final ServerTemplate serverTemplate) {
        return buildExecutor.existingContainers(serverTemplate)::contains;
    }

    DeploymentScreenPopupViewImpl setViewFields(final ParameterizedCommand<DeploymentScreenPopupViewImpl> onSuccess,
                                                final ValidateExistingContainerCallback existingContainersValidation) {

        view().setValidateExistingContainerCallback(existingContainersValidation);
        view().setContainerId(buildExecutor.defaultContainerId());
        view().setContainerAlias(buildExecutor.defaultContainerAlias());
        view().setStartContainer(true);

        view().configure(() -> {
            onSuccess.execute(view());

            view().hide();
        });

        return view();
    }

    private DeploymentScreenPopupViewImpl view() {
        return buildExecutor.getDeploymentScreenPopupViewImpl();
    }

    private TreeSet<String> orderedServerTemplateIds(final Map<String, ServerTemplate> serverTemplatesIds) {
        return serverTemplatesIds.keySet().stream().collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
    }

    private Map<String, Set<String>> containerNamesByServerTemplateIds(final Map<String, ServerTemplate> serverTemplatesIds) {
        return Maps.transformEntries(serverTemplatesIds,
                                     (id, server) ->
                                             FluentIterable.from(server.getContainersSpec()).transform(c -> c.getContainerName()).toSet()
        );
    }

    private ImmutableMap<String, ServerTemplate> serverTemplateByIds(final Collection<ServerTemplate> serverTemplates) {
        return Maps.uniqueIndex(serverTemplates,
                                s -> s.getId());
    }
}

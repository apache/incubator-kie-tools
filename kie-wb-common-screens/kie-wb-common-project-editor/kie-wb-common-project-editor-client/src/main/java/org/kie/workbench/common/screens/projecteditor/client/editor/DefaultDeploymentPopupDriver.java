/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.build.exec.BuildExecutionContext;
import org.uberfire.mvp.Command;

import java.util.Collection;
import java.util.function.Supplier;

public class DefaultDeploymentPopupDriver implements DeploymentPopup.Driver {

    private BuildExecutionContext context;
    private DeploymentPopup.Mode mode;
    private Supplier<Collection<ServerTemplate>> templatesSupplier;
    private Command onOkCallback;
    private Command onCancelCallback;

    public DefaultDeploymentPopupDriver(final BuildExecutionContext context,
                                        final DeploymentPopup.Mode mode,
                                        final Supplier<Collection<ServerTemplate>> templatesSupplier,
                                        final Command onOkCallback,
                                        final Command onCancelCallback) {
        this.context = context;
        this.mode = mode;
        this.templatesSupplier = templatesSupplier;
        this.onOkCallback = onOkCallback;
        this.onCancelCallback = onCancelCallback;
    }

    @Override
    public DeploymentPopup.Mode getMode() {
        return mode;
    }

    @Override
    public String getContainerId() {
        return context.getContainerId();
    }

    @Override
    public String getContainerAlias() {
        return context.getContainerAlias();
    }

    @Override
    public Collection<ServerTemplate> getAllServerTemplates() {
        return templatesSupplier.get();
    }

    @Override
    public ServerTemplate getServerTemplate() {
        return context.getServerTemplate();
    }

    @Override
    public boolean isStartContainer() {
        return context.isStartContainer();
    }

    @Override
    public void finish(String containerId, String containerAlias, String serverTemplateId, boolean startContainer) {
        if (mode.isModifyConfig()) {
            context.setContainerId(containerId);
            context.setContainerAlias(containerAlias);
            context.setStartContainer(startContainer);
        }
        if (mode.isSelectTemplate()) {

            ServerTemplate finalTemplate = getAllServerTemplates().stream()
                    .filter(template -> template.getId().equals(serverTemplateId))
                    .findAny()
                    .orElse(null);

            context.setServerTemplate(finalTemplate);
        }

        onOkCallback.execute();
    }

    @Override
    public void cancel() {
        onCancelCallback.execute();
    }
}

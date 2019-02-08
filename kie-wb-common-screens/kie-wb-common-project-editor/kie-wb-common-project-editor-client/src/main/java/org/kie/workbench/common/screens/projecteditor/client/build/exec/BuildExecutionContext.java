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

package org.kie.workbench.common.screens.projecteditor.client.build.exec;

import org.guvnor.common.services.project.model.Module;
import org.kie.server.controller.api.model.spec.ServerTemplate;

public class BuildExecutionContext {

    private final Module module;

    private String containerId;
    private String containerAlias;
    private boolean startContainer = true;

    private ServerTemplate serverTemplate;

    public BuildExecutionContext(String containerId, String containerAlias, Module module) {
        this.containerId = containerId;
        this.containerAlias = containerAlias;
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public String getContainerAlias() {
        return containerAlias;
    }

    public void setContainerAlias(String containerAlias) {
        this.containerAlias = containerAlias;
    }

    public ServerTemplate getServerTemplate() {
        return serverTemplate;
    }

    public void setServerTemplate(ServerTemplate serverTemplate) {
        this.serverTemplate = serverTemplate;
    }

    public boolean isStartContainer() {
        return startContainer;
    }

    public void setStartContainer(boolean startContainer) {
        this.startContainer = startContainer;
    }
}

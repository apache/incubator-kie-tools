/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.model;

import java.io.Serializable;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

@Portable
public class ContainerUpdateEvent implements Serializable {

    private static final long serialVersionUID = 2815969211450778262L;

    private ServerTemplateKey serverTemplateKey;

    private ContainerSpec containerSpec;

    private List<ServerInstanceKey> failedServerInstances;

    private ContainerRuntimeState containerRuntimeState;

    private ContainerRuntimeOperation containerRuntimeOperation;

    public ContainerUpdateEvent() {
    }

    public ContainerUpdateEvent(ServerTemplateKey serverTemplateKey,
            ContainerSpec containerSpec,
            List<ServerInstanceKey> failedServerInstances,
            ContainerRuntimeState containerRuntimeState,
            ContainerRuntimeOperation containerRuntimeOperation) {
        this.serverTemplateKey = serverTemplateKey;
        this.containerSpec = containerSpec;
        this.failedServerInstances = failedServerInstances;
        this.containerRuntimeState = containerRuntimeState;
        this.containerRuntimeOperation = containerRuntimeOperation;
    }

    public ServerTemplateKey getServerTemplateKey() {
        return serverTemplateKey;
    }

    public void setServerTemplateKey(ServerTemplateKey serverTemplateKey) {
        this.serverTemplateKey = serverTemplateKey;
    }

    public ContainerSpec getContainerSpec() {
        return containerSpec;
    }

    public void setContainerSpec(ContainerSpec containerSpec) {
        this.containerSpec = containerSpec;
    }

    public List<ServerInstanceKey> getFailedServerInstances() {
        return failedServerInstances;
    }

    public void setFailedServerInstances(List<ServerInstanceKey> failedServerInstances) {
        this.failedServerInstances = failedServerInstances;
    }

    public ContainerRuntimeState getContainerRuntimeState() {
        return containerRuntimeState;
    }

    public void setContainerRuntimeState(ContainerRuntimeState containerRuntimeState) {
        this.containerRuntimeState = containerRuntimeState;
    }

    public ContainerRuntimeOperation getContainerRuntimeOperation() {
        return containerRuntimeOperation;
    }

    public void setContainerRuntimeOperation(ContainerRuntimeOperation containerRuntimeOperation) {
        this.containerRuntimeOperation = containerRuntimeOperation;
    }

    @Override
    public String toString() {
        return "ContainerUpdateEvent{" +
                "serverTemplateKey=" + serverTemplateKey +
                ", containerSpec=" + containerSpec +
                ", containerRuntimeState=" + containerRuntimeState +
                ", failedServerInstances=" + failedServerInstances +
                '}';
    }
}

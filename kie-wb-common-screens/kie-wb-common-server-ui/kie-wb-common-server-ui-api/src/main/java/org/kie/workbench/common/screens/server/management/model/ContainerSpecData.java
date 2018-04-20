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

import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;

@Portable
public class ContainerSpecData {

    private ContainerSpec containerSpec;
    private Collection<Container> containers;

    public ContainerSpecData() {

    }

    public ContainerSpecData( final ContainerSpec containerSpec,
                              final Collection<Container> containers ) {
        this.containerSpec = containerSpec;
        this.containers = containers;
    }

    public ContainerSpec getContainerSpec() {
        return containerSpec;
    }

    public Collection<Container> getContainers() {
        return containers;
    }

    @Override
    public String toString() {
        return "ContainerSpecData{" +
                "containerSpec=" + containerSpec +
                ", containers=" + containers +
                '}';
    }
}
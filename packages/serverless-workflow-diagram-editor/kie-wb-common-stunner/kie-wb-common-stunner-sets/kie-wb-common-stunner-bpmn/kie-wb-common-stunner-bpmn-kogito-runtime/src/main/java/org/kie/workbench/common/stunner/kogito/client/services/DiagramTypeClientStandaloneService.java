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

package org.kie.workbench.common.stunner.kogito.client.services;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.client.diagram.DiagramTypeClientService;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

@ApplicationScoped
public class DiagramTypeClientStandaloneService implements DiagramTypeClientService {

    @Override
    public void loadDiagramType(final Metadata metadata) {
    }

    @Override
    public ProjectType getProjectType(final Metadata metadata) {
        return ProjectType.BPMN;
    }
}

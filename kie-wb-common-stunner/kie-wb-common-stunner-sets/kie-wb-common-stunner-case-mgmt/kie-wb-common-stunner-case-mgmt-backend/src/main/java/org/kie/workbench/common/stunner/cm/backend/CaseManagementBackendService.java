/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.cm.resource.CaseManagementDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.backend.service.AbstractDefinitionSetService;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class CaseManagementBackendService extends AbstractDefinitionSetService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CaseManagementBackendService.class);
    private static final String MARSHALLER_LEGACY_PROPERTY = "cm.marshaller.legacy";

    private CaseManagementDefinitionSetResourceType cmResourceType;

    protected CaseManagementBackendService() {
        this(null, null, null);
    }

    @Inject
    public CaseManagementBackendService(final @CaseManagementEditor CaseManagementDiagramMarshaller cmDiagramMarshaller,
                                        final @CaseManagementEditor CaseManagementDirectDiagramMarshaller cmDirectDiagramMarshaller,
                                        final CaseManagementDefinitionSetResourceType cmResourceType) {
        super(chooseMarshaller(cmDiagramMarshaller,
                               cmDirectDiagramMarshaller));
        this.cmResourceType = cmResourceType;
    }

    private static DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> chooseMarshaller(
            final CaseManagementDiagramMarshaller cmDiagramMarshaller,
            final CaseManagementDirectDiagramMarshaller cmDirectDiagramMarshaller) {

        Boolean useLegacyMarshaller =
                Boolean.parseBoolean(System.getProperty(MARSHALLER_LEGACY_PROPERTY, "false"));

        LOG.info("{} = {}", MARSHALLER_LEGACY_PROPERTY, useLegacyMarshaller);

        return (useLegacyMarshaller) ?
                cmDiagramMarshaller :
                cmDirectDiagramMarshaller;
    }

    @Override
    public DefinitionSetResourceType getResourceType() {
        return cmResourceType;
    }
}

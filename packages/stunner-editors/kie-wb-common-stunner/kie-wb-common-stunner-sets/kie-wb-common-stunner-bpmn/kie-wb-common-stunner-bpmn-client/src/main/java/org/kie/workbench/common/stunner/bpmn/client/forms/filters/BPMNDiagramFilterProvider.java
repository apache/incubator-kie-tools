/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.stunner.bpmn.client.diagram.DiagramTypeClientService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.kie.workbench.common.stunner.forms.client.formFilters.StunnerFormElementFilterProvider;

@Dependent
public class BPMNDiagramFilterProvider implements StunnerFormElementFilterProvider {

    private final SessionManager sessionManager;
    private final DiagramTypeClientService diagramTypeService;
    private final FieldChangeHandlerManager fieldChangeHandlerManager;
    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    BPMNDiagramFilterProvider() {
        this(null, null, null, null);
    }

    @Inject
    public BPMNDiagramFilterProvider(final SessionManager sessionManager,
                                     final DiagramTypeClientService diagramTypeService,
                                     final FieldChangeHandlerManager fieldChangeHandlerManager,
                                     final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent) {
        this.sessionManager = sessionManager;
        this.diagramTypeService = diagramTypeService;
        this.fieldChangeHandlerManager = fieldChangeHandlerManager;
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
    }

    @Override
    public Class<?> getDefinitionType() {
        return BPMNDiagramImpl.class;
    }

    @Override
    public Collection<FormElementFilter> provideFilters(String elementUUID, Object definition) {
        final BPMNDiagram diagram = (BPMNDiagram) definition;
        final Boolean isAdHoc = diagram.getDiagramSet().getAdHoc().getValue();

        final Metadata metadata = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getMetadata();
        final ProjectType currentProjectType = diagramTypeService.getProjectType(metadata);

        final Predicate predicate = t -> isAdHoc && Objects.equals(currentProjectType, ProjectType.CASE);
        final FormElementFilter filter = new FormElementFilter(BPMNDiagramImpl.CASE_MANAGEMENT_SET, predicate);

        return Arrays.asList(filter);
    }

    void onFormFieldChanged(@Observes FormFieldChanged formFieldChanged) {
        final String adHocFieldName = BPMNDiagramImpl.DIAGRAM_SET + "." + DiagramSet.ADHOC;
        if (!Objects.equals(formFieldChanged.getName(), adHocFieldName)) {
            return;
        }

        refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(sessionManager.getCurrentSession(), formFieldChanged.getUuid()));
    }
}
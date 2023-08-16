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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.stunner.bpmn.client.diagram.DiagramTypeClientService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.service.ProjectType;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDiagramFilterProviderTest {

    private static final String UUID = "uuid";

    private static final String ADHOC_FIELD_NAME = BPMNDiagramImpl.DIAGRAM_SET + "." + DiagramSet.ADHOC;

    private BPMNDiagramFilterProvider tested;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DiagramTypeClientService diagramTypeService;

    @Mock
    private FieldChangeHandlerManager fieldChangeHandlerManager;

    @Mock
    private EventSourceMock<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    @Mock
    private BPMNDiagramImpl diagramDef;

    @Mock
    private Element<? extends Definition<?>> element;

    @Mock
    private FormFieldChanged formFieldChanged;

    @Mock
    private DiagramSet diagramSet;

    @Mock
    private AdHoc adHoc;

    @Mock
    private ClientSession session;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Before
    public void setUp() throws Exception {
        when(formFieldChanged.getName()).thenReturn(ADHOC_FIELD_NAME);
        when(formFieldChanged.getUuid()).thenReturn(UUID);
        when(diagramDef.getDiagramSet()).thenReturn(diagramSet);
        when(diagramSet.getAdHoc()).thenReturn(adHoc);
        when(adHoc.getValue()).thenReturn(true);
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(diagramTypeService.getProjectType(metadata)).thenReturn(ProjectType.CASE);

        tested = new BPMNDiagramFilterProvider(sessionManager, diagramTypeService, fieldChangeHandlerManager, refreshFormPropertiesEvent);
    }

    @Test
    public void getDefinitionType() {
        assertEquals(tested.getDefinitionType(), BPMNDiagramImpl.class);
    }

    @Test
    public void provideFiltersCase() {
        final FormElementFilter filter = testAndGetFormElementFilter();
        assertTrue(filter.getPredicate().test(null));
    }

    @Test
    public void provideFiltersBPMN() {
        when(diagramTypeService.getProjectType(metadata)).thenReturn(ProjectType.BPMN);
        final FormElementFilter filter = testAndGetFormElementFilter();
        assertFalse(filter.getPredicate().test(null));
    }

    private FormElementFilter testAndGetFormElementFilter() {
        final FormElementFilter filter = tested.provideFilters(UUID, diagramDef).stream().findFirst().get();
        assertEquals(filter.getElementName(), BPMNDiagramImpl.CASE_MANAGEMENT_SET);
        return filter;
    }

    @Test
    public void onFormFieldChanged() {
        tested.onFormFieldChanged(formFieldChanged);
        final ArgumentCaptor<RefreshFormPropertiesEvent> refreshFormPropertiesArgumentCaptor = ArgumentCaptor.forClass(RefreshFormPropertiesEvent.class);
        verify(refreshFormPropertiesEvent).fire(refreshFormPropertiesArgumentCaptor.capture());
        RefreshFormPropertiesEvent refreshEvent = refreshFormPropertiesArgumentCaptor.getValue();
        assertEquals(refreshEvent.getUuid(), UUID);
        assertEquals(refreshEvent.getSession(), session);
    }
}
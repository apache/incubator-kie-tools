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

package org.kie.workbench.common.stunner.bpmn.client.components.palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.ExpandedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.getId;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNPaletteDefinitionBuilderTest {

    private static final DefaultPaletteDefinition PALETTE_DEFINITION = new DefaultPaletteDefinition(new ArrayList<>(),
                                                                                                    "defSet1");

    private static final String WID_NAME = "wid1Name";
    private static final String WID_CAT = "wid1Cat";
    private static final String WID_DISPLAY_NAME = "widDisplayName";
    private static final String WID_DESC = "widDesc";
    private static final WorkItemDefinition WID = new WorkItemDefinition()
            .setName(WID_NAME)
            .setCategory(WID_CAT)
            .setDefaultHandler("widHandler1")
            .setDisplayName(WID_DISPLAY_NAME)
            .setDescription(WID_DESC)
            .setIconDefinition(new IconDefinition().setIconData("widIconData"));

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private AdapterRegistry adapterRegistry;

    @Mock
    private DefinitionAdapter<Object> widAdapter;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private StunnerTranslationService translationService;

    @Mock
    private WorkItemDefinitionRegistry workItemDefinitionRegistry;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private ServiceTask serviceTask;

    private BPMNPaletteDefinitionBuilder tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(widAdapter);
        when(widAdapter.getId(eq(serviceTask))).thenReturn(WID_NAME);
        when(widAdapter.getCategory(eq(serviceTask))).thenReturn(WID_CAT);
        when(widAdapter.getTitle(eq(serviceTask))).thenReturn(WID_DISPLAY_NAME);
        when(widAdapter.getDescription(eq(serviceTask))).thenReturn(WID_DESC);
        ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder = spy(new ExpandedPaletteDefinitionBuilder(definitionUtils,
                                                                                                             definitionsRegistry,
                                                                                                             translationService));
        doAnswer(invocationOnMock -> {
            Consumer<DefaultPaletteDefinition> definitionConsumer = (Consumer<DefaultPaletteDefinition>) invocationOnMock.getArguments()[1];
            definitionConsumer.accept(PALETTE_DEFINITION);
            return null;
        }).when(paletteDefinitionBuilder).build(eq(canvasHandler),
                                                any(Consumer.class));
        Function<WorkItemDefinition, ServiceTask> serviceTaskBuilder =
                wid -> WID.equals(wid) ? serviceTask : null;
        tested = new BPMNPaletteDefinitionBuilder(definitionManager,
                                                  paletteDefinitionBuilder,
                                                  translationService,
                                                  () -> workItemDefinitionRegistry,
                                                  serviceTaskBuilder,
                                                  definitionUtils);
        tested.init();
    }

    @Test
    public void testFilteredItems() {
        ExpandedPaletteDefinitionBuilder delegate = tested.getPaletteDefinitionBuilder();
        assertTrue(delegate.getItemFilter().test(getId(UserTask.class)));
        assertTrue(delegate.getItemFilter().test(getId(Lane.class)));
        assertTrue(delegate.getItemFilter().test(getId(StartNoneEvent.class)));
        assertTrue(delegate.getItemFilter().test(getId(EndSignalEvent.class)));
        assertFalse(delegate.getItemFilter().test(getId(BPMNDiagramImpl.class)));
        assertFalse(delegate.getItemFilter().test(getId(SequenceFlow.class)));
        assertFalse(delegate.getItemFilter().test(getId(NoneTask.class)));
        assertFalse(delegate.getItemFilter().test(getId(Association.class)));
    }

    @Test
    public void testBuild() {
        Consumer<DefaultPaletteDefinition> resultConsumer = mock(Consumer.class);
        tested.build(canvasHandler,
                     resultConsumer);
        verify(resultConsumer, times(1)).accept(eq(PALETTE_DEFINITION));
        assertTrue(PALETTE_DEFINITION.getItems().isEmpty());
    }

    @Test
    public void testBuildWithServiceTasks() {
        when(workItemDefinitionRegistry.items()).thenReturn(Collections.singletonList(WID));
        when(workItemDefinitionRegistry.get(eq(WID_NAME))).thenReturn(WID);
        Consumer<DefaultPaletteDefinition> resultConsumer = mock(Consumer.class);
        tested.build(canvasHandler,
                     resultConsumer);
        verify(resultConsumer, times(1)).accept(eq(PALETTE_DEFINITION));
        assertFalse(PALETTE_DEFINITION.getItems().isEmpty());
    }
}

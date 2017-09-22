/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.client.palette;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess.AdHocSubprocessBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask.BusinessRuleTaskBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent.EndNoneEventBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent.EndTerminateEventBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent.IntermediateTimerEventBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.Lane.LaneBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask.NoneTaskBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway.ParallelGatewayBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask.ScriptTaskBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow.SequenceFlowBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent.StartNoneEventBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent.StartSignalEventBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent.StartTimerEventBuilder;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask.UserTaskBuilder;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidget;
import org.kie.workbench.common.stunner.cm.CaseManagementDefinitionSet;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess.ReusableSubprocessBuilder;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.service.FactoryService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementDefinitionSetPaletteBuilderImplTest {

    final Set<String> definitions = new HashSet<String>() {{
        add(CaseManagementDiagram.class.getName());
        add(Lane.class.getName());
        add(NoneTask.class.getName());
        add(UserTask.class.getName());
        add(ScriptTask.class.getName());
        add(BusinessRuleTask.class.getName());
        add(StartNoneEvent.class.getName());
        add(StartSignalEvent.class.getName());
        add(StartTimerEvent.class.getName());
        add(EndNoneEvent.class.getName());
        add(EndTerminateEvent.class.getName());
        add(IntermediateTimerEvent.class.getName());
        add(ParallelGateway.class.getName());
        add(ExclusiveDatabasedGateway.class.getName());
        add(AdHocSubprocess.class.getName());
        add(ReusableSubprocess.class.getName());
        add(SequenceFlow.class.getName());
    }};

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private BS3PaletteWidget palette;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private ClientFactoryManager clientFactoryManager;

    @Mock
    private FactoryService factoryService;
    private Caller<FactoryService> factoryServiceCaller;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private DefinitionAdapter definitionAdapter;

    private DefinitionUtils definitionUtils;
    private ClientFactoryService clientFactoryServices;
    private CaseManagementDefinitionSetPaletteBuilderImpl builder;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.definitionUtils = new DefinitionUtils(definitionManager,
                                                   factoryManager);
        this.factoryServiceCaller = new CallerMock<>(factoryService);
        this.clientFactoryServices = new ClientFactoryService(clientFactoryManager,
                                                              factoryServiceCaller);
        this.builder = new CaseManagementDefinitionSetPaletteBuilderImpl(definitionUtils,
                                                                         clientFactoryServices);

        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getId(anyObject())).thenReturn(CaseManagementDefinitionSet.class.getName());
        when(definitionSetAdapter.getDefinitions(anyObject())).thenReturn(definitions);

        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(definitionAdapter.getId(anyObject())).thenAnswer((i) -> i.getArguments()[0].getClass().getName());

        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(CaseManagementDiagram.class)))).thenReturn(CaseManagementDiagram.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(Lane.class)))).thenReturn(Lane.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(StartNoneEvent.class)))).thenReturn(StartNoneEvent.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(StartSignalEvent.class)))).thenReturn(StartSignalEvent.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(StartTimerEvent.class)))).thenReturn(StartTimerEvent.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(EndNoneEvent.class)))).thenReturn(EndNoneEvent.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(EndTerminateEvent.class)))).thenReturn(EndTerminateEvent.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(IntermediateTimerEvent.class)))).thenReturn(IntermediateTimerEvent.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(ParallelGateway.class)))).thenReturn(ParallelGateway.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(ExclusiveDatabasedGateway.class)))).thenReturn(ExclusiveDatabasedGateway.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(SequenceFlow.class)))).thenReturn(SequenceFlow.category);

        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(AdHocSubprocess.class)))).thenReturn(AdHocSubprocess.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(NoneTask.class)))).thenReturn(NoneTask.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(UserTask.class)))).thenReturn(UserTask.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(ScriptTask.class)))).thenReturn(ScriptTask.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(BusinessRuleTask.class)))).thenReturn(BusinessRuleTask.category);
        when(definitionAdapter.getCategory(argThat(new ClassOrSubclassMatcher(ReusableSubprocess.class)))).thenReturn(ReusableSubprocess.category);

        when(clientFactoryManager.newDefinition(eq(CaseManagementDiagram.class.getName()))).thenReturn(new CaseManagementDiagram.CaseManagementDiagramBuilder().build());
        when(clientFactoryManager.newDefinition(eq(Lane.class.getName()))).thenReturn(new LaneBuilder().build());
        when(clientFactoryManager.newDefinition(eq(StartNoneEvent.class.getName()))).thenReturn(new StartNoneEventBuilder().build());
        when(clientFactoryManager.newDefinition(eq(StartSignalEvent.class.getName()))).thenReturn(new StartSignalEventBuilder().build());
        when(clientFactoryManager.newDefinition(eq(StartTimerEvent.class.getName()))).thenReturn(new StartTimerEventBuilder().build());
        when(clientFactoryManager.newDefinition(eq(EndNoneEvent.class.getName()))).thenReturn(new EndNoneEventBuilder().build());
        when(clientFactoryManager.newDefinition(eq(EndTerminateEvent.class.getName()))).thenReturn(new EndTerminateEventBuilder().build());
        when(clientFactoryManager.newDefinition(eq(IntermediateTimerEvent.class.getName()))).thenReturn(new IntermediateTimerEventBuilder().build());
        when(clientFactoryManager.newDefinition(eq(ParallelGateway.class.getName()))).thenReturn(new ParallelGatewayBuilder().build());
        when(clientFactoryManager.newDefinition(eq(ExclusiveDatabasedGateway.class.getName()))).thenReturn(new ExclusiveDatabasedGatewayBuilder().build());
        when(clientFactoryManager.newDefinition(eq(SequenceFlow.class.getName()))).thenReturn(new SequenceFlowBuilder().build());

        when(clientFactoryManager.newDefinition(eq(AdHocSubprocess.class.getName()))).thenReturn(new AdHocSubprocessBuilder().build());
        when(clientFactoryManager.newDefinition(eq(NoneTask.class.getName()))).thenReturn(new NoneTaskBuilder().build());
        when(clientFactoryManager.newDefinition(eq(UserTask.class.getName()))).thenReturn(new UserTaskBuilder().build());
        when(clientFactoryManager.newDefinition(eq(ScriptTask.class.getName()))).thenReturn(new ScriptTaskBuilder().build());
        when(clientFactoryManager.newDefinition(eq(BusinessRuleTask.class.getName()))).thenReturn(new BusinessRuleTaskBuilder().build());
        when(clientFactoryManager.newDefinition(eq(ReusableSubprocess.class.getName()))).thenReturn(new ReusableSubprocessBuilder().build());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkConstruction() {
        //Must use PaletteDefinitionFactory to correctly initialise the builder
        final CaseManagementPaletteDefinitionFactory factory = new CaseManagementPaletteDefinitionFactory(shapeManager,
                                                                                                          builder,
                                                                                                          palette);
        factory.configureBuilder();

        //Construct palette
        final PaletteDefinitionBuilder.Configuration configuration = new PaletteDefinitionBuilder.Configuration() {
            @Override
            public String getDefinitionSetId() {
                return CaseManagementDefinitionSet.class.getName();
            }

            @Override
            public Set<String> getDefinitionIds() {
                return definitions;
            }
        };

        builder.build(configuration,
                      new PaletteDefinitionBuilder.Callback<DefinitionSetPalette, ClientRuntimeError>() {
                          @Override
                          public void onSuccess(final DefinitionSetPalette paletteDefinition) {
                              assertPaletteConstruction(paletteDefinition);
                          }

                          @Override
                          public void onError(final ClientRuntimeError error) {
                              fail(error.getMessage());
                          }
                      });
    }

    private void assertPaletteConstruction(final DefinitionSetPalette paletteDefinition) {
        assertNotNull(paletteDefinition);
        assertEquals(CaseManagementDefinitionSet.class.getName(),
                     paletteDefinition.getDefinitionSetId());
        assertEquals(2,
                     paletteDefinition.getItems().size());

        assertEquals(CaseManagementPaletteDefinitionFactory.ACTIVITIES,
                     paletteDefinition.getItems().get(0).getTitle());
        final List<DefinitionPaletteItem> item0Items = paletteDefinition.getItems().get(0).getItems();
        assertEquals(4,
                     item0Items.size());
        assertPaletteContains(UserTask.class.getName(),
                              item0Items);
        assertPaletteContains(ScriptTask.class.getName(),
                              item0Items);
        assertPaletteContains(BusinessRuleTask.class.getName(),
                              item0Items);
        assertPaletteContains(ReusableSubprocess.class.getName(),
                              item0Items);

        assertEquals(CaseManagementPaletteDefinitionFactory.STAGES,
                     paletteDefinition.getItems().get(1).getTitle());
        final List<DefinitionPaletteItem> item1Items = paletteDefinition.getItems().get(1).getItems();
        assertEquals(1,
                     item1Items.size());
        assertPaletteContains(AdHocSubprocess.class.getName(),
                              item1Items);
    }

    private void assertPaletteContains(final String defId,
                                       final List<DefinitionPaletteItem> items) {
        assertTrue("Expected Definition '" + defId + "' not found.",
                   items.stream().filter((i) -> i.getDefinitionId().equals(defId)).findFirst().isPresent());
    }

    private static class ClassOrSubclassMatcher<T> extends BaseMatcher<Class<T>> {

        private final Class<T> targetClass;

        public ClassOrSubclassMatcher(final Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @SuppressWarnings("unchecked")
        public boolean matches(final Object obj) {
            if (obj != null) {
                return targetClass.isAssignableFrom(obj.getClass());
            }
            return false;
        }

        public void describeTo(final Description desc) {
            desc.appendText("Matches a class or subclass.");
        }
    }
}

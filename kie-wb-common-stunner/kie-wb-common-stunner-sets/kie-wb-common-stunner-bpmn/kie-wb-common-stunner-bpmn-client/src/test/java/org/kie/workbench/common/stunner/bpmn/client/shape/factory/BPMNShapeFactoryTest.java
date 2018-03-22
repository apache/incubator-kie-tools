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

package org.kie.workbench.common.stunner.bpmn.client.shape.factory;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.BPMNDiagramShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.CatchingIntermediateEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.EndEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.GatewayShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.LaneShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.StartEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SubprocessShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.TaskShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.ThrowingIntermediateEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNShapeFactoryTest {

    @Mock
    private BasicShapesFactory basicShapesFactory;

    @Mock
    private SVGShapeFactory svgShapeFactory;

    @Mock
    private WorkItemDefinitionRegistry workItemDefinitionRegistry;

    @Mock
    private DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory;

    private BPMNShapeFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(delegateShapeFactory.delegate(any(Class.class),
                                           any(ShapeDef.class),
                                           any(Supplier.class)))
                .thenReturn(delegateShapeFactory);
        this.tested = new BPMNShapeFactory(basicShapesFactory,
                                           svgShapeFactory,
                                           delegateShapeFactory,
                                           () -> workItemDefinitionRegistry);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterDelegates() {
        tested.registerDelegates();
        final ArgumentCaptor<Supplier> factoryArgumentCaptor =
                ArgumentCaptor.forClass(Supplier.class);
        verify(delegateShapeFactory,
               times(1)).delegate(eq(BPMNDiagramImpl.class),
                                  any(BPMNDiagramShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(NoneTask.class),
                                  any(TaskShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(UserTask.class),
                                  any(TaskShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(ScriptTask.class),
                                  any(TaskShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(BusinessRuleTask.class),
                                  any(TaskShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(StartNoneEvent.class),
                                  any(StartEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(StartSignalEvent.class),
                                  any(StartEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(StartTimerEvent.class),
                                  any(StartEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(StartMessageEvent.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());

        verify(delegateShapeFactory,
               times(1)).delegate(eq(StartErrorEvent.class),
                                  any(TaskShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(ParallelGateway.class),
                                  any(GatewayShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(ExclusiveGateway.class),
                                  any(GatewayShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(InclusiveGateway.class),
                                  any(GatewayShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(Lane.class),
                                  any(LaneShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(ReusableSubprocess.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EmbeddedSubprocess.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EventSubprocess.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(AdHocSubprocess.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(MultipleInstanceSubprocess.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EndNoneEvent.class),
                                  any(EndEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EndTerminateEvent.class),
                                  any(EndEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EndErrorEvent.class),
                                  any(EndEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EndSignalEvent.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(EndMessageEvent.class),
                                  any(SubprocessShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(IntermediateTimerEvent.class),
                                  any(CatchingIntermediateEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(IntermediateSignalEventCatching.class),
                                  any(CatchingIntermediateEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(IntermediateErrorEventCatching.class),
                                  any(CatchingIntermediateEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(IntermediateMessageEventCatching.class),
                                  any(CatchingIntermediateEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(IntermediateSignalEventThrowing.class),
                                  any(ThrowingIntermediateEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(IntermediateMessageEventThrowing.class),
                                  any(ThrowingIntermediateEventShapeDef.class),
                                  factoryArgumentCaptor.capture());
        verify(delegateShapeFactory,
               times(1)).delegate(eq(SequenceFlow.class),
                                  any(SequenceFlowConnectorDef.class),
                                  factoryArgumentCaptor.capture());
        final long svgFactoryCallCount = factoryArgumentCaptor.getAllValues().stream()
                .filter(this::isSvgShapeFactory)
                .count();
        final long basicFactoryCallCount = factoryArgumentCaptor.getAllValues().stream()
                .filter(this::isBasicShapeFactory)
                .count();
        assertEquals(30,
                     svgFactoryCallCount,
                     0);
        assertEquals(1,
                     basicFactoryCallCount,
                     0);
    }

    private boolean isSvgShapeFactory(final Supplier supplier) {
        return supplier.get().equals(svgShapeFactory);
    }

    private boolean isBasicShapeFactory(final Supplier supplier) {
        return supplier.get().equals(basicShapesFactory);
    }

    @Test
    public void testNewShape() {
        final BPMNDefinition definition = mock(BPMNDefinition.class);
        tested.newShape(definition);
        verify(delegateShapeFactory,
               times(1)).newShape(eq(definition));
        verify(delegateShapeFactory,
               never()).getGlyph(anyString());
    }

    @Test
    public void testGetGlyph() {
        final String id = "id1";
        tested.getGlyph(id);
        verify(delegateShapeFactory,
               times(1)).getGlyph(eq(id));
        verify(delegateShapeFactory,
               never()).newShape(any(BPMNDefinition.class));
    }
}

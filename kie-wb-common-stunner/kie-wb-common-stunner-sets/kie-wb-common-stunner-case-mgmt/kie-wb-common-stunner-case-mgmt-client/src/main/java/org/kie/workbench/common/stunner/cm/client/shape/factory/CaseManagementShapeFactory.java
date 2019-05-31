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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.InclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartConditionalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgNullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgStageShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgUserTaskShapeDef;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;

@Dependent
public class CaseManagementShapeFactory implements ShapeFactory<BPMNDefinition, Shape> {

    private final BasicShapesFactory basicShapesFactory;
    private final CaseManagementShapeDefFactory shapeDefFactory;
    private final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory;

    @Inject
    public CaseManagementShapeFactory(final BasicShapesFactory basicShapesFactory,
                                      final CaseManagementShapeDefFactory shapeDefFactory,
                                      final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory) {
        this.basicShapesFactory = basicShapesFactory;
        this.shapeDefFactory = shapeDefFactory;
        this.delegateShapeFactory = delegateShapeFactory;
    }

    @PostConstruct
    public void init() {
        delegateShapeFactory
                .delegate(CaseManagementDiagram.class,
                          new CaseManagementSvgDiagramShapeDef(),
                          () -> shapeDefFactory)
                .delegate(AdHocSubprocess.class,
                          new CaseManagementSvgStageShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ProcessReusableSubprocess.class,
                          new CaseManagementSvgSubprocessShapeDef(),
                          () -> shapeDefFactory)
                .delegate(CaseReusableSubprocess.class,
                          new CaseManagementSvgSubprocessShapeDef(),
                          () -> shapeDefFactory)
                .delegate(UserTask.class,
                          new CaseManagementSvgUserTaskShapeDef(),
                          () -> shapeDefFactory)
                .delegate(SequenceFlow.class,
                          new SequenceFlowConnectorDef(),
                          () -> basicShapesFactory)
                .delegate(NoneTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ScriptTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(BusinessRuleTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ServiceTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartNoneEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartSignalEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartTimerEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartMessageEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartErrorEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartConditionalEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartEscalationEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartCompensationEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ParallelGateway.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ExclusiveGateway.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(InclusiveGateway.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(Lane.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EmbeddedSubprocess.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EventSubprocess.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(MultipleInstanceSubprocess.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndNoneEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndSignalEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndMessageEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndTerminateEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndErrorEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndEscalationEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndCompensationEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateTimerEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateConditionalEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateSignalEventCatching.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateErrorEventCatching.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateMessageEventCatching.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateEscalationEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateCompensationEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateSignalEventThrowing.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateMessageEventThrowing.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateEscalationEventThrowing.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(IntermediateCompensationEventThrowing.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(Association.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory);
    }

    @Override
    public Shape newShape(final BPMNDefinition definition) {
        return delegateShapeFactory.newShape(definition);
    }

    @Override
    public Glyph getGlyph(final String definitionId) {
        return delegateShapeFactory.getGlyph(definitionId);
    }
}

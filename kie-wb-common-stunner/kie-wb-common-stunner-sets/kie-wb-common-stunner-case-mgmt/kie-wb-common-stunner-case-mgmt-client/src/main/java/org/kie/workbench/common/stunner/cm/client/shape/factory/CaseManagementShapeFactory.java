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
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveGateway;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgNullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgUserTaskShapeDef;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
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
                          new CaseManagementSvgSubprocessShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EmbeddedSubprocess.class,
                          new CaseManagementSvgSubprocessShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ReusableSubprocess.class,
                          new CaseManagementSvgSubprocessShapeDef(),
                          () -> shapeDefFactory)
                .delegate(UserTask.class,
                          new CaseManagementSvgUserTaskShapeDef(),
                          () -> shapeDefFactory)
                .delegate(NoneTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ScriptTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(BusinessRuleTask.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(StartNoneEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ParallelGateway.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(ExclusiveGateway.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(Lane.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndTerminateEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(EndNoneEvent.class,
                          new CaseManagementSvgNullShapeDef(),
                          () -> shapeDefFactory)
                .delegate(SequenceFlow.class,
                          new SequenceFlowConnectorDef(),
                          () -> basicShapesFactory);
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

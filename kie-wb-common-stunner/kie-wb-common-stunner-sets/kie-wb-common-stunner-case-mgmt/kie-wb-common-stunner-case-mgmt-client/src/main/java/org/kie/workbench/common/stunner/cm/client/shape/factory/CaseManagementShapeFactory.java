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
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
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
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementReusableSubprocessTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;

@Dependent
public class CaseManagementShapeFactory implements ShapeFactory<BPMNDefinition, Shape> {

    private final CaseManagementShapeDefFactory caseManagementShapeDefFactory;
    private final BasicShapesFactory basicShapesFactory;
    private final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory;

    @Inject
    public CaseManagementShapeFactory(final CaseManagementShapeDefFactory caseManagementShapeDefFactory,
                                      final BasicShapesFactory basicShapesFactory,
                                      final DelegateShapeFactory<BPMNDefinition, Shape> delegateShapeFactory) {
        this.caseManagementShapeDefFactory = caseManagementShapeDefFactory;
        this.basicShapesFactory = basicShapesFactory;
        this.delegateShapeFactory = delegateShapeFactory;
    }

    @PostConstruct
    public void init() {
        delegateShapeFactory
                .delegate(CaseManagementDiagram.class,
                          new CaseManagementDiagramShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(AdHocSubprocess.class,
                          new CaseManagementSubprocessShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(NoneTask.class,
                          new CaseManagementTaskShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(UserTask.class,
                          new CaseManagementTaskShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(ScriptTask.class,
                          new CaseManagementTaskShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(BusinessRuleTask.class,
                          new CaseManagementTaskShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(ReusableSubprocess.class,
                          new CaseManagementReusableSubprocessTaskShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(StartNoneEvent.class,
                          new NullShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(ParallelGateway.class,
                          new NullShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(ExclusiveGateway.class,
                          new NullShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(Lane.class,
                          new NullShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(EndTerminateEvent.class,
                          new NullShapeDef(),
                          () -> caseManagementShapeDefFactory)
                .delegate(EndNoneEvent.class,
                          new NullShapeDef(),
                          () -> caseManagementShapeDefFactory)
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

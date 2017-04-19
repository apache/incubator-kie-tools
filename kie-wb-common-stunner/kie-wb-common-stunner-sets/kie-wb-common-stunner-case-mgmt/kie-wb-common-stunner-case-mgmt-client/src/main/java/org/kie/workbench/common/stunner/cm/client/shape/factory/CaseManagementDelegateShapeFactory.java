/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
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
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

@ApplicationScoped
public class CaseManagementDelegateShapeFactory
        extends DelegateShapeFactory<Object, AbstractCanvasHandler, Shape<ShapeView<?>>> {

    private final DefinitionManager definitionManager;
    private final BasicShapesFactory basicShapesFactory;
    private final CaseManagementShapesFactory caseManagementShapesFactory;

    protected CaseManagementDelegateShapeFactory() {
        this(null,
             null,
             null);
    }

    @Inject
    public CaseManagementDelegateShapeFactory(final DefinitionManager definitionManager,
                                              final BasicShapesFactory basicShapesFactory,
                                              final CaseManagementShapesFactory caseManagementShapesFactory) {
        this.definitionManager = definitionManager;
        this.basicShapesFactory = basicShapesFactory;
        this.caseManagementShapesFactory = caseManagementShapesFactory;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Register the factories to delegate
        addDelegate(basicShapesFactory);
        addDelegate(caseManagementShapesFactory);

        // Register the shapes and definitions.
        caseManagementShapesFactory.addShapeDef(CaseManagementDiagram.class,
                                                new CaseManagementDiagramShapeDef());
        caseManagementShapesFactory.addShapeDef(AdHocSubprocess.class,
                                                new CaseManagementSubprocessShapeDef());

        caseManagementShapesFactory.addShapeDef(NoneTask.class,
                                                new CaseManagementTaskShapeDef());
        caseManagementShapesFactory.addShapeDef(UserTask.class,
                                                new CaseManagementTaskShapeDef());
        caseManagementShapesFactory.addShapeDef(ScriptTask.class,
                                                new CaseManagementTaskShapeDef());
        caseManagementShapesFactory.addShapeDef(BusinessRuleTask.class,
                                                new CaseManagementTaskShapeDef());
        caseManagementShapesFactory.addShapeDef(ReusableSubprocess.class,
                                                new CaseManagementReusableSubprocessTaskShapeDef());

        caseManagementShapesFactory.addShapeDef(StartNoneEvent.class,
                                                new NullShapeDef());
        caseManagementShapesFactory.addShapeDef(ParallelGateway.class,
                                                new NullShapeDef());
        caseManagementShapesFactory.addShapeDef(ExclusiveDatabasedGateway.class,
                                                new NullShapeDef());
        caseManagementShapesFactory.addShapeDef(Lane.class,
                                                new NullShapeDef());
        caseManagementShapesFactory.addShapeDef(EndTerminateEvent.class,
                                                new NullShapeDef());
        caseManagementShapesFactory.addShapeDef(EndNoneEvent.class,
                                                new NullShapeDef());

        basicShapesFactory.addShapeDef(SequenceFlow.class,
                                       new SequenceFlowConnectorDef());
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }
}

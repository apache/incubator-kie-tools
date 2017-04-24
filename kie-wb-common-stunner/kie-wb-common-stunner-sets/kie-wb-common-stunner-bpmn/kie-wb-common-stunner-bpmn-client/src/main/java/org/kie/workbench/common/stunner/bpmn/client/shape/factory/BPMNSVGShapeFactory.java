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

package org.kie.workbench.common.stunner.bpmn.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.shape.def.BPMNDiagramShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.EndEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.GatewayShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.IntermediateTimerEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.LaneShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.ReusableSubprocessShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.SequenceFlowConnectorDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.StartEventShapeDef;
import org.kie.workbench.common.stunner.bpmn.client.shape.def.TaskShapeDef;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;

@ApplicationScoped
public class BPMNSVGShapeFactory
        extends DelegateShapeFactory<Object, AbstractCanvasHandler, Shape<ShapeView<?>>> {

    private final DefinitionManager definitionManager;
    private final SVGShapeFactory svgShapeFactory;
    private final BasicShapesFactory basicShapesFactory;

    protected BPMNSVGShapeFactory() {
        this(null,
             null,
             null);
    }

    @Inject
    public BPMNSVGShapeFactory(final DefinitionManager definitionManager,
                               final SVGShapeFactory svgShapeFactory,
                               final BasicShapesFactory basicShapesFactory) {
        this.definitionManager = definitionManager;
        this.svgShapeFactory = svgShapeFactory;
        this.basicShapesFactory = basicShapesFactory;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Register the factories to delegate.
        addDelegate(svgShapeFactory);
        addDelegate(basicShapesFactory);
        // Register the shapes and definitions.
        svgShapeFactory.addShapeDef(NoneTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(UserTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(ScriptTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(BusinessRuleTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(StartNoneEvent.class,
                                    new StartEventShapeDef());
        svgShapeFactory.addShapeDef(StartSignalEvent.class,
                                    new StartEventShapeDef());
        svgShapeFactory.addShapeDef(StartTimerEvent.class,
                                    new StartEventShapeDef());
        svgShapeFactory.addShapeDef(BPMNDiagramImpl.class,
                                    new BPMNDiagramShapeDef());
        svgShapeFactory.addShapeDef(BusinessRuleTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(ScriptTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(ParallelGateway.class,
                                    new GatewayShapeDef());
        svgShapeFactory.addShapeDef(ExclusiveDatabasedGateway.class,
                                    new GatewayShapeDef());
        svgShapeFactory.addShapeDef(UserTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(Lane.class,
                                    new LaneShapeDef());
        svgShapeFactory.addShapeDef(ReusableSubprocess.class,
                                    new ReusableSubprocessShapeDef());
        svgShapeFactory.addShapeDef(NoneTask.class,
                                    new TaskShapeDef());
        svgShapeFactory.addShapeDef(EndNoneEvent.class,
                                    new EndEventShapeDef());
        svgShapeFactory.addShapeDef(EndTerminateEvent.class,
                                    new EndEventShapeDef());
        svgShapeFactory.addShapeDef(IntermediateTimerEvent.class,
                                    new IntermediateTimerEventShapeDef());
        basicShapesFactory.addShapeDef(SequenceFlow.class,
                                       new SequenceFlowConnectorDef());
        basicShapesFactory.addShapeDef(SequenceFlow.class,
                                       new SequenceFlowConnectorDef());
    }

    @Override
    protected DefinitionManager getDefinitionManager() {
        return definitionManager;
    }
}

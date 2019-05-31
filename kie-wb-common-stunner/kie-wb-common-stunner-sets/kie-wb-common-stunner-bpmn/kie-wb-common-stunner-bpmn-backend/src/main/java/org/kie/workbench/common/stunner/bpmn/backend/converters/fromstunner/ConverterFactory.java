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
package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities.ReusableSubprocessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.associations.AssociationConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes.RootProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes.SubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.sequenceflows.SequenceFlowConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.tasks.TaskConverter;

public class ConverterFactory {

    protected final PropertyWriterFactory propertyWriterFactory;
    protected final DefinitionsBuildingContext context;
    private final TaskConverter taskConverter;
    private final FlowElementConverter flowElementConverter;
    private final StartEventConverter startEventConverter;
    private final IntermediateCatchEventConverter intermediateCatchEventConverter;
    private final IntermediateThrowEventConverter intermediateThrowEventConverter;
    private final EndEventConverter endEventConverter;
    private final LaneConverter laneConverter;
    private final GatewayConverter gatewayConverter;
    private final ReusableSubprocessConverter reusableSubprocessConverter;
    private final EdgeConverter edgeConverter;
    private final FlowElementPostConverter flowElementPostConverter;

    public ConverterFactory(DefinitionsBuildingContext context,
                            PropertyWriterFactory propertyWriterFactory) {
        this.context = context;
        this.propertyWriterFactory = propertyWriterFactory;

        this.taskConverter = new TaskConverter(propertyWriterFactory);
        this.startEventConverter = new StartEventConverter(propertyWriterFactory);
        this.intermediateCatchEventConverter = new IntermediateCatchEventConverter(propertyWriterFactory);
        this.intermediateThrowEventConverter = new IntermediateThrowEventConverter(propertyWriterFactory);
        this.endEventConverter = new EndEventConverter(propertyWriterFactory);
        this.laneConverter = new LaneConverter(propertyWriterFactory);
        this.gatewayConverter = new GatewayConverter(propertyWriterFactory);

        this.flowElementConverter = new FlowElementConverter(this);
        this.reusableSubprocessConverter = new ReusableSubprocessConverter(propertyWriterFactory);
        this.edgeConverter = new EdgeConverter(this);
        this.flowElementPostConverter = new FlowElementPostConverter();
    }

    public TaskConverter taskConverter() {
        return taskConverter;
    }

    public FlowElementConverter viewDefinitionConverter() {
        return flowElementConverter;
    }

    public StartEventConverter startEventConverter() {
        return startEventConverter;
    }

    public IntermediateCatchEventConverter intermediateCatchEventConverter() {
        return intermediateCatchEventConverter;
    }

    public IntermediateThrowEventConverter intermediateThrowEventConverter() {
        return intermediateThrowEventConverter;
    }

    public EndEventConverter endEventConverter() {
        return endEventConverter;
    }

    public LaneConverter laneConverter() {
        return laneConverter;
    }

    public GatewayConverter gatewayConverter() {
        return gatewayConverter;
    }

    public ReusableSubprocessConverter reusableSubprocessConverter() {
        return reusableSubprocessConverter;
    }

    public RootProcessConverter processConverter() {
        return new RootProcessConverter(context, propertyWriterFactory, this);
    }

    public SubProcessConverter subProcessConverter() {
        return new SubProcessConverter(context, propertyWriterFactory, this);
    }

    public EdgeConverter edgeElementConverter() {
        return edgeConverter;
    }

    public SequenceFlowConverter sequenceFlowConverter() {
        return new SequenceFlowConverter(propertyWriterFactory);
    }

    public AssociationConverter associationFlowConverter() {
        return new AssociationConverter(propertyWriterFactory);
    }

    public FlowElementPostConverter flowElementPostConverter() {
        return flowElementPostConverter;
    }
}

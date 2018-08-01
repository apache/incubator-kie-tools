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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.activities.CallActivityConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes.RootProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes.SubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks.TaskConverter;

public class ConverterFactory {

    private final DefinitionResolver definitionResolver;
    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    private final TaskConverter taskConverter;
    private final EdgeConverter edgeConverter;

    private final FlowElementConverter flowElementConverter;
    private final StartEventConverter startEventConverter;
    private final IntermediateCatchEventConverter intermediateCatchEventConverter;
    private final IntermediateThrowEventConverter intermediateThrowEventConverter;
    private final EndEventConverter endEventConverter;
    private final CallActivityConverter callActivityConverter;
    private final LaneConverter laneConverter;
    private final GatewayConverter gatewayConverter;

    public ConverterFactory(
            DefinitionResolver definitionResolver,
            TypedFactoryManager factoryManager) {
        this.definitionResolver = definitionResolver;
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = new PropertyReaderFactory(definitionResolver);

        this.flowElementConverter = new FlowElementConverter(this);

        this.startEventConverter = new StartEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateCatchEventConverter = new IntermediateCatchEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateThrowEventConverter = new IntermediateThrowEventConverter(factoryManager, propertyReaderFactory);
        this.endEventConverter = new EndEventConverter(factoryManager, propertyReaderFactory);
        this.callActivityConverter = new CallActivityConverter(factoryManager, propertyReaderFactory);
        this.taskConverter = new TaskConverter(factoryManager, propertyReaderFactory);
        this.laneConverter = new LaneConverter(factoryManager, propertyReaderFactory);
        this.gatewayConverter = new GatewayConverter(factoryManager, propertyReaderFactory);
        this.edgeConverter = new EdgeConverter(factoryManager, propertyReaderFactory);
    }

    public FlowElementConverter flowElementConverter() {
        return flowElementConverter;
    }

    public EdgeConverter edgeConverter() {
        return edgeConverter;
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

    public CallActivityConverter callActivityConverter() {
        return callActivityConverter;
    }

    public RootProcessConverter rootProcessConverter() {
        return new RootProcessConverter(factoryManager, propertyReaderFactory, definitionResolver, this);
    }

    public SubProcessConverter subProcessConverter() {
        return new SubProcessConverter(factoryManager, propertyReaderFactory, definitionResolver, this);
    }

    public LaneConverter laneConverter() {
        return laneConverter;
    }

    public GatewayConverter gatewayConverter() {
        return gatewayConverter;
    }

    public TaskConverter taskConverter() {
        return taskConverter;
    }
}

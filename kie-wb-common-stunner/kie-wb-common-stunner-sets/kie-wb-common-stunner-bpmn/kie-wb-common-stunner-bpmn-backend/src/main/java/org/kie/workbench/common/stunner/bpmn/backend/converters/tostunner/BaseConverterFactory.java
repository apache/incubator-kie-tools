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
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.activities.BaseCallActivityConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes.BaseRootProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes.BaseSubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.tasks.TaskConverter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BaseAdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;

public abstract class BaseConverterFactory<D extends BPMNDiagram, A extends BaseAdHocSubprocess, R extends BaseReusableSubprocess> {

    protected final DefinitionResolver definitionResolver;
    protected final TypedFactoryManager factoryManager;
    protected final PropertyReaderFactory propertyReaderFactory;

    private final TaskConverter taskConverter;
    private final EdgeConverter edgeConverter;

    private final FlowElementConverter flowElementConverter;
    private final StartEventConverter startEventConverter;
    private final IntermediateCatchEventConverter intermediateCatchEventConverter;
    private final IntermediateThrowEventConverter intermediateThrowEventConverter;
    private final EndEventConverter endEventConverter;
    private final BaseCallActivityConverter callActivityConverter;
    private final LaneConverter laneConverter;
    private final GatewayConverter gatewayConverter;

    public BaseConverterFactory(DefinitionResolver definitionResolver,
                                TypedFactoryManager factoryManager) {
        this.definitionResolver = definitionResolver;
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = createPropertyReaderFactory();

        this.flowElementConverter = new FlowElementConverter(this);

        this.startEventConverter = new StartEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateCatchEventConverter = new IntermediateCatchEventConverter(factoryManager, propertyReaderFactory);
        this.intermediateThrowEventConverter = new IntermediateThrowEventConverter(factoryManager, propertyReaderFactory);
        this.endEventConverter = new EndEventConverter(factoryManager, propertyReaderFactory);
        this.callActivityConverter = createCallActivityConverter();
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

    public BaseCallActivityConverter callActivityConverter() {
        return callActivityConverter;
    }

    public abstract BaseRootProcessConverter<D> rootProcessConverter();

    public abstract BaseSubProcessConverter<A> subProcessConverter();

    public LaneConverter laneConverter() {
        return laneConverter;
    }

    public GatewayConverter gatewayConverter() {
        return gatewayConverter;
    }

    public TaskConverter taskConverter() {
        return taskConverter;
    }

    protected abstract BaseCallActivityConverter<R> createCallActivityConverter();

    protected abstract PropertyReaderFactory createPropertyReaderFactory();

}

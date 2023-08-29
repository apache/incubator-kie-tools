/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner;

import org.kie.workbench.common.stunner.bpmn.client.marshall.MarshallingRequest;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.activities.BaseCallActivityConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.artifacts.DataObjectConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.artifacts.TextAnnotationConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.EndEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.IntermediateCatchEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.IntermediateThrowEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.events.StartEventConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.gateways.GatewayConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.lanes.LaneConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes.BaseRootProcessConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.processes.BaseSubProcessConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.tasks.BaseTaskConverter;

public abstract class BaseConverterFactory {

    protected final DefinitionResolver definitionResolver;
    protected final TypedFactoryManager factoryManager;
    protected final PropertyReaderFactory propertyReaderFactory;

    private final EdgeConverterManager edgeConverter;

    private final FlowElementConverter flowElementConverter;
    private final StartEventConverter startEventConverter;
    private final IntermediateCatchEventConverter intermediateCatchEventConverter;
    private final IntermediateThrowEventConverter intermediateThrowEventConverter;
    private final EndEventConverter endEventConverter;
    private final LaneConverter laneConverter;
    private final GatewayConverter gatewayConverter;
    private final TextAnnotationConverter textAnnotationConverter;
    private final DataObjectConverter dataObjectConverter;

    public BaseConverterFactory(DefinitionResolver definitionResolver,
                                TypedFactoryManager factoryManager,
                                PropertyReaderFactory propertyReaderFactory) {
        this.definitionResolver = definitionResolver;
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;

        final MarshallingRequest.Mode mode = definitionResolver.getMode();
        this.flowElementConverter = new FlowElementConverter(this);

        this.startEventConverter = new StartEventConverter(factoryManager, propertyReaderFactory,
                                                           mode);
        this.intermediateCatchEventConverter = new IntermediateCatchEventConverter(factoryManager,
                                                                                   propertyReaderFactory, mode);
        this.intermediateThrowEventConverter = new IntermediateThrowEventConverter(factoryManager,
                                                                                   propertyReaderFactory, mode);
        this.endEventConverter = new EndEventConverter(factoryManager, propertyReaderFactory, mode);
        this.laneConverter = new LaneConverter(factoryManager, propertyReaderFactory);
        this.gatewayConverter = new GatewayConverter(factoryManager, propertyReaderFactory, mode);
        this.edgeConverter = new EdgeConverterManager(factoryManager, propertyReaderFactory, mode);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager, propertyReaderFactory);
        this.dataObjectConverter = new DataObjectConverter(factoryManager, propertyReaderFactory);
    }

    public FlowElementConverter flowElementConverter() {
        return flowElementConverter;
    }

    public EdgeConverterManager edgeConverter() {
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

    public abstract BaseCallActivityConverter callActivityConverter();

    public abstract BaseRootProcessConverter rootProcessConverter();

    public abstract BaseSubProcessConverter subProcessConverter();

    public LaneConverter laneConverter() {
        return laneConverter;
    }

    public GatewayConverter gatewayConverter() {
        return gatewayConverter;
    }

    public abstract BaseTaskConverter taskConverter();

    public ProcessPostConverter newProcessPostConverter() {
        return new ProcessPostConverter();
    }

    public DefinitionResolver getDefinitionResolver() {
        return definitionResolver;
    }

    public TextAnnotationConverter textAnnotationConverter() {
        return textAnnotationConverter;
    }

    public DataObjectConverter dataObjectConverter() {
        return dataObjectConverter;
    }
}

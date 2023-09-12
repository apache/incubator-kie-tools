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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.AssociationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class AssociationConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public AssociationConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public Result<BasePropertyWriter> toFlowElement(Edge<?, ?> edge, ElementContainer process) {
        ViewConnector<Association> connector = (ViewConnector<Association>) edge.getContent();
        Association definition = connector.getDefinition();
        org.eclipse.bpmn2.Association association = bpmn2.createAssociation();
        AssociationPropertyWriter p = propertyWriterFactory.of(association);

        association.setId(edge.getUUID());

        BasePropertyWriter pSrc = process.getChildElement(edge.getSourceNode().getUUID());

        BasePropertyWriter pTgt = process.getChildElement(edge.getTargetNode().getUUID());

        if (pSrc == null || pTgt == null) {
            String msg = "BasePropertyWriter was not found for source node or target node at edge: " + edge.getUUID() + ", pSrc = " + pSrc + ", pTgt = " + pTgt;
            return Result.failure(msg);
        }

        p.setSource(pSrc);
        p.setTarget(pTgt);

        p.setConnection(connector);

        BPMNGeneralSet general = definition.getGeneral();
        p.setDocumentation(general.getDocumentation().getValue());
        p.setDirectionAssociation(definition);

        return Result.of(p);
    }
}
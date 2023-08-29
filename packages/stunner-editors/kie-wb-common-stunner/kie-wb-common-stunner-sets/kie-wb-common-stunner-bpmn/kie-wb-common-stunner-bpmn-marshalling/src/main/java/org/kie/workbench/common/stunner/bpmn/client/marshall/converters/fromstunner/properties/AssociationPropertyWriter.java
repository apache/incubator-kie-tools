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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.util.PropertyWriterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.NonDirectionalAssociation;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class AssociationPropertyWriter extends BasePropertyWriter {

    private Association association;
    private BasePropertyWriter source;
    private BasePropertyWriter target;
    private BPMNEdge bpmnEdge;

    public AssociationPropertyWriter(Association association,
                                     VariableScope variableScope) {
        super(association,
              variableScope);
        this.association = association;
    }

    public void setConnection(ViewConnector<? extends BPMNViewDefinition> connector) {
        if (connector.getSourceConnection().isPresent() && connector.getTargetConnection().isPresent()) {
            Connection sourceConnection = connector.getSourceConnection().get();
            Connection targetConnection = connector.getTargetConnection().get();

            ControlPoint[] controlPoints = connector.getControlPoints();
            bpmnEdge = PropertyWriterUtils.createBPMNEdge(source,
                                                          target,
                                                          sourceConnection,
                                                          controlPoints,
                                                          targetConnection);
            bpmnEdge.setBpmnElement(association);
        }
    }

    public BPMNEdge getEdge() {
        return bpmnEdge;
    }

    public void setSource(BasePropertyWriter pSrc) {
        this.source = pSrc;
        association.setSourceRef(pSrc.getElement());
        pSrc.setTarget(this);
    }

    public void setTarget(BasePropertyWriter pTgt) {
        this.target = pTgt;
        association.setTargetRef(pTgt.getElement());
        pTgt.setSource(this);
    }

    public void setDirectionAssociation(org.kie.workbench.common.stunner.bpmn.definition.Association definition) {
        AssociationDirection direction = AssociationDirection.ONE;
        if (definition instanceof NonDirectionalAssociation) {
            direction = AssociationDirection.NONE;
        }
        association.setAssociationDirection(direction);
    }
}

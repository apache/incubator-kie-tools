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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.List;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.AssociationDirection;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.util.PropertyReaderUtils;
import org.kie.workbench.common.stunner.bpmn.definition.DirectionalAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.NonDirectionalAssociation;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class AssociationPropertyReader extends BasePropertyReader implements EdgePropertyReader {

    private final DefinitionResolver definitionResolver;
    private final Association association;

    public AssociationPropertyReader(Association association,
                                     BPMNDiagram diagram,
                                     DefinitionResolver definitionResolver) {
        super(association,
              diagram,
              definitionResolver.getShape(association.getId()),
              definitionResolver.getResolutionFactor());
        this.association = association;
        this.definitionResolver = definitionResolver;
    }

    @Override
    public String getSourceId() {
        return association.getSourceRef().getId();
    }

    @Override
    public String getTargetId() {
        return association.getTargetRef().getId();
    }

    public Class getAssociationByDirection() {
        AssociationDirection d = association.getAssociationDirection();
        if (!AssociationDirection.NONE.equals(d)) {
            return DirectionalAssociation.class;
        }
        return NonDirectionalAssociation.class;
    }

    @Override
    public Connection getSourceConnection() {
        Point2D sourcePosition = PropertyReaderUtils.getSourcePosition(definitionResolver,
                                                                       element.getId(),
                                                                       getSourceId());
        return MagnetConnection.Builder
                .at(sourcePosition.getX(),
                    sourcePosition.getY())
                .setAuto(PropertyReaderUtils.isAutoConnectionSource(element));
    }

    @Override
    public Connection getTargetConnection() {
        Point2D targetPosition = PropertyReaderUtils.getTargetPosition(definitionResolver,
                                                                       element.getId(),
                                                                       getTargetId());
        return MagnetConnection.Builder
                .at(targetPosition.getX(),
                    targetPosition.getY())
                .setAuto(PropertyReaderUtils.isAutoConnectionTarget(element));
    }

    @Override
    public List<Point2D> getControlPoints() {
        return PropertyReaderUtils.getControlPoints(definitionResolver,
                                                    element.getId());
    }

    @Override
    public DefinitionResolver getDefinitionResolver() {
        return definitionResolver;
    }
}

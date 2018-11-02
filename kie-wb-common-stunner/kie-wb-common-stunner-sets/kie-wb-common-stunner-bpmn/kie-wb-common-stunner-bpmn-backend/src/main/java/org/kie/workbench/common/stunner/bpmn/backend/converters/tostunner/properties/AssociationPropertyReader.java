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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.List;

import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.util.PropertyReaderUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class AssociationPropertyReader extends BasePropertyReader {

    private final DefinitionResolver definitionResolver;
    private final Association association;

    public AssociationPropertyReader(Association association,
                                     BPMNPlane plane,
                                     DefinitionResolver definitionResolver) {
        super(association,
              plane,
              definitionResolver.getShape(association.getId()));
        this.association = association;
        this.definitionResolver = definitionResolver;
    }

    public String getSourceId() {
        return association.getSourceRef().getId();
    }

    public String getTargetId() {
        return association.getTargetRef().getId();
    }

    public Connection getSourceConnection() {
        Point2D sourcePosition = PropertyReaderUtils.getSourcePosition(definitionResolver,
                                                                       element.getId(),
                                                                       getSourceId());
        return MagnetConnection.Builder
                .at(sourcePosition.getX(),
                    sourcePosition.getY())
                .setAuto(PropertyReaderUtils.isAutoConnectionSource(element));
    }

    public Connection getTargetConnection() {
        Point2D targetPosition = PropertyReaderUtils.getTargetPosition(definitionResolver,
                                                                       element.getId(),
                                                                       getTargetId());
        return MagnetConnection.Builder
                .at(targetPosition.getX(),
                    targetPosition.getY())
                .setAuto(PropertyReaderUtils.isAutoConnectionTarget(element));
    }

    public List<Point2D> getControlPoints() {
        return PropertyReaderUtils.getControlPoints(definitionResolver,
                                                    element.getId());
    }
}

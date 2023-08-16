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

import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

public class GatewayPropertyReader extends FlowElementPropertyReader {

    // These values are present in the SVG declaration for the gateway shape.
    static final double WIDTH = 56d;
    static final double HEIGHT = 56d;

    public GatewayPropertyReader(Gateway element, BPMNDiagram diagram, BPMNShape shape, double resolutionFactor) {
        super(element, diagram, shape, resolutionFactor);
    }

    @Override
    protected Bounds computeBounds(org.eclipse.dd.dc.Bounds bounds) {
        final double x = bounds.getX() * resolutionFactor;
        final double y = bounds.getY() * resolutionFactor;
        return Bounds.create(x, y, x + WIDTH, y + HEIGHT);
    }

    public String getDefaultRoute() {
        return CustomAttribute.dg.of(element).get();
    }
}

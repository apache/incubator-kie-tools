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
package org.kie.workbench.common.dmn.client.graph;

import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.processing.layout.SizeHandler;

public class DMNSizeHandler implements SizeHandler {

    @Override
    public void setSize(final Node node,
                        final double width,
                        final double height) {
        if (node.getContent() instanceof Definition) {
            final Object innerDefinition = ((Definition) node.getContent()).getDefinition();
            if (innerDefinition instanceof DMNViewDefinition) {
                final RectangleDimensionsSet dimensionSet = ((DMNViewDefinition) innerDefinition).getDimensionsSet();
                dimensionSet.setHeight(new Height(height));
                dimensionSet.setWidth(new Width(width));
            }
        }
    }
}

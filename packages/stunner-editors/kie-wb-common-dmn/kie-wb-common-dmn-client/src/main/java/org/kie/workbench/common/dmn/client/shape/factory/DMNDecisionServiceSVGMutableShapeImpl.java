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
package org.kie.workbench.common.dmn.client.shape.factory;

import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.client.shape.def.DMNDecisionServiceSVGShapeDef;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.impl.SVGMutableShapeImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class DMNDecisionServiceSVGMutableShapeImpl extends SVGMutableShapeImpl<DecisionService, DMNDecisionServiceSVGShapeDef> {

    public DMNDecisionServiceSVGMutableShapeImpl(final DMNDecisionServiceSVGShapeDef shapeDef,
                                                 final SVGShapeViewImpl view) {
        super(shapeDef, view);
    }

    @Override
    protected void applyCustomProperties(final Node<View<DecisionService>, Edge> element,
                                         final MutationContext mutationContext) {
        final DecisionService instance = getDefinition(element);
        final DecisionServiceSVGShapeView decisionServiceSVGShapeView = (DecisionServiceSVGShapeView) getShapeView();
        decisionServiceSVGShapeView.setDividerLineY(instance.getDividerLineY().getValue());
    }
}

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

import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.client.shape.def.DMNDecisionServiceSVGShapeDef;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNDecisionServiceSVGMutableShapeImplTest {

    @Mock
    private DMNDecisionServiceSVGShapeDef shapeDef;

    @Mock
    private DecisionServiceSVGShapeView view;

    @Mock
    private Node<View<DecisionService>, Edge> decisionServiceNode;

    @Mock
    private View<DecisionService> decisionServiceView;

    @Mock
    private DecisionService decisionServiceDefinition;

    @Mock
    private MutationContext mutationContext;

    private DMNDecisionServiceSVGMutableShapeImpl svgMutableShape;

    @Before
    public void setup() {
        when(shapeDef.titleHandler()).thenReturn(Optional.empty());
        when(shapeDef.fontHandler()).thenReturn(Optional.empty());
        when(shapeDef.sizeHandler()).thenReturn(Optional.empty());
        when(decisionServiceNode.getContent()).thenReturn(decisionServiceView);
        when(decisionServiceView.getDefinition()).thenReturn(decisionServiceDefinition);

        this.svgMutableShape = new DMNDecisionServiceSVGMutableShapeImpl(shapeDef, view);
    }

    @Test
    public void testApplyCustomProperties() {
        final DecisionServiceDividerLineY divider = new DecisionServiceDividerLineY(25.0);
        when(decisionServiceDefinition.getDividerLineY()).thenReturn(divider);

        svgMutableShape.applyCustomProperties(decisionServiceNode, mutationContext);

        verify(view).setDividerLineY(eq(25.0));
    }
}

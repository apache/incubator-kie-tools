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
package org.kie.workbench.common.dmn.client.resources.images;

import com.ait.lienzo.client.core.shape.Node;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactoryBuilder;
import org.kie.workbench.common.dmn.client.shape.view.decisionservice.DecisionServiceSVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.factory.SVGShapeViewBuilder;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DMNDecisionServiceSVGViewFactoryBuilderTest {

    @Mock
    private SVGPrimitiveShape svgPrimitiveShape;

    @Mock
    private Shape shape;

    @Mock
    private Node shapeNode;

    private SVGShapeViewBuilder builder;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(svgPrimitiveShape.get()).thenReturn(shape);
        when(shape.asNode()).thenReturn(shapeNode);

        this.builder = new DMNDecisionServiceSVGViewFactoryBuilder();
    }

    @Test
    public void testBuild() {
        assertThat(builder.build("name",
                                 svgPrimitiveShape,
                                 100.0,
                                 200.0,
                                 false)).isInstanceOf(DecisionServiceSVGShapeView.class);
    }
}

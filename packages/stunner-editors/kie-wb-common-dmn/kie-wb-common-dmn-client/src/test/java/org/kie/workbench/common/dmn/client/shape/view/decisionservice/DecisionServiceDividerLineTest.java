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
package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionServiceDividerLineTest {

    private static final double WIDTH = 100.0;

    @Mock
    private Context2D context;

    private DecisionServiceDividerLine line;

    @Before
    public void setup() {
        this.line = new DecisionServiceDividerLine(() -> WIDTH);
    }

    @Test
    public void testAsSVGPrimitiveShape() {
        final SVGPrimitiveShape primitiveShape = line.asSVGPrimitiveShape();

        assertThat(primitiveShape.get().isDraggable()).isTrue();
    }

    @Test
    public void testPrepare() {
        assertThat(line.prepare(context, 1.0)).isTrue();

        verify(context).beginPath();
        verify(context).moveTo(eq(0.0), eq(0.0));
        verify(context).lineTo(eq(WIDTH), eq(0.0));
    }
}

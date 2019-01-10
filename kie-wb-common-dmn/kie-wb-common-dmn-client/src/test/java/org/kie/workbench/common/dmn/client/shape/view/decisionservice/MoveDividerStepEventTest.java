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
package org.kie.workbench.common.dmn.client.shape.view.decisionservice;

import com.ait.lienzo.client.core.event.AbstractNodeDragEvent;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class MoveDividerStepEventTest {

    @Mock
    private DecisionServiceSVGShapeView shape;

    @Mock
    private AbstractNodeDragEvent<?> nodeDragEvent;

    @Mock
    private MoveDividerStepHandler handler;

    private MoveDividerStepEvent event;

    @Before
    public void setup() {
        this.event = new MoveDividerStepEvent(shape, nodeDragEvent);
    }

    @Test
    public void testGetAssociatedType() {
        assertThat(event.getAssociatedType()).isEqualTo(MoveDividerStepEvent.TYPE);
    }

    @Test
    public void testDispatch() {
        event.dispatch(handler);

        verify(handler).onMoveDividerStep(eq(event));
    }
}

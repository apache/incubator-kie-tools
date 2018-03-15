/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import java.util.Arrays;

import com.ait.lienzo.client.core.shape.IPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class EventViewHandlerTestBase {

    @Mock
    protected SVGShapeView view;

    @Mock
    protected SVGPrimitive child1;

    @Mock
    protected IPrimitive<?> prim1;

    @Mock
    protected SVGPrimitive child2;

    @Mock
    protected IPrimitive<?> prim2;

    public void init() {
        when(view.getChildren()).thenReturn(Arrays.asList(child1, child2));
        when(child1.get()).thenReturn(prim1);
        when(child2.get()).thenReturn(prim2);
    }
}

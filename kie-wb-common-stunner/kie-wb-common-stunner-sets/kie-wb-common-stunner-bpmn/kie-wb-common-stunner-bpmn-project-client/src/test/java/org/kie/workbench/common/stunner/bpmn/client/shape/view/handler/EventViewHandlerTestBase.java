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

import com.ait.lienzo.client.core.shape.Circle;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class EventViewHandlerTestBase {

    @Mock
    protected SVGShapeView view;

    @Mock
    protected SVGPrimitive prim;

    @Mock
    protected Circle circle;

    protected void verifyCircleDashed(Circle circle) {
        verify(circle,
               times(1)).setDashArray(ViewHandlerHelper.DASH,
                                      ViewHandlerHelper.DASH,
                                      ViewHandlerHelper.DASH);
    }

    protected void verifyCircleNotDashed(Circle circle) {
        verify(circle,
               times(1)).setDashArray(0d,
                                      0d,
                                      0d);
    }
}

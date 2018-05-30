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

package org.kie.workbench.common.stunner.svg.client.shape.view;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SVGShapeViewResourceTest {

    @Mock
    private Function<SVGShapeViewResource.Arguments, SVGShapeView> builderFunction;

    @Mock
    private SVGShapeView view;

    private SVGShapeViewResource tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(builderFunction.apply(any(SVGShapeViewResource.Arguments.class))).thenReturn(view);
        tested = new SVGShapeViewResource(builderFunction);
    }

    @Test
    public void testBuildSpecifyingSize() {
        tested.build(100d, 222d, true);
        final ArgumentCaptor<SVGShapeViewResource.Arguments> argumentCaptor = ArgumentCaptor.forClass(SVGShapeViewResource.Arguments.class);
        verify(builderFunction,
               times(1)).apply(argumentCaptor.capture());
        final SVGShapeViewResource.Arguments arguments = argumentCaptor.getValue();
        assertEquals(100d, arguments.width, 0d);
        assertEquals(222d, arguments.heigth, 0d);
        assertTrue(arguments.resizable);
        verify(view, times(1)).refresh();
    }

    @Test
    public void testBuildDefaultSize() {
        tested.build(true);
        final ArgumentCaptor<SVGShapeViewResource.Arguments> argumentCaptor = ArgumentCaptor.forClass(SVGShapeViewResource.Arguments.class);
        verify(builderFunction,
               times(1)).apply(argumentCaptor.capture());
        final SVGShapeViewResource.Arguments arguments = argumentCaptor.getValue();
        assertTrue(arguments.resizable);
        verify(view, times(1)).refresh();
    }
}

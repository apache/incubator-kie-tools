/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.palette.factory.icons;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public abstract class AbstractIconRendererTest<RENDERER extends AbstractIconRenderer<RESOURCE_TYPE, VIEW>, RESOURCE_TYPE, VIEW extends IconRendererView<RENDERER, RESOURCE_TYPE>> {

    protected VIEW view;

    @Mock
    protected IconResource<RESOURCE_TYPE> resource;

    protected RENDERER renderer;

    @Before
    public void init() {
        view = mock(getViewClass());
        renderer = getRendererIncance(view);
        verify(view).init(renderer);
    }

    protected abstract Class<VIEW> getViewClass();

    protected abstract RENDERER getRendererIncance(VIEW view);

    @Test
    public void testGeneralFunctionallity() {
        renderer.render(resource);

        verify(view).render();

        renderer.render(resource,
                        IconRenderer.Size.LARGE);

        verify(view,
               times(2)).render();

        renderer.resize(IconRenderer.Size.SMALL);

        verify(view).resize();

        renderer.resize(IconRenderer.Size.LARGE);

        verify(view,
               times(2)).resize();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenderWithNullResource() {
        renderer.render(null);
        fail("We shouldn't be here");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenderWithNullSize() {
        renderer.render(resource,
                        null);
        fail("We shouldn't be here");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResizeWithNullSize() {
        renderer.resize(null);
        fail("We shouldn't be here");
    }
}

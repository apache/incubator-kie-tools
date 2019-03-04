/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsPageViewTest {

    @Mock
    private HTMLDivElement grid;

    private IncludedModelsPageView view;

    @Before
    public void setup() {
        view = new IncludedModelsPageView(grid);
    }

    @Test
    public void testSetGrid() {

        final HTMLElement gridHTMLElement = mock(HTMLElement.class);

        grid.innerHTML = "something";

        view.setGrid(gridHTMLElement);

        assertEquals("", grid.innerHTML);
        verify(grid).appendChild(gridHTMLElement);
    }
}

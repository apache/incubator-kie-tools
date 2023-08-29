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

package org.kie.workbench.common.dmn.client.editors.included;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsPageViewTest {

    @Mock
    private HTMLDivElement grid;

    @Mock
    private HTMLButtonElement includeModelButton;

    @Mock
    private IncludedModelsPagePresenter presenter;

    @Mock
    private ReadOnlyProvider readOnlyProvider;

    private IncludedModelsPageView view;

    @Before
    public void setup() {
        view = new IncludedModelsPageView(grid, includeModelButton, readOnlyProvider);
        view.init(presenter);
    }

    @Test
    public void testSetGrid() {

        final HTMLElement gridHTMLElement = mock(HTMLElement.class);
        final Element currentElement = mock(Element.class);

        grid.firstChild = currentElement;

        when(grid.removeChild(currentElement)).then(a -> {
            grid.firstChild = null;
            return currentElement;
        });

        view.setGrid(gridHTMLElement);

        verify(grid).removeChild(currentElement);
        verify(grid).appendChild(gridHTMLElement);
    }

    @Test
    public void testOnIncludeModelButtonClick() {
        view.onIncludeModelButtonClick(mock(ClickEvent.class));
        verify(presenter).openIncludeModelModal();
    }
}

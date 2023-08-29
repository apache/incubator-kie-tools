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

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelsPagePresenterTest {

    @Mock
    private IncludedModelsPagePresenter.View view;

    @Mock
    private DMNCardsGridComponent gridComponent;

    @Mock
    private IncludedModelModal modal;

    private IncludedModelsPagePresenter pagePresenter;

    @Before
    public void setup() {
        pagePresenter = new IncludedModelsPagePresenter(view, gridComponent, modal);
    }

    @Test
    public void testInit() {

        final HTMLElement htmlElement = mock(HTMLElement.class);
        when(gridComponent.getElement()).thenReturn(htmlElement);

        pagePresenter.init();

        verify(view).init(pagePresenter);
        verify(view).setGrid(htmlElement);
        verify(modal).init(pagePresenter);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = pagePresenter.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRefresh() {
        pagePresenter.refresh();
        verify(gridComponent).refresh();
    }

    @Test
    public void testOpenIncludeModelModal() {
        pagePresenter.openIncludeModelModal();
        verify(modal).show();
    }
}

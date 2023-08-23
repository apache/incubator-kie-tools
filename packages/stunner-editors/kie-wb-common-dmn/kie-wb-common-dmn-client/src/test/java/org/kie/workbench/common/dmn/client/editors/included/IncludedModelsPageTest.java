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

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(RootPanel.class)
public class IncludedModelsPageTest {

    @Mock
    private HTMLDivElement pageView;

    @Mock
    private TranslationService translationService;

    @Mock
    private FlashMessages flashMessages;

    @Mock
    private IncludedModelsPagePresenter includedModelsPresenter;

    @Mock
    private IncludedModelsPageState pageState;

    @Mock
    private IncludedModelsPageStateProvider stateProvider;

    private IncludedModelsPage page;

    @Before
    public void setup() {
        page = spy(new IncludedModelsPage(pageView, translationService, flashMessages, includedModelsPresenter, pageState, stateProvider) {
            protected void setupPageCSSClass(final String cssClass) {
                // Do nothing.
            }
        });
    }

    @Test
    public void testOnFocus() {

        final HTMLElement flashMessagesElement = mock(HTMLElement.class);
        final HTMLElement includedModelsPresenterElement = mock(HTMLElement.class);
        final Element currentElement = mock(Element.class);

        when(flashMessages.getElement()).thenReturn(flashMessagesElement);
        when(includedModelsPresenter.getElement()).thenReturn(includedModelsPresenterElement);

        pageView.firstChild = currentElement;

        when(pageView.removeChild(currentElement)).then(a -> {
            pageView.firstChild = null;
            return currentElement;
        });

        page.onFocus();

        verify(pageView).removeChild(currentElement);
        verify(pageView).appendChild(flashMessagesElement);
        verify(pageView).appendChild(includedModelsPresenterElement);
    }

    @Test
    public void testOnLostFocus() {
        page.onLostFocus();
        verify(flashMessages).hideMessages();
    }

    @Test
    public void testSetup() {

        page.reload();

        verify(pageState).init(stateProvider);
        verify(includedModelsPresenter).refresh();
    }
}

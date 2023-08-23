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

package org.kie.workbench.common.dmn.client.docks.navigator.included.components;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.Node;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.CallbackFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DecisionComponentsView_EnterText;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentsViewTest {

    @Mock
    private HTMLSelectElement drgElementFilter;

    @Mock
    private HTMLInputElement termFilter;

    @Mock
    private HTMLDivElement list;

    @Mock
    private HTMLDivElement emptyState;

    @Mock
    private HTMLDivElement loading;

    @Mock
    private TranslationService translationService;

    @Mock
    private DecisionComponents presenter;

    @Mock
    private HTMLDivElement componentsCounter;

    private DecisionComponentsView view;

    @Before
    public void setup() {
        view = spy(new DecisionComponentsView(drgElementFilter, termFilter, list, emptyState, loading, componentsCounter, translationService));
        view.init(presenter);
    }

    @Test
    public void testInit() {

        final JQuerySelectPicker selectPicker = mock(JQuerySelectPicker.class);
        final CallbackFunction callback = mock(CallbackFunction.class);
        final String placeholder = "placeholder";

        doReturn(selectPicker).when(view).getDrgElementFilter();
        when(view.onDrgElementFilterChange()).thenReturn(callback);
        when(translationService.format(DecisionComponentsView_EnterText)).thenReturn(placeholder);
        termFilter.placeholder = "something";

        view.init();

        verify(selectPicker).selectpicker("refresh");
        verify(selectPicker).on("hidden.bs.select", callback);
        assertEquals(placeholder, termFilter.placeholder);
    }

    @Test
    public void testOnTermFilterChange() {

        final KeyUpEvent event = mock(KeyUpEvent.class);
        final String term = "term";
        termFilter.value = term;

        view.onTermFilterChange(event);

        verify(presenter).applyTermFilter(term);
    }

    @Test
    public void testClear() {

        final Node element = mock(Node.class);

        emptyState.classList = mock(DOMTokenList.class);
        list.firstChild = element;
        when(list.removeChild(element)).then(a -> {
            list.firstChild = null;
            return element;
        });

        view.clear();

        verify(emptyState.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testAddListItem() {
        final HTMLElement listItemElement = mock(HTMLElement.class);

        view.addListItem(listItemElement);

        verify(list).appendChild(listItemElement);
    }

    @Test
    public void testShowEmptyState() {
        emptyState.classList = mock(DOMTokenList.class);

        view.showEmptyState();

        verify(emptyState.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testShowLoading() {
        loading.classList = mock(DOMTokenList.class);

        view.showLoading();

        verify(loading.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideLoading() {
        loading.classList = mock(DOMTokenList.class);

        view.hideLoading();

        verify(loading.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testDisableFilterInputs() {

        final JQuerySelectPicker selectPicker = mock(JQuerySelectPicker.class);

        doReturn(selectPicker).when(view).getDrgElementFilter();
        termFilter.value = "something";

        view.disableFilterInputs();

        assertEquals("", termFilter.value);
        assertTrue(termFilter.disabled);
        assertTrue(drgElementFilter.disabled);
        verify(selectPicker).selectpicker("val", "");
        verify(selectPicker).selectpicker("refresh");
    }

    @Test
    public void testEnableFilterInputs() {

        final JQuerySelectPicker selectPicker = mock(JQuerySelectPicker.class);

        doReturn(selectPicker).when(view).getDrgElementFilter();

        view.enableFilterInputs();

        assertFalse(termFilter.disabled);
        assertFalse(drgElementFilter.disabled);
        verify(selectPicker).selectpicker("refresh");
    }

    @Test
    public void testSetComponentsCounter() {
        view.setComponentsCounter(123);

        assertEquals("123", componentsCounter.textContent);
    }
}

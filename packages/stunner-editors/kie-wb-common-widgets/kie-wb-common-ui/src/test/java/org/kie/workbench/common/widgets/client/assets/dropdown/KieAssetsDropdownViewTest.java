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


package org.kie.workbench.common.widgets.client.assets.dropdown;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerTarget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.KieAssetsDropdownView_Select;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KieAssetsDropdownViewTest extends AbstractDropdownTest {

    @Mock
    private HTMLSelectElement nativeSelectMock;

    @Mock
    private DOMTokenList nativeSelectClassListMock;

    @Mock
    private HTMLOptionElement htmlOptionElementMock;

    @Mock
    private HTMLOptionElement htmlOptionElementClonedMock;

    @Mock
    private TranslationService translationServiceMock;

    @Mock
    private AbstractKieAssetsDropdown presenterMock;

    @Mock
    private JQuerySelectPicker dropdownMock;

    @Mock
    private JQuerySelectPicker.CallbackFunction onDropdownChangeHandlerMock;

    @Mock
    private HTMLOptionElement entryOptionMock;

    @Mock
    private HTMLOptionElement selectOptionMock;

    private KieAssetsDropdownView kieAssetsDropdownView;

    @Before
    public void setup() {
        when(dropdownMock.val()).thenReturn(DEFAULT_VALUE);
        nativeSelectMock.classList = nativeSelectClassListMock;
        when(htmlOptionElementMock.cloneNode(eq(false))).thenReturn(htmlOptionElementClonedMock);
        when(translationServiceMock.format(eq(KieAssetsDropdownView_Select))).thenReturn(KIEASSETSDROPDOWNVIEW_SELECT);
        kieAssetsDropdownView = spy(new KieAssetsDropdownView(nativeSelectMock,
                                                              htmlOptionElementMock,
                                                              translationServiceMock) {
            {
                this.presenter = presenterMock;
            }

            @Override
            protected JQuerySelectPicker dropdown() {
                return dropdownMock;
            }

            @Override
            protected JQuerySelectPicker.CallbackFunction getOnDropdownChangeHandler() {
                return onDropdownChangeHandlerMock;
            }

            @Override
            protected HTMLOptionElement entryOption(KieAssetsDropdownItem entry) {
                return entryOptionMock;
            }
        });
    }

    @Test
    public void init() {
        kieAssetsDropdownView.init();
        assertFalse(nativeSelectMock.hidden);
        verify(kieAssetsDropdownView, times(1)).dropdown();
        verify(kieAssetsDropdownView, times(1)).getOnDropdownChangeHandler();
        verify(dropdownMock, times(1)).on(eq("hidden.bs.select"), eq(onDropdownChangeHandlerMock));
    }

    @Test
    public void addValue() {
        kieAssetsDropdownView.addValue(kieAssetsDropdownItemMock);
        verify(kieAssetsDropdownView, times(1)).entryOption(eq(kieAssetsDropdownItemMock));
        verify(nativeSelectMock, times(1)).appendChild(eq(entryOptionMock));
    }

    @Test
    public void clear() {
        doReturn(selectOptionMock).when(kieAssetsDropdownView).selectOption();
        kieAssetsDropdownView.clear();
        verify(kieAssetsDropdownView, times(1)).removeChildren(eq(nativeSelectMock));
        verify(kieAssetsDropdownView, times(1)).selectOption();
        verify(nativeSelectMock, times(1)).appendChild(eq(selectOptionMock));
        verify(kieAssetsDropdownView, times(1)).refreshSelectPicker();
    }

    @Test
    public void initialize() {
        kieAssetsDropdownView.initialize();
        verify(kieAssetsDropdownView, times(2)).dropdown();
        verify(dropdownMock, times(1)).selectpicker(eq("val"), eq(""));
    }

    @Test
    public void refreshSelectPicker() {
        kieAssetsDropdownView.refreshSelectPicker();
        verify(kieAssetsDropdownView, times(1)).dropdown();
        verify(dropdownMock, times(1)).selectpicker(eq("refresh"));
    }

    @Test
    public void getValue() {
        assertEquals(DEFAULT_VALUE, kieAssetsDropdownView.getValue());
        verify(dropdownMock, times(1)).val();
    }

    @Test
    public void selectOption() {
        final HTMLOptionElement retrieved = kieAssetsDropdownView.selectOption();
        assertNotNull(retrieved);
        assertEquals(KIEASSETSDROPDOWNVIEW_SELECT, retrieved.text);
        assertEquals("", retrieved.value);
    }

    @Test
    public void onDropdownChangeHandlerMethod() {
        JQuerySelectPickerTarget targetMock = mock(JQuerySelectPickerTarget.class);
        targetMock.value = DEFAULT_VALUE;
        JQuerySelectPickerEvent eventMock = mock(JQuerySelectPickerEvent.class);
        eventMock.target = targetMock;
        kieAssetsDropdownView.onDropdownChangeHandlerMethod(eventMock);
        verify(presenterMock, times(1)).onValueChanged();
    }

    @Override
    protected KieAssetsDropdown getDropdown() {
        return null;
    }

    @Override
    protected KieAssetsDropdown.View getViewMock() {
        return kieAssetsDropdownView;
    }
}
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

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.CallbackFunction;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerTarget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdownView.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.widgets.client.assets.dropdown.KogitoKieAssetsDropdownView.SELECT_PICKER_SUBTEXT_ATTRIBUTE;
import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.KieAssetsDropdownView_Select;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoKieAssetsDropdownViewTest {

    @Mock
    private HTMLSelectElement nativeSelect;

    @Mock
    private HTMLOptionElement htmlOptionElement;

    @Mock
    private KogitoKieAssetsDropdown presenter;

    @Mock
    private JQuerySelectPicker dropdown;

    @Mock
    private TranslationService translationService;

    private KogitoKieAssetsDropdownView view;

    @Before
    public void setup() {

        view = Mockito.spy(new KogitoKieAssetsDropdownView(nativeSelect, htmlOptionElement, translationService));
        view.init(presenter);

        doReturn(dropdown).when(view).dropdown();
    }

    @Test
    public void testInit() {

        final CallbackFunction callbackFunction = mock(CallbackFunction.class);
        doReturn(callbackFunction).when(view).getOnDropdownChangeHandler();

        view.init();

        assertFalse(nativeSelect.hidden);
        verify(dropdown).on("hidden.bs.select", callbackFunction);
    }

    @Test
    public void testGetOnDropdownChangeHandler() {

        final JQuerySelectPickerEvent event = mock(JQuerySelectPickerEvent.class);
        final JQuerySelectPickerTarget target = mock(JQuerySelectPickerTarget.class);
        final String expectedValue = "newValue";

        event.target = target;
        target.value = expectedValue;

        view.getOnDropdownChangeHandler().call(event);

        final String actualValue = event.target.value;

        assertEquals(expectedValue, actualValue);
        verify(presenter).onValueChanged();
    }

    @Test
    public void testAddValue() {

        final HTMLOptionElement optionElement = mock(HTMLOptionElement.class);
        final KieAssetsDropdownItem entry = new KieAssetsDropdownItem("text", "subtext", "value", getMetaData());

        doReturn(optionElement).when(view).makeHTMLOptionElement();

        view.addValue(entry);

        assertEquals("text", optionElement.text);
        assertEquals("value", optionElement.value);
        verify(optionElement).setAttribute(SELECT_PICKER_SUBTEXT_ATTRIBUTE, "subtext");
        verify(nativeSelect).appendChild(optionElement);
    }

    @Test
    public void testClear() {

        final HTMLOptionElement oldOptionElement = mock(HTMLOptionElement.class);
        final HTMLOptionElement newOptionElement = mock(HTMLOptionElement.class);
        nativeSelect.firstChild = oldOptionElement;

        doReturn(newOptionElement).when(view).selectOption();
        when(nativeSelect.removeChild(oldOptionElement)).then(a -> {
            nativeSelect.firstChild = null;
            return oldOptionElement;
        });

        view.clear();

        verify(nativeSelect).removeChild(oldOptionElement);
        verify(nativeSelect).appendChild(newOptionElement);
        verify(view).refreshSelectPicker();
    }

    @Test
    public void testSelectOption() {

        final String select = "Select";

        doReturn(mock(HTMLOptionElement.class)).when(view).makeHTMLOptionElement();
        when(translationService.format(KieAssetsDropdownView_Select)).thenReturn(select);

        final HTMLOptionElement optionElement = view.selectOption();

        assertEquals(select, optionElement.text);
        assertEquals("", optionElement.value);
    }

    @Test
    public void testInitialize() {

        view.initialize();

        verify(dropdown).selectpicker("val", "");
    }

    @Test
    public void testRefreshSelectPicker() {
        view.refreshSelectPicker();
        verify(dropdown).selectpicker("refresh");
    }

    @Test
    public void testEnableDropdownMode() {

        nativeSelect.classList = mock(DOMTokenList.class);

        view.enableDropdownMode();

        verify(nativeSelect.classList).remove(HIDDEN_CSS_CLASS);
        verify(dropdown).selectpicker("show");
    }

    private Map<String, String> getMetaData() {
        return Stream.of(new AbstractMap.SimpleEntry<>("foo", "bar"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

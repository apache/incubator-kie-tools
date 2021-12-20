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

package org.drools.workbench.screens.scenariosimulation.client.dropdown;

import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DEFAULT_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.KIEASSETSDROPDOWNVIEW_SELECT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.workbench.common.widgets.client.resources.i18n.KieWorkbenchWidgetsConstants.KieAssetsDropdownView_Select;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationDropdownViewTest {

    @Mock
    protected HTMLSelectElement nativeSelectMock;

    @Mock
    protected DOMTokenList nativeSelectClassListMock;

    @Mock
    protected HTMLOptionElement htmlOptionElementMock;

    @Mock
    protected HTMLOptionElement htmlOptionElementClonedMock;

    @Mock
    protected TranslationService translationServiceMock;

    @Mock
    protected ScenarioSimulationDropdown presenterMock;

    @Mock
    protected JQuerySelectPicker dropdownMock;

    @Mock
    protected JQuerySelectPicker.CallbackFunction onDropdownChangeHandlerMock;

    @Mock
    protected KieAssetsDropdownItem kieAssetsDropdownItemMock;

    protected KieAssetsDropdownView assetsDropdownView;

    protected void setup() {
        nativeSelectMock.classList = nativeSelectClassListMock;
        when(htmlOptionElementMock.cloneNode(eq(false))).thenReturn(htmlOptionElementClonedMock);
        when(translationServiceMock.format(eq(KieAssetsDropdownView_Select))).thenReturn(KIEASSETSDROPDOWNVIEW_SELECT);
        doReturn(DEFAULT_VALUE).when(dropdownMock).val();
    }

    @Test
    public void init() {
        assetsDropdownView.init();
        assertFalse(nativeSelectMock.hidden);
        verify(dropdownMock, times(1)).on(eq("hidden.bs.select"), eq(onDropdownChangeHandlerMock));
    }

    @Test
    public void addValue() {
        assetsDropdownView.addValue(kieAssetsDropdownItemMock);
        verify(nativeSelectMock, times(1)).appendChild(isA(HTMLOptionElement.class));
    }

    @Test
    public void initialize() {
        assetsDropdownView.initialize();
        verify(dropdownMock, times(1)).selectpicker(eq("val"), eq(""));
    }

    @Test
    public void refreshSelectPicker() {
        assetsDropdownView.refreshSelectPicker();
        verify(dropdownMock, times(1)).selectpicker(eq("refresh"));
    }

    @Test
    public void getValue() {
        final String retrieved = assetsDropdownView.getValue();
        assertEquals(DEFAULT_VALUE, retrieved);
        verify(dropdownMock, times(1)).val();
    }
}

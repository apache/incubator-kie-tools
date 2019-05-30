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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ScenarioSimulationDropdownViewTest extends AbstractScenarioSimulationAssetsDropdownTest {

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
    private ScenarioSimulationDropdown presenterMock;

    @Mock
    private JQuerySelectPicker dropdownMock;

    @Mock
    private JQuerySelectPicker.CallbackFunction onDropdownChangeHandlerMock;

    @Mock
    protected KieAssetsDropdownItem kieAssetsDropdownItemMock;

    private ScenarioSimulationDropdownView scenarioSimulationDropdownView;

    @Before
    public void setup() {
        super.setup();
        nativeSelectMock.classList = nativeSelectClassListMock;
        when(htmlOptionElementMock.cloneNode(eq(false))).thenReturn(htmlOptionElementClonedMock);
        when(translationServiceMock.format(eq(KieAssetsDropdownView_Select))).thenReturn(KIEASSETSDROPDOWNVIEW_SELECT);
        scenarioSimulationDropdownView = spy(new ScenarioSimulationDropdownView(nativeSelectMock,
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

        });
        doReturn(DEFAULT_VALUE).when(dropdownMock).val();
    }

    @Test
    public void init() {
        scenarioSimulationDropdownView.init();
        assertFalse(nativeSelectMock.hidden);
        verify(dropdownMock, times(1)).on(eq("hidden.bs.select"), eq(onDropdownChangeHandlerMock));
    }

    @Test
    public void addValue() {
        scenarioSimulationDropdownView.addValue(kieAssetsDropdownItemMock);
        verify(nativeSelectMock, times(1)).appendChild(isA(HTMLOptionElement.class));
    }

    @Test
    public void initialize() {
        scenarioSimulationDropdownView.initialize();
        verify(dropdownMock, times(1)).selectpicker(eq("val"), eq(""));
    }

    @Test
    public void refreshSelectPicker() {
        scenarioSimulationDropdownView.refreshSelectPicker();
        verify(dropdownMock, times(1)).selectpicker(eq("refresh"));
    }

    @Test
    public void getValue() {
        final String retrieved = scenarioSimulationDropdownView.getValue();
        assertEquals(DEFAULT_VALUE, retrieved);
        verify(dropdownMock, times(1)).val();
    }
}
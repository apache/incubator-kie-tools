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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DEFAULT_VALUE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.LOWER_CASE_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SettingsScenarioSimulationDropdownTest extends AbstractScenarioSimulationDropdownTest {

    @Mock
    protected Command onMissingValueHandlerMock;

    @Before
    public void setup() {
        viewMock = mock(SettingsScenarioSimulationDropdownView.class);
        assetsDropdown = spy(new SettingsScenarioSimulationDropdown((SettingsScenarioSimulationDropdownView) viewMock, dataProviderMock) {
            {
                onValueChangeHandler = onValueChangeHandlerMock;
                onMissingValueHandler = onMissingValueHandlerMock;
                kieAssets.addAll(assetList);
                currentValue = DEFAULT_VALUE;
            }
        });
        super.setup();
    }

    @Test
    public void asWidget() {
        ((SettingsScenarioSimulationDropdown) assetsDropdown).asWidget();
        verify(((SettingsScenarioSimulationDropdownView) viewMock), times(1)).asWidget();
    }

    @Test
    public void loadAssets() {
        ((SettingsScenarioSimulationDropdown) assetsDropdown).loadAssets(LOWER_CASE_VALUE);
        /* Can't directly call super.loadAssets() method, so here it verifies clear() and
           initialize() method which represents the body of super.loadAssets() method   */
        verify(assetsDropdown, times(1)).clear();
        verify(assetsDropdown, times(1)).initializeDropdown();
        assertEquals(LOWER_CASE_VALUE, ((SettingsScenarioSimulationDropdown) assetsDropdown).currentValue);
    }

    @Test
    public void loadAssetsSameValue() {
        when(viewMock.getValue()).thenReturn(DEFAULT_VALUE);
        ((SettingsScenarioSimulationDropdown) assetsDropdown).loadAssets(DEFAULT_VALUE);
        /* Can't directly call super.loadAssets() method, so here it verifies clear() and
           initialize() method which represents the body of super.loadAssets() method   */
        verify(assetsDropdown, never()).clear();
        verify(assetsDropdown, never()).initializeDropdown();
    }

    @Test
    public void loadAssetsEmptyValue() {
        when(viewMock.getValue()).thenReturn(null);
        ((SettingsScenarioSimulationDropdown) assetsDropdown).loadAssets(DEFAULT_VALUE);
        /* Can't directly call super.loadAssets() method, so here it verifies clear() and
           initialize() method which represents the body of super.loadAssets() method   */
        verify(assetsDropdown, times(1)).clear();
        verify(assetsDropdown, times(1)).initializeDropdown();
        assertEquals(DEFAULT_VALUE, ((SettingsScenarioSimulationDropdown) assetsDropdown).currentValue);
    }

    @Test
    public void assetListConsumerMethod_Present() {
        ((SettingsScenarioSimulationDropdown) assetsDropdown).assetListConsumerMethod(assetList);
        verify(assetsDropdown, times(ITEM_NUMBER)).addValue(isA(KieAssetsDropdownItem.class));
        verify(viewMock, times(1)).refreshSelectPicker();
        verify(((SettingsScenarioSimulationDropdown) assetsDropdown), times(1)).isValuePresentInKieAssets(eq(DEFAULT_VALUE));
        verify(((SettingsScenarioSimulationDropdownView) viewMock)).initialize(eq(DEFAULT_VALUE));
        verify(onMissingValueHandlerMock, never()).execute();
        assertNull(((SettingsScenarioSimulationDropdown) assetsDropdown).currentValue);
    }

    @Test
    public void assetListConsumerMethod_NotPresent() {
        ((SettingsScenarioSimulationDropdown) assetsDropdown).currentValue = LOWER_CASE_VALUE;
        ((SettingsScenarioSimulationDropdown) assetsDropdown).assetListConsumerMethod(assetList);
        verify(assetsDropdown, times(ITEM_NUMBER)).addValue(isA(KieAssetsDropdownItem.class));
        verify(viewMock, times(1)).refreshSelectPicker();
        verify(((SettingsScenarioSimulationDropdown) assetsDropdown), times(1)).isValuePresentInKieAssets(eq(LOWER_CASE_VALUE));
        verify(((SettingsScenarioSimulationDropdownView) viewMock), times(1)).initialize();
        verify(onMissingValueHandlerMock, times(1)).execute();
        assertNull(((SettingsScenarioSimulationDropdown) assetsDropdown).currentValue);
    }

    @Test
    public void isValuePresentInKieAssets_Present() {
        assertTrue(((SettingsScenarioSimulationDropdown) assetsDropdown).isValuePresentInKieAssets(DEFAULT_VALUE));
    }

    @Test
    public void isValuePresentInKieAssets_NotPresent() {
        assertFalse(((SettingsScenarioSimulationDropdown) assetsDropdown).isValuePresentInKieAssets("ANOTHER_VALUE"));
    }

}
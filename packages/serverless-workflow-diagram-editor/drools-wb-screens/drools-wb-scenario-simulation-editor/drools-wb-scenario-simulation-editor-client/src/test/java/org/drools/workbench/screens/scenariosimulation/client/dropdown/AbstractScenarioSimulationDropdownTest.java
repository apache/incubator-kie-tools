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

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import elemental2.dom.HTMLElement;
import org.junit.Test;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdown;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItem;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownView;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DEFAULT_VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractScenarioSimulationDropdownTest {

    protected static final int ITEM_NUMBER = 3;

    @Mock
    protected KieAssetsDropdownItem kieAssetsDropdownItemMock;

    @Mock
    protected ScenarioSimulationAssetsDropdownProvider dataProviderMock;

    @Mock
    protected Command onValueChangeHandlerMock;

    @Mock
    protected HTMLElement htmlElementMock;

    protected List<KieAssetsDropdownItem> assetList = IntStream.range(0, ITEM_NUMBER)
            .mapToObj(i -> new KieAssetsDropdownItem("File_" + i + ".txt", "", DEFAULT_VALUE, new HashMap<>()))
            .collect(Collectors.toList());
    protected KieAssetsDropdown assetsDropdown;
    protected KieAssetsDropdownView viewMock;

    public void setup() {
        when(viewMock.getElement()).thenReturn(htmlElementMock);
        when(viewMock.getValue()).thenReturn(DEFAULT_VALUE);
    }

    @Test
    public void init() {
        assetsDropdown.init();
        verify(viewMock, times(1)).init(eq(assetsDropdown));
    }

    @Test
    public void loadAssets() {
        assetsDropdown.loadAssets();
        verify(assetsDropdown, times(1)).clear();
        verify(assetsDropdown, times(1)).initializeDropdown();
    }

    @Test
    public void initialize() {
        assetsDropdown.initialize();
        verify(viewMock, times(1)).refreshSelectPicker();
    }

    @Test
    public void clear() {
        assetsDropdown.clear();
        verify(viewMock, times(1)).clear();
    }

    @Test
    public void getElement() {
        final HTMLElement retrieved = assetsDropdown.getElement();
        verify(viewMock, times(1)).getElement();
        assertEquals(htmlElementMock, retrieved);
    }

    @Test
    public void getValue() {
        when(viewMock.getValue()).thenReturn(DEFAULT_VALUE);
        Optional<KieAssetsDropdownItem> retrieved = assetsDropdown.getValue();
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        verify(viewMock, times(1)).getValue();
        reset(viewMock);
        when(viewMock.getValue()).thenReturn("UNKNOWN");
        retrieved = assetsDropdown.getValue();
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void onValueChanged() {
        assetsDropdown.onValueChanged();
        verify(onValueChangeHandlerMock, times(1)).execute();
    }

    @Test
    public void initializeDropdown() {
        assetsDropdown.initializeDropdown();
        verify(dataProviderMock, times(1)).getItems(isA(Consumer.class));
    }

    @Test
    public void addValue() {
        assetsDropdown.addValue(kieAssetsDropdownItemMock);
        verify(viewMock, times(1)).addValue(eq(kieAssetsDropdownItemMock));
    }
}
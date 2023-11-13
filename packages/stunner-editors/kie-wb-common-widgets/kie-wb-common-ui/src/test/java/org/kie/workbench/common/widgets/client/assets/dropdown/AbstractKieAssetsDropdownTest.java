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

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractKieAssetsDropdownTest extends AbstractDropdownTest {

    @Mock
    protected KieAssetsDropdownItemsProvider dataProviderMock;

    @Mock
    protected Command onValueChangeHandlerMock;

    protected List<KieAssetsDropdownItem> assetList = IntStream.range(0, 3)
            .mapToObj(i -> new KieAssetsDropdownItem("File_" + i + ".txt", "", DEFAULT_VALUE, new HashMap<>()))
            .collect(Collectors.toList());

    @Mock
    private KieAssetsDropdownView viewlocalMock;

    private KieAssetsDropdown dropdownLocal;

    @Before
    public void setup() {
        dropdownLocal = spy(new AbstractKieAssetsDropdown(getViewMock(), dataProviderMock) {
            {
                onValueChangeHandler = onValueChangeHandlerMock;
                this.kieAssets.addAll(assetList);
            }
        });
        commonSetup();
    }

    @Test
    public void init() {
        getDropdown().init();
        verify(getViewMock(), times(1)).init(eq(getDropdown()));
    }

    @Test
    public void loadAssets() {
        getDropdown().loadAssets();
        verify(getDropdown(), times(1)).clear();
        verify(getDropdown(), times(1)).initializeDropdown();
    }

    @Test
    public void initialize() {
        getDropdown().initialize();
        verify(getViewMock(), times(1)).refreshSelectPicker();
    }

    @Test
    public void clear() {
        getDropdown().clear();
        verify(getViewMock(), times(1)).clear();
    }

    @Test
    public void getElement() {
        final HTMLElement retrieved = getDropdown().getElement();
        verify(getViewMock(), times(1)).getElement();
        assertEquals(htmlElementMock, retrieved);
    }

    @Test
    public void getValue() {
        when(getViewMock().getValue()).thenReturn(DEFAULT_VALUE);
        Optional<KieAssetsDropdownItem> retrieved = getDropdown().getValue();
        assertNotNull(retrieved);
        assertTrue(retrieved.isPresent());
        verify(getViewMock(), times(1)).getValue();
        reset(getViewMock());
        when(getViewMock().getValue()).thenReturn("UNKNOWN");
        retrieved = getDropdown().getValue();
        assertNotNull(retrieved);
        assertFalse(retrieved.isPresent());
    }

    @Test
    public void assetListConsumerMethod() {
        ((AbstractKieAssetsDropdown) getDropdown()).assetListConsumerMethod(assetList);
        assetList.forEach(asset ->
                                  verify(getDropdown(), times(1)).addValue(eq(asset)));
        verify(getViewMock(), times(1)).refreshSelectPicker();
        verify(getViewMock(), times(1)).initialize();
    }

    @Test
    public void onValueChanged() {
        getDropdown().onValueChanged();
        verify(onValueChangeHandlerMock, times(1)).execute();
    }

    @Test
    public void initializeDropdown() {
        getDropdown().initializeDropdown();
        verify(dataProviderMock, times(1)).getItems(isA(Consumer.class));
    }

    @Test
    public void addValue() {
        getDropdown().addValue(kieAssetsDropdownItemMock);
        verify(getViewMock(), times(1)).addValue(eq(kieAssetsDropdownItemMock));
    }

    @Override
    protected KieAssetsDropdown getDropdown() {
        return dropdownLocal;
    }

    @Override
    protected KieAssetsDropdown.View getViewMock() {
        return viewlocalMock;
    }
}
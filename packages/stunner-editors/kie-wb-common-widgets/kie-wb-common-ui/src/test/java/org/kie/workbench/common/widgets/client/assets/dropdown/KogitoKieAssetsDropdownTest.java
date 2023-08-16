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

import java.util.ArrayList;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoKieAssetsDropdownTest extends AbstractKieAssetsDropdownTest {

    @Mock
    private Consumer<List<KieAssetsDropdownItem>> kieAssetsConsumer;

    @Mock
    private KogitoKieAssetsDropdownView viewlocalMock;

    private KieAssetsDropdown dropdownLocal;

    @Before
    public void setup() {
        dropdownLocal = spy(new KogitoKieAssetsDropdown(viewlocalMock, dataProviderMock) {
            {
                onValueChangeHandler = onValueChangeHandlerMock;
                this.kieAssets.addAll(assetList);
            }
        });
        commonSetup();
    }

    @Test
    public void testRegisterOnChangeHandler() {
        final Command command = mock(Command.class);

        getDropdown().registerOnChangeHandler(command);
        getDropdown().onValueChanged();

        verify(command).execute();
    }

    @Test
    public void testLoadAssetsWhenEnvIsNotKogito() {

        doReturn(kieAssetsConsumer).when((KogitoKieAssetsDropdown) getDropdown()).getAssetListConsumer();

        getDropdown().loadAssets();

        verify(getDropdown()).clear();
        verify(viewlocalMock).enableDropdownMode();
        verify(dataProviderMock).getItems(kieAssetsConsumer);
    }

    @Test
    public void testInitialize() {
        getDropdown().initialize();
        verify(getViewMock()).refreshSelectPicker();
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(getViewMock().getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = getDropdown().getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testGetValue() {
        final List<KieAssetsDropdownItem> kieAssets = IntStream.range(0, 4).mapToObj(i -> {
            final KieAssetsDropdownItem toReturn = mock(KieAssetsDropdownItem.class);
            when(toReturn.getValue()).thenReturn("item" + i);
            return toReturn;
        }).collect(Collectors.toList());

        when(getViewMock().getValue()).thenReturn("item2");
        ((KogitoKieAssetsDropdown) getDropdown()).kieAssets.clear();
        ((KogitoKieAssetsDropdown) getDropdown()).kieAssets.addAll(kieAssets);
        final Optional<KieAssetsDropdownItem> retrieved = getDropdown().getValue();
        assertTrue(retrieved.isPresent());
        assertEquals("item2", retrieved.get().getValue());
    }

    @Test
    public void testGetValueWhenOptionDoesNotExist() {
        ((KogitoKieAssetsDropdown) getDropdown()).kieAssets.clear();
        assertFalse(getDropdown().getValue().isPresent());
    }

    @Test
    public void getAssetListConsumer() {
        final List<KieAssetsDropdownItem> expectedDropdownItems = new ArrayList<>();
        ((KogitoKieAssetsDropdown) getDropdown()).getAssetListConsumer().accept(expectedDropdownItems);
        verify(((KogitoKieAssetsDropdown) getDropdown()), times(1)).assetListConsumerMethod(eq(expectedDropdownItems));
    }

    @Test
    public void assetListConsumerMethod() {
        ((KogitoKieAssetsDropdown) getDropdown()).assetListConsumerMethod(assetList);
        assetList.forEach(item -> verify(getViewMock()).addValue(item));
        verify(getViewMock()).refreshSelectPicker();
        verify(getViewMock()).initialize();
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

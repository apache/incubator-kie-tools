/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.client.assets.dropdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.submarine.IsSubmarine;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KieAssetsDropdownTest {

    @Mock
    private KieAssetsDropdown.View view;

    @Mock
    private IsSubmarine isSubmarine;

    @Mock
    private KieAssetsDropdownItemsProvider dataProvider;

    @Mock
    private Consumer<List<KieAssetsDropdownItem>> kieAssetsConsumer;

    private KieAssetsDropdownFake dropdown;

    @Before
    public void setup() {
        dropdown = spy(new KieAssetsDropdownFake(view, isSubmarine, dataProvider));
        when(isSubmarine.get()).thenReturn(false);
    }

    @Test
    public void testSetup() {
        dropdown.setup();
        verify(view).init(dropdown);
    }

    @Test
    public void testRegisterOnChangeHandler() {
        final Command command = mock(Command.class);

        dropdown.registerOnChangeHandler(command);
        dropdown.onValueChanged();

        verify(command).execute();
    }

    @Test
    public void testLoadAssetsWhenEnvIsSubmarine() {

        when(isSubmarine.get()).thenReturn(true);

        dropdown.loadAssets();

        verify(dropdown).clear();
        verify(view).enableInputMode();
        verify(view).initialize();
    }

    @Test
    public void testLoadAssetsWhenEnvIsNotSubmarine() {

        doReturn(kieAssetsConsumer).when(dropdown).getAssetListConsumer();

        dropdown.loadAssets();

        verify(dropdown).clear();
        verify(view).enableDropdownMode();
        verify(dataProvider).getItems(kieAssetsConsumer);
    }

    @Test
    public void testInitialize() {
        dropdown.initialize();
        verify(view).refreshSelectPicker();
    }

    @Test
    public void testInitializeWhenItIsNotSubmarine() {
        when(isSubmarine.get()).thenReturn(true);
        dropdown.initialize();
        verify(view, never()).refreshSelectPicker();
    }

    @Test
    public void testClear() {

        final List<KieAssetsDropdownItem> kieAssets = spy(new ArrayList<>());
        doReturn(kieAssets).when(dropdown).getKieAssets();

        dropdown.clear();

        verify(kieAssets).clear();
        verify(view).clear();
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = dropdown.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testGetValue() {

        final KieAssetsDropdownItem dropdownItem1 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem2 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem3 = mock(KieAssetsDropdownItem.class);
        final List<KieAssetsDropdownItem> kieAssets = asList(dropdownItem1, dropdownItem2, dropdownItem3);

        when(dropdownItem1.getValue()).thenReturn("item1");
        when(dropdownItem2.getValue()).thenReturn("item2");
        when(dropdownItem3.getValue()).thenReturn("item3");
        when(view.getValue()).thenReturn("item2");

        doReturn(kieAssets).when(dropdown).getKieAssets();

        final Optional<KieAssetsDropdownItem> value = dropdown.getValue();

        assertTrue(value.isPresent());
        assertEquals(dropdownItem2, value.get());
    }

    @Test
    public void testGetValueWhenOptionDoesNotExist() {

        doReturn(emptyList()).when(dropdown).getKieAssets();

        assertFalse(dropdown.getValue().isPresent());
    }

    @Test
    public void testGetValueWhenItIsSubmarine() {

        final String expectedValue = "value";

        when(isSubmarine.get()).thenReturn(true);
        when(view.getValue()).thenReturn(expectedValue);

        final Optional<KieAssetsDropdownItem> value = dropdown.getValue();

        assertTrue(value.isPresent());
        assertEquals(expectedValue, value.get().getValue());
    }

    @Test
    public void testGetAssetListConsumer() {

        final KieAssetsDropdownItem dropdownItem1 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem2 = mock(KieAssetsDropdownItem.class);
        final KieAssetsDropdownItem dropdownItem3 = mock(KieAssetsDropdownItem.class);
        final List<KieAssetsDropdownItem> expectedDropdownItems = asList(dropdownItem1, dropdownItem2, dropdownItem3);

        dropdown.getAssetListConsumer().accept(expectedDropdownItems);

        final List<KieAssetsDropdownItem> actualDropdownItems = dropdown.getKieAssets();

        verify(view).addValue(dropdownItem1);
        verify(view).addValue(dropdownItem2);
        verify(view).addValue(dropdownItem3);
        verify(view).refreshSelectPicker();
        verify(view).initialize();
        assertEquals(expectedDropdownItems, actualDropdownItems);
    }

    class KieAssetsDropdownFake extends KieAssetsDropdown {

        KieAssetsDropdownFake(final View view,
                              final IsSubmarine isSubmarine,
                              final KieAssetsDropdownItemsProvider dataProvider) {
            super(view, isSubmarine, dataProvider);
        }
    }
}

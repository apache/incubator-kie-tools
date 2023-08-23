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
package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasValue;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseColumnHeaderMetaDataContextMenuTest<M extends HasCellEditorControls & HasListSelectorControl, V, HV extends HasValue<V>> {

    @Mock
    protected HasTypeRef hasTypeRef;

    @Mock
    protected Consumer<HV> clearValueConsumer;

    @Mock
    protected BiConsumer<HV, V> setValueConsumer;

    @Mock
    protected BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    protected ValueAndDataTypePopoverView.Presenter editor;

    @Mock
    protected ListSelectorView.Presenter listSelector;

    @Mock
    protected BiFunction<Integer, Integer, List<HasListSelectorControl.ListSelectorItem>> listSelectorItemsSupplier;

    @Mock
    protected Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer;

    @Mock
    protected HasListSelectorControl.ListSelectorItem item;

    protected M headerMetaData;

    @Before
    public void setup() {
        this.headerMetaData = getHeaderMetaData();

        when(translationService.getTranslation(Mockito.<String>any())).thenAnswer(i -> i.getArguments()[0]);
    }

    protected abstract M getHeaderMetaData();

    @Test
    public void testGetEditor() {
        assertThat(headerMetaData.getEditor()).isEqualTo(Optional.of(listSelector));
    }

    @Test
    public void testGetItems() {
        headerMetaData.getItems(0, 1);

        verify(listSelectorItemsSupplier).apply(eq(0), eq(1));
    }

    @Test
    public void testOnItemSelected() {
        headerMetaData.onItemSelected(item);

        verify(listSelectorItemConsumer).accept(eq(item));
    }
}

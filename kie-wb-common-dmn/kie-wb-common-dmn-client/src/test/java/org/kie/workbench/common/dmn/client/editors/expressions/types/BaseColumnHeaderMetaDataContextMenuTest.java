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
package org.kie.workbench.common.dmn.client.editors.expressions.types;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseColumnHeaderMetaDataContextMenuTest<M extends HasCellEditorControls & HasListSelectorControl> {

    protected static final String EDITOR_TITLE = "title";

    @Mock
    protected HasName hasName;

    @Mock
    protected HasTypeRef hasTypeRef;

    @Mock
    protected Consumer<HasName> clearDisplayNameConsumer;

    @Mock
    protected BiConsumer<HasName, Name> setDisplayNameConsumer;

    @Mock
    protected BiConsumer<HasTypeRef, QName> setTypeRefConsumer;

    @Mock
    protected CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    protected NameAndDataTypePopoverView.Presenter editor;

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

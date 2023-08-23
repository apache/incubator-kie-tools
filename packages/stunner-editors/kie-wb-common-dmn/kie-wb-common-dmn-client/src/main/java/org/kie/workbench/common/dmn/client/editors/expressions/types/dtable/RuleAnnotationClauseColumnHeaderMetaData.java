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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableTextHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public class RuleAnnotationClauseColumnHeaderMetaData extends EditableTextHeaderMetaData<TextBox, TextBoxDOMElement> implements HasListSelectorControl,
                                                                                                                                HasCellEditorControls {

    public static final String COLUMN_GROUP = "RuleAnnotationClauseColumnHeaderMetaData$RuleAnnotationClauseColumn";

    private final BiFunction<Integer, Integer, List<ListSelectorItem>> listSelectorItemsSupplier;
    private final ListSelectorView.Presenter listSelector;
    private final Consumer<ListSelectorItem> listSelectorItemConsumer;
    private final Optional<String> placeHolder;

    public RuleAnnotationClauseColumnHeaderMetaData(final Supplier<String> titleGetter,
                                                    final Consumer<String> titleSetter,
                                                    final SingletonDOMElementFactory<TextBox, TextBoxDOMElement> factory,
                                                    final Optional<String> placeHolder,
                                                    final BiFunction<Integer, Integer, List<ListSelectorItem>> listSelectorItemsSupplier,
                                                    final ListSelectorView.Presenter listSelector,
                                                    final Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer) {
        super(titleGetter,
              titleSetter,
              factory,
              COLUMN_GROUP);
        this.listSelectorItemsSupplier = listSelectorItemsSupplier;
        this.listSelector = listSelector;
        this.listSelectorItemConsumer = listSelectorItemConsumer;
        this.placeHolder = placeHolder;
    }

    @Override
    public Optional<String> getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
    }

    @Override
    public String getColumnGroup() {
        return COLUMN_GROUP;
    }

    @Override
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        return listSelectorItemsSupplier.apply(uiRowIndex, uiColumnIndex);
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        listSelectorItemConsumer.accept(item);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final RuleAnnotationClauseColumnHeaderMetaData that = (RuleAnnotationClauseColumnHeaderMetaData) o;
        return Objects.equals(listSelectorItemsSupplier, that.listSelectorItemsSupplier) &&
                Objects.equals(listSelector, that.listSelector) &&
                Objects.equals(listSelectorItemConsumer, that.listSelectorItemConsumer) &&
                Objects.equals(placeHolder, that.placeHolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                            listSelectorItemsSupplier,
                            listSelector,
                            listSelectorItemConsumer,
                            placeHolder);
    }
}

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
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.ValueAndDataTypeHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class InputClauseColumnHeaderMetaData extends ValueAndDataTypeHeaderMetaData<Text, HasText> implements HasCellEditorControls,
                                                                                                              HasListSelectorControl {

    private static final String NAME_DATA_TYPE_COLUMN_GROUP = "InputClauseColumnHeaderMetaData$NameAndDataTypeColumn";

    private final ListSelectorView.Presenter listSelector;
    private final BiFunction<Integer, Integer, List<HasListSelectorControl.ListSelectorItem>> listSelectorItemsSupplier;
    private final Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer;

    public InputClauseColumnHeaderMetaData(final HasText hasValue,
                                           final Supplier<HasTypeRef> hasTypeRef,
                                           final Consumer<HasText> clearValueConsumer,
                                           final BiConsumer<HasText, Text> setValueConsumer,
                                           final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                           final TranslationService translationService,
                                           final CellEditorControlsView.Presenter cellEditorControls,
                                           final ValueAndDataTypePopoverView.Presenter editor,
                                           final ListSelectorView.Presenter listSelector,
                                           final BiFunction<Integer, Integer, List<HasListSelectorControl.ListSelectorItem>> listSelectorItemsSupplier,
                                           final Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer) {
        super(Optional.of(hasValue),
              hasTypeRef,
              clearValueConsumer,
              setValueConsumer,
              setTypeRefConsumer,
              translationService,
              cellEditorControls,
              editor);
        this.listSelector = listSelector;
        this.listSelectorItemsSupplier = listSelectorItemsSupplier;
        this.listSelectorItemConsumer = listSelectorItemConsumer;
    }

    @Override
    protected boolean isEmptyValue(final Text value) {
        return Objects.isNull(value) || StringUtils.isEmpty(value.getValue());
    }

    @Override
    public Text toModelValue(final String componentValue) {
        return new Text(componentValue);
    }

    @Override
    public String toWidgetValue(final Text modelValue) {
        return modelValue.getValue();
    }

    @Override
    public String getValueLabel() {
        return translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_InputClauseColumnHeaderMetaData_ValueLabel);
    }

    @Override
    public String normaliseValue(final String componentValue) {
        return componentValue;
    }

    @Override
    public Text getValue() {
        return hasValue.orElse(HasText.NOP).getValue();
    }

    @Override
    public String getColumnGroup() {
        return NAME_DATA_TYPE_COLUMN_GROUP;
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_EditInputClause);
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
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
}

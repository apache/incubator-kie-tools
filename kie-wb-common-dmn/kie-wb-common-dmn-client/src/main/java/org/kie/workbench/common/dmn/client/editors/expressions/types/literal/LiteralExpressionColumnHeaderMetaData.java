/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.HasNameAndDataTypeControl;
import org.kie.workbench.common.dmn.client.editors.types.NameAndDataTypeEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditablePopupHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

class LiteralExpressionColumnHeaderMetaData extends EditablePopupHeaderMetaData<HasNameAndDataTypeControl, NameAndDataTypeEditorView.Presenter> {

    private static final String NAME_DATA_TYPE_COLUMN_GROUP = "LiteralExpressionColumnHeaderMetaData$NameAndDataTypeColumn";

    private final Supplier<String> nameSupplier;
    private final Consumer<String> nameConsumer;
    private final Supplier<QName> typeRefSupplier;

    public LiteralExpressionColumnHeaderMetaData(final Supplier<String> nameSupplier,
                                                 final Consumer<String> nameConsumer,
                                                 final Supplier<QName> typeRefSupplier,
                                                 final CellEditorControlsView.Presenter cellEditorControls,
                                                 final NameAndDataTypeEditorView.Presenter headerEditor,
                                                 final LiteralExpressionGrid gridWidget) {
        super(cellEditorControls,
              headerEditor,
              gridWidget);
        this.nameSupplier = nameSupplier;
        this.nameConsumer = nameConsumer;
        this.typeRefSupplier = typeRefSupplier;
    }

    @Override
    public String getColumnGroup() {
        return NAME_DATA_TYPE_COLUMN_GROUP;
    }

    @Override
    public String getTitle() {
        return nameSupplier.get();
    }

    @Override
    public void setTitle(final String title) {
        nameConsumer.accept(title);
    }

    String getTypeRef() {
        return typeRefSupplier.get().toString();
    }
}

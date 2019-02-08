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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.HasKindSelectControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector.KindPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditablePopupHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

class FunctionKindRowColumnHeaderMetaData extends EditablePopupHeaderMetaData<HasKindSelectControl, KindPopoverView.Presenter> {

    private static final String EXPRESSION_TYPE_GROUP = "ExpressionTypeGroup";

    private final FunctionGrid gridWidget;
    private final Supplier<Optional<FunctionDefinition>> functionSupplier;

    FunctionKindRowColumnHeaderMetaData(final Supplier<Optional<FunctionDefinition>> functionSupplier,
                                        final CellEditorControlsView.Presenter cellEditorControls,
                                        final KindPopoverView.Presenter editor,
                                        final Optional<String> editorTitle,
                                        final FunctionGrid gridWidget) {
        super(cellEditorControls,
              editor,
              editorTitle);

        this.functionSupplier = functionSupplier;
        this.gridWidget = gridWidget;
    }

    @Override
    public String getColumnGroup() {
        return EXPRESSION_TYPE_GROUP;
    }

    @Override
    protected HasKindSelectControl getPresenter() {
        return gridWidget;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public String getTitle() {
        return functionSupplier.get().get().getKind().code();
    }
}
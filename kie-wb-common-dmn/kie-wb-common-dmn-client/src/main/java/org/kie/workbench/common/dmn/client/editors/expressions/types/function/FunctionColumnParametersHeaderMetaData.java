/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.types.Point2D;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public class FunctionColumnParametersHeaderMetaData implements EditableHeaderMetaData {

    static final String PARAMETER_COLUMN_GROUP = "FunctionColumnParametersHeaderMetaData$Parameters";

    private final Supplier<FunctionDefinition> functionSupplier;
    private final CellEditorControlsView.Presenter cellEditorControls;
    private final ParametersEditorView.Presenter editor;
    private final FunctionGrid gridWidget;

    public FunctionColumnParametersHeaderMetaData(final Supplier<FunctionDefinition> functionSupplier,
                                                  final CellEditorControlsView.Presenter cellEditorControls,
                                                  final ParametersEditorView.Presenter editor,
                                                  final FunctionGrid gridWidget) {
        this.functionSupplier = functionSupplier;
        this.cellEditorControls = cellEditorControls;
        this.editor = editor;
        this.gridWidget = gridWidget;
    }

    @Override
    public String getColumnGroup() {
        return PARAMETER_COLUMN_GROUP;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public String getTitle() {
        //TODO {manstis} We need the FunctionGridRendered to render the two sections as different cells
        final StringBuilder sb = new StringBuilder(getExpressionLanguageTitle());
        sb.append(" : ");
        sb.append(getFormalParametersTitle());
        return sb.toString();
    }

    String getExpressionLanguageTitle() {
        return KindUtilities.getKind(functionSupplier.get()).code();
    }

    String getFormalParametersTitle() {
        final List<InformationItem> formalParameters = functionSupplier.get().getFormalParameter();
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (!formalParameters.isEmpty()) {
            sb.append(formalParameters.stream().map(ii -> ii.getName().getValue()).collect(Collectors.joining(", ")));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void setTitle(final String title) {
        throw new UnsupportedOperationException("Title is derived from the Decision Table Hit Policy and cannot be set on the HeaderMetaData.");
    }

    @Override
    public void edit(final GridBodyCellEditContext context) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final double absoluteCellX = context.getAbsoluteCellX();
        final double absoluteCellY = context.getAbsoluteCellY();

        editor.bind(gridWidget,
                    uiRowIndex,
                    uiColumnIndex);
        final double[] dxy = {0.0, 0.0};
        final double headerRowHeight = context.getCellHeight();
        final Optional<Point2D> rx = context.getRelativeLocation();
        rx.ifPresent(r -> {
            dxy[0] = r.getX();
            dxy[1] = r.getY() - headerRowHeight * uiRowIndex;
        });
        cellEditorControls.show(editor,
                                (int) (absoluteCellX + dxy[0]),
                                (int) (absoluteCellY + dxy[1]));
    }

    @Override
    public void destroyResources() {
        editor.hide();
    }
}
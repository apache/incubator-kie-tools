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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.HasParametersControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditablePopupHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

public class FunctionColumnParametersHeaderMetaData extends EditablePopupHeaderMetaData<HasParametersControl, ParametersEditorView.Presenter> {

    static final String PARAMETER_COLUMN_GROUP = "FunctionColumnParametersHeaderMetaData$Parameters";

    private final Supplier<FunctionDefinition> functionSupplier;
    private final FunctionGrid gridWidget;

    public FunctionColumnParametersHeaderMetaData(final Supplier<FunctionDefinition> functionSupplier,
                                                  final CellEditorControlsView.Presenter cellEditorControls,
                                                  final ParametersEditorView.Presenter editor,
                                                  final FunctionGrid gridWidget) {
        super(cellEditorControls,
              editor);
        this.functionSupplier = functionSupplier;
        this.gridWidget = gridWidget;
    }

    @Override
    protected HasParametersControl getPresenter() {
        return gridWidget;
    }

    @Override
    public String getColumnGroup() {
        return PARAMETER_COLUMN_GROUP;
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
}
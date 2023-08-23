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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ait.lienzo.client.core.shape.Group;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.HasParametersControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters.ParametersPopoverView;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditablePopupHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;

public class FunctionColumnParametersHeaderMetaData extends EditablePopupHeaderMetaData<HasParametersControl, ParametersPopoverView.Presenter> {

    static final String PARAMETER_COLUMN_GROUP = "FunctionColumnParametersHeaderMetaData$Parameters";

    private final Supplier<Optional<FunctionDefinition>> functionSupplier;
    private final FunctionGrid gridWidget;
    private final TranslationService translationService;

    public FunctionColumnParametersHeaderMetaData(final Supplier<Optional<FunctionDefinition>> functionSupplier,
                                                  final TranslationService translationService,
                                                  final CellEditorControlsView.Presenter cellEditorControls,
                                                  final ParametersPopoverView.Presenter editor,
                                                  final FunctionGrid gridWidget) {
        super(cellEditorControls,
              editor);
        this.functionSupplier = functionSupplier;
        this.translationService = translationService;
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
        return getFormalParametersTitle();
    }

    @Override
    public Group render(final GridHeaderColumnRenderContext context,
                        final double blockWidth,
                        final double blockHeight) {
        if (!hasFormalParametersSet()) {
            return RendererUtils.getEditableHeaderPlaceHolderText(this,
                                                                  context,
                                                                  blockWidth,
                                                                  blockHeight);
        } else {
            return super.render(context,
                                blockWidth,
                                blockHeight);
        }
    }

    @Override
    public Optional<String> getPlaceHolder() {
        return Optional.of(translationService.getTranslation(DMNEditorConstants.FunctionEditor_EditParameters));
    }

    String getExpressionLanguageTitle() {
        final FunctionDefinition.Kind kind = KindUtilities.getKind(functionSupplier.get().get());
        return kind == null ? translationService.getTranslation(DMNEditorConstants.FunctionEditor_Undefined) : kind.code();
    }

    String getFormalParametersTitle() {
        final List<InformationItem> formalParameters = functionSupplier.get().get().getFormalParameter();
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (!formalParameters.isEmpty()) {
            sb.append(formalParameters.stream().map(ii -> ii.getName().getValue()).collect(Collectors.joining(", ")));
        }
        sb.append(")");
        return sb.toString();
    }

    boolean hasFormalParametersSet() {
        return !functionSupplier.get().get().getFormalParameter().isEmpty();
    }
}
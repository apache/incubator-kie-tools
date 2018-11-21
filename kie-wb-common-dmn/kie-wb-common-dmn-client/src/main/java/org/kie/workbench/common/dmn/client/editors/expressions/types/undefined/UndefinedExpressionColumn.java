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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.selector.UndefinedExpressionSelectorPopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellEditContext;

public class UndefinedExpressionColumn extends DMNGridColumn<UndefinedExpressionGrid, String> {

    private final CellEditorControlsView.Presenter cellEditorControls;
    private final UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector;
    private final TranslationService translationService;

    public UndefinedExpressionColumn(final HeaderMetaData headerMetaData,
                                     final UndefinedExpressionGrid gridWidget,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector,
                                     final TranslationService translationService) {
        this(Collections.singletonList(headerMetaData),
             gridWidget,
             cellEditorControls,
             undefinedExpressionSelector,
             translationService);
    }

    public UndefinedExpressionColumn(final List<HeaderMetaData> headerMetaData,
                                     final UndefinedExpressionGrid gridWidget,
                                     final CellEditorControlsView.Presenter cellEditorControls,
                                     final UndefinedExpressionSelectorPopoverView.Presenter undefinedExpressionSelector,
                                     final TranslationService translationService) {
        super(headerMetaData,
              new UndefinedExpressionColumnRenderer(),
              gridWidget);
        this.cellEditorControls = cellEditorControls;
        this.undefinedExpressionSelector = undefinedExpressionSelector;
        this.translationService = translationService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void edit(final GridCell<String> cell,
                     final GridBodyCellEditContext context,
                     final Consumer<GridCellValue<String>> callback) {
        final int uiRowIndex = context.getRowIndex();
        final int uiColumnIndex = context.getColumnIndex();
        final double absoluteCellX = context.getAbsoluteCellX();
        final double absoluteCellY = context.getAbsoluteCellY();

        undefinedExpressionSelector.bind(gridWidget,
                                         uiRowIndex,
                                         uiColumnIndex);
        final double[] dxy = {absoluteCellX, absoluteCellY};
        final Optional<com.ait.lienzo.client.core.types.Point2D> rx = context.getRelativeLocation();
        rx.ifPresent(r -> {
            dxy[0] = r.getX();
            dxy[1] = r.getY();
        });
        cellEditorControls.show(undefinedExpressionSelector,
                                Optional.of(translationService.getTranslation(DMNEditorConstants.UndefinedExpressionEditor_SelectorTitle)),
                                (int) (dxy[0]),
                                (int) (dxy[1]));
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }
}

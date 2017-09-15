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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import java.util.stream.IntStream;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.DMNBaseGridWidget;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.TextBoxSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.StringDOMElementSingletonColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.BlueTheme;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

@Templated
@Dependent
public class ContextEditorViewImpl extends BaseExpressionEditorViewImpl<ContextEditorView.Editor, Context> implements ContextEditorView {

    public ContextEditorViewImpl() {
        this(null,
             null,
             null);
    }

    @Inject
    public ContextEditorViewImpl(final TranslationService ts,
                                 final SessionManager sessionManager,
                                 final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        super(ts,
              sessionManager,
              sessionCommandManager);
    }

    @DataField("dmn-table")
    @SuppressWarnings("unused")
    public GridLienzoPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public void init(final ContextEditorView.Editor presenter) {
        this.editor = presenter;
    }

    @Override
    public GridWidget makeGridWidget() {
        gridWidget = new DMNBaseGridWidget(new BaseGridData(),
                                           new BaseGridRenderer(new BlueTheme()),
                                           gridLayer);

        final GridData gridModel = gridWidget.getModel();
        final TextBoxSingletonDOMElementFactory factory = new TextBoxSingletonDOMElementFactory(gridPanel,
                                                                                                gridLayer,
                                                                                                gridWidget);

        gridModel.appendColumn(new RowNumberColumn());
        IntStream.range(0,
                        24).forEach(i -> gridModel.appendColumn(new StringDOMElementSingletonColumn(new BaseHeaderMetaData("col:" + i),
                                                                                                    factory,
                                                                                                    100.0)));
        IntStream.range(0,
                        25).forEach(i -> gridModel.appendRow(new BaseGridRow()));
        IntStream.range(0,
                        gridModel.getRowCount()).forEach(r -> gridModel.setCell(r,
                                                                                0,
                                                                                new BaseGridCellValue<>(r + 1)));

        return gridWidget;
    }

    @Override
    public BaseUIModelMapper<Context> makeUiModelMapper() {
        return new ContextUIModelMapper(() -> gridWidget.getModel(),
                                        () -> expression);
    }

    @Override
    public void setExpression(final Context expression) {
        this.expression = Optional.of(expression);

        getGridLayer().batch();
        getGridPanel().updatePanelSize(LIENZO_PANEL_WIDTH,
                                       LIENZO_PANEL_HEIGHT);
    }

    DefaultGridLayer getGridLayer() {
        return gridLayer;
    }

    Optional<Context> getExpression() {
        return expression;
    }
}

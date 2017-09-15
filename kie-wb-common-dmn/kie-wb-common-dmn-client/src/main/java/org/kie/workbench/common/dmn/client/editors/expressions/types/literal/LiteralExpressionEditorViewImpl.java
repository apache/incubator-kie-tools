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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.DMNBaseGridWidget;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.DMNColumnHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.TextAreaDOMElementSingletonColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.impl.BlueTheme;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

@Templated
@Dependent
public class LiteralExpressionEditorViewImpl extends BaseExpressionEditorViewImpl<LiteralExpressionEditorView.Editor, LiteralExpression> implements LiteralExpressionEditorView {

    public LiteralExpressionEditorViewImpl() {
        this(null,
             null,
             null);
    }

    @Inject
    public LiteralExpressionEditorViewImpl(final TranslationService ts,
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
    public void init(final LiteralExpressionEditorView.Editor presenter) {
        this.editor = presenter;
    }

    @Override
    public GridWidget makeGridWidget() {
        gridWidget = new DMNBaseGridWidget(new BaseGridData(),
                                           new BaseGridRenderer(new BlueTheme()),
                                           gridLayer);

        final GridData gridModel = gridWidget.getModel();
        final TextAreaSingletonDOMElementFactory factory = new TextAreaSingletonDOMElementFactory(gridPanel,
                                                                                                  gridLayer,
                                                                                                  gridWidget,
                                                                                                  sessionManager,
                                                                                                  sessionCommandManager,
                                                                                                  uiModelMapper);

        gridModel.appendColumn(new TextAreaDOMElementSingletonColumn(new DMNColumnHeaderMetaData(() -> hasName),
                                                                     factory,
                                                                     400.0));
        gridModel.appendRow(new BaseGridRow(200));

        return gridWidget;
    }

    @Override
    public BaseUIModelMapper<LiteralExpression> makeUiModelMapper() {
        return new LiteralExpressionUIModelMapper(() -> gridWidget.getModel(),
                                                  () -> expression);
    }

    @Override
    public void setExpression(final LiteralExpression expression) {
        this.expression = Optional.of(expression);
        uiModelMapper.fromDMNModel(0,
                                   0);
        gridLayer.batch();

        getGridPanel().updatePanelSize(LIENZO_PANEL_WIDTH,
                                       LIENZO_PANEL_HEIGHT);
    }

    //For Unit Tests
    DefaultGridLayer getGridLayer() {
        return gridLayer;
    }

    GridWidget getGridWidget() {
        return gridWidget;
    }
}

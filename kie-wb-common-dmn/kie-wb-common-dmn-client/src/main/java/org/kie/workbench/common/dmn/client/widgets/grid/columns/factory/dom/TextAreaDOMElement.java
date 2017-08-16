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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.function.Function;

import com.google.gwt.dom.client.Style;
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasGraphCommand;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class TextAreaDOMElement extends BaseDOMElement<String, TextArea> {

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Function<GridCellTuple, AbstractCanvasGraphCommand> hasNoValueCommand;
    private final Function<GridCellValueTuple, AbstractCanvasGraphCommand> hasValueCommand;

    public TextAreaDOMElement(final TextArea widget,
                              final GridLayer gridLayer,
                              final GridWidget gridWidget,
                              final SessionManager sessionManager,
                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final Function<GridCellTuple, AbstractCanvasGraphCommand> hasNoValueCommand,
                              final Function<GridCellValueTuple, AbstractCanvasGraphCommand> hasValueCommand) {
        super(widget,
              gridLayer,
              gridWidget);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.hasNoValueCommand = hasNoValueCommand;
        this.hasValueCommand = hasValueCommand;

        final Style style = widget.getElement().getStyle();
        style.setWidth(100,
                       Style.Unit.PCT);
        style.setHeight(100,
                        Style.Unit.PCT);
        style.setPaddingLeft(2,
                             Style.Unit.PX);
        style.setPaddingRight(2,
                              Style.Unit.PX);
        style.setPaddingTop(2,
                            Style.Unit.PX);
        style.setPaddingBottom(2,
                               Style.Unit.PX);
        style.setFontSize(10,
                          Style.Unit.PX);
        style.setProperty("resize",
                          "none");

        getContainer().getElement().getStyle().setPaddingLeft(5,
                                                              Style.Unit.PX);
        getContainer().getElement().getStyle().setPaddingRight(5,
                                                               Style.Unit.PX);
        getContainer().getElement().getStyle().setPaddingTop(5,
                                                             Style.Unit.PX);
        getContainer().getElement().getStyle().setPaddingBottom(5,
                                                                Style.Unit.PX);
        getContainer().setWidget(widget);
    }

    @Override
    public void initialise(final GridBodyCellRenderContext context) {
        transform(context);
    }

    @Override
    public void flush(final String value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        final GridData uiModel = gridWidget.getModel();

        if (value == null || value.trim().isEmpty()) {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          hasNoValueCommand.apply(new GridCellTuple(rowIndex,
                                                                                    columnIndex,
                                                                                    uiModel)));
        } else {
            sessionCommandManager.execute((AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler(),
                                          hasValueCommand.apply(new GridCellValueTuple<>(rowIndex,
                                                                                         columnIndex,
                                                                                         uiModel,
                                                                                         new BaseGridCellValue<>(value))));
        }
    }
}

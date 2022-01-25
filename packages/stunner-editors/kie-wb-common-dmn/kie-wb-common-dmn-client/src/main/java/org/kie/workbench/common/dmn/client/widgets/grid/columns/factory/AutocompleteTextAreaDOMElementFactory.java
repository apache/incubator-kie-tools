/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory;

import java.util.function.Function;

import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.MonacoEditorDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.MonacoEditorWidget;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.grid.keyboard.KeyDownHandlerCommon;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class AutocompleteTextAreaDOMElementFactory extends BaseSingletonDOMElementFactory<String, MonacoEditorWidget, MonacoEditorDOMElement> {

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Function<GridCellTuple, Command> hasNoValueCommand;
    private final Function<GridCellValueTuple, Command> hasValueCommand;

    public AutocompleteTextAreaDOMElementFactory(final DMNGridPanel gridPanel,
                                                 final GridLayer gridLayer,
                                                 final GridWidget gridWidget,
                                                 final SessionManager sessionManager,
                                                 final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                                 final Function<GridCellTuple, Command> hasNoValueCommand,
                                                 final Function<GridCellValueTuple, Command> hasValueCommand) {
        super(gridPanel,
              gridLayer,
              gridWidget);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.hasNoValueCommand = hasNoValueCommand;
        this.hasValueCommand = hasValueCommand;
    }

    public Function<GridCellTuple, Command> getHasNoValueCommand() {
        return hasNoValueCommand;
    }

    public Function<GridCellValueTuple, Command> getHasValueCommand() {
        return hasValueCommand;
    }

    @Override
    public MonacoEditorWidget createWidget() {
        return new MonacoEditorWidget();
    }

    @Override
    protected String getValue() {
        if (widget != null) {
            return widget.getValue();
        }
        return null;
    }

    @Override
    protected KeyDownHandlerCommon destroyOrFlushKeyDownHandler() {
        return new KeyDownHandlerCommon(gridPanel, gridLayer, gridWidget, this, true, false, true);
    }

    @Override
    protected MonacoEditorDOMElement createDomElementInternal(final MonacoEditorWidget widget,
                                                              final GridLayer gridLayer,
                                                              final GridWidget gridWidget) {

        final MonacoEditorDOMElement domElement = makeMonacoEditorDOMElement(widget, gridLayer, gridWidget);
        domElement.setupElements();
        return domElement;
    }

    protected MonacoEditorDOMElement makeMonacoEditorDOMElement(final MonacoEditorWidget widget,
                                                                final GridLayer gridLayer,
                                                                final GridWidget gridWidget) {
        return new MonacoEditorDOMElement(widget,
                                          gridLayer,
                                          gridWidget,
                                          sessionManager,
                                          sessionCommandManager,
                                          hasNoValueCommand,
                                          hasValueCommand);
    }
}

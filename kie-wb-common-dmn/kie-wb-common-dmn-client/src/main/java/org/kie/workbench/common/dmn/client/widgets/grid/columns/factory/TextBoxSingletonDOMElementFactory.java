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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory;

import java.util.function.Function;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellValueTuple;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class TextBoxSingletonDOMElementFactory extends BaseSingletonDOMElementFactory<String, TextBox, TextBoxDOMElement> {

    private final SessionManager sessionManager;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Function<GridCellTuple, Command> hasNoValueCommand;
    private final Function<GridCellValueTuple, Command> hasValueCommand;

    public TextBoxSingletonDOMElementFactory(final DMNGridPanel gridPanel,
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
    public TextBox createWidget() {
        return new TextBox() {{
            addKeyDownHandler(KeyDownEvent::stopPropagation);
            addMouseDownHandler(MouseDownEvent::stopPropagation);
        }};
    }

    @Override
    public TextBoxDOMElement createDomElement(final GridLayer gridLayer,
                                              final GridWidget gridWidget,
                                              final GridBodyCellRenderContext context) {
        this.widget = createWidget();
        this.e = new TextBoxDOMElement(widget,
                                       gridLayer,
                                       gridWidget,
                                       sessionManager,
                                       sessionCommandManager,
                                       hasNoValueCommand,
                                       hasValueCommand);
        widget.addBlurHandler((event) -> {
            destroyResources();
            gridPanel.setFocus(true);
        });
        return e;
    }

    @Override
    protected String getValue() {
        if (widget != null) {
            return widget.getValue();
        }
        return null;
    }
}

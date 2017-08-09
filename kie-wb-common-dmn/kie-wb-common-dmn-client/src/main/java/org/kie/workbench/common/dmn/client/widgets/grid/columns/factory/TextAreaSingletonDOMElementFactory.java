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

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseUIModelMapper;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl.BaseSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

public class TextAreaSingletonDOMElementFactory extends BaseSingletonDOMElementFactory<String, TextArea, TextAreaDOMElement> {

    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private BaseUIModelMapper<?> uiModelMapper;

    public TextAreaSingletonDOMElementFactory(final GridLienzoPanel gridPanel,
                                              final GridLayer gridLayer,
                                              final GridWidget gridWidget,
                                              final SessionManager sessionManager,
                                              final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                              final BaseUIModelMapper<?> uiModelMapper) {
        super(gridPanel,
              gridLayer,
              gridWidget);
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.uiModelMapper = uiModelMapper;
    }

    @Override
    public TextArea createWidget() {
        return new TextArea() {{
            addKeyDownHandler(KeyDownEvent::stopPropagation);
            addMouseDownHandler(MouseDownEvent::stopPropagation);
        }};
    }

    @Override
    public TextAreaDOMElement createDomElement(final GridLayer gridLayer,
                                               final GridWidget gridWidget,
                                               final GridBodyCellRenderContext context) {
        this.widget = createWidget();
        this.e = new TextAreaDOMElement(widget,
                                        gridLayer,
                                        gridWidget,
                                        sessionManager,
                                        sessionCommandManager,
                                        uiModelMapper);
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

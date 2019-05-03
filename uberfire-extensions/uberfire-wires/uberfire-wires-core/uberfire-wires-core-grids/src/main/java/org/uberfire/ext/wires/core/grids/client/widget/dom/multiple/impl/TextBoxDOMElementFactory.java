/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.dom.multiple.impl;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.TextBoxDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement Factory for multi-instance TextBoxes.
 */
public class TextBoxDOMElementFactory extends BaseDOMElementFactory<String, TextBox, TextBoxDOMElement> {

    public TextBoxDOMElementFactory(final GridLayer gridLayer,
                                    final GridWidget gridWidget) {
        super(gridLayer,
              gridWidget);
    }

    @Override
    public TextBox createWidget() {
        return new TextBox();
    }

    @Override
    public TextBoxDOMElement createDomElement(final GridLayer gridLayer,
                                              final GridWidget gridWidget) {
        final TextBox widget = createWidget();
        final TextBoxDOMElement e = new TextBoxDOMElement(widget,
                                                          gridLayer,
                                                          gridWidget);
        registerHandlers(widget, e);
        return e;
    }

    @Override
    public void registerHandlers(final TextBox widget, final TextBoxDOMElement widgetDomElement) {
        widget.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(final BlurEvent event) {
                widgetDomElement.flush(widget.getValue());
                gridLayer.batch();
            }
        });
    }
}

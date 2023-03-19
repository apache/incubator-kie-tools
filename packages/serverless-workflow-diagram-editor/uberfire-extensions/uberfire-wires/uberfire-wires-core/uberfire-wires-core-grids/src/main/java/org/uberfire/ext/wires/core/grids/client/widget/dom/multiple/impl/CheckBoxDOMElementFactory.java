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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.CheckBoxDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement Factory for multi-instance CheckBoxes.
 */
public class CheckBoxDOMElementFactory extends BaseDOMElementFactory<Boolean, CheckBox, CheckBoxDOMElement> {

    public CheckBoxDOMElementFactory(final GridLayer gridLayer,
                                     final GridWidget gridWidget) {
        super(gridLayer,
              gridWidget);
    }

    @Override
    public CheckBox createWidget() {
        return new CheckBox();
    }

    @Override
    public CheckBoxDOMElement createDomElement(final GridLayer gridLayer,
                                               final GridWidget gridWidget) {
        final CheckBox widget = createWidget();
        final CheckBoxDOMElement e = new CheckBoxDOMElement(widget,
                                                            gridLayer,
                                                            gridWidget);
        registerHandlers(widget, e);
        return e;
    }

    @Override
    public void registerHandlers(final CheckBox widget, final CheckBoxDOMElement widgetDomElement) {
        widget.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                widgetDomElement.flush(widget.getValue());
                gridLayer.batch();
            }
        });
    }
}

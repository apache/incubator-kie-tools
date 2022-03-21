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
package org.uberfire.ext.wires.core.grids.client.widget.dom.impl;

import com.google.gwt.dom.client.Style;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

/**
 * A DOMElement for CheckBoxes.
 */
public class CheckBoxDOMElement extends BaseDOMElement<Boolean, CheckBox> {

    //Hack to centre CheckBox
    private static final int SIZE = 20;

    public CheckBoxDOMElement(final CheckBox widget,
                              final GridLayer gridLayer,
                              final GridWidget gridWidget) {
        super(widget,
              gridLayer,
              gridWidget);
        final Style style = widget.getElement().getStyle();
        style.setMarginTop(0,
                           Style.Unit.PX);
        style.setMarginLeft(2,
                            Style.Unit.PX);
        style.setWidth(SIZE,
                       Style.Unit.PX);
        style.setHeight(SIZE,
                        Style.Unit.PX);

        // --- Workaround for BS2 ---
        style.setPosition(Style.Position.RELATIVE);
        style.setPaddingTop(0,
                            Style.Unit.PX);
        style.setPaddingBottom(0,
                               Style.Unit.PX);
        style.setProperty("WebkitBoxSizing",
                          "border-box");
        style.setProperty("MozBoxSizing",
                          "border-box");
        style.setProperty("boxSizing",
                          "border-box");
        style.setProperty("lineHeight",
                          "normal");
        // --- End workaround ---

        getContainer().setWidget(widget);
    }

    @Override
    public void initialise(final GridBodyCellRenderContext context) {
        final Style style = widget.getElement().getStyle();
        style.setLeft((context.getCellWidth() - SIZE) / 2,
                      Style.Unit.PX);
        style.setTop((context.getCellHeight() - SIZE) / 2,
                     Style.Unit.PX);
        transform(context);
    }

    @Override
    public void flush(final Boolean value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        gridWidget.getModel().setCellValue(rowIndex,
                                           columnIndex,
                                           new BaseGridCellValue<Boolean>(value));
    }
}

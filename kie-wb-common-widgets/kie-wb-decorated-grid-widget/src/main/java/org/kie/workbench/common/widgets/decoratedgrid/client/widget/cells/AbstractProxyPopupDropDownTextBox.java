/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.soup.project.datamodel.oracle.DropDownData;

/**
 * A Popup drop-down Editor ;-)
 */
public abstract class AbstractProxyPopupDropDownTextBox<C> implements ProxyPopupDropDown<C> {

    private final TextBox textBox;

    public AbstractProxyPopupDropDownTextBox(final TextBox textBox,
                                             final AbstractProxyPopupDropDownEditCell proxy) {

        this.textBox = textBox;

        // Tabbing out of the ListBox commits changes
        textBox.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
                boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
                if (keyEnter || keyTab) {
                    proxy.commit();
                }
            }
        });
    }

    @Override
    public void render(final Cell.Context context,
                       final C value,
                       final SafeHtmlBuilder sb,
                       final SafeHtmlRenderer<String> renderer) {
        //Render value
        if (value != null) {
            sb.append(renderer.render((value == null ? "" : convertToString(value))));
        }
    }

    @Override
    public void setValue(final C value) {
        textBox.setValue((value == null ? "" : convertToString(value)));
    }

    @Override
    public void setDropDownData(final DropDownData dd) {
        throw new UnsupportedOperationException("Only single values are supported");
    }

    // Commit the change
    @Override
    public C getValue() {
        final String value = textBox.getValue();
        if (value.length() == 0) {
            return null;
        }

        return convertFromString(value);
    }

    // Start editing the cell
    @Override
    public void startEditing(final Cell.Context context,
                             final Element parent,
                             final C value) {
        textBox.setValue((value == null ? "" : convertToString(value)));
    }

    @Override
    public void setFocus(final boolean focused) {
        textBox.setFocus(focused);
    }

    @Override
    public Widget asWidget() {
        return textBox;
    }
}

/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.soup.commons.util.ListSplitter;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.kie.workbench.common.widgets.client.widget.EnumDropDownUtilities;

/**
 * A Popup drop-down Editor for use within AbstractProxyPopupDropDownEditCell
 */
public abstract class AbstractProxyPopupDropDownListBox<C> implements ProxyPopupDropDown<C> {

    final EnumDropDownUtilities utilities = new EnumDropDownUtilities() {
        @Override
        protected int addItems(final ListBox listBox) {
            return 0;
        }

        @Override
        protected void selectItem(final ListBox listBox) {
            //Nothing needed by default
        }
    };
    private final ListBox listBox;
    private final AbstractProxyPopupDropDownEditCell proxy;
    private String[][] items;

    public AbstractProxyPopupDropDownListBox(final AbstractProxyPopupDropDownEditCell proxy,
                                             final String operator) {

        this.listBox = GWT.create(ListBox.class);// new ListBox();
        this.listBox.setMultipleSelect(OperatorsOracle.operatorRequiresList(operator));

        this.proxy = proxy;

        // Tabbing out of the ListBox commits changes
        listBox.addKeyDownHandler(new KeyDownHandler() {

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
            final String label = makeLabel(convertToString(value));

            sb.append(renderer.render(label));
        }
    }

    private String makeLabel(final String convert) {
        if (listBox.isMultipleSelect()) {
            String[] array = ListSplitter.splitPreserveQuotes("\"",
                                                              true,
                                                              convert);
            return Arrays.stream(array)
                    .map(s -> getLabel(s))
                    .collect(Collectors.joining(","));
        } else {
            return getLabel(convert);
        }
    }

    @Override
    public void setDropDownData(final DropDownData dd) {
        utilities.setDropDownData("",
                                  dd,
                                  false,
                                  proxy.getDataModelOracle().getResourcePath(),
                                  listBox);

        //Scrape values from ListBox as they may have been populated from a server-side call
        final int itemCount = listBox.getItemCount();
        this.items = new String[itemCount][2];
        for (int i = 0; i < itemCount; i++) {
            String value = listBox.getValue(i).trim();
            String text = listBox.getItemText(i).trim();
            this.items[i][0] = value;
            this.items[i][1] = text;
        }
    }

    // Lookup the display text based on the value
    private String getLabel(final String value) {
        for (int i = 0; i < this.items.length; i++) {
            if (this.items[i][0].equals(value)) {
                return items[i][1];
            }
        }
        return value;
    }

    // Commit the change
    @Override
    public C getValue() {

        // Update value
        String value = null;
        if (listBox.isMultipleSelect()) {
            for (int i = 0; i < listBox.getItemCount(); i++) {
                if (listBox.isItemSelected(i)) {
                    if (value == null) {
                        value = listBox.getValue(i);
                    } else {
                        value = value + "," + listBox.getValue(i);
                    }
                }
            }
        } else {
            int selectedIndex = listBox.getSelectedIndex();
            if (selectedIndex >= 0) {
                value = listBox.getValue(selectedIndex);
            }
        }

        return convertFromString(value);
    }

    @Override
    public void setValue(final C value) {
        final String[] convertedValues = new String[]{convertToString(value)};
        setDropDownData(DropDownData.create(convertedValues));
    }

    // Start editing the cell
    @Override
    public void startEditing(final Cell.Context context,
                             final Element parent,
                             final C value) {
        // Select the appropriate item
        boolean emptyValue = (value == null);
        if (emptyValue) {
            listBox.setSelectedIndex(0);
        } else {
            final String convertedValue = convertToString(value);
            if (listBox.isMultipleSelect()) {
                final List<String> convertedValues = Arrays.asList(convertedValue.split(","));
                for (int i = 0; i < listBox.getItemCount(); i++) {
                    listBox.setItemSelected(i,
                                            convertedValues.contains(listBox.getValue(i)));
                }
            } else {
                for (int i = 0; i < listBox.getItemCount(); i++) {
                    if (listBox.getValue(i).equals(convertedValue)) {
                        listBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setFocus(final boolean focused) {
        listBox.setFocus(focused);
    }

    @Override
    public Widget asWidget() {
        return listBox;
    }
}

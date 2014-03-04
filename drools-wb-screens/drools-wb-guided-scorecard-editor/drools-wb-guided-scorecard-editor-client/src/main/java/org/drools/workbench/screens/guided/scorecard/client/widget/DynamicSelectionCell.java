/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.scorecard.client.widget;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link com.google.gwt.cell.client.Cell} used to render a drop-down list.
 */
public class DynamicSelectionCell extends AbstractInputCell<String, String> {

    interface Template extends SafeHtmlTemplates {

        @Template("<option value=\"{0}\">{0}</option>")
        SafeHtml deselected(String option);

        @Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
        SafeHtml selected(String option);
    }

    private static Template template = GWT.create(Template.class);

    private HashMap<String, Integer> indexForOption = new HashMap<String, Integer>();

    private final List<String> options;

    /**
     * Construct a new {@link com.google.gwt.cell.client.SelectionCell} with the specified options.
     *
     * @param options the options in the cell
     */
    public DynamicSelectionCell(final List<String> options) {
        super("change");
        this.options = new ArrayList<String>(options);
        int index = 0;
        for (String option : options) {
            indexForOption.put(option,
                    index++);
        }
    }

    public void addOption(final String newOp) {
        options.add(newOp);
        refreshIndexes();
    }

    public void setOptions(final List<String> newOptions) {
        options.clear();
        indexForOption.clear();
        options.addAll(newOptions);
        int index = 0;
        for (String option : options) {
            indexForOption.put(option,
                    index++);
        }
        refreshIndexes();
    }

    public void removeOption(final String op) {
        options.remove(op);
        refreshIndexes();
    }

    private void refreshIndexes() {
        int index = 0;
        for (String option : options) {
            indexForOption.put(option,
                    index++);
        }
    }

    @Override
    public void onBrowserEvent(final Context context,
            final Element parent,
            final String value,
            final NativeEvent event,
            final ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context,
                parent,
                value,
                event,
                valueUpdater);
        final String type = event.getType();
        if ("change".equals(type)) {
            final Object key = context.getKey();
            final SelectElement select = parent.getFirstChild().cast();
            final String newValue = options.get(select.getSelectedIndex());
            setViewData(key, newValue);
            finishEditing(parent,
                    newValue,
                    key,
                    valueUpdater);
            if (valueUpdater != null) {
                valueUpdater.update(newValue);
            }
        }
    }

    @Override
    public void render(final Context context,
            final String value,
            final SafeHtmlBuilder sb) {
        // Get the view data.
        final Object key = context.getKey();
        String viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        final int selectedIndex = getSelectedIndex(viewData == null ? value : viewData);
        sb.appendHtmlConstant("<select tabindex=\"-1\">");
        int index = 0;
        for (String option : options) {
            if (index++ == selectedIndex) {
                sb.append(template.selected(option));
            } else {
                sb.append(template.deselected(option));
            }
        }
        sb.appendHtmlConstant("</select>");
    }

    private int getSelectedIndex(final String value) {
        final Integer index = indexForOption.get(value);
        if (index == null) {
            return -1;
        }
        return index.intValue();
    }

}
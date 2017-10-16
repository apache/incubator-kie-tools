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

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.soup.project.datamodel.oracle.DropDownData;

/**
 * Definition of Cell that can be used by AbstractProxyPopupDropDownEditCell.
 */
public interface ProxyPopupDropDown<C> extends IsWidget {

    /**
     * Set value for cell
     *
     * @param value
     */
    void setValue(final C value);

    /**
     * Set values for cell
     *
     * @param dd
     */
    void setDropDownData(final DropDownData dd);

    /**
     * Render value as safe HTML
     *
     * @param context
     * @param value
     * @param sb
     * @param renderer
     */
    void render(final Cell.Context context,
                final C value,
                final SafeHtmlBuilder sb,
                final SafeHtmlRenderer<String> renderer);

    /**
     * Return the new value entered within the cell
     *
     * @return new value
     */
    C getValue();

    /**
     * Initiate editing within the "Popup". Implementations should populate the
     * child controls within the "Popup" before showing the Popup
     * <code>panel</code>
     *
     * @param context
     * @param parent
     * @param value
     */
    void startEditing(final Cell.Context context,
                      final Element parent,
                      final C value);

    /**
     * Set focus to widget
     *
     * @param focused
     */
    void setFocus(final boolean focused);

    String convertToString(final C value);

    C convertFromString(final String value);
}

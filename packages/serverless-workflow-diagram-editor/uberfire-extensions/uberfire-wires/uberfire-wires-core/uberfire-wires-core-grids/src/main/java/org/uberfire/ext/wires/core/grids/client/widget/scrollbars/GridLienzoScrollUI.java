/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/*
 * Applies the scrollbar style in the Grid Lienzo panels.
 */

class GridLienzoScrollUI {

    private final GridLienzoScrollHandler gridLienzoScrollHandler;

    GridLienzoScrollUI(final GridLienzoScrollHandler gridLienzoScrollHandler) {
        this.gridLienzoScrollHandler = gridLienzoScrollHandler;
    }

    void setup() {
        applyScrollPanelStyle();
        applyInternalScrollPanelStyle();
        applyDomElementContainerStyle();
    }

    void applyScrollPanelStyle() {
        style(getScrollPanel()).setPosition(Style.Position.RELATIVE);
        style(getScrollPanel()).setOverflow(Style.Overflow.SCROLL);
    }

    void applyInternalScrollPanelStyle() {
        style(getInternalScrollPanel()).setPosition(Style.Position.ABSOLUTE);
    }

    void applyDomElementContainerStyle() {
        style(getDomElementContainer()).setPosition(Style.Position.ABSOLUTE);
        style(getDomElementContainer()).setZIndex(1);
    }

    private AbsolutePanel getScrollPanel() {
        return gridLienzoScrollHandler.getScrollPanel();
    }

    private AbsolutePanel getInternalScrollPanel() {
        return gridLienzoScrollHandler.getInternalScrollPanel();
    }

    private AbsolutePanel getDomElementContainer() {
        return gridLienzoScrollHandler.getDomElementContainer();
    }

    void enablePointerEvents(final Widget widget) {
        setPointerEvents(widget, "initial");
    }

    void disablePointerEvents(final Widget widget) {
        setPointerEvents(widget, "none");
    }

    void setPointerEvents(final Widget widget,
                          final String value) {
        style(widget).setProperty("pointerEvents", value);
    }

    Style style(final Widget widget) {
        return widget.getElement().getStyle();
    }
}

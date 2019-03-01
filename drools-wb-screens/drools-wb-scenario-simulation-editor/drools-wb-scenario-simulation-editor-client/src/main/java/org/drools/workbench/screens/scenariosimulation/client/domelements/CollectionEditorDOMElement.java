/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.domelements;

import java.util.Objects;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.workbench.screens.scenariosimulation.client.collectioneditor.CollectionViewImpl;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class CollectionEditorDOMElement extends BaseDOMElement<String, CollectionViewImpl> implements TakesValue<String>,
                                                                                                            Focusable {

    protected ScenarioGridCell scenarioGridCell;


    /**
     *
     * @param widget
     * @param gridLayer
     * @param gridWidget
     */
    public CollectionEditorDOMElement(final CollectionViewImpl widget,
                                      final GridLayer gridLayer,
                                      final GridWidget gridWidget) {
        super(widget,
              gridLayer,
              gridWidget);
        final Style style = widget.getElement().getStyle();
        style.setWidth(100,
                       Style.Unit.PCT);
        style.setHeight(100,
                        Style.Unit.PCT);
        style.setPaddingLeft(2,
                             Style.Unit.PX);
        style.setPaddingRight(2,
                              Style.Unit.PX);
        style.setPaddingTop(2,
                            Style.Unit.PX);
        style.setPaddingBottom(2,
                               Style.Unit.PX);
        style.setFontSize(10,
                          Style.Unit.PX);
        style.setProperty("resize",
                          "none");

        final SimplePanel widgetContainer = getContainer();
        final Element widgetContainerElement = widgetContainer.getElement();
        final Style widgetContainerElementStyle = widgetContainerElement.getStyle();

        widgetContainerElementStyle.setPaddingLeft(5,
                                                         Style.Unit.PX);
        widgetContainerElementStyle.setPaddingRight(5,
                                                          Style.Unit.PX);
        widgetContainerElementStyle.setPaddingTop(5,
                                                        Style.Unit.PX);
        widgetContainerElementStyle.setPaddingBottom(5,
                                                           Style.Unit.PX);

        widgetContainer.setWidget(widget);
    }

    public void setScenarioGridCell(ScenarioGridCell scenarioGridCell) {
        this.scenarioGridCell = scenarioGridCell;
    }

    public void stopEditingMode() {
        if (scenarioGridCell != null) {
            scenarioGridCell.setEditingMode(false);
        }
    }

    @Override
    public void initialise(final GridBodyCellRenderContext context) {
        transform(context);
        final Bounds visibleBounds = gridLayer.getVisibleBounds();
        final double shownWidth = visibleBounds.getWidth();
        final double widgetWidth = (shownWidth * 0.5);
        final double widgetLeft = ((shownWidth - widgetWidth) / 2);
        widgetContainer.getElement().getStyle().setWidth(widgetWidth, Style.Unit.PX);
        widgetContainer.getElement().getStyle().setLeft(widgetLeft, Style.Unit.PX);
        final double shownHeight = visibleBounds.getHeight();
        final double widgetHeight = (shownHeight * 0.5);
        widget.setFixedHeight(widgetHeight, Style.Unit.PX);
        widgetContainer.getElement().getStyle().setTop(0, Style.Unit.PX);
        // Verify Collection editor is always shown on center of the widget, even when clicked cell left margin is outside of the grid
        widgetContainer.getElement().getStyle().clearProperty("clip");
    }

    @Override
    public void setValue(final String value) {
        getWidget().setValue(value);
    }

    @Override
    public String getValue() {
        return getWidget().getValue();
    }

    @Override
    public int getTabIndex() {
        return getWidget().getTabIndex();
    }

    @Override
    public void setAccessKey(final char key) {
        getWidget().setAccessKey(key);
    }

    @Override
    public void setFocus(final boolean focused) {
        getWidget().setFocus(focused);
    }

    @Override
    public void setTabIndex(final int index) {
        getWidget().setTabIndex(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void flush(final String value) {
        if (scenarioGridCell != null) {
            scenarioGridCell.setEditingMode(false);
            String actualValue = (value == null || value.isEmpty()) ? null : value;
            String cellValue = scenarioGridCell.getValue().getValue();
            String originalValue = (cellValue == null || cellValue.isEmpty()) ? null : cellValue;
            if (Objects.equals(actualValue, originalValue)) {
                return;
            }
        }
        internalFlush(value);
    }

    protected void internalFlush(final String value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        ((ScenarioGrid) gridWidget).getEventBus().fireEvent(new SetGridCellValueEvent(rowIndex, columnIndex, value));
    }
}

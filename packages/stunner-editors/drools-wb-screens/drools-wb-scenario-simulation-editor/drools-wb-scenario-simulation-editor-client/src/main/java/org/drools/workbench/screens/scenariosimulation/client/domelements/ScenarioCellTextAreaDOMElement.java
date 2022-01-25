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
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Focusable;
import org.drools.workbench.screens.scenariosimulation.client.events.SetGridCellValueEvent;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridCell;
import org.gwtbootstrap3.client.ui.TextArea;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

public class ScenarioCellTextAreaDOMElement extends BaseDOMElement<String, TextArea> implements TakesValue<String>,
                                                                                                Focusable {

    protected ScenarioGridCell scenarioGridCell;

    public ScenarioCellTextAreaDOMElement(final TextArea widget,
                                          final GridLayer gridLayer,
                                          final GridWidget gridWidget) {
        super(widget,
              gridLayer,
              gridWidget);

        init();
    }

    public void init() {
        final Style style = widget.getElement().getStyle();
        style.setWidth(100, Style.Unit.PCT);
        style.setHeight(100, Style.Unit.PCT);
        style.setFontSize(12, Style.Unit.PX);
        style.setProperty("resize", "none");

        getContainer().setWidget(widget);
    }

    public void setScenarioGridCell(ScenarioGridCell scenarioGridCell) {
        this.scenarioGridCell = scenarioGridCell;
    }

    @Override
    public void initialise(final GridBodyCellRenderContext context) {
        transform(context);
    }

    @Override
    public String getValue() {
        return getWidget().getValue();
    }

    @Override
    public void setValue(final String value) {
        getWidget().setValue(value);
    }

    @Override
    public int getTabIndex() {
        return getWidget().getTabIndex();
    }

    @Override
    public void setTabIndex(final int index) {
        getWidget().setTabIndex(index);
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
    public void flush(final String value) {
        String actualValue = value != null && value.isEmpty() ? null : value;
        if (scenarioGridCell != null) {
            scenarioGridCell.setEditingMode(false);
            String cellValue = scenarioGridCell.getValue().getValue();
            if (Objects.equals(actualValue, cellValue)) {
                return;
            }
        }
        internalFlush(actualValue);
    }

    protected void internalFlush(final String value) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        ((ScenarioGrid) gridWidget).getEventBus().fireEvent(new SetGridCellValueEvent(((ScenarioGrid) gridWidget).getGridWidget(), rowIndex, columnIndex, value));
    }

    @Override
    public void detach() {
        super.detach();
        if (scenarioGridCell != null) {
            scenarioGridCell.setEditingMode(false);
        }
    }
}

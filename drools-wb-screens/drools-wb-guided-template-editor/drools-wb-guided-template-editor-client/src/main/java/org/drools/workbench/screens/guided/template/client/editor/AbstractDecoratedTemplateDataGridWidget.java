/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.template.client.editor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Panel;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.client.editor.events.SetInternalTemplateDataModelEvent;
import org.drools.workbench.screens.guided.template.client.editor.events.SetTemplateDataEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractDecoratedGridHeaderWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractDecoratedGridSidebarWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractDecoratedGridWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractMergableGridWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DynamicColumn;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.ResourcesProvider;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetModelEvent;

/**
 * A Decorated Grid for Template Data
 */
public abstract class AbstractDecoratedTemplateDataGridWidget
        extends AbstractDecoratedGridWidget<TemplateModel, TemplateDataColumn, String> {

    //Factories to create new data elements
    protected final TemplateDataCellFactory cellFactory;
    protected final TemplateDataCellValueFactory cellValueFactory;

    public AbstractDecoratedTemplateDataGridWidget(ResourcesProvider<TemplateDataColumn> resources,
                                                   TemplateDataCellFactory cellFactory,
                                                   TemplateDataCellValueFactory cellValueFactory,
                                                   EventBus eventBus,
                                                   Panel mainPanel,
                                                   Panel bodyPanel,
                                                   AbstractMergableGridWidget<TemplateModel, TemplateDataColumn> gridWidget,
                                                   AbstractDecoratedGridHeaderWidget<TemplateModel, TemplateDataColumn> headerWidget,
                                                   AbstractDecoratedGridSidebarWidget<TemplateModel, TemplateDataColumn> sidebarWidget) {
        super(resources,
              eventBus,
              mainPanel,
              bodyPanel,
              gridWidget,
              headerWidget,
              sidebarWidget);
        if (cellFactory == null) {
            throw new IllegalArgumentException("cellFactory cannot be null");
        }
        if (cellValueFactory == null) {
            throw new IllegalArgumentException("cellValueFactory cannot be null");
        }
        this.cellFactory = cellFactory;
        this.cellValueFactory = cellValueFactory;

        //Wire-up event handlers
        eventBus.addHandler(SetTemplateDataEvent.TYPE,
                            this);
    }

    public void onSetModel(SetModelEvent<TemplateModel> event) {

        DynamicData data = new DynamicData();
        TemplateModel model = event.getModel();
        List<DynamicColumn<TemplateDataColumn>> columns = new ArrayList<DynamicColumn<TemplateDataColumn>>();

        setupInternalModel(model,
                           columns,
                           data);

        //Raise event setting data and columns for UI components
        SetInternalTemplateDataModelEvent sime = new SetInternalTemplateDataModelEvent(model,
                                                                                       data,
                                                                                       columns);
        eventBus.fireEvent(sime);
    }

    private void setupInternalModel(TemplateModel model,
                                    List<DynamicColumn<TemplateDataColumn>> columns,
                                    DynamicData data) {

        //Get interpolation variables
        InterpolationVariable[] vars = model.getInterpolationVariablesList();
        if (vars.length == 0) {
            return;
        }

        int colIndex = 0;
        String[][] modelData = model.getTableAsArray();

        // Dummy rows because the underlying DecoratedGridWidget expects there
        // to be enough rows to receive the columns data
        for (int iRow = 0; iRow < modelData.length; iRow++) {
            data.addRow();
        }

        //Add corresponding columns to table
        for (InterpolationVariable var : vars) {
            TemplateDataColumn col = new TemplateDataColumn(var.getVarName(),
                                                            var.getDataType(),
                                                            var.getFactType(),
                                                            var.getFactField(),
                                                            var.getOperator());

            DynamicColumn<TemplateDataColumn> column = new DynamicColumn<TemplateDataColumn>(col,
                                                                                             cellFactory.getCell(col),
                                                                                             colIndex,
                                                                                             eventBus);
            columns.add(column);

            data.addColumn(colIndex,
                           makeColumnData(modelData,
                                          col,
                                          colIndex++),
                           true);
        }
    }

    // Make a row of data for insertion into a DecoratedGridWidget
    private List<CellValue<? extends Comparable<?>>> makeColumnData(String[][] data,
                                                                    TemplateDataColumn column,
                                                                    int colIndex) {
        int dataSize = data.length;
        List<CellValue<? extends Comparable<?>>> columnData = new ArrayList<CellValue<? extends Comparable<?>>>(dataSize);

        for (int iRow = 0; iRow < dataSize; iRow++) {
            String[] row = data[iRow];
            //Underlying Template model uses empty Strings as null values; which is quite different in the MergedGrid world
            String initialValue = row[colIndex];
            if (initialValue != null && initialValue.equals("")) {
                initialValue = null;
            }
            CellValue<? extends Comparable<?>> cv = cellValueFactory.convertModelCellValue(column,
                                                                                           initialValue);
            columnData.add(cv);
        }
        return columnData;
    }
}

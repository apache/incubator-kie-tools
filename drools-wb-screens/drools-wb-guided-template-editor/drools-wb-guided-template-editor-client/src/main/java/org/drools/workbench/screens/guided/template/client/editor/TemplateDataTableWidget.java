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
import java.util.Map;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.rule.client.util.GWTDateConverter;
import org.drools.workbench.screens.guided.template.client.editor.events.SetTemplateDataEvent;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractDecoratedGridWidget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.ResourcesProvider;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.CopyRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.PasteRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateModelEvent;

/**
 * A table in which Template data can be edited
 */
public class TemplateDataTableWidget extends Composite
        implements
        InsertRowEvent.Handler,
        DeleteRowEvent.Handler,
        AppendRowEvent.Handler,
        CopyRowsEvent.Handler,
        PasteRowsEvent.Handler,
        UpdateModelEvent.Handler {

    protected static final ResourcesProvider<TemplateDataColumn> resources = new TemplateDataTableResourcesProvider();
    // Decision Table data
    protected TemplateModel model;
    protected AbstractDecoratedGridWidget<TemplateModel, TemplateDataColumn, String> widget;
    protected TemplateDataCellFactory cellFactory;
    protected TemplateDataCellValueFactory cellValueFactory;
    protected TemplateDropDownManager dropDownManager;
    //This EventBus is local to the screen and should be used for local operations, set data, add rows etc
    private EventBus eventBus = new SimpleEventBus();
    //Rows that have been copied in a copy-paste operation
    private List<String[]> copiedRows = new ArrayList<String[]>();

    /**
     * Constructor
     */
    public TemplateDataTableWidget(TemplateModel model,
                                   AsyncPackageDataModelOracle oracle,
                                   boolean isReadOnly,
                                   EventBus globalEventBus) {
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }
        if (oracle == null) {
            throw new IllegalArgumentException("oracle cannot be null");
        }
        if (globalEventBus == null) {
            throw new IllegalArgumentException("globalEventBus cannot be null");
        }
        this.model = model;

        //Setup the DropDownManager that requires the Model and UI data to determine drop-down lists 
        //for dependent enumerations. This needs to be called before the columns are created.
        this.dropDownManager = new TemplateDropDownManager(model,
                                                           oracle);

        //Factories for new cell elements
        this.cellFactory = new TemplateDataCellFactory(oracle,
                                                       dropDownManager,
                                                       isReadOnly,
                                                       eventBus);
        this.cellValueFactory = new TemplateDataCellValueFactory(model,
                                                                 oracle);

        // Construct the widget from which we're composed
        widget = new VerticalDecoratedTemplateDataGridWidget(resources,
                                                             cellFactory,
                                                             cellValueFactory,
                                                             dropDownManager,
                                                             isReadOnly,
                                                             eventBus);

        //Date converter is injected so a GWT compatible one can be used here and another in testing
        TemplateDataCellValueFactory.injectDateConvertor(GWTDateConverter.getInstance());

        //Wire-up event handlers
        eventBus.addHandler(DeleteRowEvent.TYPE,
                            this);
        eventBus.addHandler(InsertRowEvent.TYPE,
                            this);
        eventBus.addHandler(AppendRowEvent.TYPE,
                            this);
        eventBus.addHandler(CopyRowsEvent.TYPE,
                            this);
        eventBus.addHandler(PasteRowsEvent.TYPE,
                            this);
        eventBus.addHandler(UpdateModelEvent.TYPE,
                            this);

        initWidget(widget);

        //Fire event for UI components to set themselves up
        SetTemplateDataEvent sme = new SetTemplateDataEvent(model);
        eventBus.fireEvent(sme);
    }

    public void appendRow() {
        AppendRowEvent are = new AppendRowEvent();
        eventBus.fireEvent(are);
    }

    /**
     * Ensure the wrapped DecoratedGridWidget's size is set too
     */
    @Override
    public void setPixelSize(int width,
                             int height) {
        if (width < 0) {
            throw new IllegalArgumentException("width cannot be less than zero");
        }
        if (height < 0) {
            throw new IllegalArgumentException("height cannot be less than zero");
        }
        super.setPixelSize(width,
                           height);
        widget.setPixelSize(width,
                            height);
    }

    @Override
    public void onDeleteRow(DeleteRowEvent event) {
        model.removeRow(event.getIndex());
    }

    @Override
    public void onCopyRows(CopyRowsEvent event) {
        copiedRows.clear();
        for (Integer iRow : event.getRowIndexes()) {
            String[] rowData = model.getTableAsArray()[iRow];
            copiedRows.add(rowData);
        }
    }

    @Override
    public void onPasteRows(PasteRowsEvent event) {
        if (copiedRows == null || copiedRows.size() == 0) {
            return;
        }
        int iRow = event.getTargetRowIndex();
        for (String[] sourceRowData : copiedRows) {
            String[] rowData = cellValueFactory.makeRowData().toArray(new String[0]);
            for (int iCol = 0; iCol < sourceRowData.length; iCol++) {
                rowData[iCol] = sourceRowData[iCol];
            }
            model.addRow(iRow,
                         rowData);
            iRow++;
        }
    }

    @Override
    public void onInsertRow(InsertRowEvent event) {
        List<String> data = cellValueFactory.makeRowData();
        model.addRow(event.getIndex(),
                     data.toArray(new String[data.size()]));
    }

    @Override
    public void onAppendRow(AppendRowEvent event) {
        List<String> data = cellValueFactory.makeRowData();
        model.addRow(data.toArray(new String[data.size()]));
    }

    @Override
    public void onUpdateModel(UpdateModelEvent event) {

        //Copy data into the underlying model
        Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates = event.getUpdates();
        for (Map.Entry<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> e : updates.entrySet()) {

            //Coordinate of change
            Coordinate originCoordinate = e.getKey();
            int originRowIndex = originCoordinate.getRow();
            int originColumnIndex = originCoordinate.getCol();

            //Changed data
            List<List<CellValue<? extends Comparable<?>>>> data = e.getValue();

            InterpolationVariable[] vars = model.getInterpolationVariablesList();

            for (int iRow = 0; iRow < data.size(); iRow++) {
                List<CellValue<? extends Comparable<?>>> rowData = data.get(iRow);
                int targetRowIndex = originRowIndex + iRow;
                for (int iCol = 0; iCol < rowData.size(); iCol++) {
                    int targetColumnIndex = originColumnIndex + iCol;
                    CellValue<? extends Comparable<?>> changedCell = rowData.get(iCol);

                    InterpolationVariable var = vars[targetColumnIndex];

                    TemplateDataColumn col = new TemplateDataColumn(var.getVarName(),
                                                                    var.getDataType(),
                                                                    var.getFactType(),
                                                                    var.getFactField(),
                                                                    var.getOperator());

                    String dcv = cellValueFactory.convertToModelCell(col,
                                                                     changedCell);

                    List<String> columnData = model.getTable().get(var.getVarName());
                    columnData.set(targetRowIndex,
                                   dcv);
                }
            }
        }
    }
}

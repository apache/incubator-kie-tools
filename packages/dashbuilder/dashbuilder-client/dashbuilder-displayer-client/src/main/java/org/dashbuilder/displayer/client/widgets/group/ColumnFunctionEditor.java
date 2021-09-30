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
package org.dashbuilder.displayer.client.widgets.group;

import java.util.List;
import java.util.ArrayList;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.client.events.GroupFunctionChangedEvent;
import org.dashbuilder.displayer.client.events.GroupFunctionDeletedEvent;
import org.uberfire.client.mvp.UberView;

@Dependent
public class ColumnFunctionEditor implements IsWidget {

    public interface View extends UberView<ColumnFunctionEditor> {

        void setDeleteOptionEnabled(boolean enabled);

        void setColumnSelectorTitle(String title);

        void clearColumnSelector();

        void addColumnItem(String columnId);

        void setSelectedColumnIndex(int i);

        String getSelectedColumnId();

        void setFunctionSelectorEnabled(boolean enabled);

        void clearFunctionSelector();

        void setVoidFunctionEnabled(boolean enabled);

        void addFunctionItem(AggregateFunctionType functionType);

        void setSelectedFunctionIndex(int i);

        int getSelectedFunctionIndex();
     }

    View view = null;
    GroupFunction groupFunction = null;
    ColumnType targetType = null;
    boolean functionsEnabled = false;
    DataSetMetadata metadata = null;
    ColumnDetailsEditor columnDetailsEditor = null;
    Event<GroupFunctionChangedEvent> changeEvent = null;
    Event<GroupFunctionDeletedEvent> deleteEvent = null;

    @Inject
    public ColumnFunctionEditor(View view,
                                ColumnDetailsEditor columnDetailsEditor,
                                Event<GroupFunctionChangedEvent> changeEvent,
                                Event<GroupFunctionDeletedEvent> deleteEvent) {
        this.view = view;
        this.columnDetailsEditor = columnDetailsEditor;
        this.changeEvent = changeEvent;
        this.deleteEvent = deleteEvent;
        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public GroupFunction getGroupFunction() {
        return groupFunction;
    }

    public ColumnType getTargetType() {
        return targetType;
    }

    public ColumnDetailsEditor getColumnDetailsEditor() {
        return columnDetailsEditor;
    }

    public void init(DataSetMetadata metadata,
                     GroupFunction groupFunction,
                     ColumnType targetType,
                     String columnTitle,
                     boolean functionsEnabled,
                     boolean canDelete) {

        this.groupFunction = groupFunction;
        this.targetType = targetType;
        this.metadata = metadata;
        this.functionsEnabled = functionsEnabled;

        columnDetailsEditor.init(metadata, this.groupFunction);
        view.setColumnSelectorTitle(columnTitle);
        view.setDeleteOptionEnabled(canDelete);
        initColumnListBox();

        if (functionsEnabled && (targetType == null || isColumnNumeric())) {
            view.setFunctionSelectorEnabled(true);
            initFunctionListBox();
        } else {
            view.setFunctionSelectorEnabled(false);
        }
    }

    public void delete() {
        deleteEvent.fire(new GroupFunctionDeletedEvent(groupFunction));
    }

    void onColumnSelected() {
        groupFunction.setSourceId(view.getSelectedColumnId());
        if (!isColumnNumeric()) {
            groupFunction.setFunction(null);
        } else {
            groupFunction.setFunction(getSupportedFunctionTypes().get(0));
        }
        initFunctionListBox();
        changeEvent.fire(new GroupFunctionChangedEvent(groupFunction));
    }

    void onFunctionSelected() {
        AggregateFunctionType selected = null;
        int i = view.getSelectedFunctionIndex();
        if (i >= 0) {
            List<AggregateFunctionType> supportedFunctions = getSupportedFunctionTypes();
            selected = supportedFunctions.get(i);
        }
        groupFunction.setFunction(selected);
        changeEvent.fire(new GroupFunctionChangedEvent(groupFunction));
    }

    protected boolean isColumnNumeric() {
        return targetType != null && targetType.equals(ColumnType.NUMBER) && functionsEnabled;
    }

    protected void initColumnListBox() {
        view.clearColumnSelector();

        for (int i=0; i<metadata.getNumberOfColumns(); i++) {
            String columnId = metadata.getColumnId(i);
            ColumnType columnType = metadata.getColumnType(i);

            // Only add columns that match the target type.
            // When the target is not specified or is numeric then all the columns are eligible
            if (targetType == null || columnType == null || isColumnNumeric() || targetType.equals(columnType)) {
                view.addColumnItem(columnId);
                if (columnId != null && columnId.equals(groupFunction.getSourceId())) {
                    view.setSelectedColumnIndex(i);
                }
            }
        }
    }

    protected void initFunctionListBox() {
        view.clearFunctionSelector();
        view.setVoidFunctionEnabled(!isColumnNumeric());

        AggregateFunctionType selected = groupFunction.getFunction();
        List<AggregateFunctionType> supportedFunctions = getSupportedFunctionTypes();
        for (int i=0; i<supportedFunctions.size(); i++) {
            AggregateFunctionType functionType = supportedFunctions.get(i);
            view.addFunctionItem(functionType);
            if (selected != null && functionType.equals(selected)) {
                view.setSelectedFunctionIndex(i);
            }
        }
    }

    public List<AggregateFunctionType> getSupportedFunctionTypes() {
        ColumnType columnType = metadata.getColumnType(groupFunction.getSourceId());
        return getSupportedFunctionTypes(columnType);
    }

    public List<AggregateFunctionType> getSupportedFunctionTypes(ColumnType columnType) {
        List<AggregateFunctionType> result = new ArrayList<AggregateFunctionType>();
        for (AggregateFunctionType function : AggregateFunctionType.values()) {
            if (function.supportType(columnType)) {
                result.add(function);
            }
        }
        return result;
    }
}

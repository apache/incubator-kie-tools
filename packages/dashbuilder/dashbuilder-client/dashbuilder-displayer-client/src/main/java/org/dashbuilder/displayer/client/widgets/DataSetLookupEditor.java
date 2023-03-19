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
package org.dashbuilder.displayer.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.common.client.StringUtils;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.DataSetOpType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.DataSetGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.client.events.DataSetFilterChangedEvent;
import org.dashbuilder.displayer.client.events.DataSetGroupDateChanged;
import org.dashbuilder.displayer.client.events.DataSetLookupChangedEvent;
import org.dashbuilder.displayer.client.events.GroupFunctionChangedEvent;
import org.dashbuilder.displayer.client.events.GroupFunctionDeletedEvent;
import org.dashbuilder.displayer.client.widgets.filter.DataSetFilterEditor;
import org.dashbuilder.displayer.client.widgets.group.ColumnFunctionEditor;
import org.dashbuilder.displayer.client.widgets.group.DataSetGroupDateEditor;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.UberView;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class DataSetLookupEditor implements IsWidget {

    public interface View extends UberView<DataSetLookupEditor> {

        void clearAll();

        void clearDataSetSelector();

        void enableDataSetSelectorHint();

        void addDataSetItem(String name,
                            String id);

        void removeDataSetItem(int index);

        void setSelectedDataSetIndex(int index);

        String getSelectedDataSetId();

        void error(ClientRuntimeError error);

        void errorDataSetNotFound(String dataSetUUID);

        void setFilterEnabled(boolean enabled);

        void setGroupEnabled(boolean enabled);

        void clearGroupColumnSelector();

        void setGroupByDateEnabled(boolean enabled);

        void setGroupColumnSelectorTitle(String title);

        void enableGroupColumnSelectorHint();

        void addGroupColumnItem(String column);

        void setSelectedGroupColumnIndex(int index);

        String getSelectedGroupColumnId();

        void setColumnsSectionEnabled(boolean enabled);

        void clearColumnList();

        void setColumnSectionTitle(String title);

        void setAddColumnOptionEnabled(boolean enabled);

        void addColumnEditor(ColumnFunctionEditor editor);

        void removeColumnEditor(ColumnFunctionEditor editor);
    }

    public interface DataSetDefFilter {

        boolean accept(DataSetDef def);
    }

    View view;
    SyncBeanManager beanManager;
    DataSetClientServices clientServices;
    DataSetFilterEditor filterEditor;
    DataSetGroupDateEditor groupDateEditor;
    DataSetLookup dataSetLookup = null;
    DataSetLookupConstraints lookupConstraints = null;
    DataSetMetadata dataSetMetadata = null;
    Event<DataSetLookupChangedEvent> changeEvent = null;
    List<DataSetDef> _dataSetDefList = new ArrayList<DataSetDef>();
    Map<Integer, ColumnFunctionEditor> _editorsMap = new HashMap<Integer, ColumnFunctionEditor>();

    DataSetDefFilter dataSetDefFilter = new DataSetDefFilter() {

        public boolean accept(DataSetDef def) {
            return true;
        }
    };

    @Inject
    public DataSetLookupEditor(final View view,
                               SyncBeanManager beanManager,
                               DataSetFilterEditor filterEditor,
                               DataSetGroupDateEditor groupDateEditor,
                               DataSetClientServices clientServices,
                               Event<DataSetLookupChangedEvent> event) {
        this.view = view;
        this.beanManager = beanManager;
        this.filterEditor = filterEditor;
        this.groupDateEditor = groupDateEditor;
        this.clientServices = clientServices;
        this.changeEvent = event;
        this.dataSetLookup = null;
        this.lookupConstraints = null;
        this.dataSetMetadata = null;
        view.init(this);
    }

    public void init(DataSetLookupConstraints lookupConstraints,
                     final DataSetLookup dataSetLookup) {
        this.dataSetLookup = dataSetLookup;
        this.lookupConstraints = lookupConstraints;
        this.view.clearAll();
        this.clientServices.getPublicDataSetDefs((List<DataSetDef> dataSetDefs) -> {
            showDataSetDefs(dataSetDefs);
            if (dataSetLookup != null && dataSetLookup.getDataSetUUID() != null) {
                fetchMetadata(dataSetLookup.getDataSetUUID(), (DataSetMetadata metadata) -> updateDataSetLookup());
            }
        });

    }

    void fetchMetadata(final String uuid,
                       final RemoteCallback<DataSetMetadata> callback) {
        try {
            clientServices.fetchMetadata(uuid,
                                         new DataSetMetadataCallback() {

                                             public void callback(DataSetMetadata metadata) {
                                                 dataSetMetadata = metadata;
                                                 callback.callback(metadata);
                                             }

                                             public void notFound() {
                                                 view.errorDataSetNotFound(uuid);
                                             }

                                             public boolean onError(ClientRuntimeError error) {
                                                 view.error(error);
                                                 return false;
                                             }
                                         });
        } catch (Exception e) {
            view.error(new ClientRuntimeError(e));
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public View getView() {
        return view;
    }

    public DataSetFilterEditor getFilterEditor() {
        return filterEditor;
    }

    public DataSetGroupDateEditor getGroupDateEditor() {
        return groupDateEditor;
    }

    public DataSetLookup getDataSetLookup() {
        return dataSetLookup;
    }

    public DataSetLookupConstraints getLookupConstraints() {
        return lookupConstraints;
    }

    public void setDataSetDefFilter(DataSetDefFilter dataSetDefFilter) {
        this.dataSetDefFilter = dataSetDefFilter;
    }

    public String getDataSetUUID() {
        return dataSetLookup == null ? null : dataSetLookup.getDataSetUUID();
    }

    public String getColumnId(int index) {
        return dataSetMetadata.getColumnId(index);
    }

    public ColumnType getColumnType(int index) {
        return dataSetMetadata.getColumnType(index);
    }

    public ColumnType getColumnType(String columnId) {
        return columnId == null ? null : dataSetMetadata.getColumnType(columnId);
    }

    public DataSetGroup getFirstGroupOp() {
        List<DataSetGroup> groupOpList = dataSetLookup.getOperationList(DataSetGroup.class);
        if (groupOpList.isEmpty()) {
            return null;
        }
        return groupOpList.get(0);
    }

    public boolean isFirstGroupOpDateBased() {
        DataSetGroup first = getFirstGroupOp();
        if (first == null) {
            return false;
        }
        ColumnGroup cg = first.getColumnGroup();
        if (cg == null) {
            return false;
        }
        ColumnType type = getColumnType(cg.getSourceId());
        return ColumnType.DATE.equals(type);
    }

    public List<GroupFunction> getFirstGroupFunctions() {
        List<DataSetGroup> groupOpList = dataSetLookup.getOperationList(DataSetGroup.class);
        if (groupOpList.isEmpty()) {
            return null;
        }
        return groupOpList.get(0).getGroupFunctions();
    }

    public int getFirstGroupFunctionIdx(GroupFunction gf) {
        List<DataSetGroup> groupOpList = dataSetLookup.getOperationList(DataSetGroup.class);
        if (groupOpList.isEmpty()) {
            return -1;
        }
        return groupOpList.get(0).getGroupFunctionIdx(gf);
    }

    public String getFirstGroupColumnId() {
        List<DataSetGroup> groupOpList = dataSetLookup.getOperationList(DataSetGroup.class);
        if (groupOpList.isEmpty()) {
            return null;
        }
        DataSetGroup groupOp = groupOpList.get(0);
        if (groupOp.getColumnGroup() == null) {
            return null;
        }
        return groupOp.getColumnGroup().getSourceId();
    }

    public List<Integer> getAvailableGroupColumnIdxs() {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < dataSetMetadata.getNumberOfColumns(); i++) {
            ColumnType columnType = dataSetMetadata.getColumnType(i);
            if (ColumnType.LABEL.equals(columnType) || ColumnType.DATE.equals(columnType) || ColumnType.NUMBER.equals(columnType)) {
                result.add(i);
            }
        }
        return result;
    }

    public void showDataSetDefs(List<DataSetDef> ds) {
        _dataSetDefList.clear();
        view.clearDataSetSelector();
        String selectedUUID = getDataSetUUID();
        if (StringUtils.isBlank(selectedUUID)) {
            view.enableDataSetSelectorHint();
        }

        boolean found = false;
        for (int i = 0; i < ds.size(); i++) {
            DataSetDef def = ds.get(i);
            if (dataSetDefFilter.accept(def)) {

                addDataSetDef(def);

                if (selectedUUID != null && selectedUUID.equals(def.getUUID())) {
                    view.setSelectedDataSetIndex(i);
                    found = true;
                }
            }
        }
        if (!StringUtils.isBlank(selectedUUID) && !found) {
            view.errorDataSetNotFound(selectedUUID);
        }
    }

    public void addDataSetDef(DataSetDef def) {
        _dataSetDefList.add(def);
        if (StringUtils.isBlank(def.getName())) {
            view.addDataSetItem(def.getUUID(),
                                def.getUUID());
        } else {
            view.addDataSetItem(def.getName(),
                                def.getUUID());
        }
    }

    public void removeDataSetDef(DataSetDef def) {
        int i = 0;
        Iterator<DataSetDef> it = _dataSetDefList.iterator();
        while (it.hasNext()) {
            DataSetDef item = it.next();
            if (item.getUUID().equals(def.getUUID())) {
                it.remove();
                view.removeDataSetItem(i);
            }
            i++;
        }
    }

    void updateDataSetLookup() {
        view.setFilterEnabled(false);
        view.setGroupEnabled(false);
        view.setColumnsSectionEnabled(false);

        if (dataSetLookup != null && dataSetMetadata != null && lookupConstraints != null) {
            updateFilterControls();
            updateGroupControls();
            updateColumnControls();
        }
    }

    void updateFilterControls() {
        view.setFilterEnabled(lookupConstraints.isFilterAllowed());
        filterEditor.init(dataSetLookup.getFirstFilterOp(),
                          dataSetMetadata);
    }

    void updateGroupControls() {

        view.setGroupEnabled(false);
        view.setGroupByDateEnabled(false);

        // Only show the group controls if group is enabled
        if (lookupConstraints.isGroupRequired() || lookupConstraints.isGroupAllowed()) {
            String groupColumnId = getFirstGroupColumnId();

            // Always ensure a group exists when required
            if (lookupConstraints.isGroupRequired() && groupColumnId == null) {
                dataSetLookup = lookupConstraints.newDataSetLookup(dataSetMetadata);
                changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
            }

            List<Integer> groupColumnIdxs = getAvailableGroupColumnIdxs();
            String rowsTitle = lookupConstraints.getGroupsTitle();

            view.setGroupEnabled(true);
            if (isFirstGroupOpDateBased()) {
                view.setGroupByDateEnabled(true);
                ColumnGroup columnGroup = getFirstGroupOp().getColumnGroup();
                groupDateEditor.init(columnGroup);
            }

            if (!StringUtils.isBlank(rowsTitle)) {
                view.setGroupColumnSelectorTitle(rowsTitle);
            }

            view.clearGroupColumnSelector();
            if (!lookupConstraints.isGroupRequired()) {
                view.enableGroupColumnSelectorHint();
            }
            for (int i = 0; i < groupColumnIdxs.size(); i++) {
                int idx = groupColumnIdxs.get(i);
                String columnId = getColumnId(idx);
                view.addGroupColumnItem(columnId);
                if (groupColumnId != null && groupColumnId.equals(columnId)) {
                    view.setSelectedGroupColumnIndex(i);
                }
            }
        }
    }

    void updateColumnControls() {
        String groupColumnId = getFirstGroupColumnId();
        List<GroupFunction> groupFunctions = getFirstGroupFunctions();
        String columnsTitle = lookupConstraints.getColumnsTitle();
        boolean functionsRequired = lookupConstraints.isFunctionRequired();
        boolean functionsEnabled = (groupColumnId != null || functionsRequired);
        boolean canDelete = groupFunctions.size() > lookupConstraints.getMinColumns();
        int n = lookupConstraints.getMaxColumns();
        boolean canAdd = lookupConstraints.areExtraColumnsAllowed() && (n < 0 || groupFunctions.size() < n);

        // Show the columns section
        view.setColumnsSectionEnabled(true);
        view.clearColumnList();
        if (!StringUtils.isBlank(columnsTitle)) {
            view.setColumnSectionTitle(columnsTitle);
        }
        view.setAddColumnOptionEnabled(canAdd);

        // Destroy old editors
        Iterator<ColumnFunctionEditor> it = _editorsMap.values().iterator();
        while (it.hasNext()) {
            ColumnFunctionEditor editor = it.next();
            beanManager.destroyBean(editor);
        }

        // Build the column editors
        _editorsMap.clear();
        ColumnType lastTargetType = null;
        ColumnType[] targetTypes = lookupConstraints.getColumnTypes(groupFunctions.size());
        for (int i = 0; i < groupFunctions.size(); i++) {
            final int columnIdx = i;
            final GroupFunction groupFunction = groupFunctions.get(columnIdx);

            if (targetTypes != null && i < targetTypes.length) {
                lastTargetType = targetTypes[i];
            }
            if (columnIdx == 0 && groupColumnId != null && lookupConstraints.isGroupColumn()) {
                continue;
            }

            ColumnType columnType = null;
            if (targetTypes != null && i < targetTypes.length) {
                columnType = targetTypes[columnIdx];
            }
            if (columnType == null) {
                columnType = lastTargetType; // Extra columns
            }

            String columnTitle = lookupConstraints.getColumnTitle(columnIdx);
            ColumnFunctionEditor columnEditor = beanManager.lookupBean(ColumnFunctionEditor.class).newInstance();
            columnEditor.init(dataSetMetadata,
                              groupFunction,
                              columnType,
                              columnTitle,
                              functionsEnabled,
                              canDelete);

            _editorsMap.put(_editorsMap.size(),
                            columnEditor);
            view.addColumnEditor(columnEditor);
        }
    }

    int getGroupFunctionLastIdx(List<GroupFunction> groupFunctions,
                                String sourceId) {
        int last = -1;
        for (GroupFunction gf : groupFunctions) {
            if (gf.getSourceId().equals(sourceId)) {
                int idx = getGroupFunctionColumnIdx(gf.getColumnId());
                if (last == -1 || last < idx) {
                    last = idx;
                }
            }
        }
        return last;
    }

    int getGroupFunctionColumnIdx(String columnId) {
        int sep = columnId.lastIndexOf('_');
        if (sep != -1) {
            try {
                String str = columnId.substring(sep + 1);
                return Integer.parseInt(str);
            } catch (Exception e) {
                // Ignore
            }
        }
        return 1;
    }

    // View notifications

    void onDataSetSelected() {
        String selectedUUID = view.getSelectedDataSetId();
        for (DataSetDef dataSetDef : _dataSetDefList) {
            if (dataSetDef.getUUID().equals(selectedUUID)) {
                fetchMetadata(selectedUUID,
                              new RemoteCallback<DataSetMetadata>() {

                                  public void callback(DataSetMetadata metadata) {
                                      dataSetLookup = lookupConstraints.newDataSetLookup(metadata);
                                      updateDataSetLookup();
                                      changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
                                  }
                              });
            }
        }
    }

    void onGroupColumnSelected() {
        DataSetGroup groupOp = getFirstGroupOp();
        if (groupOp != null) {

            // Group reset
            String columnId = view.getSelectedGroupColumnId();
            if (columnId == null) {
                groupOp.setColumnGroup(null);

                if (lookupConstraints.isGroupColumn()) {
                    groupOp.getGroupFunctions().remove(0);
                }
                if (!lookupConstraints.isFunctionRequired()) {
                    for (GroupFunction groupFunction : groupOp.getGroupFunctions()) {
                        groupFunction.setFunction(null);
                    }
                }
            }
            // Group column change
            else {
                groupOp.setColumnGroup(new ColumnGroup(columnId,
                                                       columnId));
                if (lookupConstraints.isGroupColumn()) {
                    if (groupOp.getGroupFunctions().size() > 1) {
                        groupOp.getGroupFunctions().remove(0);
                    }
                    GroupFunction groupFunction = new GroupFunction(columnId,
                                                                    columnId,
                                                                    null);
                    groupOp.getGroupFunctions().add(0,
                                                    groupFunction);
                }
            }
        }

        // Refresh the group by date editor if required
        view.setGroupByDateEnabled(false);
        if (isFirstGroupOpDateBased()) {
            view.setGroupByDateEnabled(true);
            ColumnGroup columnGroup = getFirstGroupOp().getColumnGroup();
            groupDateEditor.init(columnGroup);
        }

        // Reset the column list
        updateColumnControls();

        // Notify the changes
        changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
    }

    void onAddColumn() {
        if (lookupConstraints.areExtraColumnsAllowed()) {
            DataSetGroup op = getFirstGroupOp();
            List<GroupFunction> functionList = op.getGroupFunctions();
            GroupFunction last = functionList.get(functionList.size() - 1);

            GroupFunction clone = last.cloneInstance();
            String newColumnId = lookupConstraints.buildUniqueColumnId(dataSetLookup,
                                                                       clone);
            clone.setColumnId(newColumnId);
            functionList.add(clone);

            updateColumnControls();
            changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
        }
    }

    // DataSetFilterEditor events

    void onFilterChanged(@Observes DataSetFilterChangedEvent event) {
        DataSetFilter filterOp = event.getFilter();
        dataSetLookup.removeOperations(DataSetOpType.FILTER);
        if (filterOp != null) {
            dataSetLookup.addOperation(0,
                                       filterOp);
        }
        changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
    }

    // DataSetGroupDateEditor events

    void onDateGroupChanged(@Observes DataSetGroupDateChanged event) {
        ColumnGroup columnGroup = event.getColumnGroup();
        DataSetGroup groupOp = getFirstGroupOp();
        if (groupOp != null) {
            groupOp.setColumnGroup(columnGroup);

            changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
        }
    }

    // ColumnFunctionEditor's events

    void onColumnFunctionChanged(@Observes GroupFunctionChangedEvent event) {
        GroupFunction gf = event.getGroupFunction();
        String newColumnId = lookupConstraints.buildUniqueColumnId(dataSetLookup,
                                                                   gf);
        gf.setColumnId(newColumnId);
        changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
    }

    void onColumnFunctionDeleted(@Observes GroupFunctionDeletedEvent event) {
        List<GroupFunction> functionList = getFirstGroupFunctions();
        boolean canDelete = functionList.size() > lookupConstraints.getMinColumns();

        GroupFunction removed = event.getGroupFunction();
        int index = getFirstGroupFunctionIdx(removed);
        if (canDelete && index >= 0) {

            functionList.remove(index);
            updateColumnControls();
            changeEvent.fire(new DataSetLookupChangedEvent(dataSetLookup));
        }
    }

    // Listen to data set lifecycle events

    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        checkNotNull("event",
                     event);
        addDataSetDef(event.getDataSetDef());
    }

    void onDataSetDefModifiedEvent(@Observes DataSetDefModifiedEvent event) {
        checkNotNull("event",
                     event);

        removeDataSetDef(event.getOldDataSetDef());
        addDataSetDef(event.getNewDataSetDef());
    }

    void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        checkNotNull("event",
                     event);

        removeDataSetDef(event.getDataSetDef());
    }
}

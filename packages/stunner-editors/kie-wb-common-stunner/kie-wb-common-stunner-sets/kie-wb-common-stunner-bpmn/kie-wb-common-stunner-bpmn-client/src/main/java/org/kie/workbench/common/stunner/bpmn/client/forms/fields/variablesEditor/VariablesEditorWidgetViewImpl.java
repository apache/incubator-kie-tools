/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("VariablesEditorWidget.html#widget")
public class VariablesEditorWidgetViewImpl extends Composite implements VariablesEditorWidgetView,
                                                                        HasValue<String> {

    ListBoxValues dataTypeListBoxValues;

    private String sVariables;

    private Presenter presenter;

    @Inject
    @DataField
    protected Button addVarButton;

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @DataField
    protected TableCellElement nameth = Document.get().createTHElement();

    @DataField
    protected TableCellElement datatypeth = Document.get().createTHElement();

    @Inject
    protected DataTypeNamesService clientDataTypesService;

    @DataField
    private TableCellElement tagsth = Document.get().createTHElement();

    List<String> dataTypes;
    List<String> dataTypeDisplayNames;
    boolean readOnly = false;

    private boolean tagsDisabled = false;

    private final SessionManager sessionManager;

    protected Event<RefreshFormPropertiesEvent> refreshFormsEvent;

    protected RefreshFormPropertiesEvent refreshFormPropertiesEvent = null;

    @Inject
    public VariablesEditorWidgetViewImpl(final SessionManager sessionManager, final Event<RefreshFormPropertiesEvent> refreshFormsEvent) {
        this.sessionManager = sessionManager;
        this.refreshFormsEvent = refreshFormsEvent;
    }

    /**
     * The list of variableRows that currently exist.
     */
    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<VariableRow, VariableListItemWidgetViewImpl> variableRows;

    @Inject
    protected Event<NotificationEvent> notification;

    public void setTagsth(TableCellElement tagsth) {
        this.tagsth = tagsth;
    }

    public void setTagsDisabled(boolean tagsDisabled) {
        this.tagsDisabled = tagsDisabled;
    }

    @Override
    public String getValue() {
        return sVariables;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        if (dataTypes == null) {
            getDataTypes(value,
                         fireEvents);
        } else {
            doSetValue(value,
                       fireEvents,
                       false);
        }
    }

    void onRefreshFormPropertiesEvent(@Observes RefreshFormPropertiesEvent event) {
        if (!event.equals(refreshFormPropertiesEvent)) {
            String value = getValue();
            getDataTypes(value,
                         false);
        }
    }

    protected void doSetValue(final String value,
                              final boolean fireEvents,
                              final boolean initializeView) {
        String oldValue = sVariables;
        sVariables = value;
        if (initializeView) {
            initView();
        }
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            sVariables);
        }
        setReadOnly(readOnly);
    }

    protected void setDataTypes(final List<String> dataTypes,
                                final List<String> dataTypeDisplayNames) {
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;
        presenter.setDataTypes(dataTypes,
                               dataTypeDisplayNames);
    }

    protected void getDataTypes(final String value,
                                final boolean fireEvents) {
        final List<String> simpleDataTypes = new ArrayList<String>(Arrays.asList("Boolean",
                                                                                 "Float",
                                                                                 "Integer",
                                                                                 "Object",
                                                                                 "String"));
        final List<String> simpleDataTypeDisplayNames = new ArrayList<String>(Arrays.asList("Boolean",
                                                                                            "Float",
                                                                                            "Integer",
                                                                                            "Object",
                                                                                            "String"));

        Set<String> types = StringUtils.getSetDataTypes(value);

        clientDataTypesService
                .call(presenter.getDiagramPath())
                .then(serverDataTypes -> {
                    List<List<String>> mergedDataTypes = mergeDataTypes(simpleDataTypes,
                                                                        simpleDataTypeDisplayNames,
                                                                        serverDataTypes, types);
                    setDataTypes(mergedDataTypes.get(0),
                                 mergedDataTypes.get(1));
                    doSetValue(value,
                               fireEvents,
                               true);
                    return null;
                })
                .catch_(exception -> {
                    notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.Error_retrieving_datatypes(),
                                                            NotificationEvent.NotificationType.ERROR));
                    setDataTypes(simpleDataTypes,
                                 simpleDataTypeDisplayNames);
                    doSetValue(value,
                               fireEvents,
                               true);
                    return null;
                });
    }

    private List<List<String>> mergeDataTypes(final List<String> simpleDataTypes,
                                              final List<String> simpleDataTypeDisplayNames,
                                              final List<String> serverDataTypes,
                                              final Set<String> excludeValues) {
        List<List<String>> results = new ArrayList<List<String>>(2);
        List<String> allDataTypes = new ArrayList<String>();
        List<String> allDataTypeDisplayNames = new ArrayList<String>();
        allDataTypes.addAll(simpleDataTypes);
        allDataTypeDisplayNames.addAll(simpleDataTypeDisplayNames);

        // Create sorted map with DataTypeDisplayNames as the keys
        Map<String, String> mapServerDataTypeDisplayNames = new TreeMap<String, String>();

        for (String serverDataType : serverDataTypes) {
            boolean isAsset = false;
            if (serverDataType.contains("Asset-")) {
                serverDataType = serverDataType.substring(6);
                isAsset = true;
            }

            if (!isAsset && excludeValues.contains(serverDataType)) {
                continue;
            }
            mapServerDataTypeDisplayNames.put(StringUtils.createDataTypeDisplayName(serverDataType),
                                              serverDataType);
        }

        // Add DataTypes in order sorted by DataTypeDisplayNames
        for (Map.Entry<String, String> entry : mapServerDataTypeDisplayNames.entrySet()) {
            allDataTypes.add(entry.getValue());
            allDataTypeDisplayNames.add(entry.getKey());
        }

        results.add(allDataTypes);
        results.add(allDataTypeDisplayNames);

        return results;
    }

    @Override
    public void doSave() {
        String newValue = presenter.serializeVariables(getVariableRows());
        setValue(newValue,
                 true);
    }

    protected void initView() {
        List<VariableRow> arrVariableRows = presenter.deserializeVariables(sVariables);
        setVariableRows(arrVariableRows);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    /**
     * Tests whether a VariableRow name occurs more than once in the list of rows
     * @param name
     * @return
     */
    public boolean isDuplicateName(final String name) {
        return presenter.isDuplicateName(name);
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        addVarButton.setIcon(IconType.PLUS);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        addVarButton.setEnabled(!readOnly);
        for (int i = 0; i < getVariableRowsCount(); i++) {
            getVariableWidget(i).setReadOnly(readOnly);
        }
        checkTagsNotEnabled();
    }

    @Override
    public int getVariableRowsCount() {
        return variableRows.getValue().size();
    }

    @Override
    public void setTableDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.TABLE);
    }

    @Override
    public void setNoneDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void setVariableRows(final List<VariableRow> rows) {
        variableRows.setValue(rows);
        for (int i = 0; i < getVariableRowsCount(); i++) {
            VariableListItemWidgetView widget = getVariableWidget(i);
            widget.setDataTypes(dataTypeListBoxValues);
            widget.setTagTypes(rows.get(i).getTags());
            widget.setParentWidget(presenter);
        }
    }

    @Override
    public List<VariableRow> getVariableRows() {
        return variableRows.getValue();
    }

    @Override
    public VariableListItemWidgetView getVariableWidget(final int index) {
        return variableRows.getComponent(index);
    }

    @Override
    public void setVariablesDataTypes(final ListBoxValues dataTypeListBoxValues) {
        this.dataTypeListBoxValues = dataTypeListBoxValues;
        for (int i = 0; i < getVariableRowsCount(); i++) {
            getVariableWidget(i).setDataTypes(dataTypeListBoxValues);
        }
    }

    @EventHandler("addVarButton")
    public void handleAddVarButton(final ClickEvent e) {
        presenter.addVariable();
    }

    public void removeVariable(final VariableRow variableRow) {
        presenter.removeVariable(variableRow);
        if (getVariableRows().isEmpty()) {
            setNoneDisplayStyle();
        }
    }

    @Override
    public void setTagsNotEnabled() {
        tagsDisabled = true;
        checkTagsNotEnabled();
    }

    @Override
    public void addDataType(String dataType, String oldType) {
        if (dataType != null && !dataType.isEmpty()) {
            doAddDataType(dataType, oldType);
        }
    }

    protected void doAddDataType(String dataType, String oldType) {
      clientDataTypesService.add(dataType, oldType);
    }

    protected void checkTagsNotEnabled() {
        if (tagsDisabled) {
            tagsth.removeFromParent();
            for (int i = 0; i < getVariableRowsCount(); i++) {
                getVariableWidget(i).setTagsNotEnabled();
            }
        }
    }
}

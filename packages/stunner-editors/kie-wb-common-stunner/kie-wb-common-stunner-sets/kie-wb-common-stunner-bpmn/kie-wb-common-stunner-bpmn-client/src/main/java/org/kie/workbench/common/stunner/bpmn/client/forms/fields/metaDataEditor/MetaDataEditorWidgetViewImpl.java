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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("MetaDataEditorWidget.html#widget")
public class MetaDataEditorWidgetViewImpl extends Composite implements MetaDataEditorWidgetView,
                                                                       HasValue<String> {

    private String sAttributes;
    private Presenter presenter;

    @Inject
    @DataField
    protected Button addButton;

    @DataField
    private final TableElement table = Document.get().createTableElement();

    @DataField
    protected TableCellElement attributeth = Document.get().createTHElement();

    @DataField
    protected TableCellElement valueth = Document.get().createTHElement();

    boolean readOnly = false;

    private boolean notInitialized = true;

    @Inject
    @DataField
    @Table(root = "tbody")
    protected ListWidget<MetaDataRow, MetaDataListItemWidgetViewImpl> metaDataRows;

    @Inject
    protected Event<NotificationEvent> notification;

    @Override
    public String getValue() {
        return sAttributes;
    }

    @Override
    public void setValue(final String value) {
        setValue(value,
                 false);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        doSetValue(value,
                   fireEvents,
                   notInitialized);
    }

    protected void doSetValue(final String value,
                              final boolean fireEvents,
                              final boolean initializeView) {
        String oldValue = sAttributes;
        sAttributes = value;
        if (initializeView) {
            initView();
        }
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            sAttributes);
        }
        setReadOnly(readOnly);
    }

    @Override
    public void doSave() {
        String newValue = presenter.serializeMetaDataAttributes(getMetaDataRows());
        setValue(newValue,
                 true);
    }

    protected void initView() {
        List<MetaDataRow> arrMetadataRows = presenter.deserializeMetaDataAttributes(sAttributes);
        setMetaDataRows(arrMetadataRows);
        notInitialized = false;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public boolean isDuplicateAttribute(final String attribute) {
        return presenter.isDuplicateAttribute(attribute);
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        addButton.setIcon(IconType.PLUS);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        addButton.setEnabled(!readOnly);
        for (int i = 0; i < getMetaDataRowsCount(); i++) {
            getMetaDataWidget(i).setReadOnly(readOnly);
        }
    }

    @Override
    public int getMetaDataRowsCount() {
        return metaDataRows.getValue().size();
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
    public void setMetaDataRows(final List<MetaDataRow> rows) {
        metaDataRows.setValue(rows);
        for (int i = 0; i < getMetaDataRowsCount(); i++) {
            MetaDataListItemWidgetView widget = getMetaDataWidget(i);
            widget.setParentWidget(presenter);
        }
    }

    @Override
    public List<MetaDataRow> getMetaDataRows() {
        return metaDataRows.getValue();
    }

    @Override
    public MetaDataListItemWidgetView getMetaDataWidget(final int index) {
        return metaDataRows.getComponent(index);
    }

    @EventHandler("addButton")
    public void handleAddButton(final ClickEvent e) {
        presenter.addAttribute();
    }

    @Override
    public void removeMetaData(final MetaDataRow metaDataRow) {
        presenter.removeMetaData(metaDataRow);
        if (getMetaDataRows().isEmpty()) {
            setNoneDisplayStyle();
        }
    }
}
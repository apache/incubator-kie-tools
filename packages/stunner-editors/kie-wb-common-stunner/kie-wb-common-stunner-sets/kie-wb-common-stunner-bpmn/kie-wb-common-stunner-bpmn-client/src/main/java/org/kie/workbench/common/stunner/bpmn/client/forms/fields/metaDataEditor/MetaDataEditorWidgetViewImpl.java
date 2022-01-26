/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import io.crysknife.ui.databinding.client.components.ListComponent;
import io.crysknife.ui.databinding.client.components.ListContainer;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.client.TableCellElement;
import org.gwtproject.dom.client.TableElement;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.HasValue;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
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
    @ListContainer("tbody")
    protected ListComponent<MetaDataRow, MetaDataListItemWidgetViewImpl> metaDataRows;

    @Inject
    protected Event<NotificationEvent> notification;

    @Override
    public String getValue() {
        return sAttributes;
    }

    @Override
    public void setValue(final String value) {
        doSetValue(value, false, true);
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
        final String oldValue = sAttributes;
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
        String newValue = presenter.serializeMetaDataAttributes(removeEmptyRoles(getMetaDataRows()));
        setValue(newValue,
                true);
    }

    private List<MetaDataRow> removeEmptyRoles(List<MetaDataRow> roles) {
        return roles.stream().filter(row -> !StringUtils.isEmpty(row.getAttribute())).collect(Collectors.toList());
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
        attributeth.setInnerText("Name");
        valueth.setInnerText("Value");
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
        if (metaDataRows.getValue() == null) {
            return 0;
        }
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
        for (int i = 0; i < rows.size(); i++) {
            MetaDataListItemWidgetView view = metaDataRows.getComponent(i);
            view.setParentWidget(presenter);
            view.setModel(rows.get(i));
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
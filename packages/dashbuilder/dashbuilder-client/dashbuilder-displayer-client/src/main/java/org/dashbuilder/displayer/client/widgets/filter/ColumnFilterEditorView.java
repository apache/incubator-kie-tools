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
package org.dashbuilder.displayer.client.widgets.filter;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.i18n.CoreFunctionTypeConstants;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ColumnFilterEditorView extends Composite implements ColumnFilterEditor.View {

    interface Binder extends UiBinder<Widget, ColumnFilterEditorView> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    ColumnFilterEditor presenter;
    boolean functionSelected = false;

    @UiField
    ListBox filterListBox;

    @UiField
    Button filterDeleteIcon;

    @UiField
    Button filterExpandIcon;

    @UiField
    Panel filterDetailsPanel;

    // View interface

    public void init(ColumnFilterEditor presenter) {
        this.presenter = checkNotNull("presenter",
                                      presenter);
        initWidget(uiBinder.createAndBindUi(this));

        filterExpandIcon.addDomHandler(new ClickHandler() {
                                           public void onClick(ClickEvent event) {
                                               onExpandCollapseDetails();
                                           }
                                       },
                                       ClickEvent.getType());
        filterDeleteIcon.addDomHandler(new ClickHandler() {
                                           public void onClick(ClickEvent event) {
                                               onDeleteFilter();
                                           }
                                       },
                                       ClickEvent.getType());
    }

    @Override
    public void clearFunctionSelector() {
        filterListBox.clear();
        functionSelected = false;
    }

    @Override
    public void addFunctionItem(CoreFunctionType ft) {
        String function = CoreFunctionTypeConstants.INSTANCE.getString(ft.name());
        filterListBox.addItem(function);
        filterExpandIcon.setVisible(true);
    }

    @Override
    public void setFunctionSelected(String function) {
        filterListBox.insertItem(function,
                                 0);
        filterListBox.setTitle(function);
        if (functionSelected) {
            filterListBox.removeItem(1);
        }
        functionSelected = true;
    }

    @Override
    public int getSelectedFunctionIndex() {
        return filterListBox.getSelectedIndex() - (functionSelected ? 1 : 0);
    }

    @Override
    public void showFilterConfig() {
        filterExpandIcon.setVisible(true);
        filterExpandIcon.setIcon(IconType.ANGLE_DOWN);
        filterExpandIcon.setTitle(CommonConstants.INSTANCE.collapse());
        filterDetailsPanel.setVisible(true);
    }

    public void hideParamConfigWidgets() {
        filterDetailsPanel.setVisible(false);
        filterExpandIcon.setIcon(IconType.ANGLE_RIGHT);
        filterExpandIcon.setTitle(CommonConstants.INSTANCE.expand());
    }

    @Override
    public void addFilterConfigWidget(IsWidget widget) {
        filterDetailsPanel.add(widget);
        filterExpandIcon.setVisible(true);
    }

    @Override
    public void clearFilterConfig() {
        filterDetailsPanel.clear();
        filterDetailsPanel.setVisible(false);
        filterExpandIcon.setVisible(false);
    }

    // UI events

    @UiHandler(value = "filterListBox")
    public void onFilterSelected(ChangeEvent changeEvent) {
        presenter.onSelectFilterFunction();
    }

    public void onExpandCollapseDetails() {
        if (filterDetailsPanel.isVisible()) {
            hideParamConfigWidgets();
        } else {
            showFilterConfig();
        }
    }

    protected void onDeleteFilter() {
        presenter.onDeleteFilter();
    }

    // Internals

    DateTimeFormat _dateTimeFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);
    NumberFormat _numberFormat = NumberFormat.getDecimalFormat();

    @Override
    public String formatDate(Date date) {
        return _dateTimeFormat.format(date);
    }

    @Override
    public String formatNumber(Number number) {
        return _numberFormat.format(number);
    }
}

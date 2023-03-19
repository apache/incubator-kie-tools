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

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.i18n.DateIntervalTypeConstants;
import org.dashbuilder.dataset.client.resources.i18n.DayOfWeekConstants;
import org.dashbuilder.dataset.client.resources.i18n.MonthConstants;
import org.dashbuilder.dataset.date.DayOfWeek;
import org.dashbuilder.dataset.date.Month;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;

@Dependent
public class DataSetGroupDateEditorView extends Composite implements DataSetGroupDateEditor.View {

    interface Binder extends UiBinder<Widget, DataSetGroupDateEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    CheckBox fixedStrategyCheckBox;

    @UiField
    ListBox intervalTypeListBox;

    @UiField
    Panel maxIntervalsGroup;

    @UiField
    Panel firstDayPanel;

    @UiField
    Panel firstMonthPanel;

    @UiField
    TextBox maxIntervalsTextBox;

    @UiField
    CheckBox emptyIntervalsCheckBox;

    @UiField
    ListBox firstDayListBox;

    @UiField
    ListBox firstMonthListBox;

    DataSetGroupDateEditor presenter;

    @Override
    public void init(DataSetGroupDateEditor presenter) {
        this.presenter = presenter;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setFixedModeValue(boolean enabled) {
        fixedStrategyCheckBox.setValue(enabled);
    }

    @Override
    public boolean getFixedModeValue() {
        return fixedStrategyCheckBox.getValue();
    }

    @Override
    public void clearIntervalTypeSelector() {
        intervalTypeListBox.clear();
    }

    @Override
    public void addIntervalTypeItem(DateIntervalType entry) {
        intervalTypeListBox.addItem(DateIntervalTypeConstants.INSTANCE.getString(entry.name()));
    }

    @Override
    public void setSelectedIntervalTypeIndex(int index) {
        intervalTypeListBox.setSelectedIndex(index);
    }

    @Override
    public int getSelectedIntervalTypeIndex() {
        return intervalTypeListBox.getSelectedIndex();
    }

    @Override
    public void setFirstDayVisibility(boolean visible) {
        firstDayPanel.setVisible(visible);
    }

    @Override
    public void clearFirstDaySelector() {
        firstDayListBox.clear();
    }

    @Override
    public void addFirstDaySelectorItem(DayOfWeek entry) {
        firstDayListBox.addItem(DayOfWeekConstants.INSTANCE.getString(entry.name()));
    }

    @Override
    public void setSelectedFirstDayIndex(int index) {
        firstDayListBox.setSelectedIndex(index);
    }

    @Override
    public int getSelectedFirstDayIndex() {
        return firstDayListBox.getSelectedIndex();
    }

    @Override
    public void setFirstMonthVisibility(boolean visible) {
        firstMonthPanel.setVisible(visible);
    }

    @Override
    public void clearFirstMonthSelector() {
        firstMonthListBox.clear();
    }

    @Override
    public void addFirstMonthSelectorItem(Month entry) {
        firstMonthListBox.addItem(MonthConstants.INSTANCE.getString(entry.name()));
    }

    @Override
    public void setSelectedFirstMonthIndex(int index) {
        firstMonthListBox.setSelectedIndex(index);
    }

    @Override
    public int getSelectedFirstMonthIndex() {
        return firstMonthListBox.getSelectedIndex();
    }

    @Override
    public void setEmptyIntervalsValue(boolean enabled) {
        emptyIntervalsCheckBox.setValue(enabled);
    }

    @Override
    public boolean getEmptyIntervalsValue() {
        return emptyIntervalsCheckBox.getValue();
    }

    @Override
    public void setMaxIntervalsVisibility(boolean visible) {
        maxIntervalsGroup.setVisible(visible);
    }

    @Override
    public void setMaxIntervalsValue(String max) {
        maxIntervalsTextBox.setText(max);
    }

    @Override
    public String getMaxIntervalsValue() {
        return maxIntervalsTextBox.getText();
    }

    // UI events

    @UiHandler(value = "fixedStrategyCheckBox")
    public void onFixedModeSelected(ClickEvent clickEvent) {
        presenter.onFixedStrategyChanged();
    }

    @UiHandler(value = "intervalTypeListBox")
    public void onIntervalTypeSelected(ChangeEvent changeEvent) {
        presenter.onIntervalTypeSelected();
    }

    @UiHandler(value = "emptyIntervalsCheckBox")
    public void onEmptyIntervalsChanged(ClickEvent clickEvent) {
        presenter.onEmptyIntervalsChanged();
    }

    @UiHandler(value = "maxIntervalsTextBox")
    public void onMaxIntervalsChanged(ChangeEvent changeEvent) {
        presenter.onMaxIntervalsChanged();
    }

    @UiHandler(value = "firstDayListBox")
    public void onFirstDaySelected(ChangeEvent changeEvent) {
        presenter.onFirstDaySelected();
    }

    @UiHandler(value = "firstMonthListBox")
    public void onFirstMonthSelected(ChangeEvent changeEvent) {
        presenter.onFirstMonthSelected();
    }
}

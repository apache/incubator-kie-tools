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

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.i18n.DateIntervalTypeConstants;
import org.dashbuilder.dataset.client.resources.i18n.TimeModeConstants;
import org.dashbuilder.dataset.date.TimeInstant;
import org.dashbuilder.dataset.group.DateIntervalType;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.ListBox;

@Dependent
public class TimeInstantEditorView extends Composite implements TimeInstantEditor.View {

    interface Binder extends UiBinder<Widget, TimeInstantEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ListBox timeModeList;

    @UiField
    ListBox intervalTypeList;

    @UiField(provided = true)
    TimeAmountEditor timeAmountEditor;

    TimeInstantEditor presenter = null;

    @Override
    public void init(TimeInstantEditor presenter) {
        this.presenter = presenter;
        this.timeAmountEditor = presenter.getTimeAmountEditor();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void clearTimeModeSelector() {
        timeModeList.clear();
    }

    @Override
    public void addTimeModeItem(TimeInstant.TimeMode mode) {
        timeModeList.addItem(TimeModeConstants.INSTANCE.getString(mode.name()));
    }

    @Override
    public void setSelectedTimeModeIndex(int index) {
        timeModeList.setSelectedIndex(index);
    }

    @Override
    public void enableIntervalTypeSelector() {
        intervalTypeList.setVisible(true);
    }

    @Override
    public void disableIntervalTypeSelector() {
        intervalTypeList.setVisible(false);
    }

    @Override
    public void clearIntervalTypeSelector() {
        intervalTypeList.clear();
    }

    @Override
    public void addIntervalTypeItem(DateIntervalType type) {
        intervalTypeList.addItem(DateIntervalTypeConstants.INSTANCE.getString(type.name()));
    }

    @Override
    public void setSelectedIntervalTypeIndex(int index) {
        intervalTypeList.setSelectedIndex(index);
    }

    @Override
    public int getTimeModeSelectedIndex() {
        return timeModeList.getSelectedIndex();
    }

    @Override
    public int getSelectedIntervalTypeIndex() {
        return intervalTypeList.getSelectedIndex();
    }

    // UI events

    @UiHandler(value = "timeModeList")
    public void onTimeModeSelected(ChangeEvent changeEvent) {
        presenter.changeTimeMode();
    }

    @UiHandler(value = "intervalTypeList")
    public void onIntervalTypeSelected(ChangeEvent changeEvent) {
        presenter.changeIntervalType();
    }
}

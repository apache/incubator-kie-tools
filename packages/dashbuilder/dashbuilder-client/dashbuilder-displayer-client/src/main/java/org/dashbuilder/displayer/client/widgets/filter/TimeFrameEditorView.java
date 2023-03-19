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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.dataset.client.resources.i18n.MonthConstants;
import org.dashbuilder.dataset.date.Month;
import org.gwtbootstrap3.client.ui.ListBox;

@Dependent
public class TimeFrameEditorView extends Composite implements TimeFrameEditor.View {

    interface Binder extends UiBinder<Widget, TimeFrameEditorView> {}
    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    TimeInstantEditor fromEditor;

    @UiField(provided = true)
    TimeInstantEditor toEditor;

    @UiField
    Label firstMonthLabel;

    @UiField
    ListBox firstMonthList;

    TimeFrameEditor presenter = null;

    @Override
    public void init(TimeFrameEditor presenter) {
        this.presenter = presenter;
        fromEditor = presenter.getFromEditor();
        toEditor = presenter.getToEditor();
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void hideFirstMonthSelector() {
        firstMonthLabel.setVisible(false);
        firstMonthList.setVisible(false);
    }

    @Override
    public void showFirstMonthSelector() {
        firstMonthLabel.setVisible(true);
        firstMonthList.setVisible(true);
    }

    @Override
    public void clearFirstMonthSelector() {
        firstMonthList.clear();
    }

    @Override
    public void addFirstMonthItem(Month month) {
        firstMonthList.addItem(MonthConstants.INSTANCE.getString(month.name()));
    }

    @Override
    public void setSelectedFirstMonthIndex(int index) {
        firstMonthList.setSelectedIndex(index);
    }

    @Override
    public int getSelectedFirstMonthIndex() {
        return firstMonthList.getSelectedIndex();
    }

    // UI events

    @UiHandler(value = "firstMonthList")
    public void onFirstMonthSelected(ChangeEvent changeEvent) {
        presenter.changeFirstMonth();
    }
}

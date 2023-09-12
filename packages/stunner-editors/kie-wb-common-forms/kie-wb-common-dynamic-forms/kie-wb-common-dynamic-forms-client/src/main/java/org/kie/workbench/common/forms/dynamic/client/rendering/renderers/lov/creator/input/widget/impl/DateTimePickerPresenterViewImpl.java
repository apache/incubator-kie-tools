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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import javax.inject.Inject;

import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DateTimePickerPresenterViewImpl implements DateTimePickerPresenterView,
                                                        IsElement {

    public static final String DATE_PICKER_CELL_STYLE = "kie-wb-common-forms-lov-creator-datetimepickercell";

    private Presenter presenter;

    @Inject
    @DataField
    private Div container;

    private DateTimePicker dateTimePicker = new DateTimePicker();

    public void initDatePicker() {
        DOMUtil.removeAllChildren(container);

        dateTimePicker = new DateTimePicker();
        dateTimePicker.setGWTFormat(DateEditableColumnGenerator.DEFAULT_DATE_AND_TIME_FORMAT_MASK);
        dateTimePicker.setHighlightToday(true);
        dateTimePicker.setShowTodayButton(true);
        dateTimePicker.setAutoClose(true);

        dateTimePicker.addChangeDateHandler(event -> {
            presenter.notifyDateChange(dateTimePicker.getValue());
        });

        dateTimePicker.addHideHandler(hideEvent -> {
            hideEvent.stopPropagation();

            presenter.notifyHide();
        });
        dateTimePicker.getElement().setClassName(DATE_PICKER_CELL_STYLE);

        DOMUtil.appendWidgetToElement(container, dateTimePicker);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {

        initDatePicker();

        dateTimePicker.setValue(presenter.getDate());
        dateTimePicker.show();
    }

    @Override
    public void hide() {
        dateTimePicker.hide();
    }
}

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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input;

import java.util.Date;

import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RootPanel;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

@Templated
public class DatePickerWrapperViewImpl extends Composite implements DatePickerWrapperView {

    private Presenter presenter;
    private DatePicker datePicker;
    private DateTimePicker dateTimePicker;
    private boolean disabledClearButton = false;
    private boolean showtime;

    @Inject
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Inject
    TranslationService translationService;

    @Inject
    @DataField
    private Span selector;

    @Inject
    @DataField
    private Button clearBtn;

    @Inject
    @DataField
    private Button showCalendarBtn;

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        initialiseTooltips();
    }

    @Override
    public void setDatePickerWidget(boolean showTime) {

        this.showtime = showTime;
        DOMUtil.removeAllChildren(selector);
        if (showtime) {
            dateTimePicker = new DateTimePicker();
            dateTimePicker.setAutoClose(true);
            dateTimePicker.setHighlightToday(true);
            dateTimePicker.setShowTodayButton(true);
            dateTimePicker.setReadOnly(true);
            DOMUtil.appendWidgetToElement(selector, dateTimePicker);
        } else {
            datePicker = new DatePicker();
            datePicker.setAutoClose(true);
            datePicker.setHighlightToday(true);
            datePicker.setShowTodayButton(true);
            datePicker.setReadOnly(true);
            datePicker.setContainer(RootPanel.get());
            DOMUtil.appendWidgetToElement(selector, datePicker);
        }
    }

    @Override
    public void setId(String id) {
        if (showtime) {
            dateTimePicker.setId(id);
        } else {
            datePicker.setId(id);
        }
    }

    @Override
    public void setName(String name) {
        if (showtime) {
            dateTimePicker.setName(name);
        } else {
            datePicker.setName(name);
        }
    }

    @Override
    public void setPlaceholder(String placeholder) {
        if (showtime) {
            dateTimePicker.setPlaceholder(placeholder);
        } else {
            datePicker.setPlaceholder(placeholder);
        }
    }

    @Override
    public void setDateValue(Date date) {
        if (showtime) {
            dateTimePicker.setValue(date);
        } else {
            datePicker.setValue(date);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (showtime) {
            dateTimePicker.setEnabled(enabled);
        } else {
            datePicker.setEnabled(enabled);
        }
    }

    @Override
    public Date getDateValue() {
        if (showtime) {
            return dateTimePicker.getValue();
        } else {
            return datePicker.getValue();
        }
    }

    @Override
    public void addDateValueChangeHandler(ValueChangeHandler<Date> handler) {
        if (showtime) {
            dateTimePicker.addValueChangeHandler(handler);
        } else {
            datePicker.addValueChangeHandler(handler);
        }
    }

    @Override
    public void disableActions() {
        if (showtime) {
            dateTimePicker.hide();
        } else {
            datePicker.hide();
        }
        disabledClearButton = true;
        clearBtn.setDisabled(true);
        showCalendarBtn.setDisabled(true);
    }

    public void initialiseTooltips() {

        clearBtn.setAttribute("data-content",
                              translationService.getTranslation(FormRenderingConstants.DatePickerWrapperViewImplClearDateTooltip));
        showCalendarBtn.setAttribute("data-content",
                                     translationService.getTranslation(FormRenderingConstants.DatePickerWrapperViewImplShowDateTooltip));
        jQueryPopover.wrap(clearBtn).popover();
        jQueryPopover.wrap(showCalendarBtn).popover();
    }

    @Override
    public HasValue<Date> wrapped() {
        return presenter;
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("clearBtn")
    public void onClear(Event event) {
        if (!disabledClearButton) {
            this.presenter.setValue(null, true);
        }
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("showCalendarBtn")
    public void onShowCalendar(Event event) {
        if (!disabledClearButton) {
            if (showtime) {
                dateTimePicker.show();
            } else {
                datePicker.show();
            }
        }
    }
}

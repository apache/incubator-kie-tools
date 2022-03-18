/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.common;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.gwtbootstrap3.client.shared.event.HideEvent;
import org.gwtbootstrap3.client.shared.event.HideHandler;
import org.gwtbootstrap3.client.shared.event.ShowHandler;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasPlaceholder;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.constants.DeviceSize;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.constants.*;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.events.ChangeDateHandler;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.events.ChangeMonthHandler;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.events.ChangeYearHandler;
import org.gwtbootstrap3.extras.datepicker.client.ui.base.events.ClearDateHandler;

/**
 * A DatePicker wrapping BS3's DatePicker component adding additional functionality
 * required by KIE Workbench.; such as prevention of empty values and support for
 * setting the date format with GWT's DateTimeFormat patterns.
 */
public class DatePicker extends Composite
        implements HasEnabled,
                   Focusable,
                   HasId,
                   HasResponsiveness,
                   HasVisibility,
                   HasPlaceholder,
                   HasAutoClose,
                   HasDaysOfWeekDisabled,
                   HasEndDate,
                   HasForceParse,
                   HasHighlightToday,
                   HasKeyboardNavigation,
                   HasMinView,
                   HasShowTodayButton,
                   HasStartDate,
                   HasStartView,
                   HasViewSelect,
                   HasWeekStart,
                   HasDateTimePickerHandlers,
                   HasLanguage,
                   HasName,
                   HasValue<Date>,
                   HasPosition,
                   IsEditor<LeafValueEditor<Date>> {

    private final boolean allowEmptyValues;
    private final org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker datePicker;
    //Default GWT date format
    private String gwtDateFormat = "dd-MMM-yyyy";
    private DateTimeFormat gwtDateTimeFormat = DateTimeFormat.getFormat(gwtDateFormat);
    private String localeName;

    public DatePicker() {
        this(true);
    }

    /**
     * Basic constuctor of uberfire datePicker without setting the datepicker container and without setting the hide
     * handler to manage the interaction with org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker popup
     * @param datePicker
     */
    public DatePicker(org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker datePicker) {
        this.datePicker = datePicker;
        this.allowEmptyValues = true;
    }

    public DatePicker(final boolean allowEmptyValues) {
        datePicker = GWT.create(org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker.class);
        this.allowEmptyValues = allowEmptyValues;
        datePicker.setContainer(RootPanel.get());

        datePicker.setAutoClose(true);
        setLocaleName();
        setFormat(gwtDateFormat);

        //When the popup Date Picker component is hidden assert empty values
        datePicker.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent hideEvent) {
                final Date value = getDataPickerDate();
                if (!allowEmptyValues && value == null) {
                    doSetValue(new Date(),
                               true);
                } else {
                    doSetValue(value,
                               true);
                }
            }
        });

        initWidget(datePicker);
    }

    protected Date getDataPickerDate() {
        DateTimeFormat dtf = DateTimeFormat.getFormat("dd/M/yyyy");
        String dateStr = parseDate(datePicker.getTextBox().getElement(),
                                   DatePickerFormatUtilities.convertToBS3DateFormat("dd/M/yyyy"));
        if(dateStr == null || dateStr.isEmpty()){
            return null;
        } else {
            return dtf.parse(dateStr);
        }
    }

    public void setContainer(final Widget container) {
        datePicker.setContainer(container);
    }

    @Override
    public void setAutoClose(final boolean autoClose) {
        datePicker.setAutoClose(autoClose);
    }

    @Override
    public void onShow(final Event e) {
        datePicker.onShow(e);
    }

    @Override
    public HandlerRegistration addShowHandler(final ShowHandler showHandler) {
        return datePicker.addShowHandler(showHandler);
    }

    @Override
    public void onHide(final Event e) {
        datePicker.onHide(e);
    }

    @Override
    public HandlerRegistration addHideHandler(final HideHandler hideHandler) {
        return datePicker.addHideHandler(hideHandler);
    }

    @Override
    public void onChangeDate(final Event e) {
        datePicker.onChangeDate(e);
    }

    @Override
    public HandlerRegistration addChangeDateHandler(final ChangeDateHandler changeDateHandler) {
        return datePicker.addChangeDateHandler(changeDateHandler);
    }

    @Override
    public void onChangeYear(final Event e) {
        datePicker.onChangeYear(e);
    }

    @Override
    public HandlerRegistration addChangeYearHandler(final ChangeYearHandler changeYearHandler) {
        return datePicker.addChangeYearHandler(changeYearHandler);
    }

    @Override
    public void onChangeMonth(final Event e) {
        datePicker.onChangeMonth(e);
    }

    @Override
    public HandlerRegistration addChangeMonthHandler(final ChangeMonthHandler changeMonthHandler) {
        return datePicker.addChangeMonthHandler(changeMonthHandler);
    }

    @Override
    public void onClearDate(final Event e) {
        datePicker.onClearDate(e);
    }

    @Override
    public HandlerRegistration addClearDateHandler(final ClearDateHandler outOfRangeHandler) {
        return datePicker.addClearDateHandler(outOfRangeHandler);
    }

    @Override
    public void setDaysOfWeekDisabled(final DatePickerDayOfWeek... daysOfWeekDisabled) {
        datePicker.setDaysOfWeekDisabled(daysOfWeekDisabled);
    }

    @Override
    public boolean isEnabled() {
        return datePicker.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        datePicker.setEnabled(enabled);
    }

    @Override
    public void setEndDate(final Date endDate) {
        datePicker.setEndDate(endDate);
    }

    @Override
    public void setEndDate(final String endDate) {
        datePicker.setEndDate(endDate);
    }

    @Override
    public void clearEndDate() {
        datePicker.clearEndDate();
    }

    @Override
    public void setForceParse(final boolean forceParse) {
        datePicker.setForceParse(forceParse);
    }

    /**
     * Set the format of the Date shown in the TextBox component.
     * This is converted to BS3's Date Format that the underlying jQuery-based BS3 DatePicker
     * uses to convert values in the TextBox to selections in the popup date picker element.
     * @param gwtDateFormat
     */
    public void setFormat(final String gwtDateFormat) {
        this.gwtDateFormat = gwtDateFormat;
        this.gwtDateTimeFormat = DateTimeFormat.getFormat(this.gwtDateFormat);
        if (getLocaleName().equals("")) {
            datePicker.setLanguage(DatePickerLanguage.EN);
        } else {
            datePicker.setLanguage(DatePickerLanguage.valueOf(getLocaleName().toUpperCase()));
        }
        datePicker.setFormat(DatePickerFormatUtilities.convertToBS3DateFormat(gwtDateFormat));
    }

    public String getLocaleName() {
        if (localeName != null && !localeName.isEmpty() && !localeName.equalsIgnoreCase("default")) {
            return localeName;
        }
        return "";
    }

    public void setLocaleName(String localeName) {
        if (localeName != null) {
            this.localeName = localeName;
        } else {
            this.localeName = "";
        }
    }

    public void setLocaleName() {
        localeName = LocaleInfo.getCurrentLocale().getLocaleName();
    }

    @Override
    public void setHighlightToday(final boolean highlightToday) {
        datePicker.setHighlightToday(highlightToday);
    }

    @Override
    public String getId() {
        return datePicker.getId();
    }

    @Override
    public void setId(final String id) {
        datePicker.setId(id);
    }

    @Override
    public void setHasKeyboardNavigation(final boolean hasKeyboardNavigation) {
        datePicker.setHasKeyboardNavigation(hasKeyboardNavigation);
    }

    @Override
    public DatePickerLanguage getLanguage() {
        return datePicker.getLanguage();
    }

    @Override
    public void setLanguage(final DatePickerLanguage language) {
        datePicker.setLanguage(language);
    }

    @Override
    public void setMinView(final DatePickerMinView datePickerMinView) {
        datePicker.setMinView(datePickerMinView);
    }

    @Override
    public String getName() {
        return datePicker.getName();
    }

    @Override
    public void setName(final String name) {
        datePicker.setName(name);
    }

    @Override
    public String getPlaceholder() {
        return datePicker.getPlaceholder();
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        datePicker.setPlaceholder(placeholder);
    }

    @Override
    public DatePickerPosition getPosition() {
        return datePicker.getPosition();
    }

    @Override
    public void setPosition(final DatePickerPosition position) {
        datePicker.setPosition(position);
    }

    @Override
    public void setVisibleOn(final DeviceSize deviceSize) {
        datePicker.setVisibleOn(deviceSize);
    }

    @Override
    public void setHiddenOn(final DeviceSize deviceSize) {
        datePicker.setHiddenOn(deviceSize);
    }

    @Override
    public void setShowTodayButton(final boolean showTodayButton) {
        datePicker.setShowTodayButton(showTodayButton);
    }

    @Override
    public void setStartDate(final Date startDate) {
        datePicker.setStartDate(startDate);
    }

    @Override
    public void setStartDate(final String startDate) {
        datePicker.setStartDate(startDate);
    }

    @Override
    public void clearStartDate() {
        datePicker.clearStartDate();
    }

    @Override
    public void setStartView(final DatePickerMinView datePickerMinView) {
        datePicker.setStartView(datePickerMinView);
    }

    @Override
    //We don't delegate this to the wrapped DatePicker as that has some issues with the mismatch between GWT's DateFormat and BS3's DateFormat
    public Date getValue() {
        try {
            return gwtDateTimeFormat != null && datePicker.getTextBox().getValue() != null ? gwtDateTimeFormat.parse(datePicker.getTextBox().getValue()) : null;
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    //We don't delegate this to the wrapped DatePicker as that has some issues with the mismatch between GWT's DateFormat and BS3's DateFormat
    public void setValue(final Date value) {
        if (!allowEmptyValues && value == null) {
            doSetValue(new Date(),
                       true);
        } else {
            doSetValue(value,
                       false);
        }
    }

    @Override
    //We don't delegate this to the wrapped DatePicker as that has some issues with the mismatch between GWT's DateFormat and BS3's DateFormat
    public void setValue(final Date value,
                         final boolean fireEvents) {
        if (!allowEmptyValues && value == null) {
            doSetValue(new Date(),
                       true);
        } else {
            doSetValue(value,
                       fireEvents);
        }
    }

    //This is essentially an override of the wrapped DatePicker's setValue() method however
    //we need to override it to use the "improved" GWT DateFormat<->BS3's DateFormat handling.
    private void doSetValue(final Date value,
                            final boolean fireEvents) {
        datePicker.getTextBox().setValue(value != null ? gwtDateTimeFormat.format(value) : null);
        update(datePicker.getTextBox().getElement());

        if (fireEvents) {
            ValueChangeEvent.fire(datePicker,
                                  value);
        }
    }

    //Unfortunately the wrapped DatePicker hides the "update" method so we have to repeat it here
    private native void update(Element e) /*-{
        $wnd.jQuery(e).datepicker('update');
    }-*/;

    private final native String parseDate(Element e,
                                          String format) /*-{
        var dateStr = $wnd.jQuery(e).datepicker('getFormattedDate', format);
        return dateStr
    }-*/;

    @Override
    public com.google.gwt.event.shared.HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Date> handler) {
        return datePicker.addValueChangeHandler(handler);
    }

    @Override
    public void setViewSelect(final DatePickerMinView datePickerMinView) {
        datePicker.setViewSelect(datePickerMinView);
    }

    @Override
    public void setWeekStart(final DatePickerDayOfWeek weekStart) {
        datePicker.setWeekStart(weekStart);
    }

    @Override
    public LeafValueEditor<Date> asEditor() {
        return datePicker;
    }

    @Override
    public int getTabIndex() {
        return datePicker.getTextBox().getTabIndex();
    }

    @Override
    public void setTabIndex(final int index) {
        datePicker.getTextBox().setTabIndex(index);
    }

    @Override
    public void setAccessKey(final char key) {
        datePicker.getTextBox().setAccessKey(key);
    }

    @Override
    public void setFocus(final boolean focused) {
        datePicker.getTextBox().setFocus(focused);
    }

    public HandlerRegistration addBlurHandler(final BlurHandler handler) {
        return datePicker.getTextBox().addBlurHandler(handler);
    }
}

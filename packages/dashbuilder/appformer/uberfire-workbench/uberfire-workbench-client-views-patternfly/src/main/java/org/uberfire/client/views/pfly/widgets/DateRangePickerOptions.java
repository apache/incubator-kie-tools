/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
public class DateRangePickerOptions {

    private DateRangePickerOptions() {
    }

    @JsOverlay
    public static final DateRangePickerOptions create() {
        final DateRangePickerOptions options = new DateRangePickerOptions();

        options.setApplyClass("btn-primary");
        options.setLocale(new Object());
        options.setFormat("lll");

        options.setRanges(new DateRangePickerOptions.Range());

        return options;
    }

    @JsProperty
    public native void setApplyClass(String applyClass);

    @JsProperty
    public native void setStartDate(Moment startDate);

    @JsProperty
    public native void setEndDate(Moment endDate);

    @JsProperty
    public native void setMinDate(Moment minDate);

    @JsProperty
    public native void setMaxDate(Moment maxDate);

    @JsProperty
    public native void setAutoApply(Boolean autoApply);

    @JsProperty
    public native void setAutoUpdateInput(Boolean autoUpdateInput);

    @JsProperty
    public native void setShowCustomRangeLabel(Boolean showCustomRangeLabel);

    @JsProperty
    public native void setTimePicker(Boolean timePicker);

    @JsProperty
    public native void setTimePickerIncrement(int timePickerIncrement);

    @JsProperty
    public native void setTimePicker24Hour(Boolean timePicker24Hour);

    @JsProperty
    public native void setSingleDatePicker(Boolean singleDatePicker);

    @JsProperty
    public native void setDrops(String drops);

    @JsProperty
    public native void setParentEl(String parentEl);

    @JsProperty
    protected native void setLocale(Object locale);

    @JsProperty(name = "locale.format")
    public native void setFormat(String format);

    @JsProperty(name = "locale.customRangeLabel")
    public native void setCustomRangeLabel(String customRangeLabel);

    @JsProperty(name = "locale.applyLabel")
    public native void setApplyLabel(String applyLabel);

    @JsProperty(name = "locale.cancelLabel")
    public native void setCancelLabel(String cancelLabel);

    @JsProperty(name = "locale.fromLabel")
    public native void setFromLabel(String fromLabel);

    @JsProperty(name = "locale.toLabel")
    public native void setToLabel(String toLabel);

    @JsProperty(name = "locale.weekLabel")
    public native void setWeekLabel(String weekLabel);

    @JsProperty(name = "locale.daysOfWeek")
    public native void setDaysOfWeek(String[] daysOfWeek);

    @JsProperty(name = "locale.monthNames")
    public native void setMonthNames(String[] monthNames);

    @JsProperty
    protected native Range getRanges();

    @JsProperty
    protected native void setRanges(Range range);

    @JsOverlay
    public final void addRange(final String key,
                               final Moment startRange,
                               final Moment endRange) {
        getRanges().addRange(key,
                             startRange,
                             endRange);
    }

    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    private static class Range {

        public static native void defineProperty(Range range,
                                                 String property,
                                                 PropertyDescriptor descriptor);

        @JsOverlay
        public final void addRange(final String key,
                                   final Moment startRange,
                                   final Moment endRange) {
            final PropertyDescriptor descriptor = new PropertyDescriptor();
            final Moment[] value = new Moment[]{startRange, endRange};
            descriptor.setValue(value);
            descriptor.setEnumerable(true);
            defineProperty(this,
                           key,
                           descriptor);
        }
    }

    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    private static class PropertyDescriptor {

        @JsProperty
        public native void setValue(Object value);

        @JsProperty
        public native void setEnumerable(boolean enumerable);
    }
}

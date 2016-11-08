/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.kie.workbench.common.forms.common.rendering.client.widgets.flatViews.impl.DateFlatView;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.model.impl.basic.datePicker.DatePickerFieldDefinition;

@Dependent
public class DatePickerFieldRenderer extends FieldRenderer<DatePickerFieldDefinition> {

    private Widget input;

    protected WidgetHandler handler;

    @Override
    public String getName() {
        return "DatePicker";
    }

    @Override
    public void initInputWidget() {
        if ( field.getShowTime() ) {
            DateTimePicker box = new DateTimePicker();
            box.setPlaceholder( field.getPlaceHolder() );
            box.setEnabled( !field.getReadonly() );
            box.setAutoClose( true );
            box.setHighlightToday( true );
            box.setShowTodayButton( true );
            handler = readOnly -> box.setEnabled( !readOnly );
            input = box;
        } else {
            final DatePicker box = new DatePicker();
            box.setPlaceholder( field.getPlaceHolder() );
            box.setEnabled( !field.getReadonly() );
            box.setAutoClose( true );
            box.setHighlightToday( true );
            box.setShowTodayButton( true );
            handler = readOnly -> box.setEnabled( !readOnly );
            input = box;
        }
    }

    @Override
    public IsWidget getInputWidget() {
        return input;
    }

    @Override
    public IsWidget getPrettyViewWidget() {
        return new DateFlatView();
    }

    @Override
    public String getSupportedCode() {
        return DatePickerFieldDefinition.CODE;
    }

    @Override
    protected void setReadOnly( boolean readOnly ) {
        handler.setReadOnly( readOnly );
    }

    protected interface WidgetHandler {
        void setReadOnly( boolean readOnly );
    }
}

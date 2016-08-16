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

package org.kie.workbench.common.forms.model.impl.basic.datePicker;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.forms.model.impl.basic.HasPlaceHolder;

@Portable
@Bindable
public class DatePickerFieldDefinition extends FieldDefinition implements HasPlaceHolder {
    public static final String CODE = "DatePicker";

    @FieldDef( label = "Placeholder", position = 1)
    protected String placeHolder = "";

    @FieldDef( label = "Show Time", position = 2)
    protected Boolean showTime = Boolean.TRUE;

    public DatePickerFieldDefinition() {
        super( CODE );
    }

    @Override
    public String getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public void setPlaceHolder( String placeHolder ) {
        this.placeHolder = placeHolder;
    }

    public Boolean getShowTime() {
        return showTime;
    }

    public void setShowTime( Boolean showTime ) {
        this.showTime = showTime;
    }

    @Override
    protected void doCopyFrom( FieldDefinition other ) {
        if ( other instanceof DatePickerFieldDefinition ) {
            DatePickerFieldDefinition otherDate = (DatePickerFieldDefinition) other;
            setPlaceHolder( otherDate.getPlaceHolder() );
            setShowTime( otherDate.getShowTime() );
        } else if ( other instanceof  HasPlaceHolder ) {
            setPlaceHolder( ((HasPlaceHolder) other).getPlaceHolder() );
        }
    }
}

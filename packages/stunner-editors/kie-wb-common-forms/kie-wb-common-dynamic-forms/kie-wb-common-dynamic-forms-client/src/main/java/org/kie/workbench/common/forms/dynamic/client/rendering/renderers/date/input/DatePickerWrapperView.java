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

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.common.rendering.client.widgets.FormWidget;

public interface DatePickerWrapperView extends IsWidget,
                                               FormWidget<Date> {

    void setPresenter(Presenter presenter);

    void setDatePickerWidget(boolean showTime);

    Date getDateValue();

    void setDateValue(Date dateValue);

    void addDateValueChangeHandler(ValueChangeHandler<Date> handler);

    void disableActions();

    void setId(String id);

    void setName(String name);

    void setPlaceholder(String placeholder);

    void setEnabled(boolean enabled);

    interface Presenter extends HasValue<Date> {

        void setDatePickerWidget(boolean showTime);

        void disableActions();

        void setId(String id);

        void setName(String name);

        void setPlaceholder(String placeholder);

        void setEnabled(boolean enabled);
    }
}

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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

public interface ComboBoxWidgetView extends IsWidget {

    static final String CUSTOM_PROMPT = "New" + ListBoxValues.EDIT_SUFFIX;
    static final String ENTER_TYPE_PROMPT = "Enter name" + ListBoxValues.EDIT_SUFFIX;

    void setComboBoxValues(final ListBoxValues valueListBoxValues);

    void setValue(final String value);

    String getValue();

    void setReadOnly(boolean readOnly);
}

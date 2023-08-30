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

import javax.annotation.PostConstruct;

import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.comboBoxEditor.annotation.FixedValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;

/**
 * Combobox with fixed list of values, it doesn't allow custom values to be inserted.
 */
@FixedValues
@Templated("ComboBoxWidget.html")
public class ComboBoxFixedValuesWidgetViewImpl extends ComboBoxWidgetViewImpl implements ComboBoxFixedValuesWidgetView {

    @PostConstruct
    public void init() {
        super.init();
        //Do not allow custom values to be inserted on the combobox
        valueComboBox.setShowCustomValues(false);
    }

    @Override
    public void setComboBoxValues(final ListBoxValues valueListBoxValues) {
        valueComboBox.setListBoxValues(valueListBoxValues);
    }
}

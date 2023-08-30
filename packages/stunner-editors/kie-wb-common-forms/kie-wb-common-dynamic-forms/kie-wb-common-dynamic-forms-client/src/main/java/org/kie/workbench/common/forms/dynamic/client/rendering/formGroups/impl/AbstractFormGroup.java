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


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractFormGroup<VIEW extends FormGroupView> implements FormGroup {

    protected Widget bindable;

    protected VIEW view;

    public AbstractFormGroup(VIEW view) {
        this.view = view;
    }

    public void render(Widget bindable, FieldDefinition fieldDefinition) {
        this.bindable = bindable;

        view.render(bindable, fieldDefinition);
    }

    @Override
    public IsWidget getBindableWidget() {
        return bindable;
    }

    @Override
    public void setVisible(boolean visible) {
        view.setVisible(visible);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
    
    @Override
    public Map<String, Widget> getPartsWidgets() {
        return view.getViewPartsWidgets();
    }
}

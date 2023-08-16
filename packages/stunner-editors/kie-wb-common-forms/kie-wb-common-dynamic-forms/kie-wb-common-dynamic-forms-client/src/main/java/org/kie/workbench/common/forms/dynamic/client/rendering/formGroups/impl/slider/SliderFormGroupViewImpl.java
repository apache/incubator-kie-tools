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


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
public class SliderFormGroupViewImpl implements SliderFormGroupView {

    private static final String PART_SLIDER_LABEL = "Slider Label";

    @Inject
    @DataField
    private FieldLabel fieldLabel;

    @Inject
    @DataField
    protected Div fieldContainer;

    @Inject
    @DataField
    protected Div helpBlock;

    @Inject
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private Map<String, Widget> viewPartsWidgets = new HashMap<>();

    @Override
    public void render(Widget widget,
                       FieldDefinition fieldDefinition) {

        render("", widget, fieldDefinition);
        
        viewPartsWidgets.put(PART_SLIDER_LABEL,
                             wrapperWidgetUtil.getWidget(this, fieldLabel.getElement()));
    }

    public void render(String inputId, Widget widget, FieldDefinition fieldDefinition) {

        fieldLabel.renderForInputId(inputId, fieldDefinition);

        DOMUtil.appendWidgetToElement(fieldContainer, widget);
        
    }

    @Override
    public HTMLElement getHelpBlock() {
        return helpBlock;
    }
    
    @Override
    public Map<String, Widget> getViewPartsWidgets() {
        return viewPartsWidgets;
    }

    @PreDestroy
    public void clear() {
        wrapperWidgetUtil.clear(this);
    }
}

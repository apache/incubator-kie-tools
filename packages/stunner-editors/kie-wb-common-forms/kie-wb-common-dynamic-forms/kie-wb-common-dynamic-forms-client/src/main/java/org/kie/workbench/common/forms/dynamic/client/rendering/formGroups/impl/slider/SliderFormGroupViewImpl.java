/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
@Dependent
public class SliderFormGroupViewImpl implements SliderFormGroupView {

    private static final String PART_SLIDER_LABEL = "Slider Label";

    @Inject
    @DataField
    private FieldLabel fieldLabel;

    @Inject
    @DataField
    protected HTMLDivElement fieldContainer;

    @Inject
    @DataField
    protected HTMLDivElement helpBlock;

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

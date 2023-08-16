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


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
public class FieldSetFormGroupViewImpl implements IsElement,
                                                  FieldSetFormGroupView {

    private static final String PART_LEGEND_TEXT = "Legend Text";

    @Inject
    private FieldRequired fieldRequired;

    @Inject
    private FieldHelp fieldHelp;

    @Inject
    @Named("legend")
    @DataField
    private HTMLElement legend;

    @Inject
    @DataField
    private Span legendText;

    @DataField
    protected SimplePanel fieldContainer = new SimplePanel();

    @Inject
    @DataField
    protected Div formGroup;

    @Inject
    @DataField
    protected Div helpBlock;

    @Inject
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private Map<String, Widget> partsWidgets = new HashMap<>();

    @Override
    public void render(Widget widget,
                       FieldDefinition field) {

        DOMUtil.addEnumStyleName(formGroup, Style.Visibility.HIDDEN);

        legendText.setTextContent(field.getLabel());

        if (field.getRequired()) {
            legend.appendChild(fieldRequired.getElement());
        }

        if (field.getHelpMessage() != null && !field.getHelpMessage().trim().isEmpty()) {
            fieldHelp.showHelpMessage(field.getHelpMessage());
            legend.appendChild(fieldHelp.getElement());
        }

        fieldContainer.clear();

        fieldContainer.add(widget);
        
        partsWidgets.put(PART_LEGEND_TEXT, wrapperWidgetUtil.getWidget(this, legendText));
    }
    
    @Override
    public Map<String, Widget> getViewPartsWidgets() {
        return partsWidgets;
    }

    @PreDestroy
    public void clear() {
        wrapperWidgetUtil.clear(this);
    }
}

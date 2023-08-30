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


package org.kie.workbench.common.forms.adf.service.definitions;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.adf.definitions.settings.LabelPosition;
import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutDefinition;

/**
 * Settings to generate {@link FormDefinition} for a specific java model
 */
public class FormDefinitionSettings {

    private String modelType;

    private I18nSettings i18nSettings = new I18nSettings();

    private LabelPosition labelPosition = LabelPosition.DEFAULT;

    private LayoutDefinition layout;

    private List<FormElement> formElements = new ArrayList<>();

    public FormDefinitionSettings(String modelType) {
        this.modelType = modelType;
    }

    public String getModelType() {
        return modelType;
    }

    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        this.labelPosition = labelPosition;
    }

    public LayoutDefinition getLayout() {
        return layout;
    }

    public void setLayout(LayoutDefinition layout) {
        this.layout = layout;
    }

    public void addFormElement(FormElement element) {
        if (element != null) {
            formElements.add(element);
        }
    }

    public List<FormElement> getFormElements() {
        return formElements;
    }

    public I18nSettings getI18nSettings() {
        return i18nSettings;
    }

    public void setI18nSettings(I18nSettings i18nSettings) {
        this.i18nSettings = i18nSettings;
    }
}

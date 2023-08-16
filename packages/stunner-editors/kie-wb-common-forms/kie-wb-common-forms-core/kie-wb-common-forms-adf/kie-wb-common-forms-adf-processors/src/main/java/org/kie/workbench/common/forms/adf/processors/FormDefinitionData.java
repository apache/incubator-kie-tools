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


package org.kie.workbench.common.forms.adf.processors;

import java.util.ArrayList;
import java.util.List;

public class FormDefinitionData {

    private final String modelClass;
    private final String builderClass;

    private List<FormDefinitionFieldData> elements = new ArrayList<>();
    private String startElement;
    private String i18nBundle;
    private List<String> layoutColumns;

    public FormDefinitionData(String modelClass, String builderClass) {
        this.modelClass = modelClass;
        this.builderClass = builderClass;
    }

    public String getModelClass() {
        return modelClass;
    }

    public String getBuilderClass() {
        return builderClass;
    }

    public List<FormDefinitionFieldData> getElements() {
        return elements;
    }

    public String getStartElement() {
        return startElement;
    }

    public void setStartElement(String startElement) {
        this.startElement = startElement;
    }

    public String getI18nBundle() {
        return i18nBundle;
    }

    public void setI18nBundle(String i18nBundle) {
        this.i18nBundle = i18nBundle;
    }

    public List<String> getLayoutColumns() {
        return layoutColumns;
    }

    public void setLayoutColumns(List<String> layoutColumns) {
        this.layoutColumns = layoutColumns;
    }
}


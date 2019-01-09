/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.template.client.editor;

public class TemplateDataColumn {

    private String templateVar;
    private String dataType;
    private String factType;
    private String factField;
    private String operator;

    public TemplateDataColumn(String templateVar,
                              String dataType,
                              String factType,
                              String factField,
                              String operator) {
        this.templateVar = templateVar;
        this.dataType = dataType;
        this.factType = factType;
        this.factField = factField;
        this.operator = operator;
    }

    public String getDataType() {
        return dataType;
    }

    public String getFactField() {
        return factField;
    }

    public void setFactField(String factField) {
        this.factField = factField;
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public String getTemplateVar() {
        return templateVar;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}

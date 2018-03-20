/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.migration.legacy.model;

import org.apache.commons.lang3.StringUtils;

public class DataHolder implements Comparable {

    private String id;
    private String inputId;
    private String outputId;
    private String type;
    private String className;
    private String renderColor;
    private String supportedType;

    public DataHolder(String id, String inputId, String outputId, String type, String className, String renderColor, String supportedType) {
        this.id = id;
        this.inputId = inputId;
        this.outputId = outputId;
        this.type = type;
        this.className = className;
        this.renderColor = renderColor;
        this.supportedType = supportedType;
    }

    public String getUniqeId() {
        return id;
    }

    public String getInputId() {
        return inputId;
    }

    public String getOuputId() {
        return outputId;
    }

    public String getType() {
        return type;
    }

    public String getSupportedType() {
        return supportedType;
    }

    public String getClassName() {
        return className;
    }

    public String getRenderColor() {
        return renderColor;
    }

    public boolean containsInputBinding(String bindingString) {
        return containsBinding(bindingString, getInputId());
    }

    public boolean containsOutputBinding(String bindingString) {
        return containsBinding(bindingString, getOuputId());
    }

    public boolean containsBinding(String bindingString) {
        return containsBinding(bindingString, getInputId()) || containsBinding(bindingString, getOuputId());
    }

    protected boolean containsBinding(String bindingString, String id) {
        if (StringUtils.isEmpty(bindingString) || StringUtils.isEmpty(id)) {
            return false;
        }

        String[] parts = bindingString.split("/");

        if (parts == null || parts.length != 2 || StringUtils.isEmpty(parts[0])) {
            return false;
        }

        return id.equals(parts[0]);
    }

    public int compareTo(Object o) {
        return getUniqeId().compareTo(((DataHolder) o).getUniqeId());
    }

    public boolean ownsField(Field field) {
        return containsBinding(field.getInputBinding()) || containsBinding(field.getOutputBinding());
    }
}

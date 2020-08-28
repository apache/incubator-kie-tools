/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.external.model;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ComponentParameter {

    private String name;
    private String type;
    private String category;
    private String defaultValue;
    private String label;
    private List<String> comboValues;

    public ComponentParameter() {
        // default constructor used internally
    }

    public ComponentParameter(@MapsTo("name") String name,
                              @MapsTo("type") String type,
                              @MapsTo("category") String category,
                              @MapsTo("defaultValue") String defaultValue,
                              @MapsTo("label") String label,
                              @MapsTo("comboValues") List<String> comboValues) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.defaultValue = defaultValue;
        this.label = label;
        this.comboValues = comboValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getComboValues() {
        return comboValues;
    }

    public void setComboValues(List<String> comboValues) {
        this.comboValues = comboValues;
    }

}
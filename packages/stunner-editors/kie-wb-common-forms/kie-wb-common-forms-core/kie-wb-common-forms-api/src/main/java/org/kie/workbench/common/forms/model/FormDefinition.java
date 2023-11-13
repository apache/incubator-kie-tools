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

package org.kie.workbench.common.forms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@Portable
public class FormDefinition {

    private String id;
    private String name;
    private FormModel model;

    private List<FieldDefinition> fields = new ArrayList<FieldDefinition>();

    private LayoutTemplate layoutTemplate;

    public FormDefinition() {
    }

    public FormDefinition(@MapsTo("model") FormModel model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

    public LayoutTemplate getLayoutTemplate() {
        return layoutTemplate;
    }

    public void setLayoutTemplate(LayoutTemplate layoutTemplate) {
        this.layoutTemplate = layoutTemplate;
    }

    public FormModel getModel() {
        return model;
    }

    public void setModel(FormModel model) {
        this.model = model;
    }

    public FieldDefinition getFieldByBinding(final String binding) {
        return getFieldBy(field -> field.getBinding() != null && field.getBinding().equals(binding));
    }

    public FieldDefinition getFieldByName(final String name) {
        return getFieldBy(field -> field.getName().equals(name));
    }

    public FieldDefinition getFieldById(final String fieldId) {
        return getFieldBy(field -> field.getId().equals(fieldId));
    }

    public FieldDefinition getFieldByBoundProperty(final ModelProperty property) {
        return getFieldByBinding(property.getName());
    }

    protected FieldDefinition getFieldBy(Predicate<FieldDefinition> predicate) {
        if (predicate != null) {
            Optional<FieldDefinition> result = fields.stream().filter(predicate).findFirst();
            if (result.isPresent()) {
                return result.get();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FormDefinition form = (FormDefinition) o;

        if (!id.equals(form.id)) {
            return false;
        }
        if (!name.equals(form.name)) {
            return false;
        }
        if (!model.equals(form.model)) {
            return false;
        }
        if (!fields.equals(form.fields)) {
            return false;
        }
        return layoutTemplate.equals(form.layoutTemplate);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ~~result;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = ~~result;
        result = 31 * result + model.hashCode();
        result = ~~result;
        result = 31 * result + fields.hashCode();
        result = ~~result;
        result = 31 * result + layoutTemplate.hashCode();
        result = ~~result;
        return result;
    }
}

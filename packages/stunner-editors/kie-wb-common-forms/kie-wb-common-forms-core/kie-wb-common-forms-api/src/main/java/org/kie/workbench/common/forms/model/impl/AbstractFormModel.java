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


package org.kie.workbench.common.forms.model.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeKind;

public abstract class AbstractFormModel implements FormModel {

    protected String name;

    protected List<ModelProperty> properties = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ModelProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<ModelProperty> properties) {
        this.properties = properties;
    }

    @Override
    public ModelProperty getProperty(final String name) {
        return properties.stream().filter(property -> property.getName().equals(name)).findFirst().orElse(null);
    }

    public void addProperty(final String name,
                            final String className) {
        addProperty(new ModelPropertyImpl(name, new TypeInfoImpl(className)));
    }

    public void addProperty(final String name,
                            final String className,
                            final TypeKind typeKind,
                            final boolean multiple) {
        addProperty(new ModelPropertyImpl(name, new TypeInfoImpl(typeKind, className, multiple)));
    }

    public void addProperty(final ModelProperty property) {
        Objects.requireNonNull(property, "Parameter named 'property' should be not null!");

        if (getProperty(property.getName()) != null) {
            throw new IllegalArgumentException("The model already has a '" + property.getName() + "' property.");
        }

        properties.add(property);
    }
}

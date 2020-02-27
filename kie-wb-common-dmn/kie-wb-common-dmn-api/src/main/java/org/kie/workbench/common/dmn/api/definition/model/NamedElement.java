/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.api.definition.model;

import javax.validation.Valid;

import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.NameHolder;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;

public abstract class NamedElement extends DMNElement implements HasName {

    protected Name name;

    @Property
    @FormField(afterElement = "description", type = NameFieldType.class)
    @Valid
    protected NameHolder nameHolder;

    public NamedElement() {
        this(new Id(),
             new Description(),
             new Name());
    }

    public NamedElement(final Id id,
                        final Description description,
                        final Name name) {
        super(id,
              description);
        this.name = name;
        this.nameHolder = new NameHolder(name);
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Name getName() {
        return nameHolder.getValue();
    }

    @Override
    public void setName(final Name name) {
        this.name = name;
        this.nameHolder.setValue(name);
    }

    // ------------------
    // Errai Data Binding
    // ------------------
    public NameHolder getNameHolder() {
        return nameHolder;
    }

    public void setNameHolder(final NameHolder nameHolder) {
        this.name.setValue(nameHolder.getValue().getValue());
        this.nameHolder = nameHolder;
    }
}

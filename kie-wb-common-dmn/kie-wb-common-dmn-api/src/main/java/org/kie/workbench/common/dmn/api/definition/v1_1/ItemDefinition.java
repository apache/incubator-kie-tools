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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static java.util.Collections.singletonList;

@Portable
public class ItemDefinition extends NamedElement implements HasTypeRef {

    private QName typeRef;
    private UnaryTests allowedValues;
    private List<ItemDefinition> itemComponent;
    private String typeLanguage;
    private Boolean isCollection;

    public ItemDefinition() {
        this(new Id(),
             new Description(),
             new Name(),
             null,
             null,
             null,
             null,
             false);
    }

    public ItemDefinition(final Id id,
                          final Description description,
                          final Name name,
                          final QName typeRef,
                          final UnaryTests allowedValues,
                          final List<ItemDefinition> itemComponent,
                          final String typeLanguage,
                          final Boolean isCollection) {
        super(id,
              description,
              name);
        this.typeRef = typeRef;
        this.allowedValues = allowedValues;
        this.itemComponent = itemComponent;
        this.typeLanguage = typeLanguage;
        this.isCollection = isCollection;
    }

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(final QName value) {
        this.typeRef = value;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(singletonList(this));
    }

    public UnaryTests getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(final UnaryTests value) {
        this.allowedValues = value;
    }

    public List<ItemDefinition> getItemComponent() {
        if (itemComponent == null) {
            itemComponent = new ArrayList<>();
        }
        return this.itemComponent;
    }

    public String getTypeLanguage() {
        return typeLanguage;
    }

    public void setTypeLanguage(final String value) {
        this.typeLanguage = value;
    }

    public boolean isIsCollection() {
        if (isCollection == null) {
            return false;
        } else {
            return isCollection;
        }
    }

    public void setIsCollection(final Boolean value) {
        this.isCollection = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemDefinition)) {
            return false;
        }

        final ItemDefinition that = (ItemDefinition) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (typeRef != null ? !typeRef.equals(that.typeRef) : that.typeRef != null) {
            return false;
        }
        if (allowedValues != null ? !allowedValues.equals(that.allowedValues) : that.allowedValues != null) {
            return false;
        }
        if (itemComponent != null ? !itemComponent.equals(that.itemComponent) : that.itemComponent != null) {
            return false;
        }
        if (typeLanguage != null ? !typeLanguage.equals(that.typeLanguage) : that.typeLanguage != null) {
            return false;
        }
        return isCollection != null ? isCollection.equals(that.isCollection) : that.isCollection == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         allowedValues != null ? allowedValues.hashCode() : 0,
                                         itemComponent != null ? itemComponent.hashCode() : 0,
                                         typeLanguage != null ? typeLanguage.hashCode() : 0,
                                         isCollection != null ? isCollection.hashCode() : 0);
    }
}

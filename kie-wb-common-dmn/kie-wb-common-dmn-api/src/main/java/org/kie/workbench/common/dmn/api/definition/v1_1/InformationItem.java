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

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.QNameHolder;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@PropertySet
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class InformationItem extends DMNElement implements HasName,
                                                           HasTypeRef,
                                                           DMNPropertySet {

    //InformationItem should extend NamedElement however we do not want to expose Name as a @FormField
    protected Name name;

    protected QName typeRef;

    @Property
    @FormField(afterElement = "name", type = QNameFieldType.class)
    @Valid
    protected QNameHolder typeRefHolder;

    public InformationItem() {
        this(new Id(),
             new Description(),
             new Name(),
             new QName());
    }

    public InformationItem(final @MapsTo("id") Id id,
                           final @MapsTo("description") Description description,
                           final @MapsTo("name") Name name,
                           final @MapsTo("typeRef") QName typeRef) {
        super(id,
              description);
        this.name = name;
        this.typeRef = typeRef;
        this.typeRefHolder = new QNameHolder(typeRef);
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public void setName(final Name name) {
        this.name = name;
    }

    @Override
    public QName getTypeRef() {
        return typeRefHolder.getValue();
    }

    @Override
    public void setTypeRef(final QName typeRef) {
        this.typeRefHolder.setValue(typeRef);
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    // ------------------
    // Errai Data Binding
    // ------------------
    public QNameHolder getTypeRefHolder() {
        return typeRefHolder;
    }

    public void setTypeRefHolder(final QNameHolder typeRefHolder) {
        this.typeRefHolder = typeRefHolder;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InformationItem)) {
            return false;
        }

        final InformationItem that = (InformationItem) o;

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
        return typeRefHolder != null ? typeRefHolder.equals(that.typeRefHolder) : that.typeRefHolder == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         typeRefHolder != null ? typeRefHolder.hashCode() : 0);
    }
}

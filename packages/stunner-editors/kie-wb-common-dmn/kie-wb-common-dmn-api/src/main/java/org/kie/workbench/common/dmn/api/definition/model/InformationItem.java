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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.NameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameFieldType;
import org.kie.workbench.common.dmn.api.property.dmn.QNameHolder;
import org.kie.workbench.common.dmn.api.resource.i18n.DMNAPIConstants;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static java.util.Collections.singletonList;

@Portable
@Bindable
@Definition
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class InformationItem extends NamedElement implements DMNPropertySet,
                                                             IsInformationItem {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new Sets.Builder<String>().build();

    protected QName typeRef;

    @Property
    @FormField(afterElement = "nameHolder", type = QNameFieldType.class)
    @Valid
    protected QNameHolder typeRefHolder;

    public InformationItem() {
        this(new Id(),
             new Description(),
             new Name(),
             new QName());
    }

    public InformationItem(final Id id,
                           final Description description,
                           final Name name,
                           final QName typeRef) {
        super(id,
              description,
              name);
        this.typeRef = typeRef;
        this.typeRefHolder = new QNameHolder(typeRef);
    }

    // -----------------------
    // Stunner core properties
    // -----------------------

    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public InformationItem copy() {
        final InformationItem clonedInformationItem = new InformationItem();
        clonedInformationItem.description = Optional.ofNullable(description).map(Description::copy).orElse(null);
        clonedInformationItem.nameHolder = Optional.ofNullable(nameHolder).map(NameHolder::copy).orElse(null);
        clonedInformationItem.typeRef = Optional.ofNullable(typeRef).map(QName::copy).orElse(null);
        clonedInformationItem.typeRefHolder = Optional.ofNullable(typeRefHolder).map(QNameHolder::copy).orElse(null);
        return clonedInformationItem;
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

    @Override
    public List<HasTypeRef> getHasTypeRefs() {
        return new ArrayList<>(singletonList(this));
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

    // ------------------------------------------------------
    // DomainObject requirements - to use in Properties Panel
    // ------------------------------------------------------

    @Override
    public String getDomainObjectUUID() {
        return getId().getValue();
    }

    @Override
    public String getDomainObjectNameTranslationKey() {
        return DMNAPIConstants.InformationItemPrimary_DomainObjectName;
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
        if (nameHolder != null ? !nameHolder.equals(that.nameHolder) : that.nameHolder != null) {
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
                                         nameHolder != null ? nameHolder.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0,
                                         typeRefHolder != null ? typeRefHolder.hashCode() : 0);
    }
}

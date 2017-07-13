/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.property.DMNPropertySet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Label;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldLabel;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;

@Portable
@Bindable
@PropertySet
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id")
public class InformationItem extends NamedElement implements DMNPropertySet {

    @FieldLabel
    @org.kie.workbench.common.stunner.core.definition.annotation.Name
    public static final transient String propertySetName = "InformationItem";

    @Property
    @FormField(afterElement = "name")
    protected QName typeRef;

    public InformationItem() {
        this(new Id(),
             new Label(),
             new Description(),
             new Name(),
             new QName());
    }

    public InformationItem(final @MapsTo("id") Id id,
                           final @MapsTo("label") Label label,
                           final @MapsTo("description") Description description,
                           final @MapsTo("name") Name name,
                           final @MapsTo("typeRef") QName typeRef) {
        super(id,
              label,
              description,
              name);
        this.typeRef = typeRef;
    }

    public String getPropertySetName() {
        return propertySetName;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public QName getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(final QName typeRef) {
        this.typeRef = typeRef;
    }
}

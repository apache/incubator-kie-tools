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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class ItemDefinition extends NamedElement {

    private QName typeRef;
    private UnaryTests allowedValues;
    private List<ItemDefinition> itemComponent;
    private String typeLanguage;
    private Boolean isCollection;

    public ItemDefinition() {
        this(new Id(),
             new Description(),
             new Name(),
             new QName(),
             new UnaryTests(),
             new ArrayList<>(),
             "",
             false);
    }

    public ItemDefinition(final @MapsTo("id") Id id,
                          final @MapsTo("description") Description description,
                          final @MapsTo("name") Name name,
                          final @MapsTo("typeRef") QName typeRef,
                          final @MapsTo("allowedValues") UnaryTests allowedValues,
                          final @MapsTo("itemComponent") List<ItemDefinition> itemComponent,
                          final @MapsTo("typeLanguage") String typeLanguage,
                          final @MapsTo("isCollection") Boolean isCollection) {
        super(id,
              description,
              name);
        this.typeRef = typeRef;
        this.allowedValues = allowedValues;
        this.itemComponent = itemComponent;
        this.typeLanguage = typeLanguage;
        this.isCollection = isCollection;
    }

    public QName getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(final QName value) {
        this.typeRef = value;
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
}

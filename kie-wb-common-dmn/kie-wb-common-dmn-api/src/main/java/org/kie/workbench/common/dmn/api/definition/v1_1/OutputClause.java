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
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.resource.i18n.DMNAPIConstants;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.domainobject.DomainObject;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static java.util.Collections.singletonList;
import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getNotNullHasTypeRefs;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        i18n = @I18nSettings(keyPreffix = "org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause"),
        startElement = "id")
public class OutputClause extends DMNElement implements HasTypeRef,
                                                        DomainObject {

    @Category
    private static final String stunnerCategory = Categories.DOMAIN_OBJECTS;

    @Labels
    private static final Set<String> stunnerLabels = new Sets.Builder<String>().build();

    @PropertySet
    @FormField(afterElement = "description", labelKey = "outputValues")
    protected OutputClauseUnaryTests outputValues;

    @PropertySet
    @FormField(afterElement = "outputValues", labelKey = "defaultOutputEntry")
    protected OutputClauseLiteralExpression defaultOutputEntry;

    private String name;
    private QName typeRef;

    public OutputClause() {
        this(new Id(),
             new Description(),
             new OutputClauseUnaryTests(),
             new OutputClauseLiteralExpression(),
             null,
             new QName());
    }

    public OutputClause(final Id id,
                        final Description description,
                        final OutputClauseUnaryTests outputValues,
                        final OutputClauseLiteralExpression defaultOutputEntry,
                        final String name,
                        final QName typeRef) {
        super(id,
              description);
        this.outputValues = outputValues;
        this.defaultOutputEntry = defaultOutputEntry;
        this.name = name;
        this.typeRef = typeRef;
    }

    @Override
    public List<HasTypeRef> getHasTypeRefs() {

        final List<HasTypeRef> hasTypeRefs = new ArrayList<>(singletonList(this));

        hasTypeRefs.addAll(getNotNullHasTypeRefs(getDefaultOutputEntry()));

        return hasTypeRefs;
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

    public OutputClauseUnaryTests getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(final OutputClauseUnaryTests value) {
        this.outputValues = value;
    }

    public OutputClauseLiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    public void setDefaultOutputEntry(final OutputClauseLiteralExpression value) {
        this.defaultOutputEntry = value;
    }

    public String getName() {
        return name;
    }

    public void setName(final String value) {
        this.name = value;
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

    // ------------------------------------------------------
    // DomainObject requirements - to use in Properties Panel
    // ------------------------------------------------------

    @Override
    public String getDomainObjectUUID() {
        return getId().getValue();
    }

    @Override
    public String getDomainObjectNameTranslationKey() {
        return DMNAPIConstants.OutputClause_DomainObjectName;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OutputClause)) {
            return false;
        }

        final OutputClause that = (OutputClause) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (outputValues != null ? !outputValues.equals(that.outputValues) : that.outputValues != null) {
            return false;
        }
        if (defaultOutputEntry != null ? !defaultOutputEntry.equals(that.defaultOutputEntry) : that.defaultOutputEntry != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return typeRef != null ? typeRef.equals(that.typeRef) : that.typeRef == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         outputValues != null ? outputValues.hashCode() : 0,
                                         defaultOutputEntry != null ? defaultOutputEntry.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         typeRef != null ? typeRef.hashCode() : 0);
    }
}

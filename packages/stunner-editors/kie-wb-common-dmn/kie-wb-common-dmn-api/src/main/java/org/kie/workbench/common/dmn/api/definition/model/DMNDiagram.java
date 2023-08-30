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
package org.kie.workbench.common.dmn.api.definition.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition()
@FormDefinition(policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)},
        startElement = "id")
@CanContain(roles = {
        "input-data",
        "knowledge-source",
        "business-knowledge-model",
        "decision",
        "text-annotation",
        "association",
        "information-requirement",
        "knowledge-requirement",
        "authority-requirement",
        "decision-service"
})
public class DMNDiagram extends DMNModelInstrumentedBase implements DMNDefinition {

    @Category
    private static final String stunnerCategory = Categories.DIAGRAM;

    @Labels
    private static final Set<String> stunnerLabels = Stream.of("dmn_diagram").collect(Collectors.toSet());

    @Property
    @FormField(readonly = true)
    @Valid
    protected Id id;

    @Property
    @FormField
    @Valid
    protected Definitions definitions;

    public DMNDiagram() {
        this(new Id(),
             new Definitions());
    }

    public DMNDiagram(final Id id,
                      final Definitions definitions) {
        this.id = id;
        this.definitions = definitions;
    }

    public Id getId() {
        return id;
    }

    public void setId(final Id id) {
        this.id = id;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void setDefinitions(final Definitions definitions) {
        this.definitions = definitions;
    }

    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DMNDiagram)) {
            return false;
        }

        final DMNDiagram that = (DMNDiagram) o;

        if (definitions != null ? !definitions.equals(that.definitions) : that.definitions != null) {
            return false;
        }
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(definitions != null ? definitions.hashCode() : 0,
                                         id != null ? id.hashCode() : 0);
    }
}

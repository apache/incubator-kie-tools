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

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.rules.AcyclicDirectedGraphRule;
import org.kie.workbench.common.dmn.api.rules.SingleConnectorPerTypeGraphRule;
import org.kie.workbench.common.dmn.api.validation.NoValidation;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanConnect;
import org.kie.workbench.common.stunner.core.rule.annotation.RuleExtension;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = EdgeFactory.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id", defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)})
@CanConnect(startRole = "business-knowledge-model", endRole = "text-annotation")
@CanConnect(startRole = "decision", endRole = "text-annotation")
@CanConnect(startRole = "input-data", endRole = "text-annotation")
@CanConnect(startRole = "knowledge-source", endRole = "text-annotation")
@RuleExtension(handler = AcyclicDirectedGraphRule.class, typeArguments = {Association.class})
@RuleExtension(handler = SingleConnectorPerTypeGraphRule.class, typeArguments = {Association.class})
@NoValidation
public class Association extends Artifact {

    @Category
    private static final String stunnerCategory = Categories.CONNECTORS;

    @Labels
    private static final Set<String> stunnerLabels = new Sets.Builder<String>()
            .add("association")
            .build();

    public Association() {
        this(new Id(),
             new org.kie.workbench.common.dmn.api.property.dmn.Description());
    }

    public Association(final Id id,
                       final org.kie.workbench.common.dmn.api.property.dmn.Description description) {
        super(id,
              description);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Association)) {
            return false;
        }

        final Association that = (Association) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return description != null ? description.equals(that.description) : that.description == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0);
    }
}

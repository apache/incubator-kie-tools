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

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
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

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = EdgeFactory.class, builder = AuthorityRequirement.AuthorityRequirementBuilder.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)})
@CanConnect(startRole = "knowledge-source", endRole = "decision")
@CanConnect(startRole = "knowledge-source", endRole = "business-knowledge-model")
@CanConnect(startRole = "knowledge-source", endRole = "knowledge-source")
@CanConnect(startRole = "input-data", endRole = "knowledge-source")
@RuleExtension(handler = AcyclicDirectedGraphRule.class, typeArguments = {AuthorityRequirement.class})
@RuleExtension(handler = SingleConnectorPerTypeGraphRule.class, typeArguments = {AuthorityRequirement.class})
@NoValidation
public class AuthorityRequirement extends DMNModelInstrumentedBase {

    @Category
    private static final String stunnerCategory = Categories.CONNECTORS;

    @Labels
    private static final Set<String> stunnerLabels = Stream.of("authority-requirement").collect(Collectors.toSet());

    @NonPortable
    public static class AuthorityRequirementBuilder extends BaseNodeBuilder<AuthorityRequirement> {

        @Override
        public AuthorityRequirement build() {
            return new AuthorityRequirement();
        }
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
}

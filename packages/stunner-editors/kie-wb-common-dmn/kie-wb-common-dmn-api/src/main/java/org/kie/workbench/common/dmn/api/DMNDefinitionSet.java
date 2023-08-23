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
package org.kie.workbench.common.dmn.api;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.ImportedValues;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.factory.DMNGraphFactory;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.Occurrences;

@ApplicationScoped
@Bindable
@DefinitionSet(
        graphFactory = DMNGraphFactory.class,
        qualifier = DMNEditor.class,
        definitions = {
                DMNDiagram.class,
                InputData.class,
                KnowledgeSource.class,
                BusinessKnowledgeModel.class,
                Decision.class,
                TextAnnotation.class,
                Association.class,
                InformationRequirement.class,
                KnowledgeRequirement.class,
                AuthorityRequirement.class,
                LiteralExpression.class,
                LiteralExpressionPMMLDocument.class,
                LiteralExpressionPMMLDocumentModel.class,
                ImportedValues.class,
                UnaryTests.class,
                InformationItem.class,
                InformationItemPrimary.class,
                InputClause.class,
                OutputClause.class,
                NOPDomainObject.class,
                DecisionService.class
        },
        builder = DMNDefinitionSet.DMNDefinitionSetBuilder.class
)
@CanContain(roles = {"dmn_diagram"})
@Occurrences(role = "dmn_diagram", max = 1)
public class DMNDefinitionSet {

    @Description
    public static final transient String description = "DMN";

    public DMNDefinitionSet() {
    }

    public String getDescription() {
        return description;
    }

    @NonPortable
    public static class DMNDefinitionSetBuilder implements Builder<DMNDefinitionSet> {

        @Override
        public DMNDefinitionSet build() {
            return new DMNDefinitionSet();
        }
    }
}

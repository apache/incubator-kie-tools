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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.List;

import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class DecisionConverter implements NodeConverter<org.kie.dmn.model.api.Decision, org.kie.workbench.common.dmn.api.definition.v1_1.Decision> {

    private FactoryManager factoryManager;

    public DecisionConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<Decision>, ?> nodeFromDMN(final org.kie.dmn.model.api.Decision dmn) {
        @SuppressWarnings("unchecked")
        Node<View<Decision>, ?> node = (Node<View<Decision>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                           Decision.class).asNode();
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        InformationItem informationItem = InformationItemPropertyConverter.wbFromDMN(dmn.getVariable());
        Expression expression = ExpressionPropertyConverter.wbFromDMN(dmn.getExpression());
        Decision decision = new Decision(id,
                                         description,
                                         name,
                                         new Question(),
                                         new AllowedAnswers(),
                                         informationItem,
                                         expression,
                                         new BackgroundSet(),
                                         new FontSet(),
                                         new RectangleDimensionsSet());
        node.getContent().setDefinition(decision);

        if (informationItem != null) {
            informationItem.setParent(decision);
        }
        if (expression != null) {
            expression.setParent(decision);
        }

        return node;
    }

    @Override
    public org.kie.dmn.model.api.Decision dmnFromNode(final Node<View<Decision>, ?> node) {
        Decision source = node.getContent().getDefinition();
        org.kie.dmn.model.api.Decision d = new org.kie.dmn.model.v1_2.TDecision();
        d.setId(source.getId().getValue());
        d.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        d.setName(source.getName().getValue());
        org.kie.dmn.model.api.InformationItem variable = InformationItemPropertyConverter.dmnFromWB(source.getVariable());
        if (variable != null) {
            variable.setParent(d);
        }
        d.setVariable(variable);
        org.kie.dmn.model.api.Expression expression = ExpressionPropertyConverter.dmnFromWB(source.getExpression());
        if (expression != null) {
            expression.setParent(d);
        }
        d.setExpression(expression);
        // DMN spec table 2: Requirements connection rules
        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        org.kie.dmn.model.api.InformationRequirement iReq = new org.kie.dmn.model.v1_2.TInformationRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredDecision(ri);
                        d.getInformationRequirement().add(iReq);
                    } else if (drgElement instanceof BusinessKnowledgeModel) {
                        org.kie.dmn.model.api.KnowledgeRequirement iReq = new org.kie.dmn.model.v1_2.TKnowledgeRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredKnowledge(ri);
                        d.getKnowledgeRequirement().add(iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredAuthority(ri);
                        d.getAuthorityRequirement().add(iReq);
                    } else if (drgElement instanceof InputData) {
                        org.kie.dmn.model.api.InformationRequirement iReq = new org.kie.dmn.model.v1_2.TInformationRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredInput(ri);
                        d.getInformationRequirement().add(iReq);
                    } else {
                        throw new UnsupportedOperationException("wrong model definition.");
                    }
                }
            }
        }
        return d;
    }
}

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
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class BusinessKnowledgeModelConverter implements NodeConverter<org.kie.dmn.model.api.BusinessKnowledgeModel, org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel> {

    private FactoryManager factoryManager;

    public BusinessKnowledgeModelConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<BusinessKnowledgeModel>, ?> nodeFromDMN(final org.kie.dmn.model.api.BusinessKnowledgeModel dmn) {
        @SuppressWarnings("unchecked")
        Node<View<BusinessKnowledgeModel>, ?> node = (Node<View<BusinessKnowledgeModel>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                                       BusinessKnowledgeModel.class).asNode();
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        InformationItem informationItem = InformationItemPropertyConverter.wbFromDMN(dmn.getVariable());
        FunctionDefinition functionDefinition = FunctionDefinitionPropertyConverter.wbFromDMN(dmn.getEncapsulatedLogic());
        BusinessKnowledgeModel bkm = new BusinessKnowledgeModel(id,
                                                                description,
                                                                name,
                                                                informationItem,
                                                                functionDefinition,
                                                                new BackgroundSet(),
                                                                new FontSet(),
                                                                new RectangleDimensionsSet());
        node.getContent().setDefinition(bkm);

        if (informationItem != null) {
            informationItem.setParent(bkm);
        }
        if (functionDefinition != null) {
            functionDefinition.setParent(bkm);
        }

        return node;
    }

    @Override
    public org.kie.dmn.model.api.BusinessKnowledgeModel dmnFromNode(final Node<View<BusinessKnowledgeModel>, ?> node) {
        BusinessKnowledgeModel source = node.getContent().getDefinition();
        org.kie.dmn.model.api.BusinessKnowledgeModel result = new org.kie.dmn.model.v1_1.TBusinessKnowledgeModel();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setName(source.getName().getValue());
        result.setVariable(InformationItemPropertyConverter.dmnFromWB(source.getVariable()));
        result.setEncapsulatedLogic(FunctionDefinitionPropertyConverter.dmnFromWB(source.getEncapsulatedLogic()));
        // DMN spec table 2: Requirements connection rules
        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof BusinessKnowledgeModel) {
                        org.kie.dmn.model.api.KnowledgeRequirement iReq = new org.kie.dmn.model.v1_1.TKnowledgeRequirement();
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_1.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredKnowledge(ri);
                        result.getKnowledgeRequirement().add(iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_1.TAuthorityRequirement();
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_1.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredAuthority(ri);
                        result.getAuthorityRequirement().add(iReq);
                    } else {
                        throw new UnsupportedOperationException("wrong model definition.");
                    }
                }
            }
        }
        return result;
    }
}

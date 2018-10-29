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

import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class KnowledgeSourceConverter implements NodeConverter<org.kie.dmn.model.api.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource> {

    private FactoryManager factoryManager;

    public KnowledgeSourceConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<KnowledgeSource>, ?> nodeFromDMN(final org.kie.dmn.model.api.KnowledgeSource dmn) {
        @SuppressWarnings("unchecked")
        Node<View<KnowledgeSource>, ?> node = (Node<View<KnowledgeSource>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                         KnowledgeSource.class).asNode();
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        KnowledgeSourceType ksType = new KnowledgeSourceType(dmn.getType());
        LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        KnowledgeSource ks = new KnowledgeSource(id,
                                                 description,
                                                 name,
                                                 ksType,
                                                 locationURI,
                                                 new BackgroundSet(),
                                                 new FontSet(),
                                                 new RectangleDimensionsSet());
        node.getContent().setDefinition(ks);
        return node;
    }

    @Override
    public org.kie.dmn.model.api.KnowledgeSource dmnFromNode(final Node<View<KnowledgeSource>, ?> node) {
        KnowledgeSource source = node.getContent().getDefinition();
        org.kie.dmn.model.api.KnowledgeSource result = new org.kie.dmn.model.v1_2.TKnowledgeSource();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setName(source.getName().getValue());
        result.setType(source.getType().getValue());
        result.setLocationURI(source.getLocationURI().getValue());
        // DMN spec table 2: Requirements connection rules
        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredDecision(ri);
                        result.getAuthorityRequirement().add(iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredAuthority(ri);
                        result.getAuthorityRequirement().add(iReq);
                    } else if (drgElement instanceof InputData) {
                        org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());
                        iReq.setRequiredInput(ri);
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

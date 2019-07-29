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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.dmn.backend.definition.v1_1.HrefBuilder.getHref;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class KnowledgeSourceConverter implements NodeConverter<org.kie.dmn.model.api.KnowledgeSource, org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource> {

    private FactoryManager factoryManager;

    public KnowledgeSourceConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<KnowledgeSource>, ?> nodeFromDMN(final org.kie.dmn.model.api.KnowledgeSource dmn,
                                                      final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<KnowledgeSource>, ?> node = (Node<View<KnowledgeSource>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                               getDefinitionId(KnowledgeSource.class)).asNode();
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final KnowledgeSourceType ksType = new KnowledgeSourceType(dmn.getType());
        final LocationURI locationURI = new LocationURI(dmn.getLocationURI());
        final KnowledgeSource ks = new KnowledgeSource(id,
                                                       description,
                                                       name,
                                                       ksType,
                                                       locationURI,
                                                       new BackgroundSet(),
                                                       new FontSet(),
                                                       new GeneralRectangleDimensionsSet());
        node.getContent().setDefinition(ks);

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, ks);

        return node;
    }

    @Override
    public org.kie.dmn.model.api.KnowledgeSource dmnFromNode(final Node<View<KnowledgeSource>, ?> node,
                                                             final Consumer<ComponentWidths> componentWidthsConsumer) {
        final KnowledgeSource source = node.getContent().getDefinition();
        final org.kie.dmn.model.api.KnowledgeSource result = new org.kie.dmn.model.v1_2.TKnowledgeSource();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setName(source.getName().getValue());
        result.setType(source.getType().getValue());
        result.setLocationURI(source.getLocationURI().getValue());
        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, result);
        // DMN spec table 2: Requirements connection rules
        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    if (drgElement instanceof Decision) {
                        final org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredDecision(ri);
                        result.getAuthorityRequirement().add(iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        final org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredAuthority(ri);
                        result.getAuthorityRequirement().add(iReq);
                    } else if (drgElement instanceof InputData) {
                        final org.kie.dmn.model.api.AuthorityRequirement iReq = new org.kie.dmn.model.v1_2.TAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final org.kie.dmn.model.api.DMNElementReference ri = new org.kie.dmn.model.v1_2.TDMNElementReference();
                        ri.setHref(getHref(drgElement));
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

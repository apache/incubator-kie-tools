/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAuthorityRequirement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.HrefBuilder.getHref;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class KnowledgeSourceConverter implements NodeConverter<JSITKnowledgeSource, org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource> {

    private FactoryManager factoryManager;

    public KnowledgeSourceConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<KnowledgeSource>, ?> nodeFromDMN(final JSITKnowledgeSource dmn,
                                                      final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<KnowledgeSource>, ?> node = (Node<View<KnowledgeSource>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                               getDefinitionId(KnowledgeSource.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
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
    @SuppressWarnings("unchecked")
    public JSITKnowledgeSource dmnFromNode(final Node<View<KnowledgeSource>, ?> node,
                                           final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final KnowledgeSource source = node.getContent().getDefinition();
        final JSITKnowledgeSource result = new JSITKnowledgeSource();
        result.setId(source.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        description.ifPresent(result::setDescription);
        result.setName(source.getName().getValue());
        result.setType(source.getType().getValue());
        result.setLocationURI(source.getLocationURI().getValue());
        result.setAuthorityRequirement(new ArrayList<>());
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
                        final JSITAuthorityRequirement iReq = new JSITAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredDecision(ri);
                        result.addAuthorityRequirement(iReq);
                    } else if (drgElement instanceof KnowledgeSource) {
                        final JSITAuthorityRequirement iReq = new JSITAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredAuthority(ri);
                        result.addAuthorityRequirement(iReq);
                    } else if (drgElement instanceof InputData) {
                        final JSITAuthorityRequirement iReq = new JSITAuthorityRequirement();
                        iReq.setId(e.getUUID());
                        final JSITDMNElementReference ri = new JSITDMNElementReference();
                        ri.setHref(getHref(drgElement));
                        iReq.setRequiredInput(ri);
                        result.addAuthorityRequirement(iReq);
                    } else {
                        throw new UnsupportedOperationException("wrong model definition.");
                    }
                }
            }
        }
        return result;
    }
}

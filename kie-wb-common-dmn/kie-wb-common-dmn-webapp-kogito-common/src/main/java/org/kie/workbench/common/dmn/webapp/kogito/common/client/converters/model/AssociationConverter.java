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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.Association;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociationDirection;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDMNElementReference;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model.HrefBuilder.getHref;

public class AssociationConverter {

    @SuppressWarnings("unchecked")
    public static List<JSITAssociation> dmnFromWB(final Node<View<TextAnnotation>, ?> node) {
        final TextAnnotation ta = node.getContent().getDefinition();
        final JSITDMNElementReference ta_elementReference = new JSITDMNElementReference();
        ta_elementReference.setHref(new StringBuilder("#").append(ta.getId().getValue()).toString());

        final List<JSITAssociation> result = new ArrayList<>();

        final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            final Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    final JSITDMNElementReference sourceRef = new JSITDMNElementReference();
                    sourceRef.setHref(getHref(drgElement));

                    final JSITAssociation adding = new JSITAssociation();
                    adding.setId(((View<Association>) e.getContent()).getDefinition().getId().getValue());
                    final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(((View<Association>) e.getContent()).getDefinition().getDescription()));
                    description.ifPresent(adding::setDescription);
                    adding.setSourceRef(sourceRef);
                    adding.setTargetRef(ta_elementReference);
                    adding.setAssociationDirection(Js.uncheckedCast(JSITAssociationDirection.NONE.value()));
                    result.add(adding);
                }
            }
        }
        final List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        for (Edge<?, ?> e : outEdges) {
            final Node<?, ?> targetNode = e.getTargetNode();
            if (targetNode.getContent() instanceof View<?>) {
                final View<?> view = (View<?>) targetNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    final DRGElement drgElement = (DRGElement) view.getDefinition();
                    final JSITDMNElementReference targetRef = new JSITDMNElementReference();
                    targetRef.setHref(getHref(drgElement));

                    final JSITAssociation adding = new JSITAssociation();
                    adding.setId(((View<Association>) e.getContent()).getDefinition().getId().getValue());
                    final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(((View<Association>) e.getContent()).getDefinition().getDescription()));
                    description.ifPresent(adding::setDescription);
                    adding.setSourceRef(ta_elementReference);
                    adding.setTargetRef(targetRef);
                    adding.setAssociationDirection(Js.uncheckedCast(JSITAssociationDirection.NONE.value()));
                    result.add(adding);
                }
            }
        }

        return result;
    }
}
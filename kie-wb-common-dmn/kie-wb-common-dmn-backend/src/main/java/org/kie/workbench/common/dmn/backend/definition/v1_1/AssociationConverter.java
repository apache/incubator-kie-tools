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

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class AssociationConverter {

    public static List<org.kie.dmn.model.api.Association> dmnFromWB(final Node<View<TextAnnotation>, ?> node) {
        TextAnnotation ta = node.getContent().getDefinition();
        org.kie.dmn.model.api.DMNElementReference ta_elementReference = new org.kie.dmn.model.v1_1.TDMNElementReference();
        ta_elementReference.setHref(new StringBuilder("#").append(ta.getId().getValue()).toString());

        List<org.kie.dmn.model.api.Association> result = new ArrayList<>();

        List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) node.getInEdges();
        for (Edge<?, ?> e : inEdges) {
            Node<?, ?> sourceNode = e.getSourceNode();
            if (sourceNode.getContent() instanceof View<?>) {
                View<?> view = (View<?>) sourceNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    org.kie.dmn.model.api.DMNElementReference sourceRef = new org.kie.dmn.model.v1_1.TDMNElementReference();
                    sourceRef.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());

                    org.kie.dmn.model.api.Association adding = new org.kie.dmn.model.v1_1.TAssociation();
                    adding.setId(((View<Association>) e.getContent()).getDefinition().getId().getValue());
                    adding.setDescription(DescriptionPropertyConverter.dmnFromWB(((View<Association>) e.getContent()).getDefinition().getDescription()));
                    adding.setSourceRef(sourceRef);
                    adding.setTargetRef(ta_elementReference);
                    result.add(adding);
                }
            }
        }
        List<Edge<?, ?>> outEdges = (List<Edge<?, ?>>) node.getOutEdges();
        for (Edge<?, ?> e : outEdges) {
            Node<?, ?> targetNode = e.getTargetNode();
            if (targetNode.getContent() instanceof View<?>) {
                View<?> view = (View<?>) targetNode.getContent();
                if (view.getDefinition() instanceof DRGElement) {
                    DRGElement drgElement = (DRGElement) view.getDefinition();
                    org.kie.dmn.model.api.DMNElementReference targetRef = new org.kie.dmn.model.v1_1.TDMNElementReference();
                    targetRef.setHref(new StringBuilder("#").append(drgElement.getId().getValue()).toString());

                    org.kie.dmn.model.api.Association adding = new org.kie.dmn.model.v1_1.TAssociation();
                    adding.setId(((View<Association>) e.getContent()).getDefinition().getId().getValue());
                    adding.setDescription(DescriptionPropertyConverter.dmnFromWB(((View<Association>) e.getContent()).getDefinition().getDescription()));
                    adding.setSourceRef(ta_elementReference);
                    adding.setTargetRef(targetRef);
                    result.add(adding);
                }
            }
        }

        return result;
    }
}
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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.shared.core.types.DataURLType;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class DMNDocumentationDRDsFactory {

    static final String NONE = "";

    private final SessionManager sessionManager;

    private final BoxedExpressionHelper expressionHelper;

    @Inject
    public DMNDocumentationDRDsFactory(final SessionManager sessionManager,
                                       final BoxedExpressionHelper expressionHelper) {
        this.sessionManager = sessionManager;
        this.expressionHelper = expressionHelper;
    }

    public List<DMNDocumentationDRD> create(final Diagram diagram) {

        final Optional<String> previousNodeUUID = getExpressionContainerGrid().getNodeUUID();
        final List<DMNDocumentationDRD> drds = createDMNDocumentationDRDs(diagram);

        previousNodeUUID.ifPresent(uuid -> setExpressionContainerGrid(diagram, uuid));

        return drds;
    }

    String getNodeImage(final Diagram diagram,
                        final Node<View, Edge> node) {

        if (!hasExpression(node)) {
            return NONE;
        }

        setExpressionContainerGrid(diagram, node.getUUID());

        final ExpressionContainerGrid grid = getExpressionContainerGrid();
        final Viewport viewport = grid.getViewport();
        final int padding = 10;
        final int wide = (int) (grid.getWidth() + padding);
        final int high = (int) (grid.getHeight() + padding);

        viewport.setPixelSize(wide, high);

        return viewport.toDataURL(DataURLType.PNG);
    }

    void setExpressionContainerGrid(final Diagram diagram,
                                    final String uuid) {

        final Node<View, Edge> node = getNode(diagram, uuid);
        final Object definition = expressionHelper.getDefinition(node);
        final HasExpression hasExpression = expressionHelper.getHasExpression(node);
        final Optional<HasName> hasName = Optional.of((HasName) definition);

        getExpressionContainerGrid().setExpression(node.getUUID(), hasExpression, hasName, false);
    }

    private List<DMNDocumentationDRD> createDMNDocumentationDRDs(final Diagram diagram) {

        final List<DMNDocumentationDRD> dmnDocumentationDRDS = new ArrayList<>();

        getNodeStream(diagram).forEach(node -> {
            final Object definition = expressionHelper.getDefinition(node);
            if (definition instanceof DRGElement) {
                final DRGElement drgElement = (DRGElement) definition;
                dmnDocumentationDRDS.add(createDMNDocumentationDRD(diagram, node, drgElement));
            }
        });

        return dmnDocumentationDRDS;
    }

    private DMNDocumentationDRD createDMNDocumentationDRD(final Diagram diagram,
                                                          final Node<View, Edge> node,
                                                          final DRGElement drgElement) {

        final String name = getName(drgElement);
        final String description = getDescription(drgElement);
        final String type = getType(drgElement);
        final String image = getNodeImage(diagram, node);

        return DMNDocumentationDRD.create(name, type, description, image);
    }

    private String getType(final DRGElement drgElement) {
        if (drgElement instanceof HasVariable) {
            return getType(((HasVariable) drgElement).getVariable().getTypeRef());
        }
        return NONE;
    }

    private String getType(final QName qName) {
        return Optional
                .ofNullable(qName)
                .map(QName::getLocalPart)
                .orElse(NONE);
    }

    private String getName(final DRGElement drgElement) {
        return drgElement.getName().getValue();
    }

    private String getDescription(final DRGElement drgElement) {
        return drgElement.getDescription().getValue();
    }

    private ExpressionContainerGrid getExpressionContainerGrid() {
        final ExpressionEditorView.Presenter expressionEditor = getCurrentSession().getExpressionEditor();
        return ((ExpressionEditorViewImpl) expressionEditor.getView()).getExpressionContainerGrid();
    }

    private boolean hasExpression(final Node<View, Edge> node) {
        return expressionHelper.getOptionalHasExpression(node).isPresent();
    }

    private DMNSession getCurrentSession() {
        return sessionManager.getCurrentSession();
    }

    private Node<View, Edge> getNode(final Diagram diagram,
                                     final String uuid) {
        return getNodeStream(diagram)
                .filter(node -> Objects.equals(uuid, node.getUUID()))
                .findFirst()
                .orElseThrow(UnsupportedOperationException::new);
    }

    @SuppressWarnings("unchecked")
    private Stream<Node<View, Edge>> getNodeStream(final Diagram diagram) {
        final Graph graph = diagram.getGraph();
        final Iterable<Node> nodes = graph.nodes();
        return StreamSupport
                .stream(nodes.spliterator(), false)
                .map(node -> (Node<View, Edge>) node);
    }
}

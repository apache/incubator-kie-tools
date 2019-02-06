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

package org.kie.workbench.common.stunner.core.graph.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class GraphUtils {

    public static Object getProperty(final DefinitionManager definitionManager,
                                     final Element<? extends Definition> element,
                                     final String id) {

        return Optional.ofNullable(element)
                .map(Element::getContent)
                .map(Definition::getDefinition)
                .map(def -> Exceptions.<Set>swallow(() -> definitionManager.adapters().forDefinition().getProperties(def),
                                                    Collections.emptySet()))
                .map(properties -> Exceptions.swallow(() -> getProperty(definitionManager, properties, id), null))
                .orElseGet(
                        //getting by field if not found by the id (class name)
                        () -> Optional.ofNullable(element)
                                .map(Element::getContent)
                                .map(Definition::getDefinition)
                                .map(def -> getPropertyByField(definitionManager, def, id))
                                .orElse(null)
                );
    }

    public static Object getPropertyByField(final DefinitionManager definitionManager,
                                            final Object definition,
                                            final String field) {

        final int index = field.indexOf('.');
        final String firstField = index > -1 ? field.substring(0, index) : field;
        final Object property =
                Exceptions.<Optional>swallow(() -> definitionManager
                        .adapters()
                        .forDefinition()
                        .getProperty(definition, firstField), Optional.empty())
                        .orElseGet(() -> Exceptions.<Optional>swallow(() -> definitionManager
                                .adapters()
                                .forPropertySet()
                                .getProperty(definition, firstField), Optional.empty())
                                .orElse(null)
                        );

        return (index > 0 && Objects.nonNull(property))
                ? getPropertyByField(definitionManager, property, field.substring(index + 1))
                : property;
    }

    public static Object getProperty(final DefinitionManager definitionManager,
                                     final Set properties,
                                     final String id) {
        if (null != id && null != properties) {
            for (final Object property : properties) {
                final String pId = definitionManager.adapters().forProperty().getId(property);
                if (pId.equals(id)) {
                    return property;
                }
            }
        }
        return null;
    }

    public static int countDefinitionsById(final DefinitionManager definitionManager,
                                           final Graph<?, ? extends Node> target,
                                           final String id) {
        final int[] count = {0};
        target.nodes().forEach(node -> {
            if (getElementDefinitionId(definitionManager,
                                       node).equals(id)) {
                count[0]++;
            }
        });
        return count[0];
    }

    public static int countEdges(final DefinitionManager definitionManager,
                                 final String edgeId,
                                 final List<? extends Edge> edges) {
        if (null == edges) {
            return 0;
        }

        return (int) edges.stream()
                .map(edge -> getElementDefinitionId(definitionManager, edge))
                .filter(edgeId::equals)
                .count();
    }

    /**
     * Does not returns labels not being used on the graph,
     * even if included in the <code>filter</code>.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Integer> getLabelsCount(final Graph<?, ? extends Node> target,
                                                      final Set<String> filter) {
        final Map<String, Integer> labels = new LinkedHashMap<>();
        target.nodes().forEach(node -> {
            final Set<String> nodeRoles = node.getLabels();
            if (null != nodeRoles) {
                nodeRoles
                        .stream()
                        .filter(role -> null == filter || filter.contains(role))
                        .forEach(role -> {
                            final Integer i = labels.get(role);
                            labels.put(role,
                                       null != i ? i + 1 : 1);
                        });
            }
        });
        return labels;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getParentIds(final DefinitionManager definitionManager,
                                            final Graph<? extends DefinitionSet, ? extends Node> graph,
                                            final Element<?> element) {
        final List<String> result = new ArrayList<>(5);
        Element<?> p = element;
        while (p instanceof Node && p.getContent() instanceof Definition) {
            p = getParent((Node<? extends Definition<?>, ? extends Edge>) p);
            if (null != p) {
                final Object definition = ((Definition) p.getContent()).getDefinition();
                final String id = definitionManager.adapters().forDefinition().getId(definition).value();
                result.add(id);
            }
        }
        final String graphId = graph.getContent().getDefinition();
        result.add(graphId);
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Element<?> getParent(final Node<?, ? extends Edge> element) {
        return Optional.ofNullable(element)
                .map(Node::getInEdges)
                .orElse(Collections.emptyList())
                .stream()
                .filter(e -> e.getContent() instanceof Child)
                .findAny()
                .map(Edge::getSourceNode)
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static Optional<Element<?>> getParentByDefinitionId(final DefinitionManager definitionManager,
                                                               final Node<?, ? extends Edge> candidate,
                                                               final String parentDefId) {
        checkNotNull("candidate",
                     candidate);
        checkNotNull("parentDefId",
                     parentDefId);
        Element<?> p = getParent(candidate);
        while (p instanceof Node
                && p.getContent() instanceof Definition) {
            final String cID = getElementDefinitionId(definitionManager,
                                                      p);
            if (parentDefId.equals(cID)) {
                return Optional.of(p);
            }
            p = getParent((Node<?, ? extends Edge>) p);
        }
        return Optional.empty();
    }

    public static Point2D getPosition(final View element) {
        final Bound ul = element.getBounds().getUpperLeft();
        final double x = ul.getX();
        final double y = ul.getY();
        return new Point2D(x,
                           y);
    }

    public static Point2D getComputedPosition(final Node<?, ? extends Edge> element) {
        double x = 0;
        double y = 0;
        Element<?> parent = element;
        while (null != parent
                && null != parent.asNode()
                && parent.getContent() instanceof View) {
            final Point2D position = getPosition((View) parent.getContent());
            x += position.getX();
            y += position.getY();
            parent = getParent((Node<?, ? extends Edge>) parent);
        }
        return new Point2D(x,
                           y);
    }

    public static double[] getNodeSize(final View element) {
        return getSize(element.getBounds());
    }

    private static double[] getSize(Bounds bounds) {
        final Bound ul = bounds.getUpperLeft();
        final Bound lr = bounds.getLowerRight();
        final double w = lr.getX() - ul.getX();
        final double h = lr.getY() - ul.getY();
        return new double[]{Math.abs(w), Math.abs(h)};
    }

    public static boolean isRootNode(Element<? extends View<?>> element, final Graph<DefinitionSet, Node> graph) {
        if (element instanceof Node) {
            Node node = (Node) element;

            final Element<?> parent = GraphUtils.getParent(node);
            return (parent == null);
        }

        return false;
    }

    public static boolean checkBoundsExceeded(final Bounds parentBounds,
                                              final Bounds bounds) {
        if (null == parentBounds) {
            return true;
        }
        if (parentBounds.hasUpperLeft()) {
            if ((bounds.getUpperLeft().getX() < parentBounds.getUpperLeft().getX())
                    || (bounds.getUpperLeft().getY() < parentBounds.getUpperLeft().getY())) {
                return false;
            }
        }
        if (parentBounds.hasLowerRight()) {
            if ((bounds.getLowerRight().getX() > parentBounds.getLowerRight().getX())
                    || (bounds.getLowerRight().getY() > parentBounds.getLowerRight().getY())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the first node in the graph structure for the given type.
     * @param graph The graph structure.
     * @param type The Definition type..
     */
    @SuppressWarnings("unchecked")
    public static <C> Node<Definition<C>, ?> getFirstNode(final Graph<?, Node> graph,
                                                          final Class<?> type) {
        if (null != graph) {
            for (final Node node : graph.nodes()) {
                final Object content = node.getContent();
                try {
                    final Definition definitionContent = (Definition) content;
                    if (instanceOf(definitionContent.getDefinition(),
                                   type)) {
                        return node;
                    }
                } catch (final ClassCastException e) {
                    // Node content does not contains a definition.
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static boolean hasChildren(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getOutEdges()) ?
                element.getOutEdges().stream()
                        .anyMatch(edge -> (edge.getContent() instanceof Child)) :
                false;
    }

    public static boolean hasDockedNodes(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getOutEdges()) ?
                element.getOutEdges()
                        .stream()
                        .anyMatch(edge -> (edge.getContent() instanceof Dock)) :
                false;
    }

    public static boolean hasConnections(Node<? extends Definition<?>, ? extends Edge> node) {
        return Stream.concat(node.getInEdges().stream(), node.getOutEdges().stream())
                .anyMatch(hasConnectionFilter());
    }

    public static boolean hasTargetConnections(Node<? extends Definition<?>, ? extends Edge> node) {
        return node.getInEdges().stream().anyMatch(hasConnectionFilter());
    }

    private static Predicate<Edge> hasConnectionFilter() {
        return edge -> edge.getContent() instanceof ViewConnector;
    }

    public static List<Node> getDockedNodes(final Node<?, ? extends Edge> element) {
        return getNodesFromOutEdges(element, edge -> (edge.getContent() instanceof Dock));
    }

    public static List<Node> getChildNodes(final Node<?, ? extends Edge> element) {
        return getNodesFromOutEdges(element, edge -> (edge.getContent() instanceof Child));
    }

    private static List<Node> getNodesFromOutEdges(final Node<?, ? extends Edge> element, Predicate<Edge> filter) {
        Objects.requireNonNull(element.getOutEdges());
        return element.getOutEdges()
                .stream()
                .filter(filter::test)
                .map(Edge::getTargetNode)
                .collect(Collectors.toList());
    }

    public static List<Edge<? extends ViewConnector<?>, Node>> getSourceConnections(
            final Node<?, ? extends Edge> element) {
        Objects.requireNonNull(element.getOutEdges());
        return getConnections(element.getOutEdges());
    }

    public static List<Edge<? extends ViewConnector<?>, Node>> getTargetConnections(
            final Node<?, ? extends Edge> element) {
        Objects.requireNonNull(element.getInEdges());
        return getConnections(element.getInEdges());
    }

    private static List<Edge<? extends ViewConnector<?>, Node>> getConnections(List<? extends Edge> edges) {
        return edges.stream()
                .filter(edge -> (edge.getContent() instanceof ViewConnector))
                .map(edge -> (Edge<? extends ViewConnector<?>, Node>) edge)
                .collect(Collectors.toList());
    }

    public static boolean isDockedNode(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getInEdges()) ?
                element.getInEdges()
                        .stream()
                        .anyMatch(edge -> edge.getContent() instanceof Dock) :
                false;
    }

    public static Optional<Node> getDockParent(final Node<?, ? extends Edge> element) {
        return Objects.nonNull(element.getInEdges()) ?
                element.getInEdges()
                        .stream()
                        .filter(edge -> edge.getContent() instanceof Dock)
                        .map(Edge::getSourceNode)
                        .findFirst() :
                Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static Long countChildren(final Node<?, ? extends Edge> element) {
        return element.getOutEdges().stream()
                .filter(edge -> (edge.getContent() instanceof Child)).count();
    }

    private static String getElementDefinitionId(final DefinitionManager definitionManager,
                                                 final Element<?> element) {
        String targetId = null;
        if (element.getContent() instanceof Definition) {
            final Object definition = ((Definition) element.getContent()).getDefinition();
            targetId = definitionManager.adapters().forDefinition().getId(definition).value();
        } else if (element.getContent() instanceof DefinitionSet) {
            targetId = ((DefinitionSet) element.getContent()).getDefinition();
        }
        return targetId;
    }

    private static boolean instanceOf(final Object item,
                                      final Class<?> clazz) {
        return null != item && item.getClass().equals(clazz);
    }

    public static class HasParentPredicate implements BiPredicate<Node<?, ? extends Edge>, Element<?>> {

        @Override
        public boolean test(final Node<?, ? extends Edge> candidate,
                            final Element<?> parent) {
            if (null != candidate) {
                Element<?> p = getParent(candidate);
                return Objects.equals(p, parent);
            }
            return false;
        }
    }

    public static OptionalInt getChildIndex(final Element element,
                                            final String uuid) {
        if (!(element instanceof Node)) {
            return OptionalInt.empty();
        }

        final Node<?, ?> node = (Node<?, ? extends Edge>) element;
        return Objects.nonNull(node.getOutEdges()) ?
                IntStream.range(0, node.getOutEdges().size())
                        .filter(i -> uuid.equals(node.getOutEdges().get(i).getTargetNode().getUUID())).findFirst() :
                OptionalInt.empty();
    }
}

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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * A Command to delete a set of elements.
 */
@Portable
public class DeleteElementsCommand extends AbstractGraphCompositeCommand {

    private final Collection<String> uuids;
    private transient Collection<Element> elements;
    private transient DeleteCallback callback;

    public DeleteElementsCommand(final @MapsTo("uuids") Collection<String> uuids) {
        this.uuids = PortablePreconditions.checkNotNull("uuids",
                                                        uuids);
        this.callback = new DeleteCallback() {
        };
    }

    public DeleteElementsCommand(final Supplier<Collection<Element>> elements) {
        this(elements.get().stream()
                     .map(Element::getUUID)
                     .collect(Collectors.toList()));
        this.elements = elements.get();
    }

    public DeleteElementsCommand(final Supplier<Collection<Element>> elements,
                                 final DeleteCallback callback) {
        this(elements);
        this.callback = callback;
    }

    @NonPortable
    public interface DeleteCallback {

        default SafeDeleteNodeCommand.SafeDeleteNodeCommandCallback onDeleteNode(Node<?, Edge> node,
                                                                                 SafeDeleteNodeCommand.Options options) {
            return null;
        }

        default void onDeleteEdge(Edge<? extends View, Node> edge) {
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DeleteElementsCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        if (null == elements) {
            elements = uuids.stream()
                    .map(uuid -> context.getGraphIndex().get(uuid))
                    .collect(Collectors.toList());
        }
        if (elements.isEmpty()) {
            throw new IllegalArgumentException("No elements to delete.");
        }
        if (1 == elements.size()) {
            final Element<?> element = elements.iterator().next();
            if (null != element.asNode()) {
                final Node<?, Edge> node = element.asNode();
                final SafeDeleteNodeCommand.Options options = SafeDeleteNodeCommand.Options.defaults();
                commands.add(createSafeDeleteNodeCommand(node,
                                                         options,
                                                         callback));
            } else {
                final Edge<? extends View, Node> edge = (Edge<? extends View, Node>) element.asEdge();
                commands.add(new DeleteConnectorCommand(edge));
                callback.onDeleteEdge(edge);
            }
        } else {
            // Collect the edges to remove.
            final List<Element> edges = elements.stream()
                    .filter(e -> null != e.asEdge())
                    .collect(Collectors.toList());
            final Set<String> edgeIds = edges.stream()
                    .map(Element::getUUID)
                    .collect(Collectors.toSet());
            edges.forEach(edge -> {
                commands.add(new DeleteConnectorCommand((Edge<? extends View, Node>) edge));
                callback.onDeleteEdge((Edge<? extends View, Node>) edge);
            });
            // Collect the nodes to remove.
            elements.stream()
                    .filter(e -> null != e.asNode())
                    .collect(Collectors.toList())
                    .forEach(e -> {
                        final Node<?, Edge> node = (Node<?, Edge>) e;
                        final SafeDeleteNodeCommand.Options options = SafeDeleteNodeCommand.Options.exclude(edgeIds);
                        commands.add(createSafeDeleteNodeCommand(node,
                                                                 options,
                                                                 callback));
                    });
        }
        return this;
    }

    protected SafeDeleteNodeCommand createSafeDeleteNodeCommand(final Node<?, Edge> node,
                                                                final SafeDeleteNodeCommand.Options options,
                                                                final DeleteCallback callback) {
        return new SafeDeleteNodeCommand(node,
                                        callback.onDeleteNode(node, options),
                                        options);
    }

    public Collection<String> getUUIDs() {
        return uuids;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[UUIDs=" + uuids + "]";
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }
}

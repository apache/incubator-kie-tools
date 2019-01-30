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

package org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.forms.client.event.RefreshFormPropertiesEvent;

import static org.kie.workbench.common.dmn.api.definition.v1_1.common.HasTypeRefHelper.getNotNullHasTypeRefs;

@ApplicationScoped
public class PropertiesPanelNotifier {

    private final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent;

    private final SessionManager sessionManager;

    private String oldLocalPart;

    private QName newQName;

    /**
     * Represents the the current element in the Properties Panel.
     * This value is set by the {@link CanvasSelectionEvent} and the {@link DomainObjectSelectionEvent}
     **/
    private String selectedElementUUID = null;

    @Inject
    public PropertiesPanelNotifier(final Event<RefreshFormPropertiesEvent> refreshFormPropertiesEvent,
                                   final SessionManager sessionManager) {
        this.refreshFormPropertiesEvent = refreshFormPropertiesEvent;
        this.sessionManager = sessionManager;
    }

    public PropertiesPanelNotifier withOldLocalPart(final String oldLocalPart) {
        this.oldLocalPart = oldLocalPart;
        return this;
    }

    public PropertiesPanelNotifier withNewQName(final QName newQName) {
        this.newQName = newQName;
        return this;
    }

    public void notifyPanel() {

        for (final Node node : getNodes()) {

            final Object definition = getDefinition(node);

            notifyVariables(node, definition);
            notifyExpressions(node, definition);
        }
    }

    void onCanvasSelectionEvent(final @Observes CanvasSelectionEvent event) {
        final Collection<String> identifiers = event.getIdentifiers();
        if (identifiers.size() > 0) {
            setSelectedElementUUID(identifiers.iterator().next());
        }
    }

    void onDomainObjectSelectionEvent(final @Observes DomainObjectSelectionEvent event) {
        setSelectedElementUUID(event.getDomainObject().getDomainObjectUUID());
    }

    void notifyExpressions(final Node node,
                           final Object definition) {

        if (definition instanceof HasExpression) {

            final HasExpression hasExpression = asHasExpression(definition);
            final List<HasTypeRef> hasTypeRefs = getNotNullHasTypeRefs(hasExpression.getExpression());

            for (final HasTypeRef hasTypeRef : hasTypeRefs) {
                notifyOutdatedElement(node, hasTypeRef);
            }
        }
    }

    void notifyVariables(final Node node,
                         final Object definition) {

        if (definition instanceof HasVariable) {
            notifyOutdatedElement(node, asHasVariable(definition).getVariable());
        }
    }

    void notifyOutdatedElement(final Node node,
                               final HasTypeRef elementTypeRef) {

        final QName typeRef = elementTypeRef.getTypeRef();

        if (Objects.equals(typeRef.getLocalPart(), oldLocalPart)) {
            elementTypeRef.setTypeRef(newQName);
            refreshFormProperties(node);
        }
    }

    void refreshFormProperties(final Node node) {

        final ClientSession currentSession = getCurrentSession().orElseThrow(UnsupportedOperationException::new);
        final String uuid = node.getUUID();

        getSelectedElementUUID().ifPresent(selectedElementUUID -> {
            if (Objects.equals(uuid, selectedElementUUID)) {
                refreshFormPropertiesEvent.fire(new RefreshFormPropertiesEvent(currentSession, uuid));
            }
        });
    }

    Object getDefinition(final Node node) {
        final ViewImpl content = (ViewImpl) node.getContent();
        return content.getDefinition();
    }

    List<Node> getNodes() {

        final List<Node> nodes = new ArrayList<>();

        getGraph().ifPresent(graph -> {
            graph.nodes().forEach(nodes::add);
        });

        return nodes;
    }

    Optional<Graph<?, Node>> getGraph() {

        final Optional<CanvasHandler> canvasHandler = getCurrentSession().map(ClientSession::getCanvasHandler);
        final Optional<Diagram> diagram = canvasHandler.map(CanvasHandler::getDiagram);

        return diagram.map(Diagram::getGraph);
    }

    void setSelectedElementUUID(final String selectedElementUUID) {
        this.selectedElementUUID = selectedElementUUID;
    }

    Optional<String> getSelectedElementUUID() {
        return Optional.ofNullable(selectedElementUUID);
    }

    private Optional<ClientSession> getCurrentSession() {
        return Optional.ofNullable(sessionManager.getCurrentSession());
    }

    private HasExpression asHasExpression(final Object definition) {
        return (HasExpression) definition;
    }

    private HasVariable asHasVariable(final Object definition) {
        return (HasVariable) definition;
    }
}

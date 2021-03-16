/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.docks.navigator.drds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockRequiredEvent;

import static java.util.Collections.emptyList;
import static org.kie.workbench.common.dmn.client.docks.navigator.drds.DRGDiagramUtils.isDRG;

@ApplicationScoped
@Default
public class DMNDiagramsSession {

    private static Diagram NO_DIAGRAM = null;

    private ManagedInstance<DMNDiagramsSessionState> dmnDiagramsSessionStates;

    private SessionManager sessionManager;

    private DMNDiagramUtils dmnDiagramUtils;

    private Map<String, DMNDiagramsSessionState> dmnSessionStatesByPathURI = new HashMap<>();

    private Event<LockRequiredEvent> locker;

    public DMNDiagramsSession() {
        // CDI
    }

    @Inject
    public DMNDiagramsSession(final ManagedInstance<DMNDiagramsSessionState> dmnDiagramsSessionStates,
                              final SessionManager sessionManager,
                              final DMNDiagramUtils dmnDiagramUtils,
                              final Event<LockRequiredEvent> locker) {
        this.dmnDiagramsSessionStates = dmnDiagramsSessionStates;
        this.sessionManager = sessionManager;
        this.dmnDiagramUtils = dmnDiagramUtils;
        this.locker = locker;
    }

    public void destroyState(final Metadata metadata) {
        dmnSessionStatesByPathURI.remove(getSessionKey(metadata));
    }

    public DMNDiagramsSessionState setState(final Metadata metadata,
                                            final Map<String, Diagram> diagramsByDiagramElementId,
                                            final Map<String, DMNDiagramElement> dmnDiagramsByDiagramElementId) {

        final DMNDiagramsSessionState state = dmnDiagramsSessionStates.get();

        state.getDiagramsByDiagramId().putAll(diagramsByDiagramElementId);
        state.getDMNDiagramsByDiagramId().putAll(dmnDiagramsByDiagramElementId);

        dmnSessionStatesByPathURI.put(getSessionKey(metadata), state);

        return state;
    }

    public boolean isSessionStatePresent() {
        return getSessionState() != null;
    }

    public DMNDiagramsSessionState getSessionState() {
        return dmnSessionStatesByPathURI.get(getCurrentSessionKey());
    }

    public String getCurrentSessionKey() {
        return Optional
                .ofNullable(getCurrentGraphDiagram())
                .map(diagram -> getSessionKey(diagram.getMetadata()))
                .orElse("");
    }

    public String getSessionKey(final Metadata metadata) {
        return Optional
                .ofNullable(metadata)
                .map(Metadata::getPath)
                .map(Path::toURI)
                .orElse("");
    }

    public void add(final DMNDiagramElement dmnDiagram,
                    final Diagram stunnerDiagram) {
        final String diagramId = dmnDiagram.getId().getValue();
        getSessionState().getDiagramsByDiagramId().put(diagramId, stunnerDiagram);
        getSessionState().getDMNDiagramsByDiagramId().put(diagramId, dmnDiagram);
        locker.fire(new LockRequiredEvent());
    }

    public void remove(final DMNDiagramElement dmnDiagram) {
        final String diagramId = dmnDiagram.getId().getValue();
        getSessionState().getDiagramsByDiagramId().remove(diagramId);
        getSessionState().getDMNDiagramsByDiagramId().remove(diagramId);
        locker.fire(new LockRequiredEvent());
    }

    public Diagram getDiagram(final String dmnDiagramElementId) {
        return getSessionState().getDiagram(dmnDiagramElementId);
    }

    public String getCurrentDiagramId() {
        if (!Objects.isNull(getSessionState())) {
            final Optional<DMNDiagramElement> current = getCurrentDMNDiagramElement();
            if (current.isPresent()) {
                return current.get().getId().getValue();
            }
        }
        return null;
    }

    public DMNDiagramElement getDMNDiagramElement(final String dmnDiagramElementId) {
        return getSessionState().getDMNDiagramElement(dmnDiagramElementId);
    }

    public DMNDiagramTuple getDiagramTuple(final String dmnDiagramElementId) {
        return getSessionState().getDiagramTuple(dmnDiagramElementId);
    }

    public List<DMNDiagramTuple> getDMNDiagrams() {
        return getSessionState().getDMNDiagrams();
    }

    public void onDMNDiagramSelected(final @Observes DMNDiagramSelected selected) {
        final DMNDiagramElement selectedDiagramElement = selected.getDiagramElement();
        if (belongsToCurrentSessionState(selectedDiagramElement)) {
            getSessionState().setCurrentDMNDiagramElement(selectedDiagramElement);
        }
    }

    public boolean belongsToCurrentSessionState(final DMNDiagramElement diagramElement) {
        return getDMNDiagramElement(diagramElement.getId().getValue()) != null;
    }

    public Optional<DMNDiagramElement> getCurrentDMNDiagramElement() {
        return getSessionState().getCurrentDMNDiagramElement();
    }

    public Optional<Diagram> getCurrentDiagram() {
        return getSessionState().getCurrentDiagram();
    }

    public Diagram getDRGDiagram() {
        return Optional.ofNullable(getSessionState()).map(DMNDiagramsSessionState::getDRGDiagram).orElse(null);
    }

    public DMNDiagramElement getDRGDiagramElement() {
        return Optional.ofNullable(getSessionState()).map(DMNDiagramsSessionState::getDRGDiagramElement).orElse(null);
    }

    public void clear() {
        getSessionState().clear();
    }

    public List<DRGElement> getModelDRGElements() {
        return Optional.ofNullable(getSessionState()).map(DMNDiagramsSessionState::getModelDRGElements).orElse(emptyList());
    }

    public List<Import> getModelImports() {
        return Optional.ofNullable(getSessionState()).map(DMNDiagramsSessionState::getModelImports).orElse(emptyList());
    }

    public boolean isGlobalGraphSelected() {
        return getCurrentDMNDiagramElement().map(DRGDiagramUtils::isDRG).orElse(false);
    }

    public List<Graph> getGraphs() {
        return getDMNDiagrams()
                .stream()
                .map(tuple -> tuple.getStunnerDiagram().getGraph())
                .collect(Collectors.toList());
    }

    public List<Node> getNodesFromAllDiagramsWithContentId(final String contentDefinitionId) {
        final List<Node> allNodes = getAllNodes();
        return allNodes
                .stream()
                .filter(node -> definitionContainsDRGElement(node)
                        && Objects.equals(getDRGElementFromContentDefinition(node).getContentDefinitionId(), contentDefinitionId))
                .collect(Collectors.toList());
    }

    boolean definitionContainsDRGElement(final Node node) {
        return node.getContent() instanceof Definition
                && ((Definition) node.getContent()).getDefinition() instanceof DRGElement;
    }

    DRGElement getDRGElementFromContentDefinition(final Node node) {
        return ((DRGElement) ((Definition) node.getContent()).getDefinition());
    }

    public List<Node> getAllNodes() {
        final List<Node> result = new ArrayList<>();
        for (final DMNDiagramTuple tuple : getDMNDiagrams()) {
            final Diagram diagram = tuple.getStunnerDiagram();
            result.addAll(dmnDiagramUtils.getNodeStream(diagram).collect(Collectors.toList()));
        }
        return result;
    }

    public Diagram getCurrentGraphDiagram() {
        return getCurrentSession()
                .map(clientSession -> {
                    return getCanvasHandler(clientSession)
                            .map((Function<CanvasHandler, Diagram>) CanvasHandler::getDiagram)
                            .orElse(NO_DIAGRAM);
                })
                .orElse(NO_DIAGRAM);
    }

    public List<Graph> getNonGlobalGraphs() {
        return getDMNDiagrams()
                .stream()
                .filter(dmnDiagramTuple -> !isDRG(dmnDiagramTuple.getDMNDiagram()))
                .map(tuple -> tuple.getStunnerDiagram().getGraph())
                .collect(Collectors.toList());
    }

    private Optional<ClientSession> getCurrentSession() {
        return Optional.ofNullable(sessionManager.getCurrentSession());
    }

    private Optional<CanvasHandler> getCanvasHandler(final ClientSession session) {
        return Optional.ofNullable(session.getCanvasHandler());
    }
}

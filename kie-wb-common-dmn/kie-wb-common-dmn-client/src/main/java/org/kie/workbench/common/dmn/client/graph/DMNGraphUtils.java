/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.graph;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;

@Dependent
public class DMNGraphUtils {

    private static Definitions NO_DEFINITIONS = null;

    private static Diagram NO_DIAGRAM = null;

    private static CanvasHandler NO_CANVAS_HANDLER = null;

    private SessionManager sessionManager;

    private DMNDiagramUtils dmnDiagramUtils;

    public DMNGraphUtils() {
        //CDI proxy
    }

    @Inject
    public DMNGraphUtils(final SessionManager sessionManager,
                         final DMNDiagramUtils dmnDiagramUtils) {
        this.sessionManager = sessionManager;
        this.dmnDiagramUtils = dmnDiagramUtils;
    }

    public Definitions getDefinitions() {
        return getCurrentSession()
                .map(clientSession -> {
                    return getCanvasHandler(clientSession)
                            .map(canvasHandler -> dmnDiagramUtils.getDefinitions(canvasHandler.getDiagram()))
                            .orElse(NO_DEFINITIONS);
                })
                .orElse(NO_DEFINITIONS);
    }

    public Diagram getDiagram() {
        return getCurrentSession()
                .map(clientSession -> {
                    return getCanvasHandler(clientSession)
                            .map((Function<CanvasHandler, Diagram>) CanvasHandler::getDiagram)
                            .orElse(NO_DIAGRAM);
                })
                .orElse(NO_DIAGRAM);
    }

    public CanvasHandler getCanvasHandler() {
        return getCurrentSession()
                .map(clientSession -> getCanvasHandler(clientSession).orElse(NO_CANVAS_HANDLER))
                .orElse(NO_CANVAS_HANDLER);
    }

    public Definitions getDefinitions(final Diagram diagram) {
        return dmnDiagramUtils.getDefinitions(diagram);
    }

    public List<DRGElement> getDRGElements() {
        return getDRGElements(getDiagram());
    }

    public List<DRGElement> getDRGElements(final Diagram diagram) {
        return dmnDiagramUtils.getDRGElements(diagram);
    }

    public Stream<Node> getNodeStream(final Diagram diagram) {
        return dmnDiagramUtils.getNodeStream(diagram);
    }

    public Stream<Node> getNodeStream() {
        return getNodeStream(getDiagram());
    }

    public Optional<ClientSession> getCurrentSession() {
        return Optional.ofNullable(sessionManager.getCurrentSession());
    }

    private Optional<CanvasHandler> getCanvasHandler(final ClientSession session) {
        return Optional.ofNullable(session.getCanvasHandler());
    }
}

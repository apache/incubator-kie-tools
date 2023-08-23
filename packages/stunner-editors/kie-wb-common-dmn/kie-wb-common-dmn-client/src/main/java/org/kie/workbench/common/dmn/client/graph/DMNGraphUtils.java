/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;

@Dependent
public class DMNGraphUtils {

    private static CanvasHandler NO_CANVAS_HANDLER = null;

    private SessionManager sessionManager;

    private DMNDiagramUtils dmnDiagramUtils;

    private DMNDiagramsSession dmnDiagramsSession;

    public DMNGraphUtils() {
        //CDI proxy
    }

    @Inject
    public DMNGraphUtils(final SessionManager sessionManager,
                         final DMNDiagramUtils dmnDiagramUtils,
                         final DMNDiagramsSession dmnDiagramsSession) {
        this.sessionManager = sessionManager;
        this.dmnDiagramUtils = dmnDiagramUtils;
        this.dmnDiagramsSession = dmnDiagramsSession;
    }

    public Definitions getModelDefinitions() {
        return Optional
                .ofNullable(dmnDiagramsSession.getDRGDiagram())
                .map(e -> dmnDiagramUtils.getDefinitions(e))
                .orElse(null);
    }

    public List<DRGElement> getModelDRGElements() {
        return Optional
                .ofNullable(dmnDiagramsSession.getModelDRGElements())
                .orElse(new ArrayList<>());
    }

    public Definitions getDefinitions(final Diagram diagram) {
        return dmnDiagramUtils.getDefinitions(diagram);
    }

    public List<DRGElement> getDRGElements(final Diagram diagram) {
        return dmnDiagramUtils.getDRGElements(diagram);
    }

    public Stream<Node> getNodeStream(final Diagram diagram) {
        return dmnDiagramUtils.getNodeStream(diagram);
    }

    public Optional<ClientSession> getCurrentSession() {
        return Optional.ofNullable(sessionManager.getCurrentSession());
    }

    private Optional<CanvasHandler> getCanvasHandler(final ClientSession session) {
        return Optional.ofNullable(session.getCanvasHandler());
    }

    public Stream<Node> getNodeStream() {
        return getNodeStream(dmnDiagramsSession.getCurrentGraphDiagram());
    }

    public CanvasHandler getCanvasHandler() {
        return getCurrentSession()
                .map(clientSession -> getCanvasHandler(clientSession).orElse(NO_CANVAS_HANDLER))
                .orElse(NO_CANVAS_HANDLER);
    }
}

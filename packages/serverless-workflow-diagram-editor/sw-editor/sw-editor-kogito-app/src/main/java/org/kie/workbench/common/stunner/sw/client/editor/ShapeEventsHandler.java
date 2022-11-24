/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.editor;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.appformer.kogito.bridge.client.diagramApi.DiagramApi;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.State;

@Singleton
public class ShapeEventsHandler {

    @Inject
    private DiagramApi diagramApi;

    void onCanvasSelectionEvent(@Observes CanvasSelectionEvent event) {
        if (null != event.getCanvasHandler()) {
            if (event.getIdentifiers().size() == 1) {
                final String uuid = event.getIdentifiers().iterator().next();
                String nodeName = obtainNodeName(event.getCanvasHandler(), uuid);
                diagramApi.onNodeSelected(nodeName);
            }
        }
    }

    private String obtainNodeName(CanvasHandler<?, ?> handler, String uuid) {
        Node<?, ?> node = handler.getDiagram().getGraph().getNode(uuid);
        if (node == null) {
            return null;
        }

        Object content = node.getContent();
        if (content instanceof View) {
            Object bean = ((View) content).getDefinition();
            if (bean instanceof State) {
                return ((State) bean).getName();
            }
        }
        return null;
    }

    public void setDiagramApi(DiagramApi diagramApi) {
        this.diagramApi = diagramApi;
    }
}

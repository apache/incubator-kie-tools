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

package org.kie.workbench.common.dmn.client.editors.search;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.NOPDomainObject;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

@ApplicationScoped
public class DMNGraphSubIndex implements DMNSubIndex {

    private final DMNGraphUtils graphUtils;

    private final Event<CanvasSelectionEvent> canvasSelectionEvent;

    private final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent;

    private final Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent;

    private final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent;

    @Inject
    public DMNGraphSubIndex(final DMNGraphUtils graphUtils,
                            final Event<CanvasSelectionEvent> canvasSelectionEvent,
                            final Event<CanvasFocusedShapeEvent> canvasFocusedSelectionEvent,
                            final Event<CanvasClearSelectionEvent> canvasClearSelectionEventEvent,
                            final Event<DomainObjectSelectionEvent> domainObjectSelectionEvent) {
        this.graphUtils = graphUtils;
        this.canvasSelectionEvent = canvasSelectionEvent;
        this.canvasFocusedSelectionEvent = canvasFocusedSelectionEvent;
        this.canvasClearSelectionEventEvent = canvasClearSelectionEventEvent;
        this.domainObjectSelectionEvent = domainObjectSelectionEvent;
    }

    @Override
    public List<DMNSearchableElement> getSearchableElements() {
        return graphUtils
                .getNodeStream()
                .map(this::makeElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private DMNSearchableElement makeElement(final Node node) {

        return getText(node).map(text -> {

            final DMNSearchableElement element = new DMNSearchableElement();

            element.setText(text);
            element.setOnFound(makeOnFound(node.getUUID()));

            return element;
        }).orElse(null);
    }

    private Optional<String> getText(final Node node) {
        final Object content = node.getContent();
        if (content instanceof Definition) {
            final Object definition = ((Definition) content).getDefinition();
            if (definition instanceof DRGElement) {
                final DRGElement drgElement = (DRGElement) definition;
                return Optional.of(drgElement.getName().getValue());
            }
            if (definition instanceof TextAnnotation) {
                final TextAnnotation textAnnotation = (TextAnnotation) definition;
                return Optional.of(textAnnotation.getText().getValue());
            }
        }
        return Optional.empty();
    }

    private Command makeOnFound(final String uuid) {
        return () -> getCanvasHandler().ifPresent(canvasHandler -> {
            canvasSelectionEvent.fire(new CanvasSelectionEvent(canvasHandler, uuid));
            canvasFocusedSelectionEvent.fire(new CanvasFocusedShapeEvent(canvasHandler, uuid));
        });
    }

    private Optional<CanvasHandler> getCanvasHandler() {
        return Optional.ofNullable(graphUtils.getCanvasHandler());
    }

    @Override
    public void onNoResultsFound() {
        getCanvasHandler().ifPresent(canvasHandler -> {
            canvasClearSelectionEventEvent.fire(new CanvasClearSelectionEvent(canvasHandler));
            domainObjectSelectionEvent.fire(new DomainObjectSelectionEvent(canvasHandler, new NOPDomainObject()));
        });
    }
}

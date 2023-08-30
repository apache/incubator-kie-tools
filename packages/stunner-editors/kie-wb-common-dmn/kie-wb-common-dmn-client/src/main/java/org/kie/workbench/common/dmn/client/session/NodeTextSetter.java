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

package org.kie.workbench.common.dmn.client.session;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

@Dependent
public class NodeTextSetter {

    private final TextPropertyProviderFactory textPropertyProviderFactory;
    private final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent;
    private final DMNGraphUtils dmnGraphUtils;

    @Inject
    public NodeTextSetter(final TextPropertyProviderFactory textPropertyProviderFactory,
                          final Event<CanvasElementUpdatedEvent> canvasElementUpdatedEvent,
                          final DMNGraphUtils dmnGraphUtils) {
        this.textPropertyProviderFactory = textPropertyProviderFactory;
        this.canvasElementUpdatedEvent = canvasElementUpdatedEvent;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    public TextPropertyProviderFactory getTextPropertyProviderFactory() {
        return textPropertyProviderFactory;
    }

    public void setText(final String newText,
                        final Node node) {
        final String name = getName(node);
        if (!Objects.equals(name, newText)) {
            getCurrentSession().ifPresent(session -> {
                final TextPropertyProvider textPropertyProvider = getTextPropertyProviderFactory().getProvider(node);
                getCanvasHandler(session).ifPresent(canvasHandler -> {
                    textPropertyProvider.setText(canvasHandler, session.getCommandManager(), node, newText);
                    fireCanvasElementUpdated(canvasHandler, node);
                });
            });
        }
    }

    void fireCanvasElementUpdated(final AbstractCanvasHandler canvasHandler,
                                  final Node node) {
        canvasElementUpdatedEvent.fire(new CanvasElementUpdatedEvent(canvasHandler, node));
    }

    String getName(final Object node) {
        return getNamedElement(node)
                .map(namedElement -> namedElement.getName().getValue())
                .orElse("");
    }

    Optional<NamedElement> getNamedElement(final Object node) {
        return Optional
                .ofNullable(node)
                .filter(obj -> obj instanceof Node)
                .map(obj -> (Node) obj)
                .map(Node::getContent)
                .filter(content -> content instanceof View)
                .map(content -> (View) content)
                .map(View::getDefinition)
                .filter(content -> content instanceof NamedElement)
                .map(content -> (NamedElement) content);
    }

    Optional<DMNEditorSession> getCurrentSession() {
        return dmnGraphUtils.getCurrentSession().map(s -> (DMNEditorSession) s);
    }

    Optional<AbstractCanvasHandler> getCanvasHandler(final DMNEditorSession session) {
        return Optional.ofNullable(session.getCanvasHandler());
    }
}

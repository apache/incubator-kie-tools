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
package org.kie.workbench.common.stunner.core.client.session.command;

import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.AbstractCanvasHandlerEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

public abstract class AbstractClientSessionCommand<S extends ClientSession> implements ClientSessionCommand<S> {

    private static Logger LOGGER = Logger.getLogger(AbstractClientSessionCommand.class.getName());

    private S session;
    private Command statusCallback;
    private boolean enabled;

    public AbstractClientSessionCommand(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void bind(final S session) {
        this.session = session;
    }

    protected CanvasCommandFactory<AbstractCanvasHandler> loadCanvasFactory(
            final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
            final DefinitionUtils definitionUtils) {
        final Diagram diagram = session.getCanvasHandler().getDiagram();
        final String id = diagram.getMetadata().getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(id);
        return InstanceUtils.lookup(canvasCommandFactoryInstance, qualifier);
    }

    public abstract boolean accepts(final ClientSession session);

    @Override
    public ClientSessionCommand<S> listen(final Command statusCallback) {
        this.statusCallback = statusCallback;
        return this;
    }

    public void execute() {
        this.execute(new Callback<Object>() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(final Object error) {
                LOGGER.log(Level.SEVERE,
                           error.toString());
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public final void destroy() {
        doDestroy();
        enabled = false;
        session = null;
        statusCallback = null;
    }

    protected void doDestroy() {
    }

    protected void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    protected void enable(final boolean enable) {
        setEnabled(enable);
        fire();
    }

    protected void fire() {
        if (null != statusCallback) {
            statusCallback.execute();
        }
    }

    protected S getSession() {
        return session;
    }

    protected Element<? extends View<?>> getElement(String uuid) {
        AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) getSession().getCanvasHandler();
        return canvasHandler.getGraphIndex().get(uuid);
    }

    protected AbstractCanvasHandler getCanvasHandler() {
        return (AbstractCanvasHandler) getSession().getCanvasHandler();
    }

    protected Callback<Throwable> newDefaultCallback(String errorMessage) {
        return new Callback<Throwable>() {
            @Override
            public void onSuccess() {
                // Nothing to do.
            }

            @Override
            public void onError(final Throwable error) {
                LOGGER.log(Level.SEVERE,
                           errorMessage + " Details: " + error.toString());
            }
        };
    }

    protected boolean checkEventContext(final AbstractCanvasHandlerEvent canvasHandlerEvent) {
        final CanvasHandler _canvasHandler = canvasHandlerEvent.getCanvasHandler();
        return null != getSession() &&
                getSession().getCanvasHandler() != null
                && getSession().getCanvasHandler().equals(_canvasHandler);
    }
}
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

package org.kie.workbench.common.stunner.core.client.api;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public abstract class AbstractClientSessionManager implements SessionManager {

    private static Logger LOGGER = Logger.getLogger(AbstractClientSessionManager.class.getName());

    ClientSession current;

    protected abstract List<ClientSessionFactory> getFactories(final Metadata metadata);

    @Override
    @SuppressWarnings("unchecked")
    public <S extends ClientSession> ClientSessionFactory<S> getSessionFactory(final Metadata metadata,
                                                                               final Class<S> sessionType) {
        return getFactories(metadata).stream()
                .filter(factory -> factory.getSessionType().equals(sessionType))
                .findFirst()
                .orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends ClientSession> S getCurrentSession() {
        return (S) current;
    }

    @Override
    public <S extends ClientSession> void open(final S session) {
        checkNotNull("session",
                     session);
        if (null != this.current && !session.equals(this.current)) {
            this.pause();
        }
        if (!session.equals(this.current)) {
            log(Level.FINE,
                "Opening session [" + session.toString() + "] ...");
            this.current = session;
            getCurrentAbstractSession().open();
            postOpen();
            log(Level.FINE,
                "Session [" + current.toString() + "] opened");
        }
    }

    @Override
    public void pause() {
        if (null != current) {
            log(Level.FINE,
                "Pausing session [" + current.toString() + "] ...");
            getCurrentAbstractSession().pause();
            postPause();
            log(Level.FINE,
                "Session [" + current.toString() + "] paused");
        }
    }

    @Override
    public <S extends ClientSession> void resume(final S session) {
        checkNotNull("session",
                     session);
        if (null != current && !current.equals(session)) {
            pause();
        }
        if (!session.equals(current)) {
            log(Level.FINE,
                "Resuming session [" + session.toString() + "] ...");
            this.current = session;
            getCurrentAbstractSession().resume();
            postResume();
            log(Level.FINE,
                "Session [" + current.toString() + "] resumed");
        }
    }

    @Override
    public void destroy() {
        if (null != current) {
            log(Level.FINE,
                "Disposing session [" + current.toString() + "] ...");
            getCurrentAbstractSession().destroy();
            postDestroy();
            log(Level.FINE,
                "Session [" + current.toString() + "] destroyed");
            this.current = null;
        }
    }

    public void handleCommandError(final CommandException ce) {
        log(Level.SEVERE,
            "Command execution failed",
            ce);
    }

    public void handleClientError(final ClientRuntimeError error) {
        log(Level.SEVERE,
            "An error on client side happened",
            error.getThrowable());
    }

    /**
     * Called once a session has been opened.
     */
    public void postOpen() {
    }

    /**
     * Called once active session has been paused.
     */
    public void postPause() {
    }

    /**
     * Called once a session has been resumed.
     */
    public void postResume() {
    }

    /**
     * Called once active session has been destroyed.
     */
    public void postDestroy() {
    }

    protected AbstractClientSession getCurrentAbstractSession() {
        return (AbstractClientSession) current;
    }

    private void log(final Level level,
                     final String message) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message);
        }
    }

    private void log(final Level level,
                     final String message,
                     final Throwable t) {
        if (LogConfiguration.loggingIsEnabled()) {
            LOGGER.log(level,
                       message,
                       t);
        }
    }
}

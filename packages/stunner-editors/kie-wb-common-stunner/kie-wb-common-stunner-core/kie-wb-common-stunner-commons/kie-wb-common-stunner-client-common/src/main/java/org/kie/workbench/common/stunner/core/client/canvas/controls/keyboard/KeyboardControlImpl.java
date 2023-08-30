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


package org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

/**
 * A helper class for component that listen to keyboard events
 * but it only delegates to handlers if the current session is same
 * session bind to this component.
 */
@Dependent
@Default
public class KeyboardControlImpl
        extends AbstractCanvasControl<AbstractCanvas>
        implements KeyboardControl<AbstractCanvas, ClientSession> {

    private final SessionManager clientSessionManager;
    private final KeyEventHandler keyEventHandler;
    private ClientSession session;

    @Inject
    public KeyboardControlImpl(final SessionManager clientSessionManager,
                               final KeyEventHandler keyEventHandler) {
        this.clientSessionManager = clientSessionManager;
        this.keyEventHandler = keyEventHandler;
    }

    @Override
    public KeyboardControl<AbstractCanvas, ClientSession> addKeyShortcutCallback(final KeyShortcutCallback shortcutCallback) {
        this.keyEventHandler.addKeyShortcutCallback(new SessionKeyShortcutCallback(shortcutCallback));
        return this;
    }

    @Override
    protected void doInit() {
        this.keyEventHandler.setEnabled(true);
    }

    @Override
    protected void doDestroy() {
        keyEventHandler.setEnabled(false);
        keyEventHandler.clear();
        session = null;
    }

    public void setKeyEventHandlerEnabled(final boolean enabled) {
        this.keyEventHandler.setEnabled(enabled);
    }

    @Override
    public void bind(final ClientSession session) {
        this.session = session;
    }

    public class SessionKeyShortcutCallback implements KeyShortcutCallback {

        private final KeyShortcutCallback delegate;

        private SessionKeyShortcutCallback(final KeyShortcutCallback delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onKeyShortcut(final KeyboardEvent.Key... keys) {
            if (isSameSession(session)) {
                delegate.onKeyShortcut(keys);
            }
        }

        @Override
        public void onKeyUp(KeyboardEvent.Key key) {
            if (isSameSession(session)) {
                delegate.onKeyUp(key);
            }
        }

        public KeyShortcutCallback getDelegate() {
            return delegate;
        }
    }

    private boolean isSameSession(final ClientSession session) {
        return null != session && session.equals(clientSessionManager.getCurrentSession());
    }
}

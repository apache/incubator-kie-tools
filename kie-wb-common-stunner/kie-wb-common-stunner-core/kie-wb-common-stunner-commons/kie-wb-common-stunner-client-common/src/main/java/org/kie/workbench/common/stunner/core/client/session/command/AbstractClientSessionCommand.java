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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.client.session.ClientSession;
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

    @Override
    public void unbind() {
        this.session = null;
    }

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

    protected void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    protected void fire() {
        if (null != statusCallback) {
            statusCallback.execute();
        }
    }

    protected S getSession() {
        return session;
    }
}

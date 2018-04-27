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

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;

public abstract class AbstractClientSessionFactory<S extends ClientSession>
        implements ClientSessionFactory<S> {

    private static Logger LOGGER = Logger.getLogger(AbstractClientSessionFactory.class.getName());

    protected StunnerPreferences stunnerPreferences;

    protected StunnerPreferencesRegistry stunnerPreferencesRegistry;

    protected AbstractClientSessionFactory(final StunnerPreferences stunnerPreferences,
                                           final StunnerPreferencesRegistry stunnerPreferencesRegistry) {
        this.stunnerPreferences = stunnerPreferences;
        this.stunnerPreferencesRegistry = stunnerPreferencesRegistry;
    }

    protected abstract S buildSessionInstance();

    @Override
    public void newSession(Metadata metadata,
                           Consumer<S> sessionConsumer) {

        final S session = buildSessionInstance();
        stunnerPreferences.load(currentPreferences -> {
                                    stunnerPreferencesRegistry.register(currentPreferences);
                                    sessionConsumer.accept(session);
                                },
                                throwable -> {
                                    if (LogConfiguration.loggingIsEnabled()) {
                                        LOGGER.log(Level.SEVERE,
                                                   "An error was produced during StunnerPreferences initialization.",
                                                   throwable);
                                    }
                                    throw new RuntimeException(throwable);
                                });
    }
}

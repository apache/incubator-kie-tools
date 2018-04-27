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

package org.kie.workbench.common.dmn.client.session;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionFactory;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;

@ApplicationScoped
@DMNEditor
public class DMNClientFullSessionFactory extends AbstractClientSessionFactory<ClientFullSession> {

    private final ManagedInstance<DMNClientFullSession> fullSessionInstances;

    protected DMNClientFullSessionFactory() {
        this(null, null, null);
    }

    @Inject
    public DMNClientFullSessionFactory(final @DMNEditor ManagedInstance<DMNClientFullSession> fullSessionInstances,
                                       final StunnerPreferences stunnerPreferences,
                                       final StunnerPreferencesRegistry stunnerPreferencesRegistry) {
        super(stunnerPreferences,
              stunnerPreferencesRegistry);
        this.fullSessionInstances = fullSessionInstances;
    }

    @Override
    protected ClientFullSession buildSessionInstance() {
        return fullSessionInstances.get();
    }

    @Override
    public Class<ClientFullSession> getSessionType() {
        return ClientFullSession.class;
    }
}

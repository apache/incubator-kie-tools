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

package org.kie.workbench.common.stunner.cm.client.session;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;

public abstract class AbstractCaseManagementClientSessionFactory<S extends ClientSession>
        extends AbstractClientSessionFactory<S> {

    protected AbstractCaseManagementClientSessionFactory(final StunnerPreferences stunnerPreferences,
                                                         final StunnerPreferencesRegistry stunnerPreferencesRegistry) {
        super(stunnerPreferences,
              stunnerPreferencesRegistry);
    }

    @Override
    public void newSession(final Metadata metadata,
                           final Consumer<S> newSessionConsumer) {
        super.newSession(metadata,
                         newSessionConsumer::accept);
    }
}

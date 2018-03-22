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

import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

@ApplicationScoped
@DMNEditor
public class DMNClientFullSessionFactory implements ClientSessionFactory<ClientFullSession> {

    private final ManagedInstance<DMNClientFullSession> fullSessionInstances;

    protected DMNClientFullSessionFactory() {
        this(null);
    }

    @Inject
    public DMNClientFullSessionFactory(final @DMNEditor ManagedInstance<DMNClientFullSession> fullSessionInstances) {
        this.fullSessionInstances = fullSessionInstances;
    }

    @Override
    public void newSession(final Metadata metadata,
                           final Consumer<ClientFullSession> sessionConsumer) {
        sessionConsumer.accept(fullSessionInstances.get());
    }

    @Override
    public Class<ClientFullSession> getSessionType() {
        return ClientFullSession.class;
    }
}

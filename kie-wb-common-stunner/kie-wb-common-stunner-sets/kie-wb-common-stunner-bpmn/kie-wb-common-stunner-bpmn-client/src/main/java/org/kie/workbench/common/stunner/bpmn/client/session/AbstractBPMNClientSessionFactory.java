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

package org.kie.workbench.common.stunner.bpmn.client.session;

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;

public abstract class AbstractBPMNClientSessionFactory<S extends ClientSession>
        implements ClientSessionFactory<S> {

    protected abstract WorkItemDefinitionClientRegistry getWorkItemDefinitionRegistry();

    protected abstract S buildSessionInstance();

    @Override
    public void newSession(final Metadata metadata,
                           final Consumer<S> newSessionConsumer) {
        final S session = buildSessionInstance();
        getWorkItemDefinitionRegistry().load(session,
                                             metadata,
                                             () -> newSessionConsumer
                                                     .accept(session));
    }
}

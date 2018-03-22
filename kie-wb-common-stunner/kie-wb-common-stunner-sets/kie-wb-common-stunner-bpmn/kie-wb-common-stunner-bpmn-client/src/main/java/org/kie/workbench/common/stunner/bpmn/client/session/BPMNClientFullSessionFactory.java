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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ClientFullSessionImpl;

@ApplicationScoped
@BPMN
public class BPMNClientFullSessionFactory extends AbstractBPMNClientSessionFactory<ClientFullSession> {

    private final WorkItemDefinitionClientRegistry workItemDefinitionRegistry;
    private final ManagedInstance<ClientFullSessionImpl> fullSessionInstances;

    protected BPMNClientFullSessionFactory() {
        this(null, null);
    }

    @Inject
    public BPMNClientFullSessionFactory(final WorkItemDefinitionClientRegistry workItemDefinitionRegistry,
                                        final ManagedInstance<ClientFullSessionImpl> fullSessionInstances) {
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
        this.fullSessionInstances = fullSessionInstances;
    }

    @Override
    protected WorkItemDefinitionClientRegistry getWorkItemDefinitionRegistry() {
        return workItemDefinitionRegistry;
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

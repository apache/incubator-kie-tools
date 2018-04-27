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

import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.impl.AbstractClientSessionFactoryTest;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionFactory;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BPMNClientSessionFactoryTest extends AbstractClientSessionFactoryTest {

    @Mock
    private WorkItemDefinitionClientRegistry service;

    @SuppressWarnings("unchecked")
    @Override
    public void setUp() {
        super.setUp();
        doAnswer(invocationOnMock -> {
            Command callback = (Command) invocationOnMock.getArguments()[1];
            callback.execute();
            return null;
        }).when(service).load(eq(metadata),
                              any(Command.class));
    }

    @Override
    public AbstractClientSessionFactory createSessionFactory() {
        return new TestBPMNClientSession(stunnerPreferences,
                                         stunnerPreferencesRegistry);
    }

    @Override
    public void newSessionSuccessfulTest() {
        super.newSessionSuccessfulTest();
        verify(service,
               times(1)).load(eq(metadata),
                              any(Command.class));
    }

    private class TestBPMNClientSession extends AbstractBPMNClientSessionFactory {

        public TestBPMNClientSession(StunnerPreferences stunnerPreferences,
                                     StunnerPreferencesRegistry stunnerPreferencesRegistry) {
            super(stunnerPreferences,
                  stunnerPreferencesRegistry);
        }

        @Override
        protected WorkItemDefinitionClientRegistry getWorkItemDefinitionService() {
            return service;
        }

        @Override
        protected ClientSession buildSessionInstance() {
            return clientSession;
        }

        @Override
        public Class getSessionType() {
            return ClientSession.class;
        }
    }
}

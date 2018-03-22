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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class BPMNClientSessionFactoryTest {

    @Mock
    private ClientSession session;

    @Mock
    private Metadata metadata;

    @Mock
    private WorkItemDefinitionClientRegistry registry;

    private AbstractBPMNClientSessionFactory tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        doAnswer(invocationOnMock -> {
            Command callback = (Command) invocationOnMock.getArguments()[2];
            callback.execute();
            return null;
        }).when(registry).load(eq(session),
                               eq(metadata),
                               any(Command.class));
        tested = new TestBPMNClientSession();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildSession() {
        Consumer<ClientSession> sessionConsumer = mock(Consumer.class);
        tested.newSession(metadata,
                          sessionConsumer);
        verify(registry, times(1)).load(eq(session),
                                        eq(metadata),
                                        any(Command.class));
        verify(sessionConsumer, times(1)).accept(eq(session));
    }

    private class TestBPMNClientSession extends AbstractBPMNClientSessionFactory {

        @Override
        protected WorkItemDefinitionClientRegistry getWorkItemDefinitionRegistry() {
            return registry;
        }

        @Override
        protected ClientSession buildSessionInstance() {
            return session;
        }

        @Override
        public Class getSessionType() {
            return ClientSession.class;
        }
    }
}

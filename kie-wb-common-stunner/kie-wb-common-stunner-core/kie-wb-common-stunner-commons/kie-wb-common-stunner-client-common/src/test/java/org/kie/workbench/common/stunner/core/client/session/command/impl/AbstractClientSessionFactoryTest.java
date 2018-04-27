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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistry;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionFactory;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractClientSessionFactoryTest {

    @Mock
    protected StunnerPreferences stunnerPreferences;

    @Mock
    protected StunnerPreferences stunnerPreferencesResult;

    @Mock
    protected StunnerPreferencesRegistry stunnerPreferencesRegistry;

    @Mock
    protected ClientSession clientSession;

    @Mock
    protected Metadata metadata;

    protected AbstractClientSessionFactory sessionFactory;

    protected ArgumentCaptor<ParameterizedCommand> preferenceCaptor;

    protected ArgumentCaptor<ParameterizedCommand> errorCaptor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        preferenceCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        errorCaptor = ArgumentCaptor.forClass(ParameterizedCommand.class);
        sessionFactory = createSessionFactory();
    }

    public AbstractClientSessionFactory createSessionFactory() {
        return new AbstractClientSessionFactoryMock(stunnerPreferences,
                                                    stunnerPreferencesRegistry);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void newSessionSuccessfulTest() {
        final ClientSession[] result = new ClientSession[1];
        sessionFactory.newSession(metadata,
                                  newSession -> result[0] = (ClientSession) newSession);
        verify(stunnerPreferences,
               times(1)).load(preferenceCaptor.capture(),
                              errorCaptor.capture());
        preferenceCaptor.getValue().execute(stunnerPreferencesResult);
        verify(stunnerPreferencesRegistry,
               times(1)).register(stunnerPreferencesResult);
        assertEquals(clientSession,
                     result[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void newSessionUnSuccessfulTest() {
        expectedException.expect(RuntimeException.class);
        sessionFactory.newSession(metadata,
                                  newSession -> {
                                  });
        Throwable internalError = new Exception();
        verify(stunnerPreferences,
               times(1)).load(preferenceCaptor.capture(),
                              errorCaptor.capture());
        errorCaptor.getValue().execute(internalError);
        verify(stunnerPreferencesRegistry,
               times(0)).register(stunnerPreferencesResult);
    }

    class AbstractClientSessionFactoryMock extends AbstractClientSessionFactory<ClientSession> {

        public AbstractClientSessionFactoryMock(StunnerPreferences stunnerPreferences,
                                                StunnerPreferencesRegistry stunnerPreferencesRegistry) {
            super(stunnerPreferences,
                  stunnerPreferencesRegistry);
        }

        @Override
        public Class<ClientSession> getSessionType() {
            return ClientSession.class;
        }

        @Override
        protected ClientSession buildSessionInstance() {
            return clientSession;
        }
    }
}

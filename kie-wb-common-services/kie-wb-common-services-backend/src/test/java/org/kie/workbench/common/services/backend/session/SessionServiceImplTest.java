/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.session;

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoImpl;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceImplTest {

    @Mock
    private BuildInfoService buildInfoService;

    @Mock
    private KieModule kieModule;

    @Mock
    private BuildInfoImpl buildInfo;

    @Mock
    private Builder builder;

    @Mock
    private KieContainer kieContainer;

    @Captor
    private ArgumentCaptor<SessionConfiguration> sessionConfigurationArgumentCaptor;

    private SessionService sessionService;

    @Before
    public void setUp() throws Exception {
        sessionService = new SessionServiceImpl(buildInfoService);

        doReturn(buildInfo).when(buildInfoService).getBuildInfo(any());
        final Builder oldBuilder = mock(Builder.class);
        doReturn(oldBuilder).when(buildInfo).getBuilder();
        doReturn(builder).when(oldBuilder).clone();
        doReturn(kieContainer).when(builder).getKieContainer();
    }

    @Test
    public void newDefaultKieSession() {
        final KieSession kSession = mock(KieSession.class);
        doReturn(kSession).when(kieContainer).newKieSession(eq("ksessionName"));

        final KieSession resultKSession = sessionService.newKieSession(kieModule, "ksessionName");

        verify(builder).build();
        assertEquals(resultKSession, kSession);
    }

    @Test
    public void newDefaultKieSessionWithPseudoClock() {
        final KieBase kBase = mock(KieBase.class);
        final KieSession kSession = mock(KieSession.class);
        doReturn(kBase).when(kieContainer).getKieBase();
        doReturn(kSession).when(kBase).newKieSession(sessionConfigurationArgumentCaptor.capture(), eq(null));

        final KieSession resultKSession = sessionService.newDefaultKieSessionWithPseudoClock(kieModule);

        final SessionConfiguration sessionConfiguration = sessionConfigurationArgumentCaptor.getValue();

        verify(builder).build();
        assertEquals(ClockType.PSEUDO_CLOCK, sessionConfiguration.getClockType());
        assertEquals(resultKSession, kSession);
    }

    @Test
    public void kieContainerCouldNotBeBuilt() {
        doReturn(null).when(builder).getKieContainer();

        assertNull(sessionService.newKieSession(kieModule, "ksessionName"));
    }

    @Test
    public void kieContainerCouldNotBeBuiltWithPseudoClock() {
        doReturn(null).when(builder).getKieContainer();

        assertNull(sessionService.newDefaultKieSessionWithPseudoClock(kieModule));
    }

    @Test
    public void manageFailureToLoadABuilder() {
        when(buildInfoService.getBuildInfo(any())).thenReturn(null);

        assertThatThrownBy(() -> sessionService.newDefaultKieSessionWithPseudoClock(kieModule))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to clone Builder.");
    }
}
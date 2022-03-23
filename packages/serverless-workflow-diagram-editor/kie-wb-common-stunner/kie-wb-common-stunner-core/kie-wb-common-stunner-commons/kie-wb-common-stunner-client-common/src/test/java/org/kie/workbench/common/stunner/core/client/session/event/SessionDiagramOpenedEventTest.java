/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.event;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.util.EqualsAndHashCodeTestUtils;

import static org.mockito.Mockito.mock;

public class SessionDiagramOpenedEventTest {

    @Test
    public void testEqualsAndHashCode() {
        ClientSession session1 = mock(ClientSession.class);
        ClientSession session2 = mock(ClientSession.class);
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new SessionDiagramOpenedEvent(null),
                             new SessionDiagramOpenedEvent(null))
                .addTrueCase(new SessionDiagramOpenedEvent(session1),
                             new SessionDiagramOpenedEvent(session1))
                .addFalseCase(new SessionDiagramOpenedEvent(null),
                              new SessionDiagramOpenedEvent(session1))
                .addFalseCase(new SessionDiagramOpenedEvent(session1),
                              new SessionDiagramOpenedEvent(null))
                .addFalseCase(new SessionDiagramOpenedEvent(session1),
                              new SessionDiagramOpenedEvent(session2))
                .test();
    }
}

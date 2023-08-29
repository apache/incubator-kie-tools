/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.session.event;

import org.junit.Test;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.util.EqualsAndHashCodeTestUtils;

import static org.mockito.Mockito.mock;

public class SessionDiagramSavedEventTest {

    @Test
    public void testEqualsAndHashCode() {
        ClientSession session1 = mock(ClientSession.class);
        ClientSession session2 = mock(ClientSession.class);
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new SessionDiagramSavedEvent(null),
                             new SessionDiagramSavedEvent(null))
                .addTrueCase(new SessionDiagramSavedEvent(session1),
                             new SessionDiagramSavedEvent(session1))
                .addFalseCase(new SessionDiagramSavedEvent(null),
                              new SessionDiagramSavedEvent(session1))
                .addFalseCase(new SessionDiagramSavedEvent(session1),
                              new SessionDiagramSavedEvent(null))
                .addFalseCase(new SessionDiagramSavedEvent(session1),
                              new SessionDiagramSavedEvent(session2))
                .test();
    }
}

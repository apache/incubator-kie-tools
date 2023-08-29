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


package org.kie.workbench.common.stunner.client.widgets.event;

import org.junit.Test;
import org.kie.workbench.common.stunner.client.widgets.util.EqualsAndHashCodeTestUtils;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;

import static org.mockito.Mockito.mock;

public class SessionLostFocusEventTest {

    @Test
    public void testEqualsAndHashCode() {
        ClientSession session1 = mock(ClientSession.class);
        ClientSession session2 = mock(ClientSession.class);
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new SessionLostFocusEvent(null),
                             new SessionLostFocusEvent(null))
                .addTrueCase(new SessionLostFocusEvent(session1),
                             new SessionLostFocusEvent(session1))
                .addFalseCase(new SessionLostFocusEvent(null),
                              new SessionLostFocusEvent(session1))
                .addFalseCase(new SessionLostFocusEvent(session1),
                              new SessionLostFocusEvent(null))
                .addFalseCase(new SessionLostFocusEvent(session1),
                              new SessionLostFocusEvent(session2))
                .test();
    }
}

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

package org.kie.workbench.common.dmn.client.events;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EditExpressionEventTest {

    private static final String NODE_UUID = "uuid";

    @Mock
    private ClientSession session;

    @Mock
    private HasExpression hasExpression;

    @Mock
    private HasName hasName;

    private EditExpressionEvent event;

    @Before
    public void setup() {
        this.event = new EditExpressionEvent(session,
                                             NODE_UUID,
                                             hasExpression,
                                             Optional.of(hasName),
                                             false);
    }

    @Test
    public void testGetters() {
        assertThat(event.getSession()).isEqualTo(session);
        assertThat(event.getNodeUUID()).isEqualTo(NODE_UUID);
        assertThat(event.getHasExpression()).isEqualTo(hasExpression);
        assertThat(event.getHasName()).isEqualTo(Optional.of(hasName));
        assertThat(event.isOnlyVisualChangeAllowed()).isFalse();
    }
}

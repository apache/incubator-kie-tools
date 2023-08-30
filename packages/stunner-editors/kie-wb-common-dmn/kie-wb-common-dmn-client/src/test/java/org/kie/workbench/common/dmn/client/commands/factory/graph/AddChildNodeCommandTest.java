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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddChildNodeCommandTest {

    @Mock
    private Node<?, Edge> parent;

    @Mock
    private Node candidate;

    private AddChildNodeCommand command;

    @Before
    public void setup() {
        when(parent.getUUID()).thenReturn(UUID.uuid());
        when(candidate.getUUID()).thenReturn(UUID.uuid());

        this.command = new AddChildNodeCommand(parent, candidate);
    }

    @Test
    public void testSubCommandRegistration() {
        assertThat(command.getRegisterNodeCommand(candidate)).isExactlyInstanceOf(RegisterNodeCommand.class);
        assertThat(command.getSetChildNodeCommand(parent, candidate)).isExactlyInstanceOf(SetChildrenCommand.class);
    }
}

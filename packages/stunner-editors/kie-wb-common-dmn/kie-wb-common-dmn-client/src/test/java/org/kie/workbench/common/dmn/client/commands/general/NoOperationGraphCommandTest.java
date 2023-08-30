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
package org.kie.workbench.common.dmn.client.commands.general;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NoOperationGraphCommandTest {

    @Mock
    private GraphCommandExecutionContext context;

    private NoOperationGraphCommand command;

    @Before
    public void setup() {
        this.command = new NoOperationGraphCommand();
    }

    @Test
    public void testCheck() {
        assertThat(command.check(context)).isEqualTo(GraphCommandResultBuilder.SUCCESS);
    }

    @Test
    public void testExecute() {
        assertThat(command.execute(context)).isEqualTo(GraphCommandResultBuilder.SUCCESS);
    }

    @Test
    public void testUndo() {
        assertThat(command.undo(context)).isEqualTo(GraphCommandResultBuilder.SUCCESS);
    }
}

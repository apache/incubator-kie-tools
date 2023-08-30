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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPropertyValueCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateElementPropertyCommandTest {

    private String uuid = UUID.randomUUID().toString();

    @Mock
    private Element element;

    @Mock
    private Node node;

    @Mock
    private AbstractCanvasHandler abstractCanvasHandler;

    @Before
    public void setup() {
        when(element.getUUID()).thenReturn(uuid);
        when(node.getUUID()).thenReturn(uuid);
    }

    @Test
    public void testNewGraphCommand() {
        Command<GraphCommandExecutionContext, RuleViolation> command;
        String propertyId = "name";
        String value = "updated value";

        command = new UpdateElementPropertyCommand(element,
                                                   propertyId,
                                                   value).newGraphCommand(abstractCanvasHandler);
        assertEquals(command.getClass(),
                     UpdateElementPropertyValueCommand.class);

        command = new UpdateElementPropertyCommand(node,
                                                   propertyId,
                                                   value).newGraphCommand(abstractCanvasHandler);
        assertEquals(command.getClass(),
                     UpdateElementPropertyValueCommand.class);
    }
}

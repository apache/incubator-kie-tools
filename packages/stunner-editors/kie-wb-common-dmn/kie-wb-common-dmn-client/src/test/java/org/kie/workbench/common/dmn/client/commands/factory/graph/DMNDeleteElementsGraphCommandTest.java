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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.factory.canvas.DMNSafeDeleteNodeCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteElementsCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNDeleteElementsGraphCommandTest {

    @Test
    public void testCreateSafeDeleteNodeCommand() {

        final DMNGraphsProvider selectedDiagramProvider = mock(DMNGraphsProvider.class);
        final Node<?, Edge> node = mock(Node.class);
        final SafeDeleteNodeCommand.Options options = SafeDeleteNodeCommand.Options.defaults();
        final DeleteElementsCommand.DeleteCallback callback = mock(DeleteElementsCommand.DeleteCallback.class);
        final DMNDeleteElementsGraphCommand command = mock(DMNDeleteElementsGraphCommand.class);

        when(command.getGraphsProvider()).thenReturn(selectedDiagramProvider);
        when(node.getUUID()).thenReturn("uuid");
        when(command.createSafeDeleteNodeCommand(node, options, callback)).thenCallRealMethod();

        final SafeDeleteNodeCommand actual = command.createSafeDeleteNodeCommand(node, options, callback);

        assertTrue(actual instanceof DMNSafeDeleteNodeCommand);

        final DMNSafeDeleteNodeCommand dmnCommand = (DMNSafeDeleteNodeCommand) actual;
        assertEquals(dmnCommand.getNode(), node);
        assertEquals(dmnCommand.getOptions(), options);
        assertEquals(dmnCommand.getGraphsProvider(), selectedDiagramProvider);
    }
}

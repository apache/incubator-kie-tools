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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNSafeDeleteNodeCommandTest {

    @Test
    public void testShouldKeepChildren() {

        final DecisionService decisionService = mock(DecisionService.class);
        final Node<Definition<?>, Edge> candidate = mock(Node.class);

        final DMNSafeDeleteNodeCommand cmd = createMock(decisionService, candidate);

        final boolean actual = cmd.shouldKeepChildren(candidate);

        assertTrue(actual);
    }

    @Test
    public void testShouldKeepChildrenWhenIsNotDecisionService() {

        final Object decisionService = mock(Object.class);
        final Node<Definition<?>, Edge> candidate = mock(Node.class);

        final DMNSafeDeleteNodeCommand cmd = createMock(decisionService, candidate);

        final boolean actual = cmd.shouldKeepChildren(candidate);

        assertFalse(actual);
    }

    private DMNSafeDeleteNodeCommand createMock(final Object contentDefinition,
                                                final Node<Definition<?>, Edge> candidate) {

        final DMNSafeDeleteNodeCommand cmd = mock(DMNSafeDeleteNodeCommand.class);
        final Definition definition = mock(Definition.class);

        when(definition.getDefinition()).thenReturn(contentDefinition);
        when(candidate.getContent()).thenReturn(definition);
        when(cmd.shouldKeepChildren(candidate)).thenCallRealMethod();

        return cmd;
    }
}
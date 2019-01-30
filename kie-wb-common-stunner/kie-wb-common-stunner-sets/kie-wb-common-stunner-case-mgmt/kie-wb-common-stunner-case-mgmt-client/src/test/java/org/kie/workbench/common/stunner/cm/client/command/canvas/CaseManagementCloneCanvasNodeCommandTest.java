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

package org.kie.workbench.common.stunner.cm.client.command.canvas;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.command.AbstractCanvasCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.CloneCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementCloneCanvasNodeCommandTest extends CaseManagementAbstractCanvasCommandTest {

    @Mock
    private ManagedInstance<ChildrenTraverseProcessor> childrenTraverseProcessorInstances;

    private CaseManagementCloneCanvasNodeCommand tested;

    @Before
    public void setUp() throws Exception {
        super.setup();
        tested = new CaseManagementCloneCanvasNodeCommand(parent, candidate, SHAPE_SET_ID, childrenTraverseProcessorInstances);
    }

    @Test
    public void testCreateAddCanvasChildNodeCommand() throws Exception {
        final AbstractCanvasCommand command = tested.createAddCanvasChildNodeCommand(parent, candidate, SHAPE_SET_ID);

        assertTrue(CaseManagementAddChildNodeCanvasCommand.class.isInstance(command));
    }

    @Test
    public void testCreateCloneCanvasNodeCommand() throws Exception {
        final CloneCanvasNodeCommand command = tested.createCloneCanvasNodeCommand(parent, candidate, SHAPE_SET_ID);

        assertTrue(CaseManagementCloneCanvasNodeCommand.class.isInstance(command));
    }
}
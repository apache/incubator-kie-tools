/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetChildNodeCommandTest extends org.kie.workbench.common.stunner.core.graph.command.impl.SetChildNodeCommandTest {

    @Mock
    private View parentContent;

    @Mock
    private View candidateContent;

    @Mock
    private DMNModelInstrumentedBase parentDefinition;

    @Mock
    private DMNModelInstrumentedBase candidateDefinition;

    @Before
    public void setup() throws Exception {
        super.setup();

        when(parent.getContent()).thenReturn(parentContent);
        when(candidate.getContent()).thenReturn(candidateContent);
        when(parentContent.getDefinition()).thenReturn(parentDefinition);
        when(candidateContent.getDefinition()).thenReturn(candidateDefinition);
    }

    @Override
    protected SetChildNodeCommand makeSetChildNodeCommand() {
        return new SetChildNodeCommand(PARENT_UUID, CANDIDATE_UUID);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        super.testExecute();

        verify(candidateDefinition).setParent(eq(parentDefinition));
    }

    @Override
    public void testExecuteCheckFailed() {
        super.testExecuteCheckFailed();

        verify(candidateDefinition, never()).setParent(any(DMNModelInstrumentedBase.class));
    }
}

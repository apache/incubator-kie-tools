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
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SetChildrenCommandTest extends org.kie.workbench.common.dmn.client.commands.factory.graph.stunner.SetChildrenCommandTest {

    @Mock
    private View parentContent;

    @Mock
    private View candidateContent;

    @Mock
    private DMNDiagram parentDefinition;

    @Mock
    private Decision candidateDefinition;

    @Mock
    private Name candidateDefinitionName;

    @Captor
    private ArgumentCaptor<String> candidateDefinitionNameCaptor;

    @Before
    public void setup() throws Exception {
        super.setup();

        when(parent.getContent()).thenReturn(parentContent);
        when(candidate.getContent()).thenReturn(candidateContent);
        when(parentContent.getDefinition()).thenReturn(parentDefinition);
        when(candidateContent.getDefinition()).thenReturn(candidateDefinition);
        when(candidateDefinition.getName()).thenReturn(candidateDefinitionName);
    }

    @Override
    protected SetChildrenCommand createCommandInstance() {
        return new SetChildrenCommand(PARENT_UUID, new String[]{CANDIDATE_UUID});
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {
        super.testExecute();

        verify(candidateDefinition).setParent(eq(parentDefinition));

        verify(candidateDefinitionName).setValue(candidateDefinitionNameCaptor.capture());

        final String name = candidateDefinitionNameCaptor.getValue();
        assertThat(name).startsWith(Decision.class.getSimpleName());
        assertThat(name).endsWith("-1");
    }

    @Override
    public void testExecuteCheckFailed() {
        super.testExecuteCheckFailed();

        verify(candidateDefinition, never()).setParent(any(DMNModelInstrumentedBase.class));
    }
}

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
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.KindUtilities;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommandTest;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegisterNodeCommandTest extends AbstractGraphCommandTest {

    @Mock
    private Node candidate;

    @Mock
    private View candidateContent;

    @Mock
    private DMNDiagram parentDefinition;

    private Decision decision = spy(new Decision());

    private BusinessKnowledgeModel businessKnowledgeModel = spy(new BusinessKnowledgeModel());

    private RegisterNodeCommand command;

    @Before
    public void setup() {
        init();
        when(candidate.getContent()).thenReturn(candidateContent);

        this.command = new RegisterNodeCommand(candidate);
    }

    @Test
    public void testExecuteWithBusinessKnowledgeModelNode() {
        when(candidateContent.getDefinition()).thenReturn(businessKnowledgeModel);

        assertThat(command.execute(graphCommandExecutionContext).getType()).isEqualTo(CommandResult.Type.INFO);

        final FunctionDefinition encapsulatedLogic = businessKnowledgeModel.getEncapsulatedLogic();
        final Expression expression = encapsulatedLogic.getExpression();
        assertThat(expression).isInstanceOf(LiteralExpression.class);
        assertThat(expression.getParent()).isEqualTo(encapsulatedLogic);
        assertThat(KindUtilities.getKind(encapsulatedLogic)).isEqualTo(FunctionDefinition.Kind.FEEL);
    }

    @Test
    public void testExecuteWithNonBusinessKnowledgeModelNode() {
        when(candidateContent.getDefinition()).thenReturn(decision);

        assertThat(command.execute(graphCommandExecutionContext).getType()).isEqualTo(CommandResult.Type.INFO);
    }
}

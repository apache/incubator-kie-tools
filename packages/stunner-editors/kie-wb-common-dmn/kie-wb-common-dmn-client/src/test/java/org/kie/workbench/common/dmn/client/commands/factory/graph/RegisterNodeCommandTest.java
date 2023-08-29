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
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.KindUtilities;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RegisterNodeCommandTest extends AbstractGraphCommandTest {

    @Mock
    private Node candidate;

    @Mock
    private View candidateContent;

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
    public void testExecuteWithBusinessKnowledgeModelNodeWhenNodeIsNotNew() {

        final FunctionDefinition functionDefinition = new FunctionDefinition();
        final LiteralExpression literalExpression = makeLiteralExpression("123");

        when(candidateContent.getDefinition()).thenReturn(businessKnowledgeModel);
        doReturn(functionDefinition).when(businessKnowledgeModel).getEncapsulatedLogic();
        functionDefinition.setExpression(literalExpression);
        literalExpression.setParent(functionDefinition);

        assertThat(command.execute(graphCommandExecutionContext).getType()).isEqualTo(CommandResult.Type.INFO);

        final FunctionDefinition encapsulatedLogic = businessKnowledgeModel.getEncapsulatedLogic();
        final Expression expression = encapsulatedLogic.getExpression();

        assertThat(expression).isEqualTo(makeLiteralExpression("123"));
        assertThat(expression.getParent()).isEqualTo(encapsulatedLogic);
        assertThat(KindUtilities.getKind(encapsulatedLogic)).isEqualTo(FunctionDefinition.Kind.FEEL);
    }

    private LiteralExpression makeLiteralExpression(final String text) {
        final LiteralExpression expression = new LiteralExpression();
        expression.setId(new Id("0"));
        expression.setText(new Text(text));
        return expression;
    }

    @Test
    public void testExecuteWithNonBusinessKnowledgeModelNode() {
        when(candidateContent.getDefinition()).thenReturn(decision);

        assertThat(command.execute(graphCommandExecutionContext).getType()).isEqualTo(CommandResult.Type.INFO);
    }
}

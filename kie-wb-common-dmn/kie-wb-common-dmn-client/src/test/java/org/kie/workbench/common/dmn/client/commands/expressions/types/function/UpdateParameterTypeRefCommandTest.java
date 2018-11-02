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

package org.kie.workbench.common.dmn.client.commands.expressions.types.function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UpdateParameterTypeRefCommandTest {

    private static final QName OLD_PARAMETER_TYPE_REF = new QName(QName.NULL_NS_URI,
                                                                  BuiltInType.STRING.getName());

    private static final QName NEW_PARAMETER_TYPE_REF = new QName(QName.NULL_NS_URI,
                                                                  BuiltInType.DATE.getName());

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private InformationItem parameter;

    private UpdateParameterTypeRefCommand command;

    @Before
    public void setup() {
        this.parameter = new InformationItem();
        this.parameter.setTypeRef(OLD_PARAMETER_TYPE_REF);
        this.command = new UpdateParameterTypeRefCommand(parameter,
                                                         NEW_PARAMETER_TYPE_REF,
                                                         canvasOperation);
        doReturn(ruleManager).when(handler).getRuleManager();
    }

    @Test
    public void testGraphCommandAllow() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertEquals(NEW_PARAMETER_TYPE_REF,
                     parameter.getTypeRef());
    }

    @Test
    public void testGraphCommandUndo() {
        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Rename parameter and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertEquals(OLD_PARAMETER_TYPE_REF,
                     parameter.getTypeRef());
    }

    @Test
    public void testCanvasCommandAllow() {
        //There are no Canvas mutations by the command
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        //There are no Canvas mutations by the command
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.execute(handler));

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        //There are no Canvas mutations by the command
        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.undo(handler));

        verify(canvasOperation).execute();
    }
}

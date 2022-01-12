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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SetHitPolicyCommandTest {

    private static final HitPolicy OLD_HIT_POLICY = HitPolicy.ANY;

    private static final HitPolicy NEW_HIT_POLICY = HitPolicy.UNIQUE;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Mock
    private AbstractCanvasHandler handler;

    @Mock
    private GraphCommandExecutionContext gce;

    @Mock
    private RuleManager ruleManager;

    private DecisionTable dtable;

    private SetHitPolicyCommand command;

    @Before
    public void setup() {
        doReturn(ruleManager).when(handler).getRuleManager();
    }

    private void makeCommand(final HitPolicy hitPolicy) {
        this.dtable = new DecisionTable();
        this.dtable.setHitPolicy(hitPolicy);
        this.command = new SetHitPolicyCommand(dtable,
                                               NEW_HIT_POLICY,
                                               canvasOperation);
    }

    @Test
    public void testGraphCommandAllow() {
        makeCommand(OLD_HIT_POLICY);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.allow(gce));
    }

    @Test
    public void testGraphCommandExecute() {
        makeCommand(OLD_HIT_POLICY);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));

        assertEquals(NEW_HIT_POLICY,
                     dtable.getHitPolicy());
    }

    @Test
    public void testGraphCommandUndo() {
        makeCommand(OLD_HIT_POLICY);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Set Aggregator and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertEquals(OLD_HIT_POLICY,
                     dtable.getHitPolicy());
    }

    @Test
    public void testGraphCommandUndoWhenOriginallyNull() {
        makeCommand(null);

        final Command<GraphCommandExecutionContext, RuleViolation> c = command.newGraphCommand(handler);

        //Set Aggregator and then undo
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.execute(gce));
        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     c.undo(gce));

        assertEquals(HitPolicy.UNIQUE,
                     dtable.getHitPolicy());
    }

    @Test
    public void testCanvasCommandAllow() {
        makeCommand(OLD_HIT_POLICY);

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.allow(handler));
    }

    @Test
    public void testCanvasCommandExecute() {
        makeCommand(OLD_HIT_POLICY);

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.execute(handler));

        verifyZeroInteractions(handler, gce, ruleManager);

        verify(canvasOperation).execute();
    }

    @Test
    public void testCanvasCommandUndo() {
        makeCommand(OLD_HIT_POLICY);

        final Command<AbstractCanvasHandler, CanvasViolation> c = command.newCanvasCommand(handler);

        //Set Aggregator and then undo
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.execute(handler));
        reset(canvasOperation);
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     c.undo(handler));

        verifyZeroInteractions(handler, gce, ruleManager);

        verify(canvasOperation).execute();
    }
}

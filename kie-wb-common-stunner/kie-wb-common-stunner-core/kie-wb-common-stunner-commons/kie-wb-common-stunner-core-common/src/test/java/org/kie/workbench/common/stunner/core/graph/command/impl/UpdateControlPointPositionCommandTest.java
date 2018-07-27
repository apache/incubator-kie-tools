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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateControlPointPositionCommandTest {

    private static final Point2D TARGET_LOCATION = Point2D.create(7, 5);

    @Mock
    private GraphCommandExecutionContext commandExecutionContext;

    @Mock
    private RuleManager ruleManager;

    private Edge edge;
    private ViewConnectorImpl content;
    private ControlPoint controlPoint;

    private UpdateControlPointPositionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        controlPoint = ControlPoint.build(1, 4, 0);
        when(commandExecutionContext.getRuleManager()).thenReturn(ruleManager);
        content = new ViewConnectorImpl<>(mock(Object.class),
                                          BoundsImpl.build(0, 0, 10, 10));
        edge = new EdgeImpl<>("edge1");
        edge.setContent(content);
        content.setControlPoints(Collections.singletonList(controlPoint));
        tested = new UpdateControlPointPositionCommand(edge,
                                                       controlPoint,
                                                       TARGET_LOCATION);
    }

    @Test
    public void testAllow() {
        final CommandResult<RuleViolation> allow1 = tested.allow(commandExecutionContext);
        assertFalse(CommandUtils.isError(allow1));
        content.setControlPoints(Collections.singletonList(mock(ControlPoint.class)));
        final CommandResult<RuleViolation> allow2 = tested.allow(commandExecutionContext);
        assertTrue(CommandUtils.isError(allow2));
    }

    @Test
    public void testExecute() {
        final CommandResult<RuleViolation> result = tested.execute(commandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        assertEquals(7, controlPoint.getLocation().getX(), 0);
        assertEquals(5, controlPoint.getLocation().getY(), 0);
        assertEquals(Point2D.create(1, 4), tested.getOldPosition());
    }

    @Test
    public void testExecuteInvalid() {
        content.setControlPoints(Collections.singletonList(mock(ControlPoint.class)));
        final CommandResult<RuleViolation> result = tested.execute(commandExecutionContext);
        assertTrue(CommandUtils.isError(result));
        assertEquals(1, controlPoint.getLocation().getX(), 0);
        assertEquals(4, controlPoint.getLocation().getY(), 0);
    }

    @Test
    public void testUndo() {
        tested.execute(commandExecutionContext);
        final CommandResult<RuleViolation> result = tested.undo(commandExecutionContext);
        assertFalse(CommandUtils.isError(result));
        assertEquals(1, controlPoint.getLocation().getX(), 0);
        assertEquals(4, controlPoint.getLocation().getY(), 0);
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UpdateCanvasElementPositionCommandTest extends AbstractCanvasCommandTest {

    private static Bounds PANEL_BOUNDS = Bounds.create(0d, 0d, 600d, 600d);
    private static Bounds ELEMENT_BOUNDS = Bounds.create(0d, 0d, 100d, 100d);

    @Mock
    private Element candidate;

    private UpdateCanvasElementPositionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        when(canvasPanel.getLocationConstraints()).thenReturn(PANEL_BOUNDS);
        when(candidate.getContent()).thenReturn(new ViewImpl<>(mock(Object.class), ELEMENT_BOUNDS));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecuteSuccess() {
        tested = new UpdateCanvasElementPositionCommand(candidate, Point2D.create(0d, 0d));
        CommandResult<CanvasViolation> result = tested.execute(canvasHandler);
        assertNotEquals(CommandResult.Type.ERROR,
                        result.getType());
        verify(canvasHandler,
               times(1)).updateElementPosition(eq(candidate),
                                               any(MutationContext.class));
    }
}

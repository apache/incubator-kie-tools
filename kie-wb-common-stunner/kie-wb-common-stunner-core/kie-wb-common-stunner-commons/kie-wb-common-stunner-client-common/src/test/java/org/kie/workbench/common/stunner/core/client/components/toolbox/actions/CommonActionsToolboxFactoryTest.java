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

package org.kie.workbench.common.stunner.core.client.components.toolbox.actions;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.components.toolbox.Toolbox;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CommonActionsToolboxFactoryTest {

    private static final String E_UUID = "e1";

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> commandFactory;

    @Mock
    private DeleteNodeAction deleteNodeAction;

    @Mock
    private Command deleteNodeActionDestroyer;

    @Mock
    private ActionsToolboxView view;

    @Mock
    private Command viewDestroyer;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Node element;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> deleteNodeCommand;

    private CommonActionsToolboxFactory tested;

    @Before
    public void setup() throws Exception {
        when(element.getUUID()).thenReturn(E_UUID);
        when(element.asNode()).thenReturn(element);
        when(commandFactory.deleteNode(eq(element))).thenReturn(deleteNodeCommand);
        this.tested = new CommonActionsToolboxFactory(commandFactory,
                                                      () -> deleteNodeAction,
                                                      deleteNodeActionDestroyer,
                                                      () -> view,
                                                      viewDestroyer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildToolbox() {
        when(deleteNodeCommand.allow(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.SUCCESS);
        final Optional<Toolbox<?>> _toolbox =
                tested.build(canvasHandler,
                             element);
        assertTrue(_toolbox.isPresent());
        Toolbox<?> toolbox = _toolbox.get();
        assertTrue(toolbox instanceof ActionsToolbox);
        final ActionsToolbox actionsToolbox = (ActionsToolbox) toolbox;
        assertEquals(E_UUID,
                     actionsToolbox.getElementUUID());
        assertEquals(1,
                     actionsToolbox.size());
        assertEquals(deleteNodeAction,
                     actionsToolbox.iterator().next());
        verify(view,
               times(1)).init(eq(actionsToolbox));
        verify(view,
               times(1)).addButton(any(Glyph.class),
                                   anyString(),
                                   any(Consumer.class));
    }

    @Test
    public void testBuildToolboxNotAllowed() {
        when(deleteNodeCommand.allow(eq(canvasHandler))).thenReturn(CanvasCommandResultBuilder.FAILED);
        final Optional<Toolbox<?>> toolbox =
                tested.build(canvasHandler,
                             element);
        assertFalse(toolbox.isPresent());
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(deleteNodeActionDestroyer, times(1)).execute();
        verify(viewDestroyer, times(1)).execute();
    }
}

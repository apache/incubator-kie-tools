/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxBuilder;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DMNActionsToolboxControlProviderTest extends BaseDMNToolboxControlProviderTest {

    @Override
    protected AbstractToolboxControlProvider getProvider() {
        return new DMNActionsToolboxControlProvider(toolboxFactory,
                                                    toolboxCommandFactory,
                                                    definitionManager,
                                                    sessionManager,
                                                    event);
    }

    @Override
    protected void doAssertion(final boolean supports) {
        assertTrue(supports);
    }

    @Test
    public void checkGridHasExpectedConfiguration() {
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final Element item = mock(Element.class);

        final ToolboxButtonGrid grid = provider.getGrid(context,
                                                        item);
        assertNotNull(grid);
        assertEquals(3,
                     grid.getRows());
        assertEquals(1,
                     grid.getColumns());
        assertEquals(AbstractToolboxControlProvider.DEFAULT_ICON_SIZE,
                     grid.getButtonSize());
        assertEquals(AbstractToolboxControlProvider.DEFAULT_PADDING,
                     grid.getPadding());
    }

    @Test
    public void checkGetOnIsNorthWest() {
        assertEquals(ToolboxBuilder.Direction.NORTH_WEST,
                     provider.getOn());
    }

    @Test
    public void checkGetTowardsIsSouthWest() {
        assertEquals(ToolboxBuilder.Direction.SOUTH_WEST,
                     provider.getTowards());
    }
}

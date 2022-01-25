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

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.toolbox.grid.AutoGrid;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CommonActionsToolboxViewTest
        extends AbstractActionsToolboxViewTest {

    private CommonActionsToolboxView tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();
        this.tested = new CommonActionsToolboxView(glyphRenderers,
                                                   toolboxFactory);
        when(toolbox.getView()).thenReturn(tested);
    }

    @Test
    public void testInit() {
        final CommonActionsToolboxView cascade = doInit();
        assertEquals(tested,
                     cascade);
        verify(toolboxFactory,
               times(1)).forWiresShape(eq(shape));
        verify(toolboxView,
               times(1)).attachTo(eq(topLayer));
        // Verify toolbox settings.
        verify(toolboxView,
               times(1))
                .at(eq(CommonActionsToolboxView.TOOLBOX_AT));
        final ArgumentCaptor<Point2DGrid> gridCaptor = ArgumentCaptor.forClass(Point2DGrid.class);
        verify(toolboxView,
               times(1))
                .grid(gridCaptor.capture());
        final AutoGrid grid = (AutoGrid) gridCaptor.getValue();
        assertEquals(AbstractActionsToolboxView.BUTTON_SIZE,
                     grid.getIconSize(),
                     0);
        assertEquals(AbstractActionsToolboxView.BUTTON_PADDING,
                     grid.getPadding(),
                     0);
        assertEquals(CommonActionsToolboxView.GRID_TOWARDS,
                     grid.getDirection());
        // Verify toolbip.
        verify(toolboxTooltip,
               times(1)).at(CommonActionsToolboxView.TOOLTIP_AT);
        verify(toolboxTooltip,
               times(1)).towards(CommonActionsToolboxView.TOOLTIP_TOWARDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddButton() {
        doInit();
        final Consumer<MouseClickEvent> eventConsumer = mock(Consumer.class);
        tested.addButton(mock(Glyph.class),
                         "title1");
        super.testAddButton("title1");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddButtonIntoParent() {
        doInit();
        final ButtonItem buttonItem = mock(ButtonItem.class);
        tested.addButton(buttonItem);
        verify(toolboxView,
               times(1)).add(eq(buttonItem));
    }

    private CommonActionsToolboxView doInit() {
        return tested.init(toolbox,
                           canvas,
                           shape);
    }
}

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.toolbox.grid.FixedLayoutGrid;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonGridItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.client.core.shape.toolbox.items.DecoratorItem;
import com.ait.lienzo.client.core.shape.toolbox.items.TooltipItem;
import com.ait.lienzo.client.core.shape.toolbox.items.decorator.BoxDecorator;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GroupedActionsToolbox;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class GroupActionsToolboxViewTest
        extends AbstractActionsToolboxViewTest {

    @Mock
    private ButtonGridItem buttonGridItem;

    @Mock
    private BoxDecorator buttonDecorator;

    @Mock
    private GroupedActionsToolbox groupedActionsToolbox;

    private GroupActionsToolboxView tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.init();

        toolbox = groupedActionsToolbox;

        when(groupedActionsToolbox.getConnectorSize()).thenReturn(1);
        when(buttonsFactory.dropRight(any(Group.class))).thenReturn(buttonGridItem);
        when(decoratorsFactory.button()).thenReturn(buttonDecorator);
        when(buttonDecorator.configure(any(Consumer.class))).thenReturn(buttonDecorator);
        when(buttonDecorator.setBoundingBox(any(BoundingBox.class))).thenReturn(buttonDecorator);
        when(buttonDecorator.setPadding(anyDouble())).thenReturn(buttonDecorator);
        when(buttonDecorator.copy()).thenReturn(buttonDecorator);
        when(buttonGridItem.tooltip(any(TooltipItem.class))).thenReturn(buttonGridItem);
        when(buttonGridItem.grid(any(Point2DGrid.class))).thenReturn(buttonGridItem);
        when(buttonGridItem.decorate(any(DecoratorItem.class))).thenReturn(buttonGridItem);
        when(buttonGridItem.decorateGrid(any(DecoratorItem.class))).thenReturn(buttonGridItem);
        when(buttonGridItem.onMouseEnter(any(NodeMouseEnterHandler.class))).thenReturn(buttonGridItem);
        when(buttonGridItem.onMouseExit(any(NodeMouseExitHandler.class))).thenReturn(buttonGridItem);
        when(buttonGridItem.onClick(any(Consumer.class))).thenReturn(buttonGridItem);
        this.tested = new GroupActionsToolboxView(glyphRenderers,
                                                  toolboxFactory);
        when(toolbox.getView()).thenReturn(tested);
    }

    @Test
    public void testInit() {
        final GroupActionsToolboxView cascade = doInit();
        assertEquals(tested,
                     cascade);
        verify(toolboxFactory,
               times(1)).forWiresShape(eq(shape));
        verify(toolboxView,
               times(1)).attachTo(eq(topLayer));
        assertConfigureToolbox();
        assertConfigureButtonGridItem();
        assertTooltip();
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

    private GroupActionsToolboxView doInit() {
        return tested.init(toolbox,
                           canvas,
                           shape);
    }

    private void assertConfigureToolbox() {
        verify(toolboxView,
               times(1))
                .at(eq(GroupActionsToolboxView.TOOLBOX_AT));
        final ArgumentCaptor<Point2DGrid> gridCaptor = ArgumentCaptor.forClass(Point2DGrid.class);
        verify(toolboxView,
               times(1))
                .grid(gridCaptor.capture());
        final FixedLayoutGrid grid = (FixedLayoutGrid) gridCaptor.getValue();
        assertEquals(1,
                     grid.getRows());
        assertEquals(1,
                     grid.getCols());
        assertEquals(AbstractActionsToolboxView.BUTTON_SIZE,
                     grid.getIconSize(),
                     0);
        assertEquals(AbstractActionsToolboxView.BUTTON_PADDING,
                     grid.getPadding(),
                     0);
    }

    private void assertConfigureButtonGridItem() {
        final ArgumentCaptor<Point2DGrid> gridCaptor = ArgumentCaptor.forClass(Point2DGrid.class);
        verify(toolboxView,
               times(1))
                .grid(gridCaptor.capture());

        final FixedLayoutGrid grid = (FixedLayoutGrid) gridCaptor.getValue();
        assertEquals(1,
                     grid.getRows());

        assertEquals(1,
                     grid.getCols());

        assertEquals(AbstractActionsToolboxView.BUTTON_SIZE,
                     grid.getIconSize(),
                     0);
        assertEquals(AbstractActionsToolboxView.BUTTON_PADDING,
                     grid.getPadding(),
                     0);
        assertEquals(GroupActionsToolboxView.ITEMS_GRID_TOWARDS,
                     grid.getTowards());
    }

    private void assertTooltip() {
        verify(toolboxTooltip,
               times(1)).at(GroupActionsToolboxView.TOOLTIP_AT);
        verify(toolboxTooltip,
               times(1)).towards(GroupActionsToolboxView.TOOLTIP_TOWARDS);
    }
}

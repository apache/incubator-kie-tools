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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.toolbox.ToolboxVisibilityExecutors;
import com.ait.lienzo.client.core.shape.toolbox.grid.FixedLayoutGrid;
import com.ait.lienzo.client.core.shape.toolbox.grid.Point2DGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonGridItem;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ToolboxFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.ToolboxTextTooltip;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.Direction;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonIconsGlyphFactory;

/**
 * It renders the toolbox' morph actions inside a drop-down button.
 */
@Dependent
@MorphActionsToolbox
public class MorphActionsToolboxView
        extends AbstractActionsToolboxView<MorphActionsToolboxView> {

    static final double GRID_BUTTON_SIZE = 15;
    static final double GRID_BUTTON_PADDING = 5;
    static final double GRID_DECORATOR_PADDING = 10;
    static final Direction TOOLBOX_AT = Direction.SOUTH_WEST;
    static final Direction ITEMS_GRID_TOWARDS = Direction.SOUTH_EAST;
    static final Direction TOOLTIP_AT = Direction.SOUTH;
    static final Direction TOOLTIP_TOWARDS = Direction.SOUTH;

    private ButtonGridItem gridItem;

    @Inject
    public MorphActionsToolboxView(final LienzoGlyphRenderers glyphRenderers) {
        this(glyphRenderers,
             ToolboxFactory.INSTANCE);
    }

    MorphActionsToolboxView(final LienzoGlyphRenderers glyphRenderers,
                            final ToolboxFactory toolboxFactory) {
        super(glyphRenderers,
              toolboxFactory);
    }

    @Override
    protected void configure(final ActionsToolbox toolbox) {
        configureToolbox(toolbox);
        configureDropDown(toolbox);
    }

    @Override
    protected void addButton(final ButtonItem buttonItem) {
        gridItem.add(buttonItem);
    }

    @Override
    protected ToolboxTextTooltip createTooltip(final ActionsToolbox toolbox) {
        return getToolboxFactory()
                .tooltips()
                .forToolbox(getToolboxView())
                .at(TOOLTIP_AT)
                .towards(TOOLTIP_TOWARDS)
                .withText(defaultTextConsumer());
    }

    @Override
    protected double getGlyphSize() {
        return GRID_BUTTON_SIZE;
    }

    private void configureToolbox(final ActionsToolbox toolbox) {
        getToolboxView()
                .at(TOOLBOX_AT)
                .grid(createFixedGrid(BUTTON_SIZE,
                                      BUTTON_PADDING,
                                      1))
                .useShowExecutor(ToolboxVisibilityExecutors.upScaleY())
                .useHideExecutor(ToolboxVisibilityExecutors.downScaleY());
    }

    private void configureDropDown(final ActionsToolbox toolbox) {
        final Point2DGrid grid = createFixedGrid(GRID_BUTTON_SIZE,
                                                 GRID_BUTTON_PADDING,
                                                 toolbox.size());
        final Group glyphView = renderGlyph(StunnerCommonIconsGlyphFactory.GEARS,
                                            BUTTON_SIZE);
        gridItem =
                getToolboxFactory()
                        .buttons()
                        .dropRight(glyphView)
                        .grid(grid)
                        .decorate(getToolboxFactory()
                                          .decorators()
                                          .button()
                                          .setPadding(BUTTON_PADDING)
                                          .configure(path -> path.setFillColor(ColorName.LIGHTGREY)))
                        .decorateGrid(getToolboxFactory()
                                              .decorators()
                                              .button()
                                              .setPadding(GRID_DECORATOR_PADDING)
                                              .configure(path -> path.setFillColor("#e6e6e6")));
        getToolboxView().add(gridItem);
    }

    private Point2DGrid createFixedGrid(final double buttonSize,
                                        final double padding,
                                        final int cols) {
        return new FixedLayoutGrid(padding,
                                   buttonSize,
                                   ITEMS_GRID_TOWARDS,
                                   1,
                                   cols);
    }
}

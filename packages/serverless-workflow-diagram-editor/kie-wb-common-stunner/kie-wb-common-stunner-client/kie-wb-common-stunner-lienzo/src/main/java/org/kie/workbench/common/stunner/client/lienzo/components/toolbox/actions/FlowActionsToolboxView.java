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

// TODO
package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.actions;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.toolbox.ToolboxVisibilityExecutors;
import com.ait.lienzo.client.core.shape.toolbox.grid.AutoGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.ButtonItem;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ItemsToolboxHighlight;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ToolboxFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.ToolboxTextTooltip;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.Direction;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.IsToolboxActionDraggable;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;

/**
 * It renders the toolbox' actions as first level button items.
 */
@Dependent
@FlowActionsToolbox
public class FlowActionsToolboxView
        extends AbstractActionsToolboxView<FlowActionsToolboxView> {

    static final Direction TOOLBOX_AT = Direction.NORTH_EAST;
    static final Direction GRID_TOWARDS = Direction.SOUTH_EAST;

    private ItemsToolboxHighlight highlight;

    @Inject
    public FlowActionsToolboxView(final LienzoGlyphRenderers glyphRenderers) {
        this(glyphRenderers,
             ToolboxFactory.INSTANCE);
    }

    FlowActionsToolboxView(final LienzoGlyphRenderers glyphRenderers,
                           final ToolboxFactory toolboxFactory) {
        super(glyphRenderers,
              toolboxFactory);
    }

    @Override
    protected void configure(final ActionsToolbox toolbox) {
        getToolboxView()
                .at(TOOLBOX_AT)
                .grid(new AutoGrid.Builder()
                              .forBoundingBox(getToolboxView().getBoundingBox())
                              .withPadding(BUTTON_PADDING)
                              .withIconSize(getGlyphSize())
                              .towards(GRID_TOWARDS)
                              .build())
                .decorate(getToolboxFactory()
                                  .decorators()
                                  .box()
                                  .setPadding(BUTTON_PADDING)
                                  .configure(path -> {
                                      path.setFillAlpha(0.95);
                                      path.setFillColor(ColorName.WHITE);
                                      path.setStrokeAlpha(0);
                                  })
                )
                .useShowExecutor(ToolboxVisibilityExecutors.upScaleX())
                .useHideExecutor(ToolboxVisibilityExecutors.downScaleX());
        highlight = new ItemsToolboxHighlight(getToolboxView());
    }

    @Override
    protected ToolboxTextTooltip createTooltip(final ActionsToolbox toolbox) {
        return getToolboxFactory()
                .tooltips()
                .forToolbox(getToolboxView())
                .withText(defaultTextConsumer());
    }

    @Override
    protected void onButtonClick(final ActionsToolbox<ActionsToolboxView<?>> toolbox,
                                 final ToolboxAction toolboxAction,
                                 final ButtonItem button,
                                 final NodeMouseClickEvent event) {
        highlight.highlight(button);
        super.onButtonClick(toolbox, toolboxAction, button, event);
    }

    @Override
    protected void onButtonMoveStart(final ActionsToolbox<ActionsToolboxView<?>> toolbox,
                                     final IsToolboxActionDraggable toolboxAction,
                                     final ButtonItem button,
                                     final NodeMouseMoveEvent event) {
        highlight.highlight(button);
        super.onButtonMoveStart(toolbox, toolboxAction, button, event);
    }

    @Override
    protected double getGlyphSize() {
        return BUTTON_SIZE;
    }

    @Override
    public void destroy() {
        Optional.ofNullable(highlight).ifPresent(ItemsToolboxHighlight::restore);
        highlight = null;
        super.destroy();
    }
}

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

import com.ait.lienzo.client.core.shape.toolbox.ToolboxVisibilityExecutors;
import com.ait.lienzo.client.core.shape.toolbox.grid.AutoGrid;
import com.ait.lienzo.client.core.shape.toolbox.items.impl.ToolboxFactory;
import com.ait.lienzo.client.core.shape.toolbox.items.tooltip.ToolboxTextTooltip;
import com.ait.lienzo.shared.core.types.Direction;
import org.kie.workbench.common.stunner.client.lienzo.components.glyph.LienzoGlyphRenderers;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;

/**
 * It renders the toolbox' actions as first level button items.
 */
@Dependent
@CommonActionsToolbox
public class CommonActionsToolboxView
        extends AbstractActionsToolboxView<CommonActionsToolboxView> {

    static final Direction TOOLBOX_AT = Direction.NORTH_WEST;
    static final Direction GRID_TOWARDS = Direction.SOUTH_WEST;
    static final Direction TOOLTIP_AT = Direction.NORTH;
    static final Direction TOOLTIP_TOWARDS = Direction.NORTH;

    @Inject
    public CommonActionsToolboxView(final LienzoGlyphRenderers glyphRenderers) {
        this(glyphRenderers,
             ToolboxFactory.INSTANCE);
    }

    CommonActionsToolboxView(final LienzoGlyphRenderers glyphRenderers,
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
                              .withIconSize(BUTTON_SIZE)
                              .towards(GRID_TOWARDS)
                              .build())
                .useShowExecutor(ToolboxVisibilityExecutors.upScaleX())
                .useHideExecutor(ToolboxVisibilityExecutors.downScaleX());
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
        return BUTTON_SIZE;
    }
}

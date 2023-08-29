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


package org.kie.workbench.common.stunner.client.lienzo.components.views;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.core.client.components.views.AbstractCanvasTooltip;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.lienzo.primitive.PrimitiveTooltip;

/**
 * A Canvas tooltip for texts.
 */
@Dependent
public class LienzoTextTooltip extends AbstractCanvasTooltip<String> {

    private final PrimitiveTooltip tooltip;

    public LienzoTextTooltip() {
        this.tooltip = new PrimitiveTooltip();
    }

    LienzoTextTooltip(final PrimitiveTooltip tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void showAt(final String content,
                       final Point2D location) {
        tooltip.show(content,
                     new com.ait.lienzo.client.core.types.Point2D(location.getX(),
                                                                  location.getY()),
                     PrimitiveTooltip.Direction.WEST);
    }

    @Override
    public void hide() {
        tooltip.hide();
    }

    @Override
    public void destroy() {
        tooltip.remove();
    }
}

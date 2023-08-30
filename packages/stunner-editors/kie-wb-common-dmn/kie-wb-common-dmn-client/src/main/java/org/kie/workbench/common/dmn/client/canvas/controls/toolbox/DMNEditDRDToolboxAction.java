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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.resources.StunnerCommonImageResources;
import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static java.util.Collections.singletonList;

@Dependent
public class DMNEditDRDToolboxAction implements ToolboxAction<AbstractCanvasHandler> {

    private final DRDContextMenu drdContextMenu;

    @Inject
    public DMNEditDRDToolboxAction(final DRDContextMenu drdContextMenu) {
        this.drdContextMenu = drdContextMenu;
    }

    @Override
    public Glyph getGlyph(final AbstractCanvasHandler canvasHandler, final String uuid) {
        return ImageDataUriGlyph.create(StunnerCommonImageResources.INSTANCE.drd().getSafeUri());
    }

    @Override
    public String getTitle(final AbstractCanvasHandler canvasHandler, final String uuid) {
        return drdContextMenu.getTitle();
    }

    @Override
    public ToolboxAction<AbstractCanvasHandler> onMouseClick(final AbstractCanvasHandler canvasHandler, final String uuid, final MouseClickEvent event) {
        drdContextMenu.appendContextMenuToTheDOM(event.getClientX(), event.getClientY());

        final Element<? extends Definition<?>> element = CanvasLayoutUtils.getElement(canvasHandler, uuid);
        if (element instanceof Node) {
            drdContextMenu.show(singletonList(element.asNode()));
        }
        return this;
    }

}

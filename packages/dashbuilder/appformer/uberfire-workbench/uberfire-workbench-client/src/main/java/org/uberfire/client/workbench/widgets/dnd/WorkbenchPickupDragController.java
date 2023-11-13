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

package org.uberfire.client.workbench.widgets.dnd;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;

import static org.uberfire.plugin.PluginUtil.toInteger;

/**
 * A Drag Controller for the Workbench.
 */
@ApplicationScoped
public class WorkbenchPickupDragController extends PickupDragController {

    private final Image dragProxy = new Image(WorkbenchResources.INSTANCE.images().workbenchPanelDragProxy());

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    public WorkbenchPickupDragController() {
        super(new AbsolutePanel(),
              false);
        setBehaviorDragProxy(true);
        setBehaviorDragStartSensitivity(1);
    }

    @Override
    public void dragStart() {
        final WorkbenchPartPresenter.View sourceView = (WorkbenchPartPresenter.View) super.context.selectedWidgets.get(
                0);
        final var sourcePart = sourceView.getPresenter().getDefinition();
        final var sourcePanel = sourceView.getPresenter().getDefinition().getParentPanel();
        final var place = sourcePart.getPlace();
        final var title = sourceView.getPresenter().getTitle();
        final var titleDecoration = sourceView.getPresenter().getTitleDecoration();
        final var contextId = sourceView.getPresenter().getContextId();
        final var widget = sourceView.getPresenter().getPartView().getWrappedWidget();
        final var height = toInteger(sourcePanel.getHeightAsInt());
        final var width = toInteger(sourcePanel.getWidthAsInt());
        final var minHeight = toInteger(sourcePanel.getMinHeightAsInt());
        final var minWidth = toInteger(sourcePanel.getMinWidthAsInt());
        final var context = new WorkbenchDragContext(place,
                sourcePart,
                sourcePanel,
                title,
                titleDecoration,
                widget,
                contextId,
                height,
                width,
                minHeight,
                minWidth);
        dndManager.setWorkbenchContext(context);
        super.dragStart();
        final Widget movablePanel = getMoveablePanel();
        if (movablePanel != null) {
            DOMUtil.fastSetElementPosition(movablePanel.getElement(),
                    super.context.mouseX,
                    super.context.mouseY);
        }
    }

    @Override
    public void dragMove() {
        super.dragMove();
        final Widget movablePanel = getMoveablePanel();
        if (movablePanel != null) {
            DOMUtil.fastSetElementPosition(movablePanel.getElement(),
                    super.context.mouseX,
                    super.context.mouseY);
        }
    }

    @Override
    protected Widget newDragProxy(DragContext context) {
        final AbsolutePanel container = new AbsolutePanel();
        container.getElement().getStyle().setProperty("overflow",
                "visible");
        container.getElement().getStyle().setOpacity(0.5);
        container.getElement().getStyle().setZIndex(Integer.MAX_VALUE);

        //Offset to centre of dragProxy
        int offsetX = 0 - ((int) (dragProxy.getWidth() * 0.5));
        int offsetY = 0 - ((int) (dragProxy.getHeight() * 2));
        container.add(dragProxy,
                offsetX,
                offsetY);
        return container;
    }

    private Widget getMoveablePanel() {
        for (int index = 0; index < context.boundaryPanel.getWidgetCount(); index++) {
            final Widget w = context.boundaryPanel.getWidget(index);
            if (w.getStyleName().equals(DragClientBundle.INSTANCE.css().movablePanel())) {
                return w;
            }
        }
        return null;
    }
}

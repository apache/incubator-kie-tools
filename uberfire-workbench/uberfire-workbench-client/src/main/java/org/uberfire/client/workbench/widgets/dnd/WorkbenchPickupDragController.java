/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.widgets.dnd;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Drag Controller for the Workbench.
 */
@ApplicationScoped
public class WorkbenchPickupDragController extends PickupDragController {

    private final Image dragProxy = new Image( WorkbenchResources.INSTANCE.images().workbenchPanelDragProxy() );

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    public WorkbenchPickupDragController() {
        super( new AbsolutePanel(),
               false );
        setBehaviorDragProxy( true );
        setBehaviorDragStartSensitivity( 1 );
    }

    @Override
    public void dragStart() {
        final WorkbenchPartPresenter.View sourceView = (WorkbenchPartPresenter.View) super.context.selectedWidgets.get( 0 );
        final PartDefinition sourcePart = sourceView.getPresenter().getDefinition();
        final PanelDefinition sourcePanel = sourceView.getPresenter().getDefinition().getParentPanel();
        final PlaceRequest place = sourcePart.getPlace();
        final String title = sourceView.getPresenter().getTitle();
        final IsWidget titleDecoration = sourceView.getPresenter().getTitleDecoration();
        final String contextId = sourceView.getPresenter().getContextId();
        final IsWidget widget = sourceView.getPresenter().getPartView().getWrappedWidget();
        final Integer height = sourcePanel.getHeight();
        final Integer width = sourcePanel.getWidth();
        final Integer minHeight = sourcePanel.getMinHeight();
        final Integer minWidth = sourcePanel.getMinWidth();
        final WorkbenchDragContext context = new WorkbenchDragContext( place,
                                                                       sourcePart,
                                                                       sourcePanel,
                                                                       sourceView.getPresenter().getMenus(),
                                                                       title,
                                                                       titleDecoration,
                                                                       widget,
                                                                       contextId,
                                                                       height,
                                                                       width,
                                                                       minHeight,
                                                                       minWidth );
        dndManager.setWorkbenchContext( context );
        super.dragStart();
        updateDragProxyPosition();
    }

    @Override
    public void dragMove() {
        super.dragMove();
        updateDragProxyPosition();
    }

    private void updateDragProxyPosition() {
        DOMUtil.fastSetElementPosition( dragProxy.getElement(), super.context.mouseX - 20, super.context.mouseY - 20 );
    }

    @Override
    protected Widget newDragProxy( DragContext context ) {
        final Style style = dragProxy.getElement().getStyle();
        style.setPosition( Style.Position.FIXED );
        style.setOpacity( 0.5 );
        style.setZIndex( Integer.MAX_VALUE );

        return new SimplePanel( dragProxy );
    }

}

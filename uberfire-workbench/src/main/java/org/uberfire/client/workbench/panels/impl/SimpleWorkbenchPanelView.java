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
package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.ContextPanel;
import org.uberfire.client.workbench.widgets.panel.RequiresResizeFlowPanel;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("SimpleWorkbenchPanelView")
public class SimpleWorkbenchPanelView
        extends BaseWorkbenchPanelView<SimpleWorkbenchPanelPresenter> {

    @Inject
    protected ListBarWidget listBar;

    protected RequiresResizeFlowPanel container = new RequiresResizeFlowPanel();
    protected ContextPanel contextWidget = new ContextPanel();

    @PostConstruct
    private void setupDragAndDrop() {
        listBar.setDndManager( dndManager );
        listBar.setup( false, false );

        //When a tab is selected ensure content is resized and set focus
        listBar.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( SelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus();
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );

        listBar.addOnFocusHandler( new Command() {
            @Override
            public void execute() {
                panelManager.onPanelFocus( presenter.getDefinition() );
            }
        } );
        listBar.asWidget().getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );

        container.add( contextWidget );
        container.add( listBar );
        initWidget( container );
    }

    public void enableDnd() {
        listBar.enableDnd();
    }

    @Override
    public void init( final SimpleWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public SimpleWorkbenchPanelPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void clear() {
        listBar.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        listBar.addPart( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        listBar.changeTitle( part, title, titleDecoration );
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        listBar.selectPart( part );
    }

    @Override
    public void removePart( final PartDefinition part ) {
        listBar.clear();
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        listBar.setFocus( hasFocus );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );
            presenter.onResize( width, height );

            listBar.onResize();

            super.onResize();
        }
    }
}

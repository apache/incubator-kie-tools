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
import javax.inject.Named;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("MultiTabView")
public class MultiTabWorkbenchPanelView
        extends BaseWorkbenchPanelView<MultiTabWorkbenchPanelPresenter> {

    private UberTabPanel tabPanel;

    public MultiTabWorkbenchPanelView() {
        this.tabPanel = makeTabPanel();
        initWidget( this.tabPanel );
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void setupDragAndDrop() {
        dndManager.registerDropController( this, factory.newDropController( this ) );
    }

    @Override
    public void init( final MultiTabWorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
        tabPanel.setPresenter( presenter );
        tabPanel.setDndManager( dndManager );
    }

    @Override
    public void clear() {
        tabPanel.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        tabPanel.addTab( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        tabPanel.changeTitle( part, title, titleDecoration );
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        tabPanel.selectTab( part );
    }

    @Override
    public void removePart( final PartDefinition part ) {
        tabPanel.remove( part );
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        this.tabPanel.setFocus( hasFocus );
    }

    protected UberTabPanel makeTabPanel() {
        final UberTabPanel tabPanel = new UberTabPanel();

//        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
//        tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<PartDefinition>() {
//            @Override
//            public void onBeforeSelection( final BeforeSelectionEvent<PartDefinition> event ) {
//
//            }
//        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( SelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus();
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );

        tabPanel.addOnFocusHandler( new Command() {
            @Override
            public void execute() {
                panelManager.onPanelFocus( presenter.getDefinition() );
            }
        } );

        return tabPanel;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );
            presenter.onResize( width, height );
            tabPanel.onResize();
            super.onResize();
        }
    }

}

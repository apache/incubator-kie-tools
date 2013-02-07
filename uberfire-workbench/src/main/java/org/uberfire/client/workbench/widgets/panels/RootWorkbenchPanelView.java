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
package org.uberfire.client.workbench.widgets.panels;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.annotations.RootWorkbenchPanel;
import org.uberfire.client.workbench.annotations.WorkbenchPosition;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.tab.UberTabPanel;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@RootWorkbenchPanel
public class RootWorkbenchPanelView
        extends ResizeComposite
        implements WorkbenchPanelView {

    @Inject
    @WorkbenchPosition(position = Position.NORTH)
    private PanelHelper helperNorth;

    @Inject
    @WorkbenchPosition(position = Position.SOUTH)
    private PanelHelper helperSouth;

    @Inject
    @WorkbenchPosition(position = Position.EAST)
    private PanelHelper helperEast;

    @Inject
    @WorkbenchPosition(position = Position.WEST)
    private PanelHelper helperWest;

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    @Inject
    private BeanFactory factory;

    private UberTabPanel tabPanel;

    protected WorkbenchPanelPresenter presenter;

    public RootWorkbenchPanelView() {
        this.tabPanel = makeTabPanel();
        initWidget( this.tabPanel );
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void setupDragAndDrop() {
        dndManager.registerDropController( this, factory.newDropController( this ) );
    }

    @Override
    public void init( final WorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
        tabPanel.setPresenter( presenter );
        tabPanel.setDndManager( dndManager );
    }

    @Override
    public WorkbenchPanelPresenter getPresenter() {
        return this.presenter;
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
    public void addPanel( final PanelDefinition panel,
                          final WorkbenchPanelView view,
                          final Position position ) {

        switch ( position ) {
            case NORTH:
                helperNorth.add( view,
                                 this,
                                 panel.getHeight(),
                                 panel.getMinHeight() );
                break;

            case SOUTH:
                helperSouth.add( view,
                                 this,
                                 panel.getHeight(),
                                 panel.getMinHeight() );
                break;

            case EAST:
                helperEast.add( view,
                                this,
                                panel.getWidth(),
                                panel.getMinWidth() );
                break;

            case WEST:
                helperWest.add( view,
                                this,
                                panel.getWidth(),
                                panel.getMinWidth() );
                break;

            default:
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

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
    public void removePanel() {

        //Find the position that needs to be deleted
        Position position = Position.NONE;
        final WorkbenchPanelView view = this;
        final Widget parent = view.asWidget().getParent().getParent().getParent();
        if ( parent instanceof HorizontalSplitterPanel ) {
            final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) parent;
            if ( view.asWidget().equals( hsp.getWidget( Position.EAST ) ) ) {
                position = Position.EAST;
            } else if ( view.asWidget().equals( hsp.getWidget( Position.WEST ) ) ) {
                position = Position.WEST;
            }
        } else if ( parent instanceof VerticalSplitterPanel ) {
            final VerticalSplitterPanel vsp = (VerticalSplitterPanel) parent;
            if ( view.asWidget().equals( vsp.getWidget( Position.NORTH ) ) ) {
                position = Position.NORTH;
            } else if ( view.asWidget().equals( vsp.getWidget( Position.SOUTH ) ) ) {
                position = Position.SOUTH;
            }
        }

        switch ( position ) {
            case NORTH:
                helperNorth.remove( view );
                break;

            case SOUTH:
                helperSouth.remove( view );
                break;

            case EAST:
                helperEast.remove( view );
                break;

            case WEST:
                helperWest.remove( view );
                break;
        }

        //Release DnD DropController
        dndManager.unregisterDropController( this );
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        this.tabPanel.setFocus( hasFocus );
    }

    protected UberTabPanel makeTabPanel() {
        final UberTabPanel tabPanel = new UberTabPanel();

        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
        tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<PartDefinition>() {
            @Override
            public void onBeforeSelection( final BeforeSelectionEvent<PartDefinition> event ) {
                presenter.onPartLostFocus(event.getItem());
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<PartDefinition>() {

            @Override
            public void onSelection( SelectionEvent<PartDefinition> event ) {
                presenter.onPartFocus( event.getSelectedItem() );
            }
        } );

        return tabPanel;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        final int width = parent.getOffsetWidth();
        final int height = parent.getOffsetHeight();
        setPixelSize( width, height );
        presenter.onResize( width, height );
        tabPanel.onResize();
        super.onResize();
    }

}

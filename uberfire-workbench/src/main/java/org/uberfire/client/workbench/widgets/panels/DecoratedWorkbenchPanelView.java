/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.workbench.widgets.panels;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.RootWorkbenchPanelPresenter;
import org.uberfire.client.workbench.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.WorkbenchPartPresenter;
import org.uberfire.client.workbench.annotations.DecoratedWorkbenchPanel;
import org.uberfire.client.workbench.annotations.WorkbenchPosition;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.commons.util.Preconditions;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@DecoratedWorkbenchPanel
public class DecoratedWorkbenchPanelView extends ResizeComposite
        implements
        WorkbenchPanelView {

    protected static final int TAB_BAR_HEIGHT = 32;

    protected static final int FOCUS_BAR_HEIGHT = 3;

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

    private WorkbenchScrolledTabLayoutPanel tabPanel;

    protected WorkbenchPanelPresenter presenter;

    public DecoratedWorkbenchPanelView() {
        this.tabPanel = makeTabPanel();
        initWidget( this.tabPanel );
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void setupDragAndDrop() {
        dndManager.registerDropController( this,
                                           factory.newDropController( this ) );
    }

    @Override
    public void init( final WorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
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
    public void addPart( final IsWidget titleWidget,
                         final WorkbenchPartPresenter.View view ) {
        tabPanel.add( view,
                      wrapTitleWidget( titleWidget,
                                       view ) );
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
    public void changeTitle( final int index,
                             final IsWidget titleWidget ) {
        final WorkbenchPartPresenter.View view = (WorkbenchPartPresenter.View) tabPanel.getWidget( index );
        final Widget wrappedTabContent = wrapTitleWidget( titleWidget,
                                                          view );
        tabPanel.setTabWidget( index,
                               wrappedTabContent );
    }

    @Override
    public void selectPart( int index ) {
        tabPanel.selectTab( index );
        scheduleResize( tabPanel.getWidget( index ) );
    }

    @Override
    public void removePart( int indexOfPartToRemove ) {
        final int indexOfSelectedPart = tabPanel.getSelectedIndex();
        final int nextActiveTabIndex = indexOfPartToRemove > 0 ? indexOfPartToRemove - 1 : 0;
        tabPanel.remove( indexOfPartToRemove );
        if ( tabPanel.getWidgetCount() > 0 ) {
            if ( indexOfSelectedPart == -1 ) {
                tabPanel.activateTab( nextActiveTabIndex );
            } else if ( indexOfSelectedPart == indexOfPartToRemove ) {
                tabPanel.selectTab( nextActiveTabIndex );
            }
        }
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

    protected WorkbenchScrolledTabLayoutPanel makeTabPanel() {
        final WorkbenchScrolledTabLayoutPanel tabPanel = new WorkbenchScrolledTabLayoutPanel( TAB_BAR_HEIGHT,
                                                                                              FOCUS_BAR_HEIGHT,
                                                                                              WorkbenchResources.INSTANCE.images().tabPanelScrollLeft(),
                                                                                              WorkbenchResources.INSTANCE.images().tabPanelScrollRight() );

        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
        tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {

            @Override
            public void onBeforeSelection( BeforeSelectionEvent<Integer> event ) {
                presenter.onPartLostFocus();
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection( SelectionEvent<Integer> event ) {
                final Widget widget = tabPanel.getWidget( event.getSelectedItem() );
                scheduleResize( widget );
                final int index = tabPanel.getSelectedIndex();
                final PartDefinition partToSelect = ( (WorkbenchPartView) tabPanel.getWidget( index ) ).getPresenter().getDefinition();
                presenter.onPartFocus( partToSelect );
            }

        } );

        //Maximize and minimize controls
        final FocusPanel maximize = new FocusPanel();
        maximize.setTitle( WorkbenchConstants.INSTANCE.maximizePanel() );
        maximize.setStyleName( "tabBarControlMaximize" );
        maximize.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.maximize();
            }
        } );
        tabPanel.addControl( maximize );

        final FocusPanel minimize = new FocusPanel();
        minimize.setTitle( WorkbenchConstants.INSTANCE.minimizePanel() );
        minimize.setStyleName( "tabBarControlMinimize" );
        minimize.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.minimize();
            }
        } );
        tabPanel.addControl( minimize );

        return tabPanel;
    }

    private Widget wrapTitleWidget( final IsWidget titleWidget,
                                    final WorkbenchPartPresenter.View view ) {
        final FlowPanel fp = new FlowPanel();
        fp.add( titleWidget );

        //Clicking on the Tab takes focus
        fp.addDomHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.onPanelFocus();
            }

        },
                          ClickEvent.getType() );

        dndManager.makeDraggable( view.asWidget(),
                                  titleWidget );

        final FocusPanel image = new FocusPanel();
        image.getElement().getStyle().setFloat( Style.Float.RIGHT );
        image.setStyleName( WorkbenchResources.INSTANCE.CSS().closeTabImage() );
        image.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                final PartDefinition partToDeselect = view.getPresenter().getDefinition();
                presenter.onBeforePartClose( partToDeselect );
            }

        } );
        fp.add( image );
        return fp;
    }

    protected void scheduleResize( final Widget widget ) {
        if ( widget instanceof RequiresResize ) {
            final RequiresResize requiresResize = (RequiresResize) widget;
            Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    requiresResize.onResize();
                }

            } );
        }
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        final int width = parent.getOffsetWidth();
        final int height = parent.getOffsetHeight();
        setPixelSize( width,
                      height );
        presenter.onResize( width,
                            height );
        super.onResize();
    }

}

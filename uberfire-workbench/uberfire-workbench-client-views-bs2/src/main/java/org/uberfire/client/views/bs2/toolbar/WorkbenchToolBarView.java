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
package org.uberfire.client.views.bs2.toolbar;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.ToolBarItem;
import org.uberfire.workbench.model.toolbar.ToolBarTypeIcon;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.ButtonToolbar;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The Tool Bar widget
 */
public class WorkbenchToolBarView extends Composite
        implements
        WorkbenchToolBarPresenter.View {

    interface WorkbenchToolBarViewBinder
            extends
            UiBinder<Panel, WorkbenchToolBarView> {

    }

    private static WorkbenchToolBarViewBinder uiBinder = GWT.create( WorkbenchToolBarViewBinder.class );

    @UiField
    public FlowPanel masterContainer;

    @UiField
    public FlowPanel container;

    @UiField
    public ButtonToolbar toolBar;

    @UiField
    public SimplePanel simpleMargin;

    @UiField
    public com.google.gwt.user.client.ui.Button viewControl;

    @UiField
    public Tooltip tip;

    private State state = State.EXPANDED;

    enum State {
        EXPANDED, COLLAPSED;
    }

    //Map of ToolBar to GWT Widgets used to represent them
    private final Map<String, ButtonGroup> toolBarItemsMap = new HashMap<String, ButtonGroup>();

    public WorkbenchToolBarView() {
        initWidget( uiBinder.createAndBindUi( this ) );
        tip.setText( WorkbenchConstants.INSTANCE.collapseToolbar() );
        tip.setPlacement( Placement.LEFT );
        tip.setWidget( viewControl );
        simpleMargin.setVisible( false );
    }

    /**
     * Add a Tool Bar item to the view. Filtering of menu items for permissions
     * is conducted by the Presenter.
     */
    @Override
    public void addToolBar( final ToolBar _toolBar ) {

        final ButtonGroup bgroup = new ButtonGroup();

        if ( toolBarItemsMap.size() == 0 ) {
            bgroup.getElement().getStyle().setPaddingLeft( 19, Style.Unit.PX );
        }

        for ( final ToolBarItem item : _toolBar.getItems() ) {
            bgroup.add( new Tooltip( item.getTooltip() ) {{
                setPlacement( Placement.BOTTOM );
                add( new Button() {{
                    setIcon( IconType.valueOf( ( (ToolBarTypeIcon) item.getIcon() ).getType().toString() ) );
                    setEnabled( item.isEnabled() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent event ) {
                            item.getCommand().execute();
                        }
                    } );
                }} );
            }} );
        }

        toolBarItemsMap.put( _toolBar.getId(), bgroup );

        toolBar.add( bgroup );
    }

    /**
     * Remove a Tool Bar item from the view.
     */
    @Override
    public void removeToolBar( final ToolBar _toolBar ) {
        toolBar.remove( toolBarItemsMap.remove( _toolBar.getId() ) );
    }

    @Override
    public int getHeight() {
        if ( isExpanded() ) {
            return getOffsetHeight();
        }
        return 0;
    }

    private boolean isExpanded() {
        return state == State.EXPANDED;
    }

    @Override
    public void hide() {
        container.setVisible( false );
        simpleMargin.setVisible( true );
    }

    @Override
    public void show() {
        container.setVisible( true );
        simpleMargin.setVisible( false );
    }

    @UiHandler("viewControl")
    void handleClick( final ClickEvent e ) {
        if ( isExpanded() ) {
            collapse();
        } else {
            expand();
        }
    }

    private void expand() {
        container.removeFromParent();
        container.getElement().getStyle().clearTop();
        container.getElement().getStyle().clearPosition();
        container.getElement().getStyle().clearZIndex();
        masterContainer.add( container );

        tip.setText( WorkbenchConstants.INSTANCE.collapseToolbar() );
        tip.setPlacement( Placement.LEFT );
        tip.setWidget( viewControl );
        tip.reconfigure();

        new ExpandAnimation().animate( 8, getOffsetWidth() - 1, 500 );
        state = State.EXPANDED;
    }

    private void collapse() {
        new CollapseAnimation().animate( getOffsetWidth(), 8, 500 );

        container.removeFromParent();
        container.getElement().getStyle().setTop( getAbsoluteTop(), Style.Unit.PX );
        container.getElement().getStyle().setPosition( Style.Position.FIXED );
        container.getElement().getStyle().setZIndex( Integer.MAX_VALUE );
        RootPanel.get().add( container );

        tip.setText( WorkbenchConstants.INSTANCE.expandToolbar() );
        tip.setPlacement( Placement.RIGHT );
        tip.setWidget( viewControl );
        tip.reconfigure();

        state = State.COLLAPSED;
    }

    private class ExpandAnimation extends Animation {

        private int endSize;
        private int startSize;
        private int startTabLeft;
        private int endTabLeft;

        @Override
        protected void onComplete() {
            toolBar.getElement().getStyle().clearLeft();
            container.getElement().getStyle().clearWidth();
        }

        @Override
        protected void onUpdate( double progress ) {
            double delta = ( endSize - startSize ) * progress;
            double newSize = startSize + delta;

            double deltaLeft = ( endTabLeft - startTabLeft ) * progress;
            double newLeft = startTabLeft + deltaLeft;

            toolBar.getElement().getStyle().setLeft( newLeft, Style.Unit.PX );
            container.setWidth( newSize + "px" );
        }

        void animate( int startSize,
                      int endSize,
                      int duration ) {
            this.startSize = startSize;
            this.endSize = endSize;
            this.startTabLeft = toolBar.getAbsoluteLeft();
            this.endTabLeft = 0;
            run( duration );
        }
    }

    private class CollapseAnimation extends Animation {

        private int endSize;
        private int startSize;

        @Override
        protected void onComplete() {
            container.setWidth( null );
        }

        @Override
        protected void onUpdate( double progress ) {
            double delta = ( endSize - startSize ) * progress;
            double newSize = startSize + delta;
            toolBar.getElement().getStyle().setLeft( delta, Style.Unit.PX );
            container.setWidth( newSize + "px" );
        }

        void animate( int startSize,
                      int endSize,
                      int duration ) {
            this.startSize = startSize;
            this.endSize = endSize;
            run( duration );
        }
    }

}

/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.views.bs2.tab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabLink;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TabPanel.ShowEvent;
import com.github.gwtbootstrap.client.ui.TabPanel.ShownEvent;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import static com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class UberTabPanel extends ResizeComposite implements MultiPartWidget, ClickHandler {

    static class ResizeTabPanel extends TabPanel implements RequiresResize, ProvidesResize {

        public ResizeTabPanel( Tabs tabPosition ) {
            super( tabPosition );
        }

        @Override
        public void onResize() {
            // there are two layers of children in a TabPanel: the TabContent and the TabPane.
            // TabContent is just a container for all the TabPane divs, one of which is made visible at a time.
            // For compatibility with GWT LayoutPanel, we have to set both layers of children to fill their parents.
            // We do it in onResize() to get to the TabPanes no matter how they were added.
            for ( Widget child : getChildren() ) {
                Layouts.setToFillParent( child );
                if ( child instanceof RequiresResize ) {
                    ((RequiresResize) child).onResize();
                }
                for( Widget grandChild : (HasWidgets) child ) {
                    Layouts.setToFillParent( grandChild );
                }
            }
        }

    }

    private static final int MARGIN = 20;

    ResizeTabPanel tabPanel;
    private final DropdownTab dropdownTab;

    /**
     * Flag protecting {@link #updateDisplayedTabs()} from recursively invoking itself through events that it causes.
     */
    private boolean updating;

    final List<WorkbenchPartPresenter> parts = new ArrayList<WorkbenchPartPresenter>();

    final Map<WorkbenchPartPresenter.View, TabLink> tabIndex = new HashMap<WorkbenchPartPresenter.View, TabLink>();
    final Map<TabLink, WorkbenchPartPresenter.View> tabInvertedIndex = new HashMap<TabLink, WorkbenchPartPresenter.View>();
    final Map<PartDefinition, TabLink> partTabIndex = new HashMap<PartDefinition, TabLink>();
    private boolean hasFocus = false;
    private final List<Command> focusGainedHandlers = new ArrayList<Command>();

    private final PanelManager panelManager;
    WorkbenchDragAndDropManager dndManager;

    /**
     * Creates a new empty tab panel.
     *
     * @param panelManager
     *            the PanelManager that will be called upon to close a place when the user clicks on its tab's close
     *            button. (TODO: change to PlaceManager).
     */
    @Inject
    public UberTabPanel( PanelManager panelManager ) {
        this( panelManager,
              new DropdownTab( "More..." ) );
    }

    /**
     * Exposed for testing (GWTMockito doesn't like us creating real GWTBootstrap widgetsn, so this allows the option
     * of passing in mocks). For production code, use the public constructor.
     */
    UberTabPanel( PanelManager panelManager,
                  DropdownTab dropdownTab ) {
        this.panelManager = checkNotNull( "panelManager", panelManager );
        this.dropdownTab = checkNotNull( "dropdownTab", dropdownTab );
        tabPanel = new ResizeTabPanel( ABOVE );
        tabPanel.addShownHandler( new ShownEvent.Handler() {
            @Override
            public void onShow( final ShownEvent e ) {
                onResize();
                if ( e.getRelatedTarget() != null ) {
                    BeforeSelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( e.getRelatedTarget() ).getPresenter().getDefinition() );
                }
            }
        } );

        tabPanel.addShowHandler( new ShowEvent.Handler() {
            @Override
            public void onShow( final ShowEvent e ) {
                if ( e.getTarget() == null ) {
                    return;
                }
                SelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( e.getTarget() ).getPresenter().getDefinition() );
            }
        } );

        tabPanel.addDomHandler( UberTabPanel.this, ClickEvent.getType() );

        tabPanel.addStyleName( "uf-tabbar-panel" );
        initWidget( tabPanel );
    }

    @Override
    public void clear() {
        parts.clear();
        tabPanel.clear();
        dropdownTab.clear();
        partTabIndex.clear();
        tabIndex.clear();
        tabInvertedIndex.clear();
    }

    /**
     * Updates the display ({@link #tabPanel}) to reflect the current desired state of this tab panel.
     */
    private void updateDisplayedTabs() {
        if ( updating ) {
            return;
        }
        try {
            updating = true;
            tabPanel.clear();
            dropdownTab.clear();

            if ( parts.size() == 0 ) {
                return;
            }

            int availableSpace = tabPanel.getOffsetWidth();
            TabLink selectedTab = null;

            // add and measure all tabs
            for ( int i = 0; i < parts.size(); i++ ) {
                WorkbenchPartPresenter part = parts.get( i );
                TabLink tabWidget = partTabIndex.get( part.getDefinition() );
                if ( tabWidget.isActive() ) {
                    selectedTab = tabWidget;
                }
                tabWidget.setActive( false );
                tabPanel.add( tabWidget );
                availableSpace -= tabWidget.getOffsetWidth();
            }

            // if we didn't find any selected tab, let's select the first one
            if ( selectedTab == null ) {
                TabLink firstTab = (TabLink) getTabs().getWidget( 0 );
                selectedTab = firstTab;
            }

            // now work from right to left to find out how many tabs we have to collapse into the dropdown
            if ( availableSpace < 0 ) {
                LinkedList<TabLink> newDropdownContents = new LinkedList<TabLink>();
                dropdownTab.setText( "More..." );
                tabPanel.add( dropdownTab );
                while ( availableSpace - dropdownTab.getOffsetWidth() < 0 && getTabs().getWidgetCount() > 1 ) {
                    // get the last tab that isn't the dropdown tab
                    TabLink tabWidget = (TabLink) getTabs().getWidget( getTabs().getWidgetCount() - 2 );
                    availableSpace += tabWidget.getOffsetWidth();
                    tabPanel.remove( tabWidget );
                    newDropdownContents.addFirst( tabWidget );
                    if ( tabWidget == selectedTab ) {
                        dropdownTab.setText( tabInvertedIndex.get( selectedTab ).getPresenter().getTitle() );
                    }
                }

                for ( TabLink l : newDropdownContents ) {
                    dropdownTab.add( l );
                    getTabContent().add( l.getTabPane() );
                }
            }

            selectedTab.show();

        } finally {
            updating = false;
        }
    }

    @Override
    public boolean selectPart( final PartDefinition id ) {
        final TabLink tab = partTabIndex.get( id );
        if ( tab != null ) {
            tab.show();
        }
        return false;
    }

    @Override
    public boolean remove( final PartDefinition id ) {
        final TabLink tab = partTabIndex.get( id );
        if ( tab == null ) {
            return false;
        }
        int removedTabIndex = getTabs().getWidgetIndex( tab );
        final boolean wasActive = tab.isActive();

        View partView = tabInvertedIndex.remove( tab );
        parts.remove( partView.getPresenter() );
        tabIndex.remove( partView );
        partTabIndex.remove( id );

        updateDisplayedTabs();

        if ( removedTabIndex >= 0 && wasActive && getTabs().getWidgetCount() > 0 ) {
            tabPanel.selectTab( removedTabIndex <= 0 ? 0 : removedTabIndex - 1 );
        }

        return true;
    }

    @Override
    public void changeTitle( final PartDefinition id,
                             final String title,
                             final IsWidget titleDecoration ) {
        final TabLink tabLink = partTabIndex.get( id );
        if ( tabLink != null ) {
            tabLink.setText( title );
        }
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler( final BeforeSelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, BeforeSelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    @Override
    public void setPresenter( final WorkbenchPanelPresenter presenter ) {
        // not needed
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        if ( !tabIndex.containsKey( view ) ) {
            final Tab newTab = createTab( view, false, 0, 0 );
            parts.add( view.getPresenter() );
            tabIndex.put( view, newTab.asTabLink() );
            updateDisplayedTabs();
        }
    }

    boolean isFirstWidget() {
        return getTabs().getWidgetCount() == 1;
    }

    /**
     * The GwtBootstrap TabPanel doesn't support the RequiresResize/ProvidesResize contract, and UberTabPanel fills in
     * the gap. This helper method allows us to call onResize() on the widgets that need it.
     *
     * @param widget the widget that has just been resized
     */
    private void resizeIfNeeded( final Widget widget ) {
        if ( isAttached() && widget instanceof RequiresResize ) {
            ( (RequiresResize) widget ).onResize();
        }
    }

    /**
     * Creates a tab widget for the given part view, adding it to the tab/partDef/tabLink maps.
     */
    Tab createTab( final WorkbenchPartPresenter.View view,
                   final boolean isActive,
                   final int width,
                   final int height ) {

        final Tab tab = createTab( view, isActive );

        tab.addClickHandler( createTabClickHandler( view, tab ) );

        tab.add( view.asWidget() );

        resizeIfNeeded( view.asWidget() );

        tabIndex.put( view, tab.asTabLink() );
        tabInvertedIndex.put( tab.asTabLink(), view );
        partTabIndex.put( view.getPresenter().getDefinition(), tab.asTabLink() );

        dndManager.makeDraggable( view, tab.asTabLink().getWidget( 0 ) );

        return addCloseToTab( tab );
    }

    /**
     * Subroutine of {@link #createTab(View, boolean, int, int)}. Exposed for testing. Never call this except from
     * within the other createTab method.
     */
    Tab createTab( final WorkbenchPartPresenter.View view,
                   final boolean isActive ) {
        Tab tab = new Tab();
        tab.setHeading( view.getPresenter().getTitle() );
        tab.setActive( isActive );
        return tab;
    }

    private ClickHandler createTabClickHandler( final WorkbenchPartPresenter.View view,
                                                final Tab tab ) {
        return new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                UberTabPanel.this.onClick( event );
            }
        };
    }

    @Override
    public void onResize() {
        updateDisplayedTabs();

        TabLink selectedTab = getSelectedTab();
        if ( selectedTab != null ) {
            final TabPane tabPane = selectedTab.getTabPane();
            Widget tabPaneContent = tabPane.getWidget( 0 );
            resizeIfNeeded(tabPaneContent);
        }
    }

    private int getTabHeight() {
        return tabPanel.getWidget( 0 ).getOffsetHeight() + MARGIN;
    }

    /**
     * Returns the panel (from inside {@link #tabPanel}) that contains the panel content for each tab. Each child of the
     * returned panel is the GUI that will be shown then its corresponding tab is selected.
     */
    private ComplexPanel getTabContent() {
        return (ComplexPanel) tabPanel.getWidget( 1 );
    }

    /**
     * Returns the panel (from inside {@link #tabPanel}) that contains the tab widgets. Each child of the returned panel
     * is a tab in the GUI.
     */
    private ComplexPanel getTabs() {
        return (ComplexPanel) tabPanel.getWidget( 0 );
    }

    private Tab addCloseToTab( final Tab tab ) {
        final Button close = new Button( "&times;" ) {{
            setStyleName( "close" );
            addStyleName( WorkbenchResources.INSTANCE.CSS().tabCloseButton() );
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( final ClickEvent event ) {
                    final WorkbenchPartPresenter.View partToDeselect = tabInvertedIndex.get( tab.asTabLink() );
                    panelManager.closePart( partToDeselect.getPresenter().getDefinition() );
                }
            } );
        }};

        tab.addDecorate( close );

        return tab;
    }

    @Override
    public void setDndManager( final WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
        this.hasFocus = hasFocus;
        if ( hasFocus ) {
            getTabs().setStyleName( WorkbenchResources.INSTANCE.CSS().activeNavTabs(), true );
        } else {
            getTabs().removeStyleName( WorkbenchResources.INSTANCE.CSS().activeNavTabs() );
        }
    }

    @Override
    public void onClick( final ClickEvent event ) {
        if ( !hasFocus ) {
            fireFocusGained();
            View view = getSelectedPart();
            if ( view != null ) {
                SelectionEvent.fire( UberTabPanel.this, view.getPresenter().getDefinition() );
            }
        }
    }

    /**
     * Gets the selected tab, even if it's nested in the DropdownTab. Returns null if no tab is selected.
     */
    private TabLink getSelectedTab() {
        for ( TabLink tab : tabInvertedIndex.keySet() ) {
            if ( tab.isActive() ) {
                return tab;
            }
        }
        return null;
    }

    private View getSelectedPart() {
        return tabInvertedIndex.get( getSelectedTab() );
    }

    private void fireFocusGained() {
        for ( int i = focusGainedHandlers.size() - 1; i >= 0; i-- ) {
            focusGainedHandlers.get( i ).execute();
        }
    }

    @Override
    public void addOnFocusHandler( final Command doWhenFocused ) {
        focusGainedHandlers.add( checkNotNull( "doWhenFocused", doWhenFocused ) );
    }

    @Override
    public int getPartsSize() {
        return partTabIndex.size();
    }
}

package org.uberfire.client.views.pfly.tab;

import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gwtbootstrap3.client.shared.event.TabShowEvent;
import org.gwtbootstrap3.client.shared.event.TabShowHandler;
import org.gwtbootstrap3.client.shared.event.TabShownEvent;
import org.gwtbootstrap3.client.shared.event.TabShownHandler;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.base.HasActive;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.views.pfly.tab.TabPanelWithDropdowns.DropDownTab;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A wrapper around {@link TabPanelWithDropdowns} that adds the following capabilities:
 * <ul>
 *  <li>Tabs that don't fit in the tab bar are automatically collapsed into a dropdown
 *  <li>Each tab gets a close button
 *  <li>Obeys the RequiresResize/ProvidesResize contract (onResize() calls are propagated
 *      to the visible tab content widgets)
 *  <li>Participates in UberFire's panel focus system
 * </ul>
 */
public class UberTabPanel extends ResizeComposite implements MultiPartWidget, ClickHandler {

    static class ResizeTabPanel extends TabPanelWithDropdowns implements RequiresResize, ProvidesResize {

        @Override
        public void onResize() {
            Layouts.setToFillParent( tabContent );

            // TabContent is just a container for all the TabPane divs, one of which is made visible at a time.
            // For compatibility with GWT LayoutPanel, we have to set both layers of children to fill their parents.
            // We do it in onResize() to get to the TabPanes no matter how they were added.
            for ( Widget child : tabContent ) {
                Layouts.setToFillParent( child );
                if ( ((HasActive) child).isActive() ) {
                    child.setPixelSize( getOffsetWidth(), getOffsetHeight() - getTabBarHeight() );
                    if ( child instanceof RequiresResize ) {
                        ((RequiresResize) child).onResize();
                    }
                }
            }
        }

        /**
         * Returns the height (in pixels) taken up by the tab bar.
         */
        public int getTabBarHeight() {
            return tabBar.getOffsetHeight();
        }

        /**
         * Makes the tab panel look more or less prominent.
         *
         * @param hasFocus if true, the tab panel will look more prominent. If false, the tab panel will look normal.
         */
        public void setFocus( boolean hasFocus ) {
            if ( hasFocus ) {
                tabBar.addStyleName( WorkbenchResources.INSTANCE.CSS().activeNavTabs() );
            } else {
                tabBar.removeStyleName( WorkbenchResources.INSTANCE.CSS().activeNavTabs() );
            }
        }
    }

    private static final int MARGIN = 20;

    ResizeTabPanel tabPanel;
    private final DropDownTab dropdownTab;

    /**
     * Flag protecting {@link #updateDisplayedTabs()} from recursively invoking itself through events that it causes.
     */
    private boolean updating;

    final List<WorkbenchPartPresenter> parts = new ArrayList<WorkbenchPartPresenter>();

    final Map<WorkbenchPartPresenter.View, TabPanelEntry> tabIndex = new HashMap<WorkbenchPartPresenter.View, TabPanelEntry>();
    final Map<TabPanelEntry, WorkbenchPartPresenter.View> tabInvertedIndex = new HashMap<TabPanelEntry, WorkbenchPartPresenter.View>();
    final Map<PartDefinition, TabPanelEntry> partTabIndex = new HashMap<PartDefinition, TabPanelEntry>();
    private boolean hasFocus = false;
    private final List<Command> focusGainedHandlers = new ArrayList<Command>();

    private final PlaceManager panelManager;
    WorkbenchDragAndDropManager dndManager;

    /**
     * Creates a new empty tab panel.
     *
     * @param panelManager
     *            the PanelManager that will be called upon to close a place when the user clicks on its tab's close
     *            button.
     */
    public UberTabPanel( PlaceManager panelManager ) {
        this.panelManager = checkNotNull( "panelManager", panelManager );
        tabPanel = new ResizeTabPanel();
        this.dropdownTab = tabPanel.addDropdownTab( "More..." );

        tabPanel.addShowHandler( new TabShowHandler() {

            @Override
            public void onShow( TabShowEvent e ) {
                if ( e.getTab() != null ) {
                    TabPanelEntry selected = findEntryForTabWidget( e.getTab() );
                    BeforeSelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( selected ).getPresenter().getDefinition() );
                }
            }
        } );
        tabPanel.addShownHandler( new TabShownHandler() {

            @Override
            public void onShown( TabShownEvent e ) {
                onResize();
                if ( e.getTab() != null ) {
                    TabPanelEntry selected = findEntryForTabWidget( e.getTab() );
                    SelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( selected ).getPresenter().getDefinition() );
                }
            }
        });

        tabPanel.addDomHandler( UberTabPanel.this, ClickEvent.getType() );

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
     * Updates the {@link #tabPanel} to contain a tab for each part in {@link #parts} in the order the parts
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
            TabPanelEntry selectedTab = null;

            // the number of regular (not dropdown) tabs in the tab bar
            int regularTabCount = 0;

            // add and measure all tabs
            for ( int i = 0; i < parts.size(); i++ ) {
                WorkbenchPartPresenter part = parts.get( i );
                TabPanelEntry tabPanelEntry = partTabIndex.get( part.getDefinition() );
                if ( tabPanelEntry.isActive() ) {
                    selectedTab = tabPanelEntry;
                }
                tabPanelEntry.setActive( false );
                tabPanel.addItem( tabPanelEntry );
                regularTabCount++;
                availableSpace -= tabPanelEntry.getTabWidget().getOffsetWidth();
            }

            // if we didn't find any selected tab, let's select the first one
            if ( selectedTab == null ) {
                TabPanelEntry firstTab = getTab( 0 );
                selectedTab = firstTab;
            }

            // now work from right to left to find out how many tabs we have to collapse into the dropdown
            if ( availableSpace < 0 ) {
                LinkedList<TabPanelEntry> newDropdownContents = new LinkedList<TabPanelEntry>();
                dropdownTab.setText( "More..." );
                tabPanel.addDropdownTab( dropdownTab );
                while ( availableSpace - dropdownTab.getTabWidth() < 0 && regularTabCount > 1 ) {
                    // get the last tab that isn't the dropdown tab
                    TabPanelEntry tab = getTab( --regularTabCount );
                    availableSpace += tab.getTabWidget().getOffsetWidth();
                    tabPanel.remove( tab );
                    newDropdownContents.addFirst( tab );
                    if ( tab == selectedTab ) {
                        dropdownTab.setText( selectedTab.getTitle() );
                    }
                }

                for ( TabPanelEntry l : newDropdownContents ) {
                    dropdownTab.addItem( l );
                }
            }

            selectedTab.showTab();

        } finally {
            updating = false;
        }
    }

    private TabPanelEntry getTab( int i ) {
        return checkNotNull( "part entry in map", partTabIndex.get( parts.get( i ).getDefinition() ) );
    }

    @Override
    public boolean selectPart( final PartDefinition id ) {
        final TabPanelEntry tab = partTabIndex.get( id );
        if ( tab != null ) {
            tab.showTab();
        }
        return false;
    }

    @Override
    public boolean remove( final PartDefinition id ) {
        final TabPanelEntry tab = partTabIndex.get( id );
        if ( tab == null ) {
            return false;
        }
        final boolean wasActive = tab.isActive();

        View partView = tabInvertedIndex.remove( tab );
        int removedTabIndex = parts.indexOf( partView.getPresenter() );
        parts.remove( removedTabIndex );
        tabIndex.remove( partView );
        partTabIndex.remove( id );

        updateDisplayedTabs();

        if ( removedTabIndex >= 0 && wasActive && parts.size() > 0 ) {
            selectPart( parts.get( removedTabIndex <= 0 ? 0 : removedTabIndex - 1 ).getDefinition() );
        }

        return true;
    }

    @Override
    public void changeTitle( final PartDefinition id,
                             final String title,
                             final IsWidget titleDecoration ) {
        final TabPanelEntry tab = partTabIndex.get( id );
        if ( tab != null ) {
            tab.setTitle( title );
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
            final TabPanelEntry tab = tabPanel.addItem( view.getPresenter().getTitle(), view.asWidget() );

            resizeIfNeeded( view.asWidget() );

            tabIndex.put( view, tab );
            tabInvertedIndex.put( tab, view );
            partTabIndex.put( view.getPresenter().getDefinition(), tab );

            dndManager.makeDraggable( view, tab.getTabWidget() );
            addCloseToTab( tab );

            parts.add( view.getPresenter() );
            tabIndex.put( view, tab );
            updateDisplayedTabs();
        }
    }

    /**
     * The GwtBootstrap3 TabPanel doesn't support the RequiresResize/ProvidesResize contract, and UberTabPanel fills in
     * the gap. This helper method allows us to call onResize() on the widgets that need it.
     *
     * @param widget the widget that has just been resized
     */
    private void resizeIfNeeded( final Widget widget ) {
        if ( isAttached() && widget instanceof RequiresResize ) {
            ( (RequiresResize) widget ).onResize();
        }
    }

    @Override
    public void onResize() {
        updateDisplayedTabs();
        tabPanel.onResize();
    }

    private void addCloseToTab( final TabPanelEntry tab ) {
        final Button close = new Button( "&times;" );
        close.setStyleName( "close" );
        close.addStyleName( WorkbenchResources.INSTANCE.CSS().tabCloseButton() );
        close.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                final WorkbenchPartPresenter.View partToDeselect = tabInvertedIndex.get( tab );
                panelManager.closePlace( partToDeselect.getPresenter().getDefinition().getPlace() );
            }
        } );

        tab.getTabWidget().addToAnchor( close );
    }

    @Override
    public void setDndManager( final WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
        this.hasFocus = hasFocus;
        tabPanel.setFocus( hasFocus );
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
    private TabPanelEntry getSelectedTab() {
        for ( TabPanelEntry tab : tabInvertedIndex.keySet() ) {
            if ( tab.isActive() ) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Finds the TabPanelEntry associated with the given tab widget, even if it's nested in a DropdownTab.
     */
    private TabPanelEntry findEntryForTabWidget( TabListItem tabWidget ) {
        for ( TabPanelEntry tab : tabInvertedIndex.keySet() ) {
            if ( tab.getTabWidget() == tabWidget ) {
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

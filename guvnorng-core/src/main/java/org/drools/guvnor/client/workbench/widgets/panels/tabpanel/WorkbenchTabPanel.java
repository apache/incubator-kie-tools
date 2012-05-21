/*
 * Copyright 2008 Google Inc.
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
package org.drools.guvnor.client.workbench.widgets.panels.tabpanel;

import java.util.Iterator;

import org.drools.guvnor.client.resources.GuvnorResources;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.events.FocusReceivedEvent;
import org.drools.guvnor.client.workbench.events.FocusReceivedEvent.FocusReceivedEventHandler;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;

@SuppressWarnings("deprecation")
public class WorkbenchTabPanel extends Composite
    implements
    TabListener,
    SourcesTabEvents,
    HasWidgets,
    HasAnimation,
    IndexedPanel.ForIsWidget,
    HasBeforeSelectionHandlers<Integer>,
    HasSelectionHandlers<Integer>,
    RequiresResize,
    ProvidesResize,
    FocusReceivedEventHandler {
    /**
     * This extension of DeckPanel overrides the public mutator methods to
     * prevent external callers from adding to the state of the DeckPanel.
     * <p>
     * Removal of Widgets is supported so that WidgetCollection.WidgetIterator
     * operates as expected.
     * </p>
     * <p>
     * We ensure that the DeckPanel cannot become of of sync with its associated
     * TabBar by delegating all mutations to the TabBar to this implementation
     * of DeckPanel.
     * </p>
     */
    private static class TabbedDeckPanel extends DeckPanel {
        private final UnmodifiableTabBar tabBar;

        public TabbedDeckPanel(UnmodifiableTabBar tabBar) {
            this.tabBar = tabBar;
        }

        @Override
        public void add(Widget w) {
            throw new UnsupportedOperationException( "Use WorkbenchTabPanel.add() to alter the DeckPanel" );
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException( "Use WorkbenchTabPanel.clear() to alter the DeckPanel" );
        }

        @Override
        public void insert(Widget w,
                           int beforeIndex) {
            throw new UnsupportedOperationException( "Use WorkbenchTabPanel.insert() to alter the DeckPanel" );
        }

        @Override
        public boolean remove(Widget w) {
            // Removal of items from the TabBar is delegated to the DeckPanel
            // to ensure consistency
            int idx = getWidgetIndex( w );
            if ( idx != -1 ) {
                tabBar.removeTabProtected( idx );
                return super.remove( w );
            }

            return false;
        }

        protected void insertProtected(Widget w,
                                       String tabText,
                                       int beforeIndex) {

            // Check to see if the TabPanel already contains the Widget. If so,
            // remove it and see if we need to shift the position to the left.
            int idx = getWidgetIndex( w );
            if ( idx != -1 ) {
                remove( w );
                if ( idx < beforeIndex ) {
                    beforeIndex--;
                }
            }

            tabBar.insertTabProtected( w,
                                       tabText,
                                       beforeIndex );
            super.insert( w,
                          beforeIndex );
        }

        @Override
        protected WidgetCollection getChildren() {
            return super.getChildren();
        }

    }

    /**
     * This extension of TabPanel overrides the public mutator methods to
     * prevent external callers from modifying the state of the TabBar.
     */
    private class UnmodifiableTabBar extends TabBar {

        @Override
        public void insertTab(String text,
                              boolean asHTML,
                              int beforeIndex) {
            throw new UnsupportedOperationException( "Use WorkbenchTabPanel.insert() to alter the TabBar" );
        }

        @Override
        public void insertTab(Widget widget,
                              int beforeIndex) {
            throw new UnsupportedOperationException( "Use WorkbenchTabPanel.insert() to alter the TabBar" );
        }

        public void insertTabProtected(Widget w,
                                       String text,
                                       int beforeIndex) {
            checkInsertBeforeTabIndex( beforeIndex );

            Label item = new Label( text );
            item.setWordWrap( false );
            Widget closableItem = makeClosableItem( w,
                                                    item );
            insertTabWidget( closableItem,
                             beforeIndex );
        }

        private void checkInsertBeforeTabIndex(int beforeIndex) {
            if ( (beforeIndex < 0) || (beforeIndex > getTabCount()) ) {
                throw new IndexOutOfBoundsException();
            }
        }

        private Widget makeClosableItem(final Widget tabContent,
                                        final Label tabLabel) {
            final HorizontalPanel hp = new HorizontalPanel();
            hp.add( tabLabel );

            WorkbenchDragAndDropManager.getInstance().makeDraggable( tabContent,
                                                                     tabLabel );

            final SimplePanel image = new SimplePanel();
            final FocusPanel imageContainer = new FocusPanel();
            image.setStyleName( GuvnorResources.INSTANCE.guvnorCss().closeTabImage() );
            imageContainer.addClickHandler( new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    remove( tabContent );
                }

            } );
            imageContainer.setWidget( image );
            hp.add( imageContainer );
            return hp;
        }

        @Override
        public void removeTab(int index) {
            // It's possible for removeTab() to function correctly, but it's
            // preferable to have only TabbedDeckPanel.remove() be operable,
            // especially since TabBar does not export an Iterator over its values.
            throw new UnsupportedOperationException( "Use TabPanel.remove() to alter the TabBar" );
        }

        public void removeTabProtected(int index) {
            final int indexOfSelectedTab = getSelectedTab();
            super.removeTab( index );
            if ( getTabCount() == 0 ) {
                //TODO {manstis} This is a little brittle, should the DOM hierarchy change...
                //- we just call parent.remove() and that calls up, and calls up.. etc until something handles it?
                //- we pass references to the actual "containing" class in the constructors?
                Widget parent0 = getParent();
                if ( parent0 instanceof VerticalPanel ) {
                    Widget parent1 = parent0.getParent();
                    if ( parent1 instanceof WorkbenchTabPanel ) {
                        Widget parent2 = parent1.getParent();
                        if ( parent2 instanceof WorkbenchPanel ) {
                            ((WorkbenchPanel) parent2).remove();
                        }
                    }
                }
            } else {
                if ( indexOfSelectedTab == index ) {
                    selectTab( index > 0 ? index - 1 : 0 );
                }
            }
        }

    }

    private final UnmodifiableTabBar tabBar;
    private final SimplePanel        focusIndicator;
    private final TabbedDeckPanel    deck;
    private final WorkbenchPanel     owner;

    /**
     * Creates an empty tab panel.
     */
    public WorkbenchTabPanel(final EventBus eventBus,
                             final WorkbenchPanel owner) {
        this.owner = owner;
        this.tabBar = new UnmodifiableTabBar();
        this.focusIndicator = new SimplePanel();
        this.deck = new TabbedDeckPanel( tabBar );
        this.focusIndicator.getElement().setClassName( "workbenchFocusIndicator" );
        this.setFocusReceived( false );

        VerticalPanel panel = new VerticalPanel();
        panel.add( tabBar );
        panel.add( focusIndicator );
        panel.add( deck );

        panel.setCellHeight( deck,
                             "100%" );
        tabBar.setWidth( "100%" );
        tabBar.addTabListener( this );

        initWidget( panel );
        setWidth( "100%" );
        setHeight( "100%" );

        setStyleName( "gwt-TabPanel" );
        deck.setStyleName( "workbenchTabPanelBottom" );

        // Add a11y role "tabpanel"
        Accessibility.setRole( deck.getElement(),
                               Accessibility.ROLE_TABPANEL );

        //Wire-up necessary event handlers
        eventBus.addHandler( FocusReceivedEvent.TYPE,
                             this );
    }

    public void add(Widget w) {
        throw new UnsupportedOperationException( "A tabText parameter must be specified with add()." );
    }

    /**
     * Adds a widget to the tab panel. If the Widget is already attached to the
     * TabPanel, it will be moved to the right-most index.
     * 
     * @param w
     *            the widget to be added
     * @param tabText
     *            the text to be shown on its tab
     */
    public void add(Widget w,
                    String tabText) {
        insert( w,
                tabText,
                getWidgetCount() );
    }

    public HandlerRegistration addBeforeSelectionHandler(BeforeSelectionHandler<Integer> handler) {
        return addHandler( handler,
                           BeforeSelectionEvent.getType() );
    }

    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
        return addHandler( handler,
                           SelectionEvent.getType() );
    }

    public void clear() {
        while ( getWidgetCount() > 0 ) {
            remove( getWidget( 0 ) );
        }
    }

    /**
     * Gets the deck panel within this tab panel. Adding or removing Widgets
     * from the DeckPanel is not supported and will throw
     * UnsupportedOperationExceptions.
     * 
     * @return the deck panel
     */
    public DeckPanel getDeckPanel() {
        return deck;
    }

    /**
     * Gets the tab bar within this tab panel. Adding or removing tabs from from
     * the TabBar is not supported and will throw
     * UnsupportedOperationExceptions.
     * 
     * @return the tab bar
     */
    public TabBar getTabBar() {
        return tabBar;
    }

    public Widget getWidget(int index) {
        return deck.getWidget( index );
    }

    public int getWidgetCount() {
        return deck.getWidgetCount();
    }

    /**
     * Convenience overload to allow {@link IsWidget} to be used directly.
     */
    public int getWidgetIndex(IsWidget child) {
        return getWidgetIndex( asWidgetOrNull( child ) );
    }

    public int getWidgetIndex(Widget widget) {
        return deck.getWidgetIndex( widget );
    }

    /**
     * Inserts a widget into the tab panel. If the Widget is already attached to
     * the TabPanel, it will be moved to the requested index.
     * 
     * @param widget
     *            the widget to be inserted
     * @param tabText
     *            the text to be shown on its tab
     * @param asHTML
     *            <code>true</code> to treat the specified text as HTML
     * @param beforeIndex
     *            the index before which it will be inserted
     */
    public void insert(Widget widget,
                       String tabText,
                       int beforeIndex) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        deck.insertProtected( widget,
                              tabText,
                              beforeIndex );
    }

    public boolean isAnimationEnabled() {
        return deck.isAnimationEnabled();
    }

    public Iterator<Widget> iterator() {
        // The Iterator returned by DeckPanel supports removal and will invoke
        // TabbedDeckPanel.remove(), which is an active function.
        return deck.iterator();
    }

    /**
     * @deprecated Use {@link BeforeSelectionHandler#onBeforeSelection} instead
     */
    @Deprecated
    public boolean onBeforeTabSelected(SourcesTabEvents sender,
                                       int tabIndex) {
        BeforeSelectionEvent<Integer> event = BeforeSelectionEvent.fire( this,
                                                                         tabIndex );
        return event == null || !event.isCanceled();
    }

    /**
     * @deprecated Use {@link SelectionHandler#onSelection} instead
     */
    @Deprecated
    public void onTabSelected(SourcesTabEvents sender,
                              int tabIndex) {
        deck.showWidget( tabIndex );
        SelectionEvent.fire( this,
                             tabIndex );
    }

    public boolean remove(int index) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        return deck.remove( index );
    }

    /**
     * Removes the given widget, and its associated tab.
     * 
     * @param widget
     *            the widget to be removed
     */
    public boolean remove(Widget widget) {
        // Delegate updates to the TabBar to our DeckPanel implementation
        return deck.remove( widget );
    }

    /**
     * Programmatically selects the specified tab and fires events.
     * 
     * @param index
     *            the index of the tab to be selected
     */
    public void selectTab(int index) {
        selectTab( index,
                   true );
    }

    /**
     * Programmatically selects the specified tab.
     * 
     * @param index
     *            the index of the tab to be selected
     * @param fireEvents
     *            true to fire events, false not to
     */
    public void selectTab(int index,
                          boolean fireEvents) {
        tabBar.selectTab( index,
                          fireEvents );
    }

    public void setAnimationEnabled(boolean enable) {
        deck.setAnimationEnabled( enable );
    }

    /**
     * Create a {@link SimplePanel} that will wrap the contents in a tab.
     * Subclasses can use this method to wrap tabs in decorator panels.
     * 
     * @return a {@link SimplePanel} to wrap the tab contents, or null to leave
     *         tabs unwrapped
     */
    protected SimplePanel createTabTextWrapper() {
        return null;
    }

    /**
     * <b>Affected Elements:</b>
     * <ul>
     * <li>-bar = The tab bar.</li>
     * <li>-bar-tab# = The element containing the content of the tab itself.</li>
     * <li>-bar-tab-wrapper# = The cell containing the tab at the index.</li>
     * <li>-bottom = The panel beneath the tab bar.</li>
     * </ul>
     * 
     * @see UIObject#onEnsureDebugId(String)
     */
    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId( baseID );
        tabBar.ensureDebugId( baseID + "-bar" );
        deck.ensureDebugId( baseID + "-bottom" );
    }

    @Override
    public void addTabListener(TabListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeTabListener(TabListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onResize() {
        final Widget parent = getParent().getParent();
        int width = parent.getElement().getOffsetWidth();
        int height = parent.getElement().getOffsetHeight();
        this.getElement().getStyle().setWidth( width,
                                               Unit.PX );
        this.getElement().getStyle().setHeight( height,
                                                Unit.PX );
        for ( Widget child : this.deck.getChildren() ) {
            if ( child instanceof RequiresResize ) {
                ((RequiresResize) child).onResize();
            }
        }
    }

    @Override
    public void onFocusReceived(FocusReceivedEvent event) {
        boolean hasFocus = event.getWorkbenchPanel().equals( owner );
        setFocusReceived( hasFocus );
    }

    private void setFocusReceived(boolean hasFocus) {
        if ( hasFocus ) {
            focusIndicator.getElement().addClassName( "workbenchFocusIndicatorHasFocus" );
        } else {
            focusIndicator.getElement().removeClassName( "workbenchFocusIndicatorHasFocus" );
        }
    }

}

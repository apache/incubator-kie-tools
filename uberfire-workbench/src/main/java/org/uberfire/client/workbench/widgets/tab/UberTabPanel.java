package org.uberfire.client.workbench.widgets.tab;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabLink;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.widgets.panels.WorkbenchPartPresenter;

import static com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs.*;

public class UberTabPanel
        extends Composite
        implements RequiresResize,
                   HasBeforeSelectionHandlers<Integer>,
                   HasSelectionHandlers<PartDefinition> {

    private static final int MARGIN = 20;

    private final TabPanel tabPanel;

    private WorkbenchPanelPresenter     presenter;
    private WorkbenchDragAndDropManager dndManager;

    private int     maxDropdownTabLinkWidth = 0;
    private boolean alreadyScheduled        = false;

    private final Map<WorkbenchPartPresenter.View, TabLink> tabIndex         = new HashMap<WorkbenchPartPresenter.View, TabLink>();
    private final Map<TabLink, WorkbenchPartPresenter.View> tabInvertedIndex = new HashMap<TabLink, WorkbenchPartPresenter.View>();
    private final Map<PartDefinition, TabLink>              partTabIndex     = new HashMap<PartDefinition, TabLink>();

    public void clear() {
        tabPanel.clear();
        partTabIndex.clear();
        tabIndex.clear();
        tabInvertedIndex.clear();
        maxDropdownTabLinkWidth = 0;
        alreadyScheduled = false;
    }

    public void selectTab( final PartDefinition id ) {
        final TabLink tab = partTabIndex.get( id );
        if ( tab != null ) {
            int index = getTabs().getWidgetIndex( tab );
            tabPanel.selectTab( index );
        }
    }

    public void remove( final PartDefinition id ) {
        final TabLink tab = partTabIndex.get( id );
        if ( tab != null ) {
            int index = getTabs().getWidgetIndex( tab );
            final boolean wasActive = tab.isActive();

            tabPanel.remove( tab );

            if ( wasActive ) {
                try {
                    tabPanel.selectTab( index <= 0 ? 0 : index - 1 );
                } catch ( final IndexOutOfBoundsException ex ) {
                    //no more tabs, it's ok
                }
            }

            tabIndex.remove( tabInvertedIndex.remove( tab ) );
            partTabIndex.remove( id );

            scheduleResize();
        }
    }

    public void changeTitle( final PartDefinition id,
                             final String title,
                             final IsWidget titleDecoration ) {
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler( final BeforeSelectionHandler<Integer> handler ) {
        return addHandler( handler, BeforeSelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return addHandler( handler, SelectionEvent.getType() );
    }

    public UberTabPanel() {
        tabPanel = new TabPanel( ABOVE ) {{

            addShownHandler( new ShownEvent.Handler() {
                @Override
                public void onShow( final ShownEvent e ) {
                    BeforeSelectionEvent.fire( UberTabPanel.this, 0 );
                }
            } );

            addShowHandler( new ShowEvent.Handler() {
                @Override
                public void onShow( final ShowEvent e ) {
                    if ( e.getTarget() == null ) {
                        return;
                    }
                    scheduleResize( e.getTarget().getTabPane().getWidget( 0 ) );
                    SelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( e.getTarget() ).getPresenter().getDefinition() );
                }
            } );

        }};

        initWidget( tabPanel );
    }

    public void setPresenter( final WorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    public void addTab( final WorkbenchPartPresenter.View view ) {

        if ( !tabIndex.containsKey( view ) ) {
            final Tab newTab = addCloseToTab( createTab( view, false, 0, 0 ) );

            tabPanel.add( newTab );

            if ( getTabs().getWidgetCount() == 1 ) {
                tabPanel.selectTab( 0 );
            }

            scheduleResize();
        }
    }

    private void scheduleResize() {
        if ( alreadyScheduled ) {
            return;
        }
        alreadyScheduled = true;
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
                alreadyScheduled = false;
            }
        } );
    }

    private void scheduleResize( final Widget widget ) {
        if ( widget instanceof RequiresResize ) {
            Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    ( (RequiresResize) widget ).onResize();
                }
            } );
        }
    }

    private Tab createTab( final WorkbenchPartPresenter.View view,
                           final boolean isActive,
                           final int width,
                           final int height ) {

        final Tab tab = new Tab() {{
            setHeading( view.getPresenter().getTitle() );
            setActive( isActive );
        }};

        tab.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                presenter.onPanelFocus();
                if ( tab.asTabLink().getParent().getParent() instanceof DropdownTab ) {
                    final DropdownTab dropdownTab = (DropdownTab) tab.asTabLink().getParent().getParent();
                    dropdownTab.setText( "Active: " + view.getPresenter().getTitle() );
                    dropdownTab.addStyleName( Constants.ACTIVE );
                } else {
                    final Widget lastTab = getLastTab();
                    if ( lastTab instanceof DropdownTab ) {
                        ( (DropdownTab) lastTab ).setText( "More..." );
                    }
                }
            }
        } );

        tab.add( view.asWidget() );

        scheduleResize( view.asWidget() );

        tabIndex.put( view, tab.asTabLink() );
        tabInvertedIndex.put( tab.asTabLink(), view );
        partTabIndex.put( view.getPresenter().getDefinition(), tab.asTabLink() );

        dndManager.makeDraggable( view, tab.asTabLink().getWidget( 0 ) );

        return tab;
    }

    private Tab cloneTab( final TabLink tabLink,
                          final boolean fromDropdown,
                          final boolean toDropdown ) {

        final Widget content = tabLink.getTabPane().getWidget( 0 );
        final WorkbenchPartPresenter.View view = tabInvertedIndex.get( tabLink );

        if ( !fromDropdown && toDropdown && tabLink.getOffsetWidth() > maxDropdownTabLinkWidth ) {
            maxDropdownTabLinkWidth = tabLink.getOffsetWidth();
        }

        tabInvertedIndex.remove( tabLink );

        return createTab( view,
                          tabLink.isActive(),
                          content.getOffsetWidth(),
                          content.getOffsetHeight() );
    }

    private DropdownTab cloneDropdown( final DropdownTab original,
                                       final int excludedIndex ) {
        final DropdownTab newDropdown = new DropdownTab( original.getText() );

        boolean isAnyTabActive = false;

        for ( int i = 0; i < original.getTabList().size(); i++ ) {
            final Tab currentTab = original.getTabList().get( i );
            if ( i != excludedIndex ) {
                if ( !isAnyTabActive ) {
                    isAnyTabActive = currentTab.isActive();
                }
                newDropdown.add( cloneTab( currentTab.asTabLink(), true, true ) );
            }
        }

        if ( isAnyTabActive ) {
            newDropdown.addStyleName( Constants.ACTIVE );
        } else {
            newDropdown.setText( "More..." );
        }

        return newDropdown;
    }

    private void shrinkTabBar() {
        final Widget lastTab = getLastTab();

        if ( lastTab instanceof TabLink ) {
            maxDropdownTabLinkWidth = 0;
            final TabLink tab = (TabLink) lastTab;

            final DropdownTab dropdown = new DropdownTab( "More..." );

            final Tab clonedTab = cloneTab( tab, false, true );

            if ( clonedTab.isActive() ) {
                dropdown.setText( "Active: " + clonedTab.asTabLink().getText() );
                dropdown.addStyleName( Constants.ACTIVE );
            }

            dropdown.add( clonedTab );

            tabPanel.add( dropdown );

            tabPanel.remove( tab );
            scheduleResize();
        } else if ( lastTab instanceof DropdownTab ) {
            final TabLink lastTabLink = (TabLink) getBeforeLastTab();

            final Tab clonedTab = cloneTab( lastTabLink, false, true );
            final DropdownTab dropdown = cloneDropdown( (DropdownTab) lastTab, -1 );

            if ( clonedTab.isActive() ) {
                dropdown.setText( "Active: " + clonedTab.asTabLink().getText() );
                dropdown.addStyleName( Constants.ACTIVE );
            }

            dropdown.add( clonedTab );
            tabPanel.add( dropdown );

            tabPanel.remove( lastTabLink );
            tabPanel.remove( lastTab );

            scheduleResize();
        }
    }

    private void expandTabBar() {
        final DropdownTab dropdown = (DropdownTab) getLastTab();

        int index = dropdown.getTabList().size() - 1;

        final TabLink tab = dropdown.getTabList().get( index ).asTabLink();

        final Tab newTab = addCloseToTab( cloneTab( tab, true, false ) );

        tabPanel.add( newTab );

        if ( dropdown.getTabList().size() > 2 ) {

            tabPanel.add( cloneDropdown( dropdown, index ) );
        } else if ( dropdown.getTabList().size() == 2 ) {
            final TabLink _tab = dropdown.getTabList().get( index - 1 ).asTabLink();

            tabPanel.add( addCloseToTab( cloneTab( _tab, true, false ) ) );
            maxDropdownTabLinkWidth = 0;
        }

        tabPanel.remove( dropdown );
        scheduleResize();
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );

            final ComplexPanel content = getTabContent();
            for ( int i = 0; i < content.getWidgetCount(); i++ ) {
                final Widget widget = content.getWidget( i );
                ( (TabPane) widget ).getWidget( 0 ).setPixelSize( width, height - getTabHeight() );
                scheduleResize( ( (TabPane) widget ).getWidget( 0 ) );
            }

            final ComplexPanel tabs = getTabs();
            if ( tabs != null && tabs.getWidgetCount() > 0 ) {
                final Widget firstTabItem = tabs.getWidget( 0 );
                final Widget lastTabItem = getLastTab();
                if ( tabs.getWidgetCount() > 1 &&
                        ( width < getTabBarWidth() || tabs.getOffsetHeight() > firstTabItem.getOffsetHeight() ) ) {
                    shrinkTabBar();
                } else if ( lastTabItem instanceof DropdownTab
                        && ( getTabBarWidth() + getLastTab().getOffsetWidth() ) < width ) {
                    expandTabBar();
                }
            }
        }
    }

    private int getTabHeight() {
        return tabPanel.getWidget( 0 ).getOffsetHeight() + MARGIN;
    }

    private ComplexPanel getTabContent() {
        return (ComplexPanel) tabPanel.getWidget( 1 );
    }

    private ComplexPanel getTabs() {
        return (ComplexPanel) tabPanel.getWidget( 0 );
    }

    private Widget getLastTab() {
        final ComplexPanel tabs = getTabs();
        return tabs.getWidget( tabs.getWidgetCount() - 1 );
    }

    private Widget getBeforeLastTab() {
        final ComplexPanel tabs = getTabs();
        return tabs.getWidget( tabs.getWidgetCount() - 2 );
    }

    private int getTabBarWidth() {
        final ComplexPanel tabs = getTabs();

        int width = 0;
        for ( final Widget currentTab : tabs ) {
            width += currentTab.getOffsetWidth();
        }

        int margin = 42;

        if ( getLastTab() instanceof DropdownTab ) {
            margin = maxDropdownTabLinkWidth;
        }

        return width + margin;
    }

    private Tab addCloseToTab( final Tab tab ) {
        final Button close = new Button( "&times;" ) {{
            setStyleName( "close" );
            addStyleName( WorkbenchResources.INSTANCE.CSS().tabCloseButton() );
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( final ClickEvent event ) {
                    final WorkbenchPartPresenter.View partToDeselect = tabInvertedIndex.get( tab.asTabLink() );
                    presenter.onBeforePartClose( partToDeselect.getPresenter().getDefinition() );
                }
            } );
        }};

        tab.addDecorate( close );

        return tab;
    }

    public void setDndManager( final WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    public void setFocus( final boolean hasFocus ) {
        if ( hasFocus ) {
            getTabs().setStyleName( WorkbenchResources.INSTANCE.CSS().activeNavTabs(), true );
        } else {
            getTabs().removeStyleName( WorkbenchResources.INSTANCE.CSS().activeNavTabs() );
        }
    }

}

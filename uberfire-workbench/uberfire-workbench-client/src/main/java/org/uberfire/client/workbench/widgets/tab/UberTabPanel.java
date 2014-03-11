package org.uberfire.client.workbench.widgets.tab;

import java.util.HashMap;
import java.util.List;
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
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import static com.github.gwtbootstrap.client.ui.resources.Bootstrap.Tabs.*;

public class UberTabPanel
        extends Composite
        implements MultiPartWidget,
                   ClickHandler {

    private static final int MARGIN = 20;

    TabPanel tabPanel;

    private WorkbenchPanelPresenter presenter;
    WorkbenchDragAndDropManager dndManager;

    private int maxDropdownTabLinkWidth = 0;
    private boolean alreadyScheduled = false;

    final Map<WorkbenchPartPresenter.View, TabLink> tabIndex = new HashMap<WorkbenchPartPresenter.View, TabLink>();
    final Map<TabLink, WorkbenchPartPresenter.View> tabInvertedIndex = new HashMap<TabLink, WorkbenchPartPresenter.View>();
    final Map<PartDefinition, TabLink> partTabIndex = new HashMap<PartDefinition, TabLink>();
    private boolean hasFocus = false;
    private Command addOnFocusHandler;

    public void clear() {
        tabPanel.clear();
        partTabIndex.clear();
        tabIndex.clear();
        tabInvertedIndex.clear();
        maxDropdownTabLinkWidth = 0;
        alreadyScheduled = false;
    }

    public void selectPart( final PartDefinition id ) {
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
            if ( index < 0 ) {
                final DropdownTab _dropdown = (DropdownTab) getLastTab();
                for ( int i = 0; i < _dropdown.getTabList().size(); i++ ) {
                    final TabLink activeTab = _dropdown.getTabList().get( i ).asTabLink();
                    if ( activeTab.equals( tab ) ) {
                        index = i;
                        break;
                    }
                }
                final DropdownTab cloneDropdown = cloneDropdown( _dropdown, index );
                if ( cloneDropdown.getTabList().size() > 0 ) {
                    tabPanel.add( cloneDropdown );
                }
                tabPanel.remove( _dropdown );
            } else {
                final boolean wasActive = tab.isActive();

                tabPanel.remove( tab );

                if ( wasActive ) {
                    try {
                        tabPanel.selectTab( index <= 0 ? 0 : index - 1 );
                    } catch ( final IndexOutOfBoundsException ex ) {
                        //no more tabs, it's ok
                    }
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
        final TabLink tabLink = partTabIndex.get( id );
        if ( tabLink != null ) {
            tabLink.setText( title );

            if ( tabLink.getParent().getParent() instanceof DropdownTab ) {
                final DropdownTab dropdownTab = (DropdownTab) tabLink.getParent().getParent();
                dropdownTab.setText( "Active: " + title );
                dropdownTab.addStyleName( Constants.ACTIVE );
            }
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

    public UberTabPanel() {
        tabPanel = new TabPanel( ABOVE ) {{

            addShownHandler( new ShownEvent.Handler() {
                @Override
                public void onShow( final ShownEvent e ) {
                    if ( e.getRelatedTarget() != null ) {
                        BeforeSelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( e.getRelatedTarget() ).getPresenter().getDefinition() );
                    }
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

            addDomHandler( UberTabPanel.this, ClickEvent.getType() );
        }};

        initWidget( tabPanel );
    }

    public void setPresenter( final WorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    public void addPart( final WorkbenchPartPresenter.View view ) {

        if ( !tabIndex.containsKey( view ) ) {
            final Tab newTab = createTab( view, false, 0, 0 );

            final Widget lastTab = getLastTab();
            if ( lastTabIsDropdownTab( lastTab ) ) {
                final Tab clonedTab = cloneTab( newTab.asTabLink(), false, true );
                final DropdownTab dropdown = cloneDropdown( (DropdownTab) lastTab, -1 );

                dropdown.setText( "Active: " + newTab.asTabLink().getText() );
                dropdown.addStyleName( Constants.ACTIVE );

                dropdown.add( clonedTab );
                tabPanel.add( dropdown );

                tabPanel.remove( lastTab );
            } else {
                tabPanel.add( newTab );
                if ( isFirstWidget() ) {
                    tabPanel.selectTab( 0 );
                }
            }

            scheduleResize();
        }
    }

    boolean lastTabIsDropdownTab( Widget lastTab ) {
        return lastTab != null && lastTab instanceof DropdownTab;
    }

    boolean isFirstWidget() {
        return getTabs().getWidgetCount() == 1;
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
        if ( getParent() != null ) {
            if ( widget instanceof RequiresResize ) {
                Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        ( (RequiresResize) widget ).onResize();
                    }
                } );
            }
        }
    }

    Tab createTab( final WorkbenchPartPresenter.View view,
                   final boolean isActive,
                   final int width,
                   final int height ) {

        final Tab tab = createTab( view, isActive );

        tab.addClickHandler( createTabClickHandler( view, tab ) );

        tab.add( view.asWidget() );

        scheduleResize( view.asWidget() );

        tabIndex.put( view, tab.asTabLink() );
        tabInvertedIndex.put( tab.asTabLink(), view );
        partTabIndex.put( view.getPresenter().getDefinition(), tab.asTabLink() );

        dndManager.makeDraggable( view, tab.asTabLink().getWidget( 0 ) );

        return addCloseToTab( tab );
    }

    private ClickHandler createTabClickHandler( final WorkbenchPartPresenter.View view,
                                                final Tab tab ) {
        return new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                UberTabPanel.this.onClick( event );
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
        };
    }

    Tab createTab( final WorkbenchPartPresenter.View view,
                   final boolean isActive ) {
        return new Tab() {{
            setHeading( view.getPresenter().getTitle() );
            setActive( isActive );
        }};
    }

    Tab cloneTab( final TabLink tabLink,
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

    DropdownTab cloneDropdown( final DropdownTab original,
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

        final Tab newTab = cloneTab( tab, true, false );

        tabPanel.add( newTab );

        if ( dropdown.getTabList().size() > 2 ) {

            tabPanel.add( cloneDropdown( dropdown, index ) );
        } else if ( dropdown.getTabList().size() == 2 ) {
            final TabLink _tab = dropdown.getTabList().get( index - 1 ).asTabLink();

            tabPanel.add( cloneTab( _tab, true, false ) );
            maxDropdownTabLinkWidth = 0;
        }

        tabPanel.remove( dropdown );
        scheduleResize();
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width;
            final int height;
            if ( parent.getParent() != null ) {
                if ( parent.getParent().getParent() != null ) {
                    width = parent.getParent().getParent().getOffsetWidth();
                    height = parent.getParent().getParent().getOffsetHeight();
                } else {
                    width = parent.getParent().getOffsetWidth();
                    height = parent.getParent().getOffsetHeight();
                }
            } else {
                width = parent.getOffsetWidth();
                height = parent.getOffsetHeight();
            }

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

    Widget getLastTab() {
        final ComplexPanel tabs = getTabs();
        if ( tabs.getWidgetCount() <= 0 ) {
            return null;
        }
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
            addOnFocusHandler.execute();

            for ( int i = 0; i < getTabs().getWidgetCount(); i++ ) {
                final Widget _widget = getTabs().getWidget( i );
                if ( _widget instanceof TabLink && ( (TabLink) _widget ).isActive() ) {
                    SelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( _widget ).getPresenter().getDefinition() );
                    break;
                } else if ( _widget instanceof DropdownTab ) {
                    final List<Tab> tabs = ( (DropdownTab) _widget ).getTabList();
                    for ( final Tab activeTab : tabs ) {
                        if ( activeTab.isActive() ) {
                            SelectionEvent.fire( UberTabPanel.this, tabInvertedIndex.get( activeTab ).getPresenter().getDefinition() );
                            break;
                        }
                    }
                }

            }

        }
    }

    public void addOnFocusHandler( final Command command ) {
        this.addOnFocusHandler = command;
    }

    @Override
    public int getPartsSize() {
        return partTabIndex.size();
    }
}

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

package org.uberfire.client.common;

import com.github.gwtbootstrap.client.ui.DropdownTab;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabLink;
import com.github.gwtbootstrap.client.ui.TabPane;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.resources.i18n.CommonConstants;

public class MultiPageEditorView
        extends Composite
        implements RequiresResize {

    private static final int MARGIN = 20;

    final TabPanel tabPanel;

    private int maxDropdownTabLinkWidth = 0;
    private boolean alreadyScheduled = false;

    public MultiPageEditorView() {
        this( MultiPageEditor.TabPosition.BELOW );
    }

    public MultiPageEditorView( final MultiPageEditor.TabPosition tabPosition ) {
        tabPanel = new TabPanel() {{
            setTabPosition( tabPosition.getPosition() );

            addShownHandler( new TabPanel.ShownEvent.Handler() {
                @Override
                public void onShow( final TabPanel.ShownEvent e ) {
                    try {
                        ( (Page.PageView) ( (LayoutPanel) e.getRelatedTarget().getTabPane().getWidget( 0 ) ).getWidget( 0 ) ).onLostFocus();
                    } catch ( final Exception ex ) {
                    }
                }
            } );

            addShowHandler( new TabPanel.ShowEvent.Handler() {
                @Override
                public void onShow( final TabPanel.ShowEvent e ) {
                    final Page.PageView widget = ( (Page.PageView) ( (LayoutPanel) e.getTarget().getTabPane().getWidget( 0 ) ).getWidget( 0 ) );
                    scheduleResize( widget );
                    widget.onFocus();
                }
            } );
        }};

        initWidget( tabPanel );
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

    public void addPage( final Page page ) {
        final Tab tab = createTab( page.getLabel(), page.getView(), false, 0, 0 );

        tabPanel.add( tab );

        if ( getTabs().getWidgetCount() == 1 ) {
            tabPanel.selectTab( 0 );
        }

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        } );
    }

    public void selectPage( int index ) {
        tabPanel.selectTab( index );
    }

    public void clear() {
        tabPanel.clear();
    }

    public void shrinkTabBar() {
        final Widget lastTab = getLastTab();

        if ( lastTab instanceof TabLink ) {
            maxDropdownTabLinkWidth = 0;
            final TabLink tab = (TabLink) lastTab;

            final DropdownTab dropdown = new DropdownTab( CommonConstants.INSTANCE.More() ) {{
                setDropup( true );
            }};

            final Tab clonedTab = cloneTab( tab, false, true );

            if ( clonedTab.isActive() ) {
                dropdown.setText( CommonConstants.INSTANCE.Active()+" " + clonedTab.asTabLink().getText() );
                dropdown.addStyleName( Constants.ACTIVE );
            }

            dropdown.add( clonedTab );

            tabPanel.add( dropdown );

            tabPanel.remove( tab );
            scheduleResize();
        } else if ( lastTab instanceof DropdownTab ) {
            final TabLink lastTabLink = (TabLink) getBeforeLastTab();
            if ( lastTabLink == null ) {
                return;
            }

            final Tab clonedTab = cloneTab( lastTabLink, false, true );
            final DropdownTab dropdown = cloneDropdown( (DropdownTab) lastTab, -1 );

            if ( clonedTab.isActive() ) {
                dropdown.setText( CommonConstants.INSTANCE.Active()+" " + clonedTab.asTabLink().getText() );
                dropdown.addStyleName( Constants.ACTIVE );
            }

            dropdown.add( clonedTab );
            tabPanel.add( dropdown );

            tabPanel.remove( lastTabLink );
            tabPanel.remove( lastTab );

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

    public void expandTabBar() {
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

    public Tab createTab( final String label,
                          final IsWidget widget,
                          final boolean isActive,
                          final int width,
                          final int height ) {

        final Tab tab = new Tab() {{
            setHeading( label );
            setActive( isActive );
        }};

        tab.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( final ClickEvent event ) {
                if ( tab.asTabLink().getParent().getParent() instanceof DropdownTab ) {
                    final DropdownTab dropdownTab = (DropdownTab) tab.asTabLink().getParent().getParent();
                    dropdownTab.setText( CommonConstants.INSTANCE.Active()+" " + label );
                    dropdownTab.addStyleName( Constants.ACTIVE );
                } else {
                    final Widget lastTab = getLastTab();
                    if ( lastTab instanceof DropdownTab ) {
                        ( (DropdownTab) lastTab ).setText( CommonConstants.INSTANCE.More() );
                    }
                }
            }
        } );

        final LayoutPanel flowPanel = new LayoutPanel() {{
            add( widget );
            setPixelSize( width, height );
        }};

        tab.add( flowPanel );

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if ( MultiPageEditorView.this.getParent() != null ) {
                    flowPanel.setPixelSize( MultiPageEditorView.this.getParent().getOffsetWidth(),
                                            MultiPageEditorView.this.getParent().getOffsetHeight() - getTabHeight() );
                }
            }
        } );

        return tab;
    }

    public Tab cloneTab( final TabLink tabLink,
                         final boolean fromDropdown,
                         final boolean toDropdown ) {

        final String heading = tabLink.getText();
        final Widget content = ( (ComplexPanel) tabLink.getTabPane().getWidget( 0 ) ).getWidget( 0 );

        if ( !fromDropdown && toDropdown && tabLink.getOffsetWidth() > maxDropdownTabLinkWidth ) {
            maxDropdownTabLinkWidth = tabLink.getOffsetWidth();
        }

        return createTab( heading, content, tabLink.isActive(), content.getOffsetWidth(), content.getOffsetHeight() );
    }

    private DropdownTab cloneDropdown( final DropdownTab original,
                                       final int excludedIndex ) {
        final DropdownTab newDropdown = new DropdownTab( original.getText() ) {{
            setDropup( true );
        }};

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
            newDropdown.setText( CommonConstants.INSTANCE.More() );
        }

        return newDropdown;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );

            if ( width == 0 && height == 0 ) {
                //it's `invisible` = makes no sense try to resize
                return;
            }

            final ComplexPanel content = getTabContent();
            for ( int i = 0; i < content.getWidgetCount(); i++ ) {
                final Widget widget = content.getWidget( i );
                if ( widget instanceof TabPane ) {
                    final TabPane tabPane = (TabPane) widget;
                    final LayoutPanel flowPanel = ( (LayoutPanel) ( tabPane ).getWidget( 0 ) );
                    flowPanel.setPixelSize( width, height - getTabHeight() );

                    //Resize children
                    for ( int iChild = 0; iChild < tabPane.getWidgetCount(); iChild++ ) {
                        final Widget childWidget = tabPane.getWidget( iChild );
                        if ( childWidget instanceof RequiresResize ) {
                            ( (RequiresResize) childWidget ).onResize();
                        }
                    }
                }
            }

            final ComplexPanel tabs = getTabs();
            if ( tabs != null && tabs.getWidgetCount() > 0 ) {
                final Widget firstTabItem = tabs.getWidget( 0 );
                final Widget lastTabItem = getLastTab();
                if ( width < getTabBarWidth() || tabs.getOffsetHeight() > firstTabItem.getOffsetHeight() ) {
                    shrinkTabBar();
                } else if ( lastTabItem instanceof DropdownTab
                        && ( getTabBarWidth() + getLastTab().getOffsetWidth() ) < width ) {
                    expandTabBar();
                }
            }
        }
    }

    private int getTabHeight() {
        return tabPanel.getWidget( 1 ).getOffsetHeight() + MARGIN;
    }

    private ComplexPanel getTabContent() {
        return (ComplexPanel) tabPanel.getWidget( 0 );
    }

    private ComplexPanel getTabs() {
        return (ComplexPanel) tabPanel.getWidget( 1 );
    }

    private Widget getLastTab() {
        final ComplexPanel tabs = getTabs();
        return tabs.getWidget( tabs.getWidgetCount() - 1 );
    }

    private Widget getBeforeLastTab() {
        final ComplexPanel tabs = getTabs();
        int index = tabs.getWidgetCount() - 2;
        if ( index < 0 ) {
            return null;
        }
        return tabs.getWidget( index );
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

}

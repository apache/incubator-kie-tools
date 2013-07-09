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
package org.uberfire.client.workbench.widgets.listbar;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.commons.data.Pair;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.DragArea;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;
import static com.google.gwt.dom.client.Style.Display.*;

/**
 * The Menu Bar widget
 */
public class ListBarWidget
        extends Composite implements MultiPartWidget {

    interface ListBarWidgetBinder
            extends
            UiBinder<FlowPanel, ListBarWidget> {

    }

    private static ListBarWidgetBinder uiBinder = GWT.create( ListBarWidgetBinder.class );

    @UiField
    SimplePanel title;

    @UiField
    FlowPanel header;

    @UiField
    ButtonGroup contextMenu;

    @UiField
    Button closeButton;

    @UiField
    DropdownButton dropdownCaret;

    @UiField
    FlowPanel content;

    @UiField
    FlowPanel menuArea;

    private WorkbenchPanelPresenter presenter;
    private WorkbenchDragAndDropManager dndManager;

    private final Map<PartDefinition, FlowPanel> partContentView = new HashMap<PartDefinition, FlowPanel>();
    private final Map<PartDefinition, Widget> partTitle = new HashMap<PartDefinition, Widget>();
    private LinkedHashSet<PartDefinition> parts = new LinkedHashSet<PartDefinition>();

    private Pair<PartDefinition, FlowPanel> currentPart;

    public ListBarWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
        menuArea.setVisible( false );

        closeButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( currentPart != null ) {
                    presenter.onBeforePartClose( currentPart.getK1() );
                }
            }
        } );
    }

    @Override
    public void setPresenter( final WorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setDndManager( final WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    @Override
    public void clear() {
        contextMenu.clear();
        menuArea.setVisible( false );
        title.clear();
        content.clear();

        parts.clear();
        partContentView.clear();
        partTitle.clear();
        currentPart = null;
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        final PartDefinition partDefinition = view.getPresenter().getDefinition();
        if ( parts.contains( partDefinition ) ) {
            selectPart( partDefinition );
            return;
        }

        menuArea.setVisible( true );

        parts.add( partDefinition );

        final FlowPanel panel = new FlowPanel();
        panel.add( view );
        content.add( panel );
        partContentView.put( partDefinition, panel );

        final Widget title = buildTitle( view.getPresenter().getTitle() );
        partTitle.put( partDefinition, title );

        dndManager.makeDraggable( view, title );
    }

    private void updateBreadcrumb( final PartDefinition partDefinition ) {
        this.title.clear();

        final Widget title = partTitle.get( partDefinition );
        this.title.add( title );
    }

    private Widget buildTitle( final String title ) {
        final SpanElement spanElement = Document.get().createSpanElement();
        spanElement.setInnerText( title );

        return new DragArea() {{
            add( spanElement );
        }};
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {

    }

    @Override
    public void selectPart( final PartDefinition part ) {
        if ( !parts.contains( part ) ) {
            //not necessary to check if current is part
            return;
        }

        if ( currentPart != null ) {
            parts.add( currentPart.getK1() );
            currentPart.getK2().getElement().getStyle().setDisplay( NONE );
        }

        currentPart = Pair.newPair( part, partContentView.get( part ) );
        currentPart.getK2().getElement().getStyle().setDisplay( BLOCK );
        updateBreadcrumb( part );
        parts.remove( currentPart.getK1() );

        dropdownCaret.setRightDropdown( true );
        dropdownCaret.clear();
        dropdownCaret.add( new CustomList() );
        setupContextMenu();

        scheduleResize();
    }

    private void setupContextMenu() {
        contextMenu.clear();
        final WorkbenchPartPresenter.View part = (WorkbenchPartPresenter.View) currentPart.getK2().getWidget( 0 );
        part.getPresenter().getMenus();

        if ( part.getPresenter().getMenus() != null && part.getPresenter().getMenus().getItems().size() > 0 ) {
            for ( final MenuItem menuItem : part.getPresenter().getMenus().getItems() ) {
                final Widget result = makeItem( menuItem, true );
                if ( result != null ) {
                    contextMenu.add( result );
                }
            }
        }
    }

    @Override
    public void remove( final PartDefinition part ) {
        if ( currentPart.getK1().equals( part ) ) {
            if ( parts.size() > 0 ) {
                selectPart( parts.iterator().next() );
            } else {
                clear();
            }
        }

        parts.remove( part );
        partContentView.remove( part );
        partTitle.remove( part );
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
    }

    @Override
    public void addOnFocusHandler( final Command command ) {
    }

    @Override
    public HandlerRegistration addBeforeSelectionHandler( final BeforeSelectionHandler<PartDefinition> handler ) {
        return null;
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return null;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            setPixelSize( width, height );
            content.setPixelSize( width, height );

            for ( int i = 0; i < content.getWidgetCount(); i++ ) {
                final Widget widget = content.getWidget( i );
                ( (FlowPanel) widget ).getWidget( 0 ).setPixelSize( width, height - getHeaderHeight() );
                scheduleResize( ( (FlowPanel) widget ).getWidget( 0 ) );
            }
        }
    }

    private int getHeaderHeight() {
        return header.getOffsetHeight();
    }

    private void scheduleResize() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
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

    private Widget makeItem( final MenuItem item,
                             boolean isRoot ) {
        if ( item instanceof MenuItemCommand ) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            final Widget gwtItem;
            if ( isRoot ) {
                gwtItem = new Button( cmdItem.getCaption() ) {{
                    setSize( MINI );
                    setEnabled( item.isEnabled() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent event ) {
                            cmdItem.getCommand().execute();
                        }
                    } );
                }};
                item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        ( (Button) gwtItem ).setEnabled( enabled );
                    }
                } );
            } else {
                gwtItem = new NavLink( cmdItem.getCaption() ) {{
                    setDisabled( !item.isEnabled() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent event ) {
                            cmdItem.getCommand().execute();
                        }
                    } );
                }};
                item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        ( (NavLink) gwtItem ).setDisabled( !enabled );
                    }
                } );
            }

            return gwtItem;

        } else if ( item instanceof MenuGroup ) {
            final MenuGroup groups = (MenuGroup) item;
            final Widget gwtItem;
            if ( isRoot ) {
                gwtItem = new DropdownButton( groups.getCaption() ) {{
                    setSize( MINI );
                    for ( final MenuItem _item : groups.getItems() ) {
                        add( makeItem( _item, false ) );
                    }
                }};
            } else {
                gwtItem = new Dropdown( groups.getCaption() ) {{
                    for ( final MenuItem _item : groups.getItems() ) {
                        add( makeItem( _item, false ) );
                    }
                }};
            }

            return gwtItem;
        }

        return null;
    }

    class CustomList extends Composite {

        final FlowPanel panel = new FlowPanel();

        CustomList() {
            initWidget( panel );
            if ( content.getOffsetWidth() < 10 ) {
                return;
            }
            final String ctitle = ( (WorkbenchPartPresenter.View) partContentView.get( currentPart.getK1() ).getWidget( 0 ) ).getPresenter().getTitle();
            panel.add( new NavLink( ctitle ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                    }
                } );
            }} );

            for ( final PartDefinition part : parts ) {
                final String title = ( (WorkbenchPartPresenter.View) partContentView.get( part ).getWidget( 0 ) ).getPresenter().getTitle();
                panel.add( new NavLink( title ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( final ClickEvent event ) {
                            selectPart( part );
                        }
                    } );
                }} );
            }
            int width = content.getOffsetWidth() - 10;
            setWidth( width + "px" );
        }

    }
}

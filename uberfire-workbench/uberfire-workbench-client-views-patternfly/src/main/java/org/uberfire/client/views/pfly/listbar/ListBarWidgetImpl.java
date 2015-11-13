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
package org.uberfire.client.views.pfly.listbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.NavbarLink;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.views.pfly.maximize.MaximizeToggleButton;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.listbar.ListbarPreferences;
import org.uberfire.client.workbench.widgets.listbar.ResizeFocusPanel;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import static com.google.gwt.dom.client.Style.Display.*;

/**
 * Implementation of ListBarWidget based on PatternFly components.
 */
@Dependent
public class ListBarWidgetImpl
        extends ResizeComposite implements ListBarWidget {

    interface ListBarWidgetBinder
            extends
            UiBinder<ResizeFocusPanel, ListBarWidgetImpl> {

    }

    private static ListBarWidgetBinder uiBinder = GWT.create( ListBarWidgetBinder.class );

    /**
     * Preferences bean that applications can optionally provide. If this injection is unsatisfied, default settings are used.
     */
    @Inject
    Instance<ListbarPreferences> optionalListBarPrefs;

    @Inject
    PanelManager panelManager;

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private User identity;

    @UiField
    FocusPanel container;

    @UiField
    PartListDropdown titleDropDown;

    @UiField
    PanelHeader header;

    @UiField
    Panel panel;

    @UiField
    ButtonGroup contextMenu;

    @UiField
    Button closeButton;

    @UiField
    ButtonGroup toolBar;

    @UiField
    MaximizeToggleButton maximizeButton;

    /**
     * Wraps maximizeButton, which is the view.
     */
    MaximizeToggleButtonPresenter maximizeButtonPresenter;

    @UiField
    PanelBody content;

    WorkbenchPanelPresenter presenter;

    final Map<PartDefinition, FlowPanel> partContentView = new HashMap<PartDefinition, FlowPanel>();
    LinkedHashSet<PartDefinition> parts = new LinkedHashSet<PartDefinition>();

    Pair<PartDefinition, FlowPanel> currentPart;

    @PostConstruct
    void postConstruct() {
        initWidget( uiBinder.createAndBindUi( this ) );
        maximizeButtonPresenter = new MaximizeToggleButtonPresenter( maximizeButton );
        titleDropDown.setHideOnSingleElement( getListbarPreferences().isHideTitleDropDownOnSingleElement() );
        setupEventHandlers();

        Layouts.setToFillParent( this );
        scheduleResize();
    }

    void setupEventHandlers() {
        this.container.addMouseOutHandler( new MouseOutHandler() {
            @Override
            public void onMouseOut( MouseOutEvent event ) {
                titleDropDown.removeStyleName( "open" );
            }
        } );

        this.container.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {
                if ( currentPart != null && currentPart.getK1() != null ) {
                    selectPart( currentPart.getK1() );
                }
            }
        } );

        this.maximizeButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( maximizeButton.isMaximized() ) {
                    panelManager.onPartMaximized( currentPart.getK1() );
                } else {
                    panelManager.onPartMinimized( currentPart.getK1() );
                }
            }
        } );

        closeButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( currentPart != null ) {
                    if ( maximizeButton.isMaximized() ) {
                        panelManager.onPartMinimized( currentPart.getK1() );
                    }
                    panelManager.closePart( currentPart.getK1() );
                }
            }
        } );

        titleDropDown.addSelectionHandler( new SelectionHandler<PartDefinition>() {
            @Override
            public void onSelection( final SelectionEvent<PartDefinition> event ) {
                selectPart( event.getSelectedItem() );
            }
        } );

        titleDropDown.addCloseHandler( new CloseHandler<PartDefinition>() {
            @Override
            public void onClose( final CloseEvent<PartDefinition> event ) {
                panelManager.closePart( event.getTarget() );
            }
        } );
    }

    ListbarPreferences getListbarPreferences() {
        try {
            return optionalListBarPrefs.isUnsatisfied() ? new ListbarPreferences() : optionalListBarPrefs.get();
        } catch ( IOCResolutionException e ) {
            return new ListbarPreferences();
        }
    }

    @Override
    public void enableDnd() {
        titleDropDown.enableDragAndDrop();
    }

    @Override
    public void disableDnd() {
        titleDropDown.disableDragAndDrop();
    }

    @Override
    public void setPresenter( final WorkbenchPanelPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setDndManager( final WorkbenchDragAndDropManager dndManager ) {
        this.titleDropDown.setDndManager( dndManager );
    }

    @Override
    public void clear() {
        contextMenu.clear();
        header.setVisible( false );
        content.clear();

        parts.clear();
        partContentView.clear();
        titleDropDown.clear();
        currentPart = null;
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        final PartDefinition partDefinition = view.getPresenter().getDefinition();
        if ( parts.contains( partDefinition ) ) {
            selectPart( partDefinition );
            return;
        }

        parts.add( partDefinition );

        final FlowPanel panel = new FlowPanel();
        Layouts.setToFillParent( panel );
        panel.add( view );
        content.add( panel );

        // IMPORTANT! if you change what goes in this map, update the remove(PartDefinition) method
        partContentView.put( partDefinition, panel );

        titleDropDown.addPart( view );

        header.setVisible( true );

        scheduleResize();
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        titleDropDown.changeTitle( part, title, titleDecoration );
    }

    @Override
    public boolean selectPart( final PartDefinition part ) {
        if ( !parts.contains( part ) ) {
            //not necessary to check if current is part
            return false;
        }

        if ( currentPart != null ) {
            if ( currentPart.getK1().equals( part ) ) {
                return true;
            }
            parts.add( currentPart.getK1() );
            panelManager.onPartHidden( currentPart.getK1() );
            currentPart.getK2().getElement().getStyle().setDisplay( NONE );
        }

        currentPart = Pair.newPair( part, partContentView.get( part ) );
        currentPart.getK2().getElement().getStyle().setDisplay( BLOCK );
        parts.remove( currentPart.getK1() );

        titleDropDown.selectPart( part );
        setupContextMenu();

        scheduleResize();

        SelectionEvent.fire( ListBarWidgetImpl.this, part );

        return true;
    }

    void setupContextMenu() {
        contextMenu.clear();
        final WorkbenchPartPresenter.View part = (WorkbenchPartPresenter.View) currentPart.getK2().getWidget( 0 );
        if ( part.getPresenter().getMenus() != null && part.getPresenter().getMenus().getItems().size() > 0 ) {
            for ( final MenuItem menuItem : part.getPresenter().getMenus().getItems() ) {
                final Widget result = makeItem( menuItem, true );
                if ( result != null ) {
                    contextMenu.add( result );
                }
            }
        }
        contextMenu.setVisible( contextMenu.getWidgetCount() > 0 );
    }

    @Override
    public boolean remove( final PartDefinition part ) {
        titleDropDown.removePart( part );
        if ( currentPart.getK1().equals( part ) ) {
            if ( parts.size() > 0 ) {
                presenter.selectPart( parts.iterator().next() );
            } else {
                clear();
            }
        }

        boolean removed = parts.remove( part );
        FlowPanel view = partContentView.remove( part );
        if ( view != null ) {
            // FIXME null check should not be necessary, but sometimes the entry in partContentView is missing!
            content.remove( view );
        }

        if( currentPart == null ){
            header.setVisible( false );
        }

        scheduleResize();
        return removed;
    }

    @Override
    public void setFocus( final boolean hasFocus ) {
    }

    @Override
    public void addOnFocusHandler( final Command command ) {
    }

    @Override
    public int getPartsSize() {
        if ( currentPart == null ) {
            return 0;
        }
        return parts.size() + 1;
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
    public void onResize() {
        if ( !isAttached() ) {
            return;
        }

        super.onResize();

        // FIXME only need to do this for the one visible part .. need to call onResize() when switching parts anyway
        for ( int i = 0; i < content.getWidgetCount(); i++ ) {
            final FlowPanel container = (FlowPanel) content.getWidget( i );
            final Widget containedWidget = container.getWidget( 0 );
            if ( containedWidget instanceof RequiresResize ) {
                ( (RequiresResize) containedWidget ).onResize();
            }
        }
    }

    // TODO refactor this to use a MenuVisitor
    private Widget makeItem( final MenuItem item,
                             boolean isRoot ) {
        if ( !authzManager.authorize( item, identity ) ) {
            return null;
        }

        if ( item instanceof MenuItemCommand ) {
            final MenuItemCommand cmdItem = (MenuItemCommand) item;
            if ( isRoot ) {
                final Button button = new Button( cmdItem.getCaption() );
                button.setSize( ButtonSize.SMALL );
                button.setEnabled( item.isEnabled() );
                button.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        cmdItem.getCommand().execute();
                    }
                } );
                item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        button.setEnabled( enabled );
                    }
                } );
                return button;
            } else {
                final NavbarLink navbarLink = new NavbarLink();
                navbarLink.setText( cmdItem.getCaption() );
                if ( !item.isEnabled() ) {
                    navbarLink.addStyleName( "disabled" );
                }
                navbarLink.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        cmdItem.getCommand().execute();
                    }
                } );
                item.addEnabledStateChangeListener( new EnabledStateChangeListener() {
                    @Override
                    public void enabledStateChanged( final boolean enabled ) {
                        if ( enabled ) {
                            navbarLink.removeStyleName( "disabled" );
                        } else {
                            navbarLink.addStyleName( "disabled" );
                        }
                    }
                } );
                return navbarLink;
            }

        } else if ( item instanceof MenuGroup ) {
            final MenuGroup groups = (MenuGroup) item;
            if ( isRoot ) {
                final List<Widget> widgetList = new ArrayList<Widget>();
                for ( final MenuItem _item : groups.getItems() ) {
                    final Widget widget = makeItem( _item, false );
                    if ( widget != null ) {
                        widgetList.add( widget );
                    }
                }

                if ( widgetList.isEmpty() ) {
                    return null;
                }

                return makeDropDownMenuButton( groups.getCaption(),
                        widgetList );

            } else {
                final List<Widget> widgetList = new ArrayList<Widget>();
                for ( final MenuItem _item : groups.getItems() ) {
                    final Widget result = makeItem( _item, false );
                    if ( result != null ) {
                        widgetList.add( result );
                    }
                }

                if ( widgetList.isEmpty() ) {
                    return null;
                }

                return makeDropDownMenuButton( groups.getCaption(),
                        widgetList );
            }

        } else if ( item instanceof MenuCustom ) {
            final Object result = ( (MenuCustom) item ).build();
            if ( result instanceof Widget ) {
                return (Widget) result;
            }
        }

        return null;
    }

    private Widget makeDropDownMenuButton( final String caption,
                                           final List<Widget> widgetList ) {
        final ButtonGroup buttonGroup = new ButtonGroup();
        final Button dropdownButton = new Button( caption );
        dropdownButton.setDataToggle( Toggle.DROPDOWN );
        dropdownButton.setSize( ButtonSize.EXTRA_SMALL );
        final DropDownMenu dropDownMenu = new DropDownMenu();
        for ( final Widget _item : widgetList ) {
            dropDownMenu.add( _item );
        }
        buttonGroup.add( dropdownButton );
        buttonGroup.add( dropDownMenu );
        return buttonGroup;
    }

    private void scheduleResize() {
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResize();
            }
        } );
    }

    /**
     * Returns the toggle button, that can be used to trigger maximizing and unmaximizing
     * of the panel containing this list bar. Make the button visible by calling {@link Widget#setVisible(boolean)}
     * and set its maximize and unmaximize actions with {@link MaximizeToggleButton#setMaximizeCommand(Command)} and
     * {@link MaximizeToggleButton#setUnmaximizeCommand(Command)}.
     */
    @Override
    public MaximizeToggleButtonPresenter getMaximizeButton() {
        return maximizeButtonPresenter;
    }

    @Override
    public boolean isDndEnabled() {
        return this.titleDropDown.isDndEnabled();
    }

    @Override
    public void enableClosePart() {
        closeButton.setVisible( true );
    }

    @Override
    public void disableClosePart() {
        closeButton.setVisible( false );
    }
}

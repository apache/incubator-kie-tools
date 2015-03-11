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

import static com.github.gwtbootstrap.client.ui.resources.ButtonSize.*;
import static com.google.gwt.dom.client.Style.Display.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCResolutionException;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.util.Layouts;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.DragArea;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.EnabledStateChangeListener;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuItemCommand;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.DropdownButton;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The drop-down list that appears when the user clicks the chooser button in the header of a
 * {@link MultiListWorkbenchPanelView}.
 */
@Dependent
public class ListBarWidget
extends ResizeComposite implements MultiPartWidget {

    /**
     * When a part is added to the list bar, a special title widget is created for it. This title widget is draggable.
     * To promote testability, the draggable title widget is given a predictable debug ID of the form
     * {@code DEBUG_ID_PREFIX + DEBUG_TITLE_PREFIX + partName}.
     * <p>
     * Note that debug IDs are only assigned when the app inherits the GWT Debug module. See
     * {@link Widget#ensureDebugId(com.google.gwt.dom.client.Element, String)} for details.
     */
    public static final String DEBUG_TITLE_PREFIX = "ListBar-title-";

    interface ListBarWidgetBinder
    extends
    UiBinder<ResizeFocusPanel, ListBarWidget> {

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
    SimplePanel title;

    @UiField
    Button contextDisplay;

    @UiField
    FlowPanel header;

    @UiField
    FlowPanel contextMenu;

    @UiField
    Button closeButton;

    @UiField
    DropdownButton dropdownCaret;

    @UiField
    ButtonGroup dropdownCaretContainer;

    @UiField
    ButtonGroup closeButtonContainer;

    @UiField
    MaximizeToggleButton maximizeButton;

    @UiField
    FlowPanel content;

    @UiField
    FlowPanel menuArea;

    PartChooserList partChooserList = null;

    WorkbenchPanelPresenter presenter;

    private WorkbenchDragAndDropManager dndManager;

    private final Map<PartDefinition, FlowPanel> partContentView = new HashMap<PartDefinition, FlowPanel>();
    private final Map<PartDefinition, Widget> partTitle = new HashMap<PartDefinition, Widget>();
    LinkedHashSet<PartDefinition> parts = new LinkedHashSet<PartDefinition>();

    boolean isMultiPart = true;
    boolean isDndEnabled = true;
    Pair<PartDefinition, FlowPanel> currentPart;

    @PostConstruct
    void postConstruct() {
        initWidget( uiBinder.createAndBindUi( this ) );
        maximizeButton.setVisible( false );
        setup( true, true );
        scheduleResize();
    }

    public void setup( boolean isMultiPart,
                       boolean isDndEnabled ) {
        this.isMultiPart = isMultiPart;
        this.isDndEnabled = isDndEnabled;
        this.menuArea.setVisible( false );

        if ( isMultiPart ) {
            closeButton.setTitle( WorkbenchConstants.INSTANCE.closePanel() );
            closeButton.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    if ( currentPart != null ) {
                        panelManager.closePart( currentPart.getK1() );
                    }
                }
            } );
        } else {
            dropdownCaretContainer.setVisible( false );
            closeButtonContainer.setVisible( false );
        }

        container.addFocusHandler( new FocusHandler() {
            @Override
            public void onFocus( FocusEvent event ) {
                if ( currentPart != null && currentPart.getK1() != null ) {
                    selectPart( currentPart.getK1() );
                }
            }
        } );

        if ( isPropertyListbarContextDisable() ) {
            contextDisplay.removeFromParent();
        }

        content.getElement().getStyle().setPosition( Position.RELATIVE );
        content.getElement().getStyle().setTop( 0.0, Unit.PX );
        content.getElement().getStyle().setLeft( 0.0, Unit.PX );
        content.getElement().getStyle().setWidth( 100.0, Unit.PCT );
        // height is calculated and set in onResize()
    }

    boolean isPropertyListbarContextDisable() {
        if ( optionalListBarPrefs.isUnsatisfied() ) {
            return true;
        }

        // as of Errai 3.0.4.Final, Instance.isUnsatisfied() always returns false. The try-catch is a necessary safety net.
        try {
            return optionalListBarPrefs.get().isContextEnabled();
        } catch ( IOCResolutionException e ) {
            return true;
        }
    }

    public void enableDnd() {
        this.isDndEnabled = true;
    }

    public void setExpanderCommand( final Command command ) {
        if ( !isPropertyListbarContextDisable() ) {
            contextDisplay.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    command.execute();
                }
            } );
        }
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
        if ( partChooserList != null ) {
            partChooserList.clear();
        }
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
        Layouts.setToFillParent( panel );
        panel.add( view );
        content.add( panel );

        // IMPORTANT! if you change what goes in this map, update the remove(PartDefinition) method
        partContentView.put( partDefinition, panel );

        final Widget title = buildTitle( view.getPresenter().getTitle() );
        partTitle.put( partDefinition, title );
        title.ensureDebugId( DEBUG_TITLE_PREFIX + view.getPresenter().getTitle() );

        if ( isDndEnabled ) {
            dndManager.makeDraggable( view, title );
        }

        scheduleResize();
    }

    private void updateBreadcrumb( final PartDefinition partDefinition ) {
        this.title.clear();

        final Widget title = partTitle.get( partDefinition );
        this.title.add( title );
    }

    private Widget buildTitle( final String title ) {
        final SpanElement spanElement = Document.get().createSpanElement();
        spanElement.getStyle().setWhiteSpace( Style.WhiteSpace.NOWRAP );
        spanElement.getStyle().setOverflow( Style.Overflow.HIDDEN );
        spanElement.getStyle().setTextOverflow( Style.TextOverflow.ELLIPSIS );
        spanElement.getStyle().setDisplay( BLOCK );
        spanElement.setInnerText( title.replaceAll( " ", "\u00a0" ) );

        return new DragArea() {{
            add( spanElement );
        }};
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        final Widget _title = buildTitle( title );
        partTitle.put( part, _title );
        if ( isDndEnabled ) {
            dndManager.makeDraggable( partContentView.get( part ), _title );
        }
        setupDropdown();
        if ( currentPart != null && currentPart.getK1().equals( part ) ) {
            updateBreadcrumb( part );
        }
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
            currentPart.getK2().getElement().getStyle().setDisplay( NONE );
        }

        currentPart = Pair.newPair( part, partContentView.get( part ) );
        currentPart.getK2().getElement().getStyle().setDisplay( BLOCK );
        updateBreadcrumb( part );
        parts.remove( currentPart.getK1() );

        setupDropdown();
        setupContextMenu();

        scheduleResize();

        SelectionEvent.fire( ListBarWidget.this, part );

        return true;
    }

    private void setupDropdown() {
        if ( isMultiPart ) {
            dropdownCaret.setRightDropdown( true );
            dropdownCaret.clear();
            partChooserList = new PartChooserList();
            dropdownCaret.add( partChooserList );
        } else {
            dropdownCaretContainer.setVisible( false );
        }
    }

    private void setupContextMenu() {
        contextMenu.clear();
        final WorkbenchPartPresenter.View part = (WorkbenchPartPresenter.View) currentPart.getK2().getWidget( 0 );
        if ( part.getPresenter().getMenus() != null && part.getPresenter().getMenus().getItems().size() > 0 ) {
            for ( final MenuItem menuItem : part.getPresenter().getMenus().getItems() ) {
                final Widget result = makeItem( menuItem, true );
                if ( result != null ) {
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add( result );
                    contextMenu.add( bg );
                }
            }
        }
    }

    @Override
    public boolean remove( final PartDefinition part ) {
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
        partTitle.remove( part );
        setupDropdown();

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

        // need explicit resize here because height: 100% in CSS makes the panel too tall
        int contentHeight = getOffsetHeight() - header.getOffsetHeight();

        if ( contentHeight < 0 ) {
            // occasionally (like 1 in 20 times) the panel has 0px height when we get the onResize() call
            // this is a temporary workaround until we figure it out
            content.getElement().getStyle().setHeight( 100, Unit.PCT );
        } else {
            content.getElement().getStyle().setHeight( contentHeight, Unit.PX );
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
        if ( partChooserList != null ) {
            partChooserList.onResize();
        }
    }

    private Widget makeItem( final MenuItem item,
                             boolean isRoot ) {
        if ( !authzManager.authorize( item, identity ) ) {
            return null;
        }

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

                gwtItem = new DropdownButton( groups.getCaption() ) {{
                    setSize( MINI );
                    for ( final Widget _item : widgetList ) {
                        add( _item );
                    }
                }};
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

                gwtItem = new Dropdown( groups.getCaption() ) {{
                    for ( final Widget widget : widgetList ) {
                        add( widget );
                    }
                }};
            }

            return gwtItem;
        } else if ( item instanceof MenuCustom ) {
            final Object result = ( (MenuCustom) item ).build();
            if ( result instanceof Widget ) {
                return (Widget) result;
            }
        }

        return null;
    }

    /**
     * This is the list that appears when you click the down-arrow button in the header (dropdownCaret). It lists all
     * the available parts. Clicking on a list item selects its associated part, making it visible, and hiding all other
     * parts.
     */
    class PartChooserList extends ResizeComposite {

        final ResizeFlowPanel panel = new ResizeFlowPanel();

        PartChooserList() {
            initWidget( panel );
            if ( currentPart != null ) {
                final String ctitle = ( (WorkbenchPartPresenter.View) partContentView.get( currentPart.getK1() ).getWidget( 0 ) ).getPresenter().getTitle();
                panel.add( new NavLink( ctitle ) );

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
            }
            onResize();
        }

        @Override
        public void onResize() {
            int contentAbsoluteRight = content.getAbsoluteLeft() + content.getOffsetWidth();
            int caretAbsoluteRight = dropdownCaretContainer.getAbsoluteLeft() + dropdownCaretContainer.getOffsetWidth();
            int width = content.getOffsetWidth() - ( contentAbsoluteRight - caretAbsoluteRight );
            if ( width > 0 ) {
                setWidth( width + "px" );
            }
        }

        public void clear() {
            panel.clear();
        }
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
     * Returns the toggle button, which is initially hidden, that can be used to trigger maximizing and unmaximizing
     * of the panel containing this list bar. Make the button visible by calling {@link Widget#setVisible(boolean)}
     * and set its maximize and unmaximize actions with {@link MaximizeToggleButton#setMaximizeCommand(Command)} and
     * {@link MaximizeToggleButton#setUnmaximizeCommand(Command)}.
     */
    public MaximizeToggleButton getMaximizeButton() {
        return maximizeButton;
    }

    public boolean isDndEnabled() {
        return isDndEnabled;
    }

    public boolean isMultiPart() {
        return isMultiPart;
    }
}

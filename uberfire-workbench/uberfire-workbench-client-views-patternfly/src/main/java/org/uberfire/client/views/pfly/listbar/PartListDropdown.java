/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.pfly.listbar;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.Span;
import org.gwtbootstrap3.client.ui.html.Text;
import org.uberfire.client.views.pfly.dropdown.ListDropdown;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.dnd.DragArea;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.workbench.model.PartDefinition;

/**
 * Created by Cristiano Nicolai.
 */
public class PartListDropdown extends ListDropdown implements HasSelectionHandlers<PartDefinition>, HasCloseHandlers<PartDefinition> {

    public static final String DEBUG_TITLE_PREFIX = "PartList-title-";

    private Map<PartDefinition, Widget> partOptions = new HashMap<PartDefinition, Widget>();
    private Map<PartDefinition, Widget> partTitles = new HashMap<PartDefinition, Widget>();
    private Map<PartDefinition, WorkbenchPartPresenter.View> partView = new HashMap<PartDefinition, WorkbenchPartPresenter.View>();
    private HandlerRegistration noDragHandler;
    private WorkbenchDragAndDropManager dndManager;
    private boolean dndEnabled = true;

    public void setDndManager( final WorkbenchDragAndDropManager dndManager ) {
        this.dndManager = dndManager;
    }

    public void addPart( final WorkbenchPartPresenter.View view ) {
        final PartDefinition part = view.getPresenter().getDefinition();
        partView.put( part, view );
        final Widget title = buildTitleWidget( view.getPresenter().getTitle(), view.getPresenter().getTitleDecoration() );
        partTitles.put( part, title );
        final Widget option = buildTitleDropdownMenuItem( view.getPresenter().getTitle(), part );
        partOptions.put( part, option );

        this.add( option );
        if ( partTitles.size() == 1 ) {
            selectPart( part );
        }
    }

    public void removePart( final PartDefinition part ) {
        partTitles.remove( part );
        partView.remove( part );
        final Widget option = partOptions.remove( part );
        this.remove( option );
    }

    public void selectPart( final PartDefinition part ) {
        final Widget title = partTitles.get( part );
        this.setText( title );
        for ( final Map.Entry<PartDefinition, Widget> entry : partOptions.entrySet() ) {
            if ( entry.getKey().equals( part ) ) {
                entry.getValue().addStyleName( "uf-part-list-dropdown-selected" );
            } else {
                entry.getValue().removeStyleName( "uf-part-list-dropdown-selected" );
            }
        }
        makeDraggable( title, partView.get( part ) );
    }

    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        final Widget titleWidget = buildTitleWidget( title, titleDecoration );
        partTitles.put( part, titleWidget );
    }

    private Widget buildTitleWidget( final String title, final IsWidget titleDecoration ) {
        final String titleWidget = ( titleDecoration instanceof Image ) ? titleDecoration.toString() : "";
        final Text text = new Text( SafeHtmlUtils.htmlEscape( titleWidget + " " + title ) );
        final DragArea dragArea = new DragArea( text );
        dragArea.ensureDebugId( DEBUG_TITLE_PREFIX + title );
        dragArea.addStyleName( Styles.PULL_LEFT );
        return dragArea;
    }

    private Widget buildTitleDropdownMenuItem( final String title, final PartDefinition part ) {
        final ListItem li = new ListItem();
        li.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                SelectionEvent.fire( PartListDropdown.this, part );
            }
        }, ClickEvent.getType() );
        final Span span = new Span();
        span.add( new Text( title ) );
        final Icon icon = new Icon( IconType.TIMES );
        icon.addDomHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                CloseEvent.fire( PartListDropdown.this, part );
                //Keep dropdown menu open when removing parts
                boolean openMenu = PartListDropdown.this.getElement().hasClassName( "open" );
                if ( openMenu ) {
                    Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            PartListDropdown.this.getElement().addClassName( "open" );
                        }
                    } );
                }
            }
        }, ClickEvent.getType() );
        icon.addStyleName( Styles.PULL_RIGHT );
        final Anchor anchor = new Anchor();
        anchor.add( icon );
        anchor.add( span );
        li.add( anchor );
        return li;
    }

    public void enableDragAndDrop() {
        this.dndEnabled = true;
        if ( noDragHandler != null ) {
            noDragHandler.removeHandler();
            noDragHandler = null;
        }

        for ( final Map.Entry<PartDefinition, Widget> entry : partTitles.entrySet() ) {
            final Widget title = entry.getValue();
            final WorkbenchPartPresenter.View view = partView.get( entry.getKey() );
            makeDraggable( title, view );
        }
    }

    private void makeDraggable( final Widget title, final WorkbenchPartPresenter.View view ) {
        if ( this.dndManager == null || this.dndEnabled == false ) {
            return;
        }
        dndManager.makeDraggable( view, title );
    }

    public void disableDragAndDrop() {
        this.dndEnabled = false;
        // Prevent from dragging title element around
        if ( noDragHandler == null ) {
            noDragHandler = this.addDomHandler( new DragStartHandler() {
                @Override
                public void onDragStart( final DragStartEvent event ) {
                    event.preventDefault();
                }
            }, DragStartEvent.getType() );
        }
    }

    public boolean isDndEnabled() {
        return dndEnabled;
    }

    @Override
    public void clear() {
        super.clear();
        partOptions.clear();
        partTitles.clear();
        partView.clear();
    }

    @Override
    public HandlerRegistration addSelectionHandler( final SelectionHandler<PartDefinition> handler ) {
        return super.addHandler( handler, SelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<PartDefinition> handler ) {
        return super.addHandler( handler, CloseEvent.getType() );
    }
}

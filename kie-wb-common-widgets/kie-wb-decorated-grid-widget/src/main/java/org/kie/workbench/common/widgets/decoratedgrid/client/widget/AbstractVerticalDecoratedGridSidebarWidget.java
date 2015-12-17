/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.PasteRowsEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.ToggleMergingEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicData;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.RowMapper;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.RowGroupingChangeEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;

import java.util.ArrayList;

/**
 * A sidebar for a VericalDecisionTable. This provides a vertical list of
 * controls to add and remove the associated row from the DecisionTable.
 */
public abstract class AbstractVerticalDecoratedGridSidebarWidget<M, T> extends AbstractDecoratedGridSidebarWidget<M, T> {

    /**
     * Widget to render selectors beside rows. Two selectors are provided per
     * row: (1) A "add new row (above selected)" and (2) "delete row".
     */
    private class VerticalSelectorWidget extends CellPanel {

        // Widgets (selectors) created (so they can be removed later)
        private ArrayList<Widget> widgets = new ArrayList<Widget>();

        private VerticalSelectorWidget( EventBus eventBus ) {
            getBody().getParentElement().<TableElement>cast().setCellSpacing( 0 );
            getBody().getParentElement().<TableElement>cast().setCellPadding( 0 );
            sinkEvents( Event.getTypeInt( "click" ) );
        }

        // Append a row to the end
        public void appendRow() {

            //UI Components
            Element tre = DOM.createTR();
            Element tce = DOM.createTD();
            tre.setClassName( getRowStyle( widgets.size() ) );
            tce.getStyle().setHeight( resources.rowHeight(),
                                      Unit.PX );
            tce.addClassName( resources.selectorCell() );
            DOM.appendChild( getBody(),
                             tre );
            tre.appendChild( tce );

            Widget widget = makeRowWidget();
            add( widget,
                 tce );

            widgets.add( widget );
            fixStyles( widgets.size() );
        }

        // Insert a new row before the given index
        public void insertRowBefore( int index ) {

            //UI Components
            Element tre = DOM.createTR();
            Element tce = DOM.createTD();
            tre.setClassName( getRowStyle( widgets.size() ) );
            tce.getStyle().setHeight( resources.rowHeight(),
                                      Unit.PX );
            tce.addClassName( resources.selectorCell() );
            DOM.insertChild( getBody(),
                             tre,
                             index );
            tre.appendChild( tce );

            Widget widget = makeRowWidget();
            add( widget,
                 tce );

            widgets.add( index,
                         widget );
            fixStyles( index );
        }

        // Delete a row at the given index
        public void deleteRow( int index ) {

            //UI Components
            Widget widget = widgets.get( index );
            remove( widget );
            getBody().<TableSectionElement>cast().deleteRow( index );

            widgets.remove( index );
            fixStyles( index );
        }

        // Redraw sidebar with the given number of rows
        private void redraw() {
            //Remove existing
            final int rowsToRemove = widgets.size();
            for ( int iRow = 0; iRow < rowsToRemove; iRow++ ) {
                deleteRow( 0 );
            }
            //Add selector for each row
            for ( int iRow = 0; iRow < data.size(); iRow++ ) {
                appendRow();
            }

        }

        // Row styles need to be re-applied after inserting and deleting rows
        private void fixStyles( int iRow ) {
            while ( iRow < getBody().getChildCount() ) {
                TableRowElement tre = getBody().getChild( iRow ).<TableRowElement>cast();
                tre.setClassName( getRowStyle( iRow ) );
                iRow++;
            }
        }

        // Get style applicable to row
        private String getRowStyle( int iRow ) {
            boolean isEven = iRow % 2 == 0;
            String trClasses = isEven ? resources.cellTableEvenRow() : resources.cellTableOddRow();
            return trClasses;
        }

        // Make the selector Widget
        private Widget makeRowWidget() {

            final HorizontalPanel hp = new HorizontalPanel();
            hp.setVerticalAlignment( VerticalPanel.ALIGN_MIDDLE );
            hp.setHorizontalAlignment( HorizontalPanel.ALIGN_CENTER );
            hp.setWidth( resources.sidebarWidth() + "px" );

            //Add row icon
            if ( !isReadOnly ) {
                FocusPanel fp = new FocusPanel();
                fp.setHeight( "100%" );
                fp.setWidth( "50%" );
                fp.add( new Image( resources.selectorAddIcon() ) );
                if ( !isReadOnly ) {
                    fp.addClickHandler( new ClickHandler() {

                        public void onClick( ClickEvent event ) {
                            //Raise an event to add row
                            int index = rowMapper.mapToAbsoluteRow( widgets.indexOf( hp ) );
                            InsertRowEvent ire = new InsertRowEvent( index );
                            eventBus.fireEvent( ire );
                        }

                    } );
                }
                hp.add( fp );
            } else {
                SimplePanel sp = new SimplePanel();
                sp.setHeight( "100%" );
                sp.setWidth( "50%" );
                sp.add( new Image( resources.selectorAddIcon() ) );
                hp.add( sp );
            }

            //Delete row icon
            if ( !isReadOnly ) {
                FocusPanel fp = new FocusPanel();
                fp.setHeight( "100%" );
                fp.setWidth( "50%" );
                fp.add( new Image( resources.selectorDeleteIcon() ) );
                fp.addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        //Raise an event to delete row
                        int index = rowMapper.mapToAbsoluteRow( widgets.indexOf( hp ) );
                        DeleteRowEvent ire = new DeleteRowEvent( index );
                        eventBus.fireEvent( ire );
                    }

                } );
                hp.add( fp );
            } else {
                SimplePanel sp = new SimplePanel();
                sp.setHeight( "100%" );
                sp.setWidth( "50%" );
                sp.add( new Image( resources.selectorDeleteIcon() ) );
                hp.add( sp );
            }

            //Add a context menu to copy\paste rows
            if ( !isReadOnly ) {
                hp.addDomHandler( new ContextMenuHandler() {

                    public void onContextMenu( ContextMenuEvent event ) {
                        //Prevent the default context menu
                        event.preventDefault();
                        event.stopPropagation();

                        //Set source row index and show
                        int clientX = event.getNativeEvent().getClientX();
                        int clientY = event.getNativeEvent().getClientY();
                        showContextMenu( widgets.indexOf( hp ),
                                         clientX,
                                         clientY );
                    }

                },
                                  ContextMenuEvent.getType() );
            }

            return hp;
        }

    }

    /**
     * Simple spacer to ensure scrollable part of sidebar aligns with grid.
     */
    private class VerticalSideBarSpacerWidget extends CellPanel
            implements
            ToggleMergingEvent.Handler {

        private Image icon = new Image();
        private Element tre = DOM.createTR();
        private Element tce = DOM.createTD();
        private Element outerDiv = DOM.createDiv();
        private Element innerDiv = DOM.createDiv();

        private boolean isMerged = false;

        public void setHeight( int height ) {
            super.setHeight( height + "px" );
        }

        private void setPadding( int padding ) {
            getBody().getParentElement().<TableElement>cast().setCellPadding( 0 );
        }

        private VerticalSideBarSpacerWidget() {

            // Create DOM structure. The spacer is constructed of a single cell HTML table 
            // containing two nested DIVs. These DIVs are used to control the row height 
            // across all browsers and centre the toggle merging icon.
            setSpacing( 0 );
            setPadding( 0 );

            setIconImage( isMerged );

            tce.addClassName( resources.selectorSpacer() );
            innerDiv.addClassName( resources.selectorSpacerInnerDiv() );
            outerDiv.addClassName( resources.selectorSpacerOuterDiv() );

            tre.appendChild( tce );
            tce.appendChild( outerDiv );
            outerDiv.appendChild( innerDiv );
            innerDiv.appendChild( icon.getElement() );
            getBody().appendChild( tre );

            //This could be moved to CSS if we always knew the icon size
            innerDiv.getStyle().setHeight( icon.getHeight(),
                                           Unit.PX );
            innerDiv.getStyle().setMarginTop( ( icon.getHeight() / 2 ) * -1,
                                              Unit.PX );
            innerDiv.getStyle().setWidth( icon.getWidth(),
                                          Unit.PX );
            innerDiv.getStyle().setMarginLeft( ( icon.getWidth() / 2 ) * -1,
                                               Unit.PX );

            // Setup event handling
            DOM.setEventListener( icon.getElement(),
                                  new EventListener() {

                                      public void onBrowserEvent( Event event ) {
                                          if ( event.getType().equals( "click" ) ) {
                                              //Raise event to toggle merging
                                              ToggleMergingEvent tme = new ToggleMergingEvent( !isMerged );
                                              eventBus.fireEvent( tme );
                                          }
                                      }

                                  } );

            DOM.sinkEvents( icon.getElement(),
                            Event.getTypeInt( "click" ) );
            eventBus.addHandler( ToggleMergingEvent.TYPE,
                                 this );
        }

        public void onToggleMerging( ToggleMergingEvent event ) {
            isMerged = event.isMerged();
            setIconImage( isMerged );
        }

        // Set the icon's image accordingly
        private void setIconImage( boolean isMerged ) {
            if ( isMerged ) {
                icon.setResource( resources.toggleUnmergeIcon() );
            } else {
                icon.setResource( resources.toggleMergeIcon() );
            }
        }
    }

    // UI Elements
    private ScrollPanel scrollPanel;
    private VerticalSelectorWidget selectors;
    private VerticalSideBarSpacerWidget spacer = new VerticalSideBarSpacerWidget();

    //Underlying model
    protected DynamicData data;
    protected RowMapper rowMapper;

    /**
     * Construct a "Sidebar" for the provided DecisionTable
     * @param resources
     * @param isReadOnly
     * @param eventBus
     */
    public AbstractVerticalDecoratedGridSidebarWidget( ResourcesProvider<T> resources,
                                                       boolean isReadOnly,
                                                       EventBus eventBus ) {
        // Argument validation performed in the superclass constructor
        super( resources,
               isReadOnly,
               eventBus );

        // Construct the Widget
        scrollPanel = new ScrollPanel();
        VerticalPanel container = new VerticalPanel();
        selectors = new VerticalSelectorWidget( eventBus );

        container.add( spacer );
        container.add( scrollPanel );
        scrollPanel.add( selectors );

        // We don't want scroll bars on the Sidebar
        scrollPanel.getElement().getStyle().setOverflow( Overflow.HIDDEN );

        initWidget( container );

    }

    @Override
    void resizeSidebar( int height ) {
        if ( height < 0 ) {
            throw new IllegalArgumentException( "height cannot be less than zero" );
        }
        spacer.setHeight( height );
    }

    @Override
    public void setHeight( String height ) {
        if ( height == null ) {
            throw new IllegalArgumentException( "height cannot be null" );
        }
        this.scrollPanel.setHeight( height );
    }

    @Override
    public void setScrollPosition( int position ) {
        if ( position < 0 ) {
            throw new IllegalArgumentException( "position cannot be less than zero" );
        }
        this.scrollPanel.setVerticalScrollPosition( position );
    }

    @Override
    protected void redraw() {
        selectors.redraw();
    }

    public void onDeleteRow( DeleteRowEvent event ) {
        int index = rowMapper.mapToMergedRow( event.getIndex() );
        selectors.deleteRow( index );
    }

    public void onInsertRow( InsertRowEvent event ) {
        int index = rowMapper.mapToMergedRow( event.getIndex() );
        selectors.insertRowBefore( index );
    }

    public void onAppendRow( AppendRowEvent event ) {
        selectors.appendRow();
    }

    public void onPasteRows( PasteRowsEvent event ) {
        int iRow = rowMapper.mapToMergedRow( event.getTargetRowIndex() );
        selectors.insertRowBefore( iRow );
    }

    public void onRowGroupingChange( RowGroupingChangeEvent event ) {
        selectors.redraw();
    }

}

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

import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.i18n.Constants;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.DynamicDataRow;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateSelectedCellsEvent;

/**
 * A Vertical implementation of MergableGridWidget, that renders columns as erm,
 * columns and rows as rows. Supports merging of cells between rows.
 */
public abstract class AbstractVerticalMergableGridWidget<M, T> extends AbstractMergableGridWidget<M, T> {

    //Deferred binding creates an appropriate class depending on browser
    private CellHeightCalculatorImpl cellHeightCalculator = GWT.create( CellHeightCalculatorImpl.class );

    public AbstractVerticalMergableGridWidget( ResourcesProvider<T> resources,
                                               AbstractCellFactory<T> cellFactory,
                                               AbstractCellValueFactory<T, ?> cellValueFactory,
                                               CellTableDropDownDataValueMapProvider dropDownManager,
                                               boolean isReadOnly,
                                               EventBus eventBus ) {
        super( resources,
               cellFactory,
               cellValueFactory,
               dropDownManager,
               isReadOnly,
               eventBus );
    }

    @Override
    public void onBrowserEvent( Event event ) {

        String eventType = event.getType();

        // Get the event target
        EventTarget eventTarget = event.getEventTarget();
        if ( !Element.is( eventTarget ) ) {
            return;
        }
        Element target = event.getEventTarget().cast();

        //Check whether "group" widget has been clicked
        boolean bGroupWidgetClick = isGroupWidgetClicked( event,
                                                          target );

        // Find the cell where the event occurred.
        TableCellElement eventTableCell = findNearestParentCell( target );
        if ( eventTableCell == null ) {
            return;
        }
        int htmlCol = eventTableCell.getCellIndex();

        Element trElem = eventTableCell.getParentElement();
        if ( trElem == null ) {
            return;
        }
        TableRowElement tr = TableRowElement.as( trElem );
        int htmlRow = tr.getSectionRowIndex();

        // Convert HTML coordinates to physical coordinates
        CellValue<?> htmlCell = data.get( htmlRow ).get( htmlCol );
        Coordinate eventPhysicalCoordinate = htmlCell.getPhysicalCoordinate();
        CellValue<?> eventPhysicalCell = data.get( eventPhysicalCoordinate.getRow() ).get( eventPhysicalCoordinate.getCol() );

        //Event handlers
        if ( eventType.equals( "mousedown" ) ) {
            handleMousedownEvent( event,
                                  eventPhysicalCoordinate,
                                  bGroupWidgetClick );
            return;

        } else if ( eventType.equals( "mousemove" ) ) {
            handleMousemoveEvent( event,
                                  eventPhysicalCoordinate );
            return;

        } else if ( eventType.equals( "mouseup" ) ) {
            handleMouseupEvent( event,
                                eventPhysicalCoordinate );
            return;

        } else if ( eventType.equals( "keydown" ) ) {
            handleKeyboardNavigationEvent( event );

            if ( event.getKeyCode() == KeyCodes.KEY_ENTER ) {

                // Enter key is a special case; as the selected cell needs to be
                // sent events and not the cell that GWT deemed the target for
                // events.
                switch ( rangeDirection ) {
                    case UP:
                        eventPhysicalCell = selections.first();
                        break;

                    case DOWN:
                        eventPhysicalCell = selections.last();
                        break;
                }
                eventPhysicalCoordinate = eventPhysicalCell.getCoordinate();
                eventTableCell = tbody.getRows().getItem( eventPhysicalCell.getHtmlCoordinate().getRow() ).getCells().getItem( eventPhysicalCell.getHtmlCoordinate().getCol() );
            }
        }

        // Pass event and physical cell to Cell Widget for handling
        Cell<CellValue<? extends Comparable<?>>> cellWidget = columns.get( eventPhysicalCoordinate.getCol() ).getCell();

        // Implementations of AbstractCell aren't forced to initialise consumed events
        Set<String> consumedEvents = cellWidget.getConsumedEvents();
        if ( consumedEvents != null && consumedEvents.contains( eventType ) ) {
            Context context = new Context( eventPhysicalCoordinate.getRow(),
                                           eventPhysicalCoordinate.getCol(),
                                           eventPhysicalCoordinate );

            //The element containing the cell's HTML is nested inside two DIVs
            Element parent = eventTableCell.getFirstChildElement().getFirstChildElement();
            cellWidget.onBrowserEvent( context,
                                       parent,
                                       eventPhysicalCell,
                                       event,
                                       null );
        }
    }

    @Override
    protected void redraw() {

        TableSectionElement nbody = Document.get().createTBodyElement();

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {

            DynamicDataRow rowData = data.get( iRow );

            TableRowElement tre = Document.get().createTRElement();
            tre.setClassName( getRowStyle( iRow ) );
            populateTableRowElement( tre,
                                     rowData );
            nbody.appendChild( tre );
        }

        // Update table to DOM
        table.replaceChild( nbody,
                            tbody );
        tbody = nbody;

    }

    @Override
    void redrawColumns( int startRedrawIndex,
                        int endRedrawIndex ) {
        if ( startRedrawIndex < 0 ) {
            throw new IllegalArgumentException( "startRedrawIndex cannot be less than zero." );
        }
        if ( startRedrawIndex > columns.size() ) {
            throw new IllegalArgumentException( "startRedrawIndex cannot be greater than the number of defined columns." );
        }
        if ( endRedrawIndex < 0 ) {
            throw new IllegalArgumentException( "endRedrawIndex cannot be less than zero." );
        }
        if ( endRedrawIndex > columns.size() ) {
            throw new IllegalArgumentException( "endRedrawIndex cannot be greater than the number of defined columns." );
        }
        if ( startRedrawIndex > endRedrawIndex ) {
            throw new IllegalArgumentException( "startRedrawIndex cannot be greater than endRedrawIndex." );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            TableRowElement tre = tbody.getRows().getItem( iRow );
            DynamicDataRow rowData = data.get( iRow );
            redrawTableRowElement( rowData,
                                   tre,
                                   startRedrawIndex,
                                   endRedrawIndex );
        }
    }

    @Override
    public void resizeColumn( DynamicColumn<?> col,
                              int width ) {
        if ( col == null ) {
            throw new IllegalArgumentException( "col cannot be null" );
        }
        if ( width < 0 ) {
            throw new IllegalArgumentException( "width cannot be less than zero" );
        }

        col.setWidth( width );
        int iCol = col.getColumnIndex();
        for ( DynamicDataRow row : data ) {
            CellValue<? extends Comparable<?>> cell = row
                    .get( iCol );
            Coordinate c = cell.getHtmlCoordinate();
            TableRowElement tre = tbody.getRows().getItem( c.getRow() );
            TableCellElement tce = tre.getCells().getItem( c.getCol() );
            DivElement div = tce.getFirstChild().<DivElement>cast();
            DivElement divText = tce.getFirstChild().getFirstChild().<DivElement>cast();

            // Set widths
            tce.getStyle().setWidth( width,
                                     Unit.PX );
            div.getStyle().setWidth( width,
                                     Unit.PX );
            divText.getStyle().setWidth( width,
                                         Unit.PX );
        }

    }

    // Find the cell that contains the element. Note that the TD element is not
    // the parent. The parent is the div inside the TD cell.
    private TableCellElement findNearestParentCell( Element elem ) {
        while ( ( elem != null )
                && ( elem != table ) ) {
            String tagName = elem.getTagName();
            if ( "td".equalsIgnoreCase( tagName )
                    || "th".equalsIgnoreCase( tagName ) ) {
                return elem.cast();
            }
            elem = elem.getParentElement();
        }
        return null;
    }

    // Row styles need to be re-applied after inserting and deleting rows
    private void fixRowStyles( int iRow ) {
        while ( iRow < tbody.getChildCount() ) {
            Element e = Element.as( tbody.getChild( iRow ) );
            TableRowElement tre = TableRowElement.as( e );
            tre.setClassName( getRowStyle( iRow ) );
            iRow++;
        }
    }

    // Get style applicable to row
    private String getRowStyle( int iRow ) {
        String evenRowStyle = resources.cellTableEvenRow();
        String oddRowStyle = resources.cellTableOddRow();
        boolean isEven = iRow % 2 == 0;
        String trClasses = isEven ? evenRowStyle : oddRowStyle;
        return trClasses;
    }

    //Handle "Key Down" events relating to keyboard navigation
    private void handleKeyboardNavigationEvent( Event event ) {
        if ( event.getKeyCode() == KeyCodes.KEY_DELETE && !isReadOnly ) {
            for ( CellValue<?> cell : selections ) {
                cell.removeState( CellValue.CellState.OTHERWISE );
            }
            eventBus.fireEvent( new UpdateSelectedCellsEvent( null ) );
            return;

        } else if ( event.getKeyCode() == KeyCodes.KEY_RIGHT
                || ( event.getKeyCode() == KeyCodes.KEY_TAB && !event.getShiftKey() ) ) {
            moveSelection( MOVE_DIRECTION.RIGHT );
            event.preventDefault();
            return;

        } else if ( event.getKeyCode() == KeyCodes.KEY_LEFT
                || ( event.getKeyCode() == KeyCodes.KEY_TAB && event.getShiftKey() ) ) {
            moveSelection( MOVE_DIRECTION.LEFT );
            event.preventDefault();
            return;

        } else if ( event.getKeyCode() == KeyCodes.KEY_UP ) {
            if ( event.getShiftKey() ) {
                extendSelection( MOVE_DIRECTION.UP );
            } else {
                moveSelection( MOVE_DIRECTION.UP );
            }
            event.preventDefault();
            return;

        } else if ( event.getKeyCode() == KeyCodes.KEY_DOWN ) {
            if ( event.getShiftKey() ) {
                extendSelection( MOVE_DIRECTION.DOWN );
            } else {
                moveSelection( MOVE_DIRECTION.DOWN );
            }
            event.preventDefault();
            return;

        }

    }

    //Handle "Mouse Down" events
    private void handleMousedownEvent( Event event,
                                       Coordinate eventCoordinate,
                                       boolean bGroupWidgetClick ) {
        if ( event.getButton() == NativeEvent.BUTTON_LEFT ) {

            if ( bGroupWidgetClick ) {

                groupCells( eventCoordinate );

            } else if ( event.getShiftKey() ) {

                // Shift-click range selection
                extendSelection( eventCoordinate );
                return;

            } else {

                //Start of potential mouse-drag select operation
                startSelecting( eventCoordinate );
                bDragOperationPrimed = true;
                return;
            }

        }
    }

    //Handle "Mouse Move" events
    private void handleMousemoveEvent( Event event,
                                       Coordinate eventCoordinate ) {
        if ( event.getButton() == NativeEvent.BUTTON_LEFT ) {

            if ( bDragOperationPrimed && !rangeOriginCell.equals( data.get( eventCoordinate ) ) ) {
                extendSelection( eventCoordinate );
                return;
            }

        }
    }

    //Handle "Mouse Up" events
    private void handleMouseupEvent( Event event,
                                     Coordinate eventCoordinate ) {
        bDragOperationPrimed = false;
    }

    // Build a TableCellElement
    @SuppressWarnings("rawtypes")
    private TableCellElement makeTableCellElement( int iCol,
                                                   DynamicDataRow rowData ) {

        TableCellElement tce = null;

        // Column to handle rendering
        DynamicColumn<T> column = columns.get( iCol );

        CellValue<? extends Comparable<?>> cellData = rowData.get( iCol );
        int rowSpan = cellData.getRowSpan();
        if ( rowSpan > 0 ) {

            // Use Elements rather than Templates as it's easier to set attributes that need to be dynamic
            tce = Document.get().createTDElement();
            DivElement div = Document.get().createDivElement();
            DivElement divText = Document.get().createDivElement();
            tce.addClassName( resources.cellTableCell() );
            tce.addClassName( resources.cellTableColumn( column.getModelColumn() ) );
            div.setClassName( resources.cellTableCellDiv() );
            divText.addClassName( resources.cellTableTextDiv() );

            // Set widths
            int colWidth = column.getWidth();
            div.getStyle().setWidth( colWidth,
                                     Unit.PX );
            divText.getStyle().setWidth( colWidth,
                                         Unit.PX );
            tce.getStyle().setWidth( colWidth,
                                     Unit.PX );

            // Set heights, TD includes border, DIV does not
            int divHeight = cellHeightCalculator.calculateHeight( rowSpan );
            div.getStyle().setHeight( divHeight,
                                      Unit.PX );
            tce.setRowSpan( rowSpan );

            //Styling depending upon state
            if ( cellData.isOtherwise() ) {
                tce.addClassName( resources.cellTableCellOtherwise() );
            } else {
                tce.removeClassName( resources.cellTableCellOtherwise() );
            }
            if ( cellData instanceof CellValue.GroupedCellValue ) {
                CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cellData;
                if ( gcv.hasMultipleValues() ) {
                    tce.addClassName( resources.cellTableCellMultipleValues() );
                }
            } else {
                tce.removeClassName( resources.cellTableCellMultipleValues() );
            }
            if ( cellData.isSelected() ) {
                tce.addClassName( resources.cellTableCellSelected() );
            } else {
                tce.removeClassName( resources.cellTableCellSelected() );
            }

            // Render the cell and set inner HTML
            SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
            if ( !cellData.isOtherwise() ) {
                Coordinate c = cellData.getCoordinate();
                Context context = new Context( c.getRow(),
                                               c.getCol(),
                                               c );
                column.render( context,
                               rowData,
                               cellBuilder );
            } else {
                cellBuilder.appendEscaped( "<otherwise>" );
            }
            divText.setInnerHTML( cellBuilder.toSafeHtml().asString() );

            // Construct the table
            tce.appendChild( div );
            div.appendChild( divText );
            tce.setTabIndex( 0 );

            //Add on "Grouping" widget, if applicable
            if ( rowSpan > 1 || cellData.isGrouped() ) {
                Element de = DOM.createDiv();
                DivElement divGroup = DivElement.as( de );
                divGroup.setTitle( Constants.INSTANCE.groupCells() );
                divGroup.addClassName( resources.cellTableGroupDiv() );
                if ( cellData.isGrouped() ) {
                    divGroup.setInnerHTML( selectorUngroupedCellsHtml );
                } else {
                    divGroup.setInnerHTML( selectorGroupedCellsHtml );
                }
                div.appendChild( divGroup );
            }

        }
        return tce;

    }

    // Populate the content of a TableRowElement. This is used to populate
    // new, empty, TableRowElements with complete rows for insertion into an
    // HTML table based upon visible columns
    private TableRowElement populateTableRowElement( TableRowElement tre,
                                                     DynamicDataRow rowData ) {

        tre.getStyle().setHeight( resources.rowHeight(),
                                  Unit.PX );
        for ( int iCol = 0; iCol < columns.size(); iCol++ ) {
            DynamicColumn<T> column = columns.get( iCol );
            if ( column.isVisible() ) {
                TableCellElement tce = makeTableCellElement( iCol,
                                                             rowData );
                if ( tce != null ) {
                    tre.appendChild( tce );
                }
            }
        }

        return tre;

    }

    // Redraw a row adding new cells if necessary. This is used to populate part
    // of a row from the given index onwards, when a new column has been
    // inserted. It is important the indexes on the underlying data have
    // been set correctly before calling as they are used to determine the
    // correct HTML element in which to render a cell.
    private void redrawTableRowElement( DynamicDataRow rowData,
                                        TableRowElement tre,
                                        int startColIndex,
                                        int endColIndex ) {

        for ( int iCol = startColIndex; iCol <= endColIndex; iCol++ ) {

            // Only redraw visible columns
            DynamicColumn<?> column = columns.get( iCol );
            if ( column.isVisible() ) {

                int maxColumnIndex = tre.getCells().getLength() - 1;
                int requiredColumnIndex = rowData.get( iCol ).getHtmlCoordinate().getCol();
                if ( requiredColumnIndex > maxColumnIndex ) {

                    // Make a new TD element
                    TableCellElement newCell = makeTableCellElement( iCol,
                                                                     rowData );
                    if ( newCell != null ) {
                        tre.appendChild( newCell );
                    }

                } else {

                    // Reuse an existing TD element
                    TableCellElement newCell = makeTableCellElement( iCol,
                                                                     rowData );
                    if ( newCell != null ) {
                        TableCellElement oldCell = tre.getCells().getItem( requiredColumnIndex );
                        tre.replaceChild( newCell,
                                          oldCell );
                    }
                }
            }
        }

    }

    @Override
    protected void createEmptyRowElement( int index ) {
        tbody.insertRow( index );
        fixRowStyles( index );
    }

    @Override
    protected void createRowElement( int index,
                                     DynamicDataRow rowData ) {
        TableRowElement tre = tbody.insertRow( index );
        populateTableRowElement( tre,
                                 rowData );
        fixRowStyles( index );
    }

    @Override
    protected void deleteRowElement( int index ) {
        Node tre = tbody.getChild( index );
        tbody.removeChild( tre );
    }

    @Override
    protected void redrawRows( int startRedrawIndex,
                               int endRedrawIndex ) {
        if ( startRedrawIndex < 0 ) {
            throw new IllegalArgumentException( "startRedrawIndex cannot be less than zero." );
        }
        if ( startRedrawIndex > data.size() ) {
            throw new IllegalArgumentException( "startRedrawIndex cannot be greater than the number of rows in the table." );
        }
        if ( endRedrawIndex < 0 ) {
            throw new IllegalArgumentException( "endRedrawIndex cannot be less than zero." );
        }
        if ( endRedrawIndex > data.size() ) {
            throw new IllegalArgumentException( "endRedrawIndex cannot be greater than the number of rows in the table." );
        }
        if ( startRedrawIndex > endRedrawIndex ) {
            throw new IllegalArgumentException( "startRedrawIndex cannot be greater than endRedrawIndex." );
        }

        //Redraw replacement rows
        for ( int iRow = startRedrawIndex; iRow <= endRedrawIndex; iRow++ ) {

            DynamicDataRow rowData = data.get( iRow );

            TableRowElement tre = Document.get().createTRElement();
            populateTableRowElement( tre,
                                     rowData );
            tbody.replaceChild( tre,
                                tbody.getChild( iRow ) );
        }

        fixRowStyles( startRedrawIndex );
    }

    @Override
    protected void removeRowElement( int index ) {
        if ( index < 0 ) {
            throw new IllegalArgumentException( "Index cannot be less than zero." );
        }
        if ( index > data.size() ) {
            throw new IllegalArgumentException( "Index cannot be greater than the number of rows." );
        }
        tbody.deleteRow( index );
        fixRowStyles( index );
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    void deselectCell( CellValue<? extends Comparable<?>> cell ) {
        if ( cell == null ) {
            throw new IllegalArgumentException( "cell cannot be null" );
        }

        Coordinate hc = cell.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement>cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement>cast();

        //Merging, grouping etc could have led to the selected HTML cell disappearing
        if ( tce != null ) {
            String cellSelectedStyle = resources.cellTableCellSelected();
            String cellMultipleValuesStyle = resources.cellTableCellMultipleValues();
            String cellOtherwiseStyle = resources.cellTableCellOtherwise();
            tce.removeClassName( cellSelectedStyle );

            //Re-apply applicable styling
            if ( cell.isOtherwise() ) {
                tce.addClassName( cellOtherwiseStyle );
            }
            if ( cell instanceof CellValue.GroupedCellValue ) {
                CellValue.GroupedCellValue gcv = (CellValue.GroupedCellValue) cell;
                if ( gcv.hasMultipleValues() ) {
                    tce.addClassName( cellMultipleValuesStyle );
                }
            }
        }
    }

    @Override
    void hideColumn( int index ) {
        if ( index < 0 ) {
            throw new IllegalArgumentException( "index cannot be less than zero" );
        }
        if ( index > columns.size() ) {
            throw new IllegalArgumentException( "index cannot be greater than the number of rows" );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow rowData = data.get( iRow );
            CellValue<? extends Comparable<?>> cell = rowData.get( index );

            if ( cell.getRowSpan() > 0 ) {
                Coordinate hc = cell.getHtmlCoordinate();
                TableRowElement tre = tbody.getRows().getItem( hc.getRow() );
                TableCellElement tce = tre.getCells().getItem( hc.getCol() );
                tre.removeChild( tce );
            }
        }
    }

    @Override
    void selectCell( CellValue<? extends Comparable<?>> cell ) {
        if ( cell == null ) {
            throw new IllegalArgumentException( "cell cannot be null" );
        }

        Coordinate hc = cell.getHtmlCoordinate();
        TableRowElement tre = tbody.getRows().getItem( hc.getRow() )
                .<TableRowElement>cast();
        TableCellElement tce = tre.getCells().getItem( hc.getCol() )
                .<TableCellElement>cast();

        //Cell selected style takes precedence
        String cellSelectedStyle = resources.cellTableCellSelected();
        String cellOtherwiseStyle = resources.cellTableCellOtherwise();
        String cellMultipleValuesStyle = resources.cellTableCellMultipleValues();

        tce.removeClassName( cellMultipleValuesStyle );
        tce.removeClassName( cellOtherwiseStyle );
        tce.addClassName( cellSelectedStyle );
        tce.focus();
    }

    @Override
    void showColumn( int index ) {
        if ( index < 0 ) {
            throw new IllegalArgumentException( "index cannot be less than zero" );
        }
        if ( index > columns.size() ) {
            throw new IllegalArgumentException( "index cannot be greater than the number of rows" );
        }

        for ( int iRow = 0; iRow < data.size(); iRow++ ) {
            DynamicDataRow rowData = data.get( iRow );
            TableCellElement tce = makeTableCellElement( index,
                                                         rowData );
            if ( tce != null ) {

                CellValue<? extends Comparable<?>> cell = rowData.get( index );
                Coordinate hc = cell.getHtmlCoordinate();

                TableRowElement tre = tbody.getRows().getItem( hc.getRow() );
                TableCellElement ntce = tre.insertCell( hc.getCol() );
                tre.replaceChild( tce,
                                  ntce );
            }
        }
    }

}

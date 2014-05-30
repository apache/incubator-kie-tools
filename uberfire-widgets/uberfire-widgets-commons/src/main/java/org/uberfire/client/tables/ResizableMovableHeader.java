package org.uberfire.client.tables;

import com.github.gwtbootstrap.client.ui.DataGrid;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import org.uberfire.commons.validation.PortablePreconditions;

import static com.google.gwt.dom.client.Style.Unit.*;

/**
 * A column header that supports resizing and moving
 * See https://github.com/gchatelet/GwtResizableDraggableColumns/blob/master/src/fr/mikrosimage/gwt/client/ResizableHeader.java
 * @param <T>
 */
public abstract class ResizableMovableHeader<T> extends Header<String> {

    private static final Style.Cursor MOVE_CURSOR = Cursor.MOVE;
    private static final String MOVE_COLOR = "gray";
    private static final int MOVE_HANDLE_WIDTH = 32;

    private static final Style.Cursor RESIZE_CURSOR = Cursor.COL_RESIZE;
    private static final String RESIZE_COLOR = "gray";
    private static final int RESIZE_HANDLE_WIDTH = 8;

    private static final double GHOST_OPACITY = .3;

    private static final int MINIMUM_COLUMN_WIDTH = 30;

    private final Document document = Document.get();

    private final String title;
    private final DataGrid<T> table;
    private final ColumnPicker columnPicker;
    private final Column<T, ?> column;

    private final Element tableElement;
    private HeaderHelper current;

    public ResizableMovableHeader( final String title,
                                   final DataGrid<T> table,
                                   final ColumnPicker columnPicker,
                                   final Column<T, ?> column ) {
        super( new HeaderCell() );
        this.title = PortablePreconditions.checkNotNull( "title",
                                                         title );
        this.table = PortablePreconditions.checkNotNull( "table",
                                                         table );
        this.columnPicker = PortablePreconditions.checkNotNull( "columnPicker",
                                                                columnPicker );
        this.column = PortablePreconditions.checkNotNull( "column",
                                                          column );
        this.tableElement = table.getElement();
    }

    @Override
    public String getValue() {
        return title;
    }

    @Override
    public void onBrowserEvent( final Context context,
                                final Element target,
                                final NativeEvent event ) {
        if ( current == null ) {
            current = new HeaderHelper( target,
                                        event );
        }
    }

    interface IDragCallback {

        void dragFinished();
    }

    private static NativeEvent getEventAndPreventPropagation( final NativePreviewEvent event ) {
        final NativeEvent nativeEvent = event.getNativeEvent();
        nativeEvent.preventDefault();
        nativeEvent.stopPropagation();
        return nativeEvent;
    }

    private static void setLine( final Style style,
                                 final int width,
                                 final int top,
                                 final int height,
                                 final String color ) {
        style.setPosition( Position.ABSOLUTE );
        style.setTop( top,
                      PX );
        style.setHeight( height,
                         PX );
        style.setWidth( width,
                        PX );
        style.setBackgroundColor( color );
        style.setZIndex( Integer.MAX_VALUE );
    }

    private class HeaderHelper implements NativePreviewHandler,
                                          IDragCallback {

        private final HandlerRegistration handler = Event.addNativePreviewHandler( this );
        private final Element source;
        private final Element handles;
        private final Element moveHandle;
        private final Element resizeHandle;
        private boolean dragging;

        public HeaderHelper( final Element target,
                             final NativeEvent event ) {
            event.preventDefault();
            event.stopPropagation();
            this.source = target;
            this.handles = document.createDivElement();

            final int leftBound = target.getOffsetLeft() + target.getOffsetWidth();
            this.moveHandle = createSpanElement( MOVE_CURSOR,
                                                 leftBound - RESIZE_HANDLE_WIDTH - MOVE_HANDLE_WIDTH,
                                                 MOVE_HANDLE_WIDTH );
            this.resizeHandle = createSpanElement( RESIZE_CURSOR,
                                                   leftBound - RESIZE_HANDLE_WIDTH,
                                                   RESIZE_HANDLE_WIDTH );
            handles.appendChild( moveHandle );
            handles.appendChild( resizeHandle );
            source.appendChild( handles );
        }

        private SpanElement createSpanElement( final Cursor cursor,
                                               final double left,
                                               final double width ) {
            final SpanElement span = document.createSpanElement();
            span.setAttribute( "title",
                               title );
            final Style style = span.getStyle();
            style.setCursor( cursor );
            style.setPosition( Position.ABSOLUTE );
            style.setBottom( 0,
                             PX );
            style.setHeight( source.getOffsetHeight(),
                             PX );
            style.setTop( source.getOffsetTop(),
                          PX );
            style.setWidth( width,
                            PX );
            style.setLeft( left,
                           PX );
            return span;
        }

        @Override
        public void onPreviewNativeEvent( final NativePreviewEvent event ) {
            final NativeEvent natEvent = event.getNativeEvent();
            final Element element = natEvent.getEventTarget().cast();
            final String eventType = natEvent.getType();
            if ( !( element == moveHandle || element == resizeHandle ) ) {
                if ( "mousedown".equals( eventType ) ) {
                    //No need to do anything, the event will be passed on to the column sort handler
                } else if ( !dragging && "mouseover".equals( eventType ) ) {
                    cleanUp();
                }
                return;
            }
            final NativeEvent nativeEvent = getEventAndPreventPropagation( event );
            if ( "mousedown".equals( eventType ) ) {
                if ( element == resizeHandle ) {
                    moveHandle.removeFromParent();
                    new ColumnResizeHelper( this,
                                            source,
                                            nativeEvent );
                } else {
                    new ColumnMoverHelper( this,
                                           source,
                                           nativeEvent );
                }
                dragging = true;
            }
        }

        private void cleanUp() {
            handler.removeHandler();
            handles.removeFromParent();
            current = null;
        }

        public void dragFinished() {
            dragging = false;
            cleanUp();
        }
    }

    private class ColumnResizeHelper implements NativePreviewHandler {

        private final HandlerRegistration handler = Event.addNativePreviewHandler( this );
        private final DivElement resizeLine = document.createDivElement();
        private final Style resizeLineStyle = resizeLine.getStyle();
        private final Element header;
        private final IDragCallback dragCallback;

        private ColumnResizeHelper( final IDragCallback dragCallback,
                                    final Element header,
                                    final NativeEvent event ) {
            this.dragCallback = dragCallback;
            this.header = header;
            setLine( resizeLineStyle,
                     2,
                     0,
                     getTableBodyHeight(),
                     RESIZE_COLOR );
            moveLine( event.getClientX() );
            tableElement.appendChild( resizeLine );
        }

        @Override
        public void onPreviewNativeEvent( final NativePreviewEvent event ) {
            final NativeEvent nativeEvent = getEventAndPreventPropagation( event );
            final int clientX = nativeEvent.getClientX();
            final String eventType = nativeEvent.getType();
            if ( "mousemove".equals( eventType ) ) {
                moveLine( clientX );
            } else if ( "mouseup".equals( eventType ) ) {
                handler.removeHandler();
                resizeLine.removeFromParent();
                dragCallback.dragFinished();
                columnResized( Math.max( clientX - header.getAbsoluteLeft(),
                                         MINIMUM_COLUMN_WIDTH ) );
            }
        }

        private void moveLine( final int clientX ) {
            final int xPos = clientX - table.getAbsoluteLeft();
            resizeLineStyle.setLeft( xPos,
                                     PX );
        }
    }

    private class ColumnMoverHelper implements NativePreviewHandler {

        private static final int ghostLineWidth = 4;
        private final HandlerRegistration handler = Event.addNativePreviewHandler( this );
        private final DivElement ghostLine = document.createDivElement();
        private final Style ghostLineStyle = ghostLine.getStyle();
        private final DivElement ghostColumn = document.createDivElement();
        private final Style ghostColumnStyle = ghostColumn.getStyle();
        private final int columnWidth;
        private final int[] columnXPositions;
        private final IDragCallback dragCallback;
        private int fromIndex = -1;
        private int toIndex;

        private ColumnMoverHelper( final IDragCallback dragCallback,
                                   final Element target,
                                   final NativeEvent event ) {
            final int clientX = event.getClientX();
            final Element tr = target.getParentElement();
            final int columns = tr.getChildCount();

            this.dragCallback = dragCallback;
            this.columnWidth = target.getOffsetWidth();
            this.columnXPositions = new int[ columns + 1 ];
            this.columnXPositions[ 0 ] = tr.getAbsoluteLeft();
            for ( int i = 0; i < columns; ++i ) {
                final int xPos = columnXPositions[ i ] + ( (Element) tr.getChild( i ) ).getOffsetWidth();
                if ( xPos > clientX && fromIndex == -1 ) {
                    fromIndex = i;
                }
                columnXPositions[ i + 1 ] = xPos;
            }
            toIndex = fromIndex;
            final int bodyHeight = getTableBodyHeight();
            setLine( ghostColumnStyle,
                     columnWidth,
                     0,
                     bodyHeight,
                     MOVE_COLOR );
            setLine( ghostLineStyle,
                     ghostLineWidth,
                     0,
                     bodyHeight,
                     RESIZE_COLOR );
            ghostColumnStyle.setOpacity( GHOST_OPACITY );
            moveColumn( clientX );
            tableElement.appendChild( ghostColumn );
            tableElement.appendChild( ghostLine );
        }

        @Override
        public void onPreviewNativeEvent( final NativePreviewEvent event ) {
            final NativeEvent nativeEvent = getEventAndPreventPropagation( event );
            final String eventType = nativeEvent.getType();
            if ( "mousemove".equals( eventType ) ) {
                moveColumn( nativeEvent.getClientX() );
            } else if ( "mouseup".equals( eventType ) ) {
                handler.removeHandler();
                ghostColumn.removeFromParent();
                ghostLine.removeFromParent();
                if ( fromIndex != toIndex ) {
                    columnMoved( fromIndex,
                                 toIndex );
                }
                dragCallback.dragFinished();
            }
        }

        private void moveColumn( final int clientX ) {
            final int pointer = clientX - columnWidth / 2;
            ghostColumnStyle.setLeft( pointer - table.getAbsoluteLeft(),
                                      PX );
            for ( int i = 0; i < columnXPositions.length - 1; ++i ) {
                if ( clientX < columnXPositions[ i + 1 ] ) {
                    final int adjustedIndex = i > fromIndex ? i + 1 : i;
                    int lineXPos = columnXPositions[ adjustedIndex ] - table.getAbsoluteLeft();
                    if ( adjustedIndex == columnXPositions.length - 1 ) {
                        lineXPos -= ghostLineWidth;
                    } else if ( adjustedIndex > 0 ) {
                        lineXPos -= ghostLineWidth / 2;
                    }
                    ghostLineStyle.setLeft( lineXPos,
                                            PX );
                    toIndex = i;
                    break;
                }
            }
        }
    }

    private static class HeaderCell extends AbstractCell<String> {

        public HeaderCell() {
            super( "mousemove" );
        }

        @Override
        public void render( final Context context,
                            final String value,
                            final SafeHtmlBuilder sb ) {
            sb.append( SafeHtmlUtils.fromString( value ) );
        }
    }

    protected void columnResized( final int newWidth ) {
        table.setColumnWidth( column,
                              newWidth + "px" );
    }

    protected void columnMoved( final int fromIndex,
                                final int beforeIndex ) {
        columnPicker.columnMoved( fromIndex,
                                  beforeIndex );
        table.removeColumn( fromIndex );
        table.insertColumn( beforeIndex,
                            column,
                            this );
    }

    protected abstract int getTableBodyHeight();

};


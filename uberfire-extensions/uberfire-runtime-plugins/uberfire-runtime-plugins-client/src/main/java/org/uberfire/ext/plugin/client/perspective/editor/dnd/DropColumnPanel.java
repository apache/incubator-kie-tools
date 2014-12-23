package org.uberfire.ext.plugin.client.perspective.editor.dnd;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.plugin.client.perspective.editor.components.HTMLView;
import org.uberfire.ext.plugin.client.perspective.editor.components.ScreenView;
import org.uberfire.ext.plugin.client.perspective.editor.row.RowView;
import org.uberfire.ext.plugin.client.perspective.editor.structure.ColumnEditorUI;
import org.uberfire.ext.plugin.client.perspective.editor.util.DragType;

public class DropColumnPanel extends FlowPanel {

    private final ColumnEditorUI parent;
    private final int parentIndex;


    public DropColumnPanel( final ColumnEditorUI parent ) {


        this.parent = parent;
        this.parentIndex = parent.getWidget().getWidgetIndex( this );
        setSize( "100%", "20px" );
        addDragOverHandler( new DragOverHandler() {
            @Override
            public void onDragOver( DragOverEvent event ) {
                getElement().getStyle().setBorderStyle( Style.BorderStyle.SOLID );
                getElement().getStyle().setBorderColor( "Red" );
                getElement().getStyle().setBorderWidth( 1, Style.Unit.PX );
            }
        } );
        addDragLeaveHandler( new DragLeaveHandler() {
            @Override
            public void onDragLeave( DragLeaveEvent event ) {
                getElement().getStyle().setBorderStyle( Style.BorderStyle.NONE );
            }
        } );
        addDropHandler( new DropHandler() {
                            @Override
                            public void onDrop( DropEvent event ) {
                                event.preventDefault();
//                                Window.alert( "1" );
                                if ( isAGridDrop( event ) ) {
//                                    Window.alert( "3" );
                                    String gridData = event.getData( DragType.GRID.name() );
                                    handleGridDrop( gridData );
                                }
                                else if ( isAScreenDrop( event ) ) {
//                                    Window.alert( "6" );
                                    handleScreenDrop( event );
//                                    Window.alert( "7" );
                                }
                                else if ( isAExternalComponentDrop( event ) ) {
                                    handleExternalScreenDrop( event );
                                }
                                else if ( isHtmlDrop( event ) ) {
                                    handleHTMLDrop();
                                }
                                removeDropBorder();
                            }
                        }

                      );
    }

    private void removeDropBorder() {
        getElement().getStyle().setBorderStyle( Style.BorderStyle.NONE );
    }

    private void handleExternalScreenDrop( DropEvent event ) {
        parent.getWidget().remove( this );
        parent.getWidget().add( new ScreenView( parent, DragType.EXTERNAL, event.getData( DragType.EXTERNAL.name() ) ) );
    }

    private void handleHTMLDrop() {
        parent.getWidget().remove( this );
        parent.getWidget().add( new HTMLView( parent ) );
        getElement().getStyle().setBorderStyle( Style.BorderStyle.NONE );
    }

    private void handleScreenDrop( DropEvent event ) {
//        Window.alert( "8" );
        parent.getWidget().remove( this );
//        Window.alert( "9" );
        parent.getWidget().add( new ScreenView( parent, DragType.SCREEN ) );
//        Window.alert( "10" );
    }

    private void handleGridDrop( String grid ) {
        parent.getWidget().remove( this );
        parent.getWidget().add( new RowView( parent, grid, this ) );
    }

    private boolean isAScreenDrop( DropEvent event ) {
//        Window.alert( "4" );
        return isARegularScreenEvent( event );
    }

    private boolean isAExternalComponentDrop( DropEvent event ) {
        return isAExternalComponent( event );
    }

    private boolean isAExternalComponent( DropEvent event ) {
        return ( event.getData( DragType.EXTERNAL.name() ) != null ) &&( !event.getData( DragType.EXTERNAL.name() ).isEmpty() );
    }

    private boolean isARegularScreenEvent( DropEvent event ) {
//        Window.alert( "5" );
        final String data = event.getData( DragType.SCREEN.name() );
//        Window.alert(data);
        return ( data != null ) && ( !event.getData( DragType.SCREEN.name() ).isEmpty() );
    }

    private boolean isAGridDrop( DropEvent event ) {
//        Window.alert( "2" );
        return ( event.getData( DragType.GRID.name() ) != null ) && ( !event.getData( DragType.GRID.name() ).isEmpty() );
    }

    private boolean isHtmlDrop( DropEvent event ) {
        return ( event.getData( DragType.HTML.name() ) != null ) && (!event.getData( DragType.HTML.name() ).isEmpty());
    }

    private HandlerRegistration addDropHandler( DropHandler handler ) {
        return addBitlessDomHandler( handler, DropEvent.getType() );
    }

    private HandlerRegistration addDragOverHandler( DragOverHandler handler ) {
        return addBitlessDomHandler( handler, DragOverEvent.getType() );
    }

    private HandlerRegistration addDragLeaveHandler( DragLeaveHandler handler ) {
        return addBitlessDomHandler( handler, DragLeaveEvent.getType() );
    }

}
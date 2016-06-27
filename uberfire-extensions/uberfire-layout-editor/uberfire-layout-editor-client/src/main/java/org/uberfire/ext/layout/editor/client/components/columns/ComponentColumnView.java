package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.mvp.Command;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Dependent
@Templated
public class ComponentColumnView extends Composite
        implements UberView<ComponentColumn>,
        ComponentColumn.View {

    public static final String COL_CSS_CLASS = "col-md-";

    private ComponentColumn presenter;

    @DataField
    private Element col = DOM.createDiv();

    @Inject
    @DataField
    private FlowPanel colUp;

    @DataField
    private Element row = DOM.createDiv();

    @DataField
    private Element topPanel = DOM.createDiv();

    @Inject
    @DataField
    private FlowPanel colDown;

    @Inject
    @DataField
    private FlowPanel left;

    @Inject
    @DataField
    private FlowPanel right;

    @Inject
    @DataField
    private FlowPanel content;


    @Inject
    @DataField
    private Button move;

    @Inject
    @DataField
    private Button remove;

    @Inject
    @DataField
    private Button edit;

    String cssSize = "";

    private final int originalLeftRightWidth = 5;

    private ColumnDrop.Orientation contentDropOrientation;

    @Inject
    private DragHelperComponentColumn helper;

    @Override
    public void init( ComponentColumn presenter ) {
        this.presenter = presenter;
    }

    public void resizeEventObserver( @Observes ContainerResizeEvent event ) {
        calculateSize();
    }

    @Override
    public void calculateSize() {

        Scheduler.get().scheduleDeferred( () -> {
            controlPadding();
            calculateLeftRightWidth();
            calculateContentWidth();
            col.addClassName( "container" );

        } );
    }

    private void controlPadding() {
        if ( !presenter.isInnerColumn() ) {
            col.addClassName( "no-padding" );
        } else {
            if ( col.hasClassName( "no-padding" ) ) {
                col.removeClassName( "no-padding" );
            }
        }
    }

    private void calculateLeftRightWidth() {
        if ( originalLeftRightWidth >= 0 ) {
            left.setWidth( originalLeftRightWidth + "px" );
            right.setWidth( originalLeftRightWidth + "px" );
        }
    }

    private void calculateContentWidth() {
        int smallSpace = 2;
        final int colWidth = col.getOffsetWidth();
        final int contentWidth = colWidth - ( originalLeftRightWidth * 2 ) - smallSpace;
        if ( contentWidth >= 0 ) {
            content.setWidth( contentWidth + "px" );
            colDown.setWidth( contentWidth + "px" );
            colUp.setWidth( contentWidth + "px" );
        }
    }

    @Override
    public void setCursor() {
        content.getElement().getStyle().setCursor( Style.Cursor.DEFAULT );
        if ( presenter.canResize() ) {
            left.getElement().getStyle().setCursor( Style.Cursor.COL_RESIZE );
        }
    }

    @Override
    public void setSize( String size ) {
        if ( !col.getClassName().isEmpty() ) {
            col.removeClassName( cssSize );
        }
        cssSize = COL_CSS_CLASS + size;
        col.addClassName( cssSize );
    }


    @Override
    public void clearContent() {
        content.clear();
    }

    @Override
    public void setContent() {
        Scheduler.get().scheduleDeferred( () -> {
            content.clear();
            content.add( getPreviewWidget() );
        } );
    }

    @Override
    public void showConfigComponentModal( Command configurationFinish, Command configurationCanceled ) {
        helper.showConfigModal( configurationFinish, configurationCanceled );
    }

    @Override
    public boolean hasModalConfiguration() {
        return helper.hasModalConfiguration();
    }

    @Override
    public void setup( LayoutComponent layoutComponent ) {
        helper.setLayoutComponent( layoutComponent );
    }


    private IsWidget getPreviewWidget() {
        return helper.getPreviewWidget( content );
    }

    @EventHandler( "left" )
    public void onResize( MouseDownEvent e ) {
        e.preventDefault();
        presenter.onResize( e.getClientX() );
    }


    @EventHandler( "colUp" )
    public void dragLeftcolUp( DragLeaveEvent e ) {
        if ( hasColPreview( colUp.getElement() ) ) {
            colUp.getElement().removeClassName( "componentDropInColumnPreview" );
        }
    }

    private boolean hasColPreview( com.google.gwt.user.client.Element element ) {
        return element.hasClassName( "componentDropInColumnPreview" );
    }


    @EventHandler( "content" )
    public void dragOverCenter( DragOverEvent e ) {
        e.preventDefault();
        if ( presenter.shouldPreviewDrop() ) {
            final int absoluteTop = content.getElement().getAbsoluteTop();
            final int absoluteBottom = content.getElement().getAbsoluteBottom();
            final int dragOverY = e.getNativeEvent().getClientY();

            if ( dragOverUp( absoluteTop, absoluteBottom, dragOverY ) ) {
                colUp.getElement().addClassName( "componentDropInColumnPreview" );
                colDown.getElement().removeClassName( "componentDropInColumnPreview" );
                contentDropOrientation = ColumnDrop.Orientation.UP;

            } else {
                colDown.getElement().addClassName( "componentDropInColumnPreview" );
                colUp.getElement().removeClassName( "componentDropInColumnPreview" );
                contentDropOrientation = ColumnDrop.Orientation.DOWN;
            }
        }
    }

    private boolean dragOverUp( int absoluteTop, int absoluteBottom, int dragOverY ) {
        return ( dragOverY - absoluteTop ) < ( absoluteBottom - dragOverY );
    }

    @EventHandler( "content" )
    public void dragLeaveCenter( DragLeaveEvent e ) {
        e.preventDefault();
        colDown.getElement().removeClassName( "componentDropInColumnPreview" );
        contentDropOrientation = null;
    }

    @EventHandler( "colUp" )
    public void dragOverColUp( DragOverEvent e ) {
        if ( presenter.shouldPreviewDrop() ) {
            contentDropOrientation = ColumnDrop.Orientation.UP;
            colUp.getElement().addClassName( "componentDropInColumnPreview" );
        }
    }

    @EventHandler( "colUp" )
    public void dragLeaveColUp( DragLeaveEvent e ) {
        colUp.getElement().removeClassName( "componentDropInColumnPreview" );
    }

    @EventHandler( "colUp" )
    public void dropUp( DropEvent drop ) {
        if ( contentDropOrientation != null ) {
            presenter.onDrop( contentDropOrientation, drop );
        }
        colUp.getElement().removeClassName( "componentDropInColumnPreview" );
        colDown.getElement().removeClassName( "componentDropInColumnPreview" );
    }

    @EventHandler( "colDown" )
    public void dropDown( DropEvent drop ) {
        if ( contentDropOrientation != null ) {
            presenter.onDrop( contentDropOrientation, drop );
        }
        colUp.getElement().removeClassName( "componentDropInColumnPreview" );
        colDown.getElement().removeClassName( "componentDropInColumnPreview" );
    }

    @EventHandler( "left" )
    public void dragEnterLeft( DragEnterEvent e ) {
        e.preventDefault();
        if ( presenter.shouldPreviewDrop() ) {
            left.getElement().addClassName( "columnDropPreview dropPreview" );
            content.getElement().addClassName( "centerPreview" );
        }

    }

    @EventHandler( "content" )
    public void dropInsideColumn( DropEvent drop ) {
        if ( contentDropOrientation != null ) {
            presenter.onDrop( contentDropOrientation, drop );
        }
        colUp.getElement().removeClassName( "componentDropInColumnPreview" );
        colDown.getElement().removeClassName( "componentDropInColumnPreview" );
    }


    @EventHandler( "left" )
    public void dragLeaveLeft( DragLeaveEvent e ) {
        e.preventDefault();
        left.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
    }


    @EventHandler( "left" )
    public void dragLeaveLeft( DragOverEvent e ) {
        e.preventDefault();
    }

    @EventHandler( "left" )
    public void dropColumnLeft( DropEvent drop ) {
        drop.preventDefault();
        left.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
        presenter.onDrop( ColumnDrop.Orientation.LEFT, drop );
    }

    @EventHandler( "right" )
    public void dragEnterRight( DragEnterEvent e ) {
        e.preventDefault();
        if ( presenter.shouldPreviewDrop() ) {
            right.getElement().addClassName( "columnDropPreview dropPreview" );
            content.getElement().addClassName( "centerPreview" );
        }
    }

    @EventHandler( "right" )
    public void dragLeaveRight( DragLeaveEvent e ) {
        e.preventDefault();
        right.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
    }

    @EventHandler( "right" )
    public void dragOver( DragOverEvent e ) {
        e.preventDefault();
    }


    @EventHandler( "right" )
    public void dropColumnRIGHT( DropEvent drop ) {
        drop.preventDefault();
        right.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
        presenter.onDrop( ColumnDrop.Orientation.RIGHT, drop );
    }

    @EventHandler( "move" )
    public void dragStartComponent( DragStartEvent e ) {
        e.stopPropagation();
        row.addClassName( "rowDndPreview" );
        presenter.dragStartComponent();
    }

    @EventHandler( "move" )
    public void dragEndComponent( DragEndEvent e ) {
        e.stopPropagation();
        row.removeClassName( "rowDndPreview" );
        presenter.dragEndComponent();
    }

    @EventHandler( "remove" )
    public void removeClick( ClickEvent e ) {
        presenter.remove();
    }

    @EventHandler( "edit" )
    public void editClick( ClickEvent e ) {
        presenter.edit();
    }

    @EventHandler( "col" )
    public void endColumnResize( MouseUpEvent e ) {

        e.preventDefault();

        presenter.endColumnResize( e.getClientX() );
        if ( col.hasClassName( "rowDndPreview" ) ) {
            col.removeClassName( "rowDndPreview" );
        }
    }

    @EventHandler( "col" )
    public void colMouseOver( MouseMoveEvent e ) {
        e.preventDefault();
    }
}
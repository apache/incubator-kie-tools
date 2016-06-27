package org.uberfire.ext.layout.editor.client.components.columns;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.components.rows.Row;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@Dependent
@Templated
public class ColumnWithComponentsView extends Composite
        implements UberView<ColumnWithComponents>,
        ColumnWithComponents.View {

    private static final String COL_CSS_CLASS = "col-md-";

    private ColumnWithComponents presenter;

    private final int originalLeftRightWidth = 5;

    @DataField
    Element colWithComponents = DOM.createDiv();

    @DataField
    Element row = DOM.createDiv();

    @Inject
    @DataField
    private FlowPanel content;

    @Inject
    @DataField
    private FlowPanel left;

    @Inject
    @DataField
    private FlowPanel right;

    String cssSize = "";

    @Override
    public void init( ColumnWithComponents presenter ) {
        this.presenter = presenter;
    }


    @Override
    public void setSize( String size ) {
        if ( hasCssSizeClass() ) {
            colWithComponents.removeClassName( cssSize );
        }
        cssSize = COL_CSS_CLASS + size + " container";
        colWithComponents.addClassName( cssSize );
    }

    private boolean hasCssSizeClass() {
        return !cssSize.isEmpty() && colWithComponents.hasClassName( cssSize );
    }

    @Override
    public void addRow( UberView<Row> view ) {
        content.add( view );
    }

    @Override
    public void setCursor() {
        content.getElement().getStyle().setCursor( Style.Cursor.DEFAULT );
        if ( presenter.canResize() ) {
            left.getElement().getStyle().setCursor( Style.Cursor.COL_RESIZE );
        }
    }

    @Override
    public void clear() {
        content.clear();
    }

    public void resizeEventObserver( @Observes ContainerResizeEvent event ) {
        calculateSize();
    }

    @Override
    public void calculateSize() {

        Scheduler.get().scheduleDeferred( () -> {

            final int colWidth = row.getOffsetWidth();

            int padding = 2;
            final int contentWidth = colWidth - ( originalLeftRightWidth * 2 ) - padding;

            left.setWidth( originalLeftRightWidth + "px" );
            right.setWidth( originalLeftRightWidth + "px" );

            content.setWidth( contentWidth + "px" );

        } );
    }

    @EventHandler( "left" )
    public void dragEnterLeft( DragEnterEvent e ) {
        e.preventDefault();
        left.getElement().addClassName( "columnDropPreview dropPreview" );
        content.getElement().addClassName( "centerPreview" );
    }

    @EventHandler( "left" )
    public void dragOverLeft( DragOverEvent e ) {
        e.preventDefault();
    }


    @EventHandler( "left" )
    public void dragLeaveLeft( DragLeaveEvent e ) {
        e.preventDefault();
        left.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
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
        right.getElement().addClassName( "columnDropPreview dropPreview" );
        content.getElement().addClassName( "centerPreview" );
    }

    @EventHandler( "right" )
    public void dragLeaveRight( DragLeaveEvent e ) {
        e.preventDefault();
        right.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
    }

    @EventHandler( "right" )
    public void dropColumnRIGHT( DropEvent drop ) {
        drop.preventDefault();
        right.getElement().removeClassName( "columnDropPreview dropPreview" );
        content.getElement().removeClassName( "centerPreview" );
        presenter.onDrop( ColumnDrop.Orientation.RIGHT, drop );
    }

    @EventHandler( "right" )
    public void dragOverRight( DragOverEvent e ) {
        e.preventDefault();
    }

    @EventHandler( "left" )
    public void colMouseDown( MouseDownEvent e ) {
        e.preventDefault();
        presenter.onMouseDown( e.getClientX() );
    }

    @EventHandler( "colWithComponents" )
    public void colMouseUp( MouseUpEvent e ) {
        e.preventDefault();
        presenter.onMouseUp( e.getClientX() );
    }

}
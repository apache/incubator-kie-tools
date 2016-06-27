package org.uberfire.ext.layout.editor.client.components.rows;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Templated
public class RowView extends Composite
        implements UberView<Row>,
        Row.View {

    static final String ROW_DROP_PREVIEW = "rowDropPreview";

    private Row presenter;


    @DataField
    Element upper = DOM.createDiv();

    @DataField
    Element bottom = DOM.createDiv();

    @DataField
    Element row = DOM.createDiv();

    @Inject
    @DataField
    FlowPanel content;

    @Override
    public void init( Row presenter ) {
        this.presenter = presenter;
        row.getStyle().setCursor( Style.Cursor.MOVE );
    }

    @Override
    public void addColumn( UberView<ComponentColumn> view ) {
        content.add( view );
    }

    @Override
    public void clear() {
        content.clear();
    }

    @EventHandler( "upper" )
    public void dragOverUpper( DragOverEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            upper.addClassName( ROW_DROP_PREVIEW );
        }
    }

    @EventHandler( "upper" )
    public void dragLeaveUpper( DragLeaveEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            upper.removeClassName( "rowDropPreview" );
        }

    }

    @EventHandler( "upper" )
    public void dropUpperEvent( DropEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            upper.removeClassName( "rowDropPreview" );
            presenter.drop( e, RowDrop.Orientation.BEFORE );
        }
    }

    @EventHandler( "bottom" )
    public void mouseOutUpper( MouseOutEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            bottom.removeClassName( "rowDropPreview" );
        }
    }

    @EventHandler( "bottom" )
    public void dragOverBottom( DragOverEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            bottom.addClassName( "rowDropPreview" );
        }

    }

    @EventHandler( "bottom" )
    public void dropBottomEvent( DropEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            bottom.removeClassName( "rowDropPreview" );
            presenter.drop( e, RowDrop.Orientation.AFTER );
        }
    }


    @EventHandler( "bottom" )
    public void dragLeaveBottom( DragLeaveEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            bottom.removeClassName( "rowDropPreview" );
        }
    }

    @EventHandler( "bottom" )
    public void mouseOutBottom( MouseOutEvent e ) {
        if ( presenter.isDropEnable() ) {
            e.preventDefault();
            bottom.removeClassName( "rowDropPreview" );
        }
    }

    @EventHandler( "row" )
    public void dragStartRow( DragStartEvent e ) {
        if ( presenter.canDrag() ) {
            presenter.dragStart();
            row.addClassName( "rowDndPreview" );
        }
    }

    @EventHandler( "row" )
    public void dragEndRow( DragEndEvent e ) {
        if ( row.hasClassName( "rowDndPreview" ) ) {
            row.removeClassName( "rowDndPreview" );
        }
        presenter.dragEndMove();
    }
}



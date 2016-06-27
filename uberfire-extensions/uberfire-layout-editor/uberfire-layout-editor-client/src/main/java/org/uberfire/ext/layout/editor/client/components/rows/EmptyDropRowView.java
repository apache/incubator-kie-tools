package org.uberfire.ext.layout.editor.client.components.rows;

import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberView;

import javax.enterprise.context.Dependent;

@Dependent
@Templated
public class EmptyDropRowView extends Composite
        implements UberView<EmptyDropRow>,
        EmptyDropRow.View {

    private EmptyDropRow presenter;


    @DataField
    private Element row = DOM.createDiv();

    @Override
    public void init( EmptyDropRow presenter ) {
        this.presenter = presenter;
    }

    @EventHandler( "row" )
    public void dragOverRow( DragOverEvent e ) {
        e.preventDefault();
        row.addClassName( "rowDropPreview" );
    }

    @EventHandler( "row" )
    public void dragLeaveUpper( DragLeaveEvent e ) {
        e.preventDefault();
        row.removeClassName( "rowDropPreview" );
    }

    @EventHandler( "row" )
    public void dropRow( DropEvent e ) {
        e.preventDefault();
        row.removeClassName( "rowDropPreview" );
        presenter.drop( e );
    }

}



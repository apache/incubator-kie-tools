package org.uberfire.ext.layout.editor.client.components.rows;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.jboss.errai.common.client.dom.DOMUtil.removeAllChildren;
import static org.uberfire.ext.layout.editor.client.infra.CSSClassNameHelper.*;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;

@Dependent
@Templated
public class RowView
        implements UberElement<Row>,
        Row.View, IsElement {

    private Row presenter;

    @Inject
    @DataField
    Div upper;

    @Inject
    @DataField
    Div bottom;

    @Inject
    @DataField
    Div row;

    @Inject
    @DataField
    Div content;

    @Inject
    @DataField( "mainrow" )
    Div mainRow;

    @Override
    public void init( Row presenter ) {
        this.presenter = presenter;
        setupEvents();
    }

    private void setupEvents() {
        setupUpperEvents();
        setupBottomEvents();
    }

    private void setupBottomEvents() {
        bottom.setOndragover( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                addClassName( bottom, "rowDropPreview" );
            }
        } );
        bottom.setOnmouseout( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeClassName( bottom, "rowDropPreview" );
            }
        } );
        bottom.setOndrop( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeClassName( bottom, "rowDropPreview" );
                presenter.drop( extractDndData( e ), RowDrop.Orientation.AFTER );
            }
        } );
        bottom.setOndragleave( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeClassName( bottom, "rowDropPreview" );
            }
        } );
    }

    private void setupUpperEvents() {
        if ( presenter.isDropEnable() ) {
            upper.setAttribute( "draggable", "true" );
        }
        upper.setOndragstart( event -> {
            if ( presenter.isDropEnable() ) {
                presenter.dragStart();
                addClassName( row, "rowDndPreview" );
                removeClassName( upper, "rowMovePreview" );
                removeClassName( bottom, "rowMovePreview" );
            }
        } );
        upper.setOndragend( event -> {
            if ( presenter.isDropEnable() ) {
                if ( hasClassName( row, "rowDndPreview" ) ) {
                    removeClassName( row, "rowDndPreview" );
                }
                presenter.dragEndMove();
            }
        } );
        upper.setOndragover( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                addClassName( upper, "rowDropPreview" );
            }
        } );
        upper.setOnmouseout( e -> {
            if ( presenter.isDropEnable() ) {
                removeClassName( upper, "rowMovePreview" );
                removeClassName( row, "rowMovePreview" );
                removeClassName( bottom, "rowMovePreview" );

                e.preventDefault();
                removeClassName( upper, "rowDropPreview" );
            }
        } );
        upper.setOnmouseover( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                addClassName( upper, "rowMovePreview" );
                addClassName( row, "rowMovePreview" );
                addClassName( bottom, "rowMovePreview" );
            }
        } );
        upper.setOndragleave( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeClassName( upper, "rowDropPreview" );
            }
        } );
        upper.setOndrop( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeClassName( upper, "rowDropPreview" );
                presenter.drop( extractDndData( e ), RowDrop.Orientation.BEFORE );
            }
        } );
    }

    @Override
    public void addColumn( UberElement<ComponentColumn> view ) {
        content.appendChild( view.getElement() );
    }

    @Override
    public void clear() {
        removeAllChildren( content );
    }


}



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
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;
import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;

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
                addCSSClass( bottom, "rowDropPreview" );
            }
        } );
        bottom.setOnmouseout( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeCSSClass( bottom, "rowDropPreview" );
            }
        } );
        bottom.setOndrop( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeCSSClass( bottom, "rowDropPreview" );
                presenter.drop( extractDndData( e ), RowDrop.Orientation.AFTER );
            }
        } );
        bottom.setOndragleave( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeCSSClass( bottom, "rowDropPreview" );
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
                addCSSClass( row, "rowDndPreview" );
                removeCSSClass( upper, "rowMovePreview" );
                removeCSSClass( bottom, "rowMovePreview" );
            }
        } );
        upper.setOndragend( event -> {
            if ( presenter.isDropEnable() ) {
                if ( hasCSSClass( row, "rowDndPreview" ) ) {
                    removeCSSClass( row, "rowDndPreview" );
                }
                presenter.dragEndMove();
            }
        } );
        upper.setOndragover( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                addCSSClass( upper, "rowDropPreview" );
            }
        } );
        upper.setOnmouseout( e -> {
            if ( presenter.isDropEnable() ) {
                removeCSSClass( upper, "rowMovePreview" );
                removeCSSClass( row, "rowMovePreview" );
                removeCSSClass( bottom, "rowMovePreview" );

                e.preventDefault();
                removeCSSClass( upper, "rowDropPreview" );
            }
        } );
        upper.setOnmouseover( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                addCSSClass( upper, "rowMovePreview" );
                addCSSClass( row, "rowMovePreview" );
                addCSSClass( bottom, "rowMovePreview" );
            }
        } );
        upper.setOndragleave( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeCSSClass( upper, "rowDropPreview" );
            }
        } );
        upper.setOndrop( e -> {
            if ( presenter.isDropEnable() ) {
                e.preventDefault();
                removeCSSClass( upper, "rowDropPreview" );
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



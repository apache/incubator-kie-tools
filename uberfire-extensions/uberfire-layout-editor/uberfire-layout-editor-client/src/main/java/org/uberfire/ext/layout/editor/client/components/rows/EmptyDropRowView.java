package org.uberfire.ext.layout.editor.client.components.rows;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;

import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.extractDndData;
import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.hasCSSClass;

@Dependent
@Templated
public class EmptyDropRowView
        implements UberElement<EmptyDropRow>,
        EmptyDropRow.View, IsElement {

    private EmptyDropRow presenter;

    @Inject
    @DataField
    private Div row;

    @Inject
    @DataField( "inner-row" )
    private Div innerRow;

    @Inject
    @Named( "h1" )
    @DataField
    private Heading title;

    @Inject
    @DataField
    private Span subtitle;


    @Override
    public void init( EmptyDropRow presenter ) {
        this.presenter = presenter;
        row.setOndragover( event -> {
            event.preventDefault();
            addSelectEmptyBorder();
        } );
        row.setOndragenter( event -> {
            addSelectEmptyBorder();
        } );
        row.setOndragleave( event -> {
            removeSelectedBorder();
        } );

        row.setOndrop( e -> {
            e.preventDefault();
            presenter.drop( extractDndData( e ) );
        } );
    }

    @Override
    public void setupText( String titleText, String subTitleText ) {
        title.setTextContent( titleText );
        subtitle.setTextContent( subTitleText );

    }

    private void removeSelectedBorder() {
        if ( hasCSSClass( row, "le-empty-preview-drop" ) ) {
            removeCSSClass( row, "le-empty-preview-drop" );
            removeCSSClass( innerRow, "le-empty-inner-preview-drop" );
        }
    }

    private void addSelectEmptyBorder() {
        if ( !hasCSSClass( row, "le-empty-preview-drop" ) ) {
            addCSSClass( row, "le-empty-preview-drop" );
            addCSSClass( innerRow, "le-empty-inner-preview-drop" );
        }
    }


}



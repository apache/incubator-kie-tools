package org.uberfire.client.screen.source;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.TextBox;

public final class SourceBreadcrumbUtil {

    private SourceBreadcrumbUtil() {

    }

    public static LIElement repo( String repoName ) {

        final LIElement element = Document.get().createLIElement();
        element.getStyle().setPadding( 0, Style.Unit.PX );
        element.getStyle().setMargin( 0, Style.Unit.PX );
        final Element italic = DOM.createElement( "em" );
        final Element strong = DOM.createElement( "strong" );
        strong.setInnerText( repoName );
        italic.appendChild( strong );
        element.appendChild( italic );

        {
            final Element divider = DOM.createSpan();
            divider.addClassName( "divider" );
            divider.setInnerHTML( " /&nbsp;" );
            element.appendChild( divider );
        }

        return element;
    }

    public static LIElement directory( final String dir ) {
        final LIElement element = Document.get().createLIElement();
        element.getStyle().setPadding( 0, Style.Unit.PX );
        element.getStyle().setMargin( 0, Style.Unit.PX );

        final Element content = DOM.createSpan();
        content.setInnerText( dir );
        element.appendChild( content );

        {
            final Element divider = DOM.createSpan();
            divider.addClassName( "divider" );
            divider.setInnerHTML( " /&nbsp;" );
            element.appendChild( divider );
        }

        return element;
    }

    public static LIElement fileName( final String filename,
                                      final TextBox editor ) {

        final LIElement element = Document.get().createLIElement();
        element.getStyle().setPadding( 0, Style.Unit.PX );
        element.getStyle().setMargin( 0, Style.Unit.PX );

        if ( editor != null ) {
            editor.setText( filename );
            element.appendChild( editor.getElement() );
            return element;
        }

        final Element strong = DOM.createElement( "strong" );
        strong.setInnerText( filename );
        element.appendChild( strong );

        return element;
    }
}

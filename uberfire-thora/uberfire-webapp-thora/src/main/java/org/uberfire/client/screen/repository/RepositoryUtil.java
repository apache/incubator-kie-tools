package org.uberfire.client.screen.repository;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.shared.repository.RepositoryInfo;

/**
 * Created with IntelliJ IDEA.
 * Date: 6/9/13
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RepositoryUtil {

    public static LIElement newRepo( final PlaceManager placeManager,
                                     final RepositoryInfo repositoryInfo,
                                     final boolean prefixWithUser ) {
        if ( prefixWithUser ) {
            final Element wrapper = DOM.createDiv();
            final HeadingElement helement = Document.get().createHElement( 3 );


            final Element icon = DOM.createElement( "i" );
            icon.addClassName( "icon-book" );
            helement.appendChild( icon );

            final Anchor owner = new Anchor( repositoryInfo.getOwner() );
            DOM.sinkEvents( owner.getElement(), Event.ONCLICK );
            DOM.setEventListener( owner.getElement(), new EventListener() {
                public void onBrowserEvent( Event event ) {
                    placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_name", repositoryInfo.getOwner() ) );
                }
            } );
            helement.appendChild( owner.getElement() );

            final Element divider = DOM.createSpan();
            divider.setInnerText( " / " );
            helement.appendChild( divider );

            final Anchor repo = new Anchor( repositoryInfo.getName() );
            DOM.sinkEvents( repo.getElement(), Event.ONCLICK );
            DOM.setEventListener( repo.getElement(), new EventListener() {
                public void onBrowserEvent( Event event ) {
                    placeManager.goTo( new DefaultPlaceRequest( "RepoBrowser" ).addParameter( "repo", repositoryInfo.getName() ) );
                }
            } );

            final Element strong = DOM.createElement( "strong" );
            strong.appendChild( repo.getElement() );
            helement.appendChild( strong );

            wrapper.appendChild( helement );

            final LIElement liElement = Document.get().createLIElement();
            liElement.appendChild( wrapper );

            final Element descr = DOM.createElement( "p" );
            descr.setInnerText( repositoryInfo.getDescription() );

            liElement.appendChild( descr );
            liElement.appendChild( DOM.createElement( "hr" ) );

            return liElement;
        }

        final Anchor label = new Anchor( repositoryInfo.getName() );
        final LIElement liElement = Document.get().createLIElement();
        final HeadingElement helement = Document.get().createHElement( 3 );
        final Element icon = DOM.createElement( "i" );
        icon.addClassName( "icon-book" );

        helement.appendChild( icon );
        helement.appendChild( label.getElement() );

        final Element descr = DOM.createElement( "p" );
        descr.setInnerText( repositoryInfo.getDescription() );

        liElement.appendChild( helement );
        liElement.appendChild( descr );
        liElement.appendChild( DOM.createElement( "hr" ) );

        DOM.sinkEvents( label.getElement(), Event.ONCLICK );
        DOM.setEventListener( label.getElement(), new EventListener() {
            public void onBrowserEvent( Event event ) {
                placeManager.goTo( new DefaultPlaceRequest( "RepoBrowser" ).addParameter( "repo", repositoryInfo.getName() ) );
            }
        } );

        return liElement;
    }

}

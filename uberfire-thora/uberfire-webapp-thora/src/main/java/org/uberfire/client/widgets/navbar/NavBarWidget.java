package org.uberfire.client.widgets.navbar;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.widgets.gravatar.GravatarImage;
import org.uberfire.client.workbench.Header;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;

@Templated("navbar.html")
@ApplicationScoped
public class NavBarWidget extends Composite implements Header {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Identity identity;

    @DataField
    @Inject
    private Image userSmallImage;

    @DataField
    @Inject
    private Image userMedImage;

    @DataField
    @Inject
    private Button signout;

    @DataField
    @Inject
    private Button settings;

    @DataField
    private Element fullName = DOM.createSpan();

    @DataField
    private Element email = DOM.createSpan();

    @DataField
    private Element userName = DOM.createSpan();

    @DataField
    private LIElement homeLink = Document.get().createLIElement();

    @PostConstruct
    private void setup() {
        String _email = identity.getProperty( "email", "--" );
        userSmallImage.setUrl( new GravatarImage( _email, 20 ).getUrl() );
        userMedImage.setUrl( new GravatarImage( _email, 96 ).getUrl() );
        fullName.setInnerText( identity.getProperty( "full_name", identity.getName() ) );
        email.setInnerText( _email );
        userName.setInnerText( identity.getName() );

        final Anchor home = new Anchor( new SafeHtmlBuilder().appendHtmlConstant( "<b class=\"icon-home\"></b>" ).toSafeHtml() );
        DOM.sinkEvents( home.getElement(), Event.ONCLICK );
        DOM.setEventListener( home.getElement(), new EventListener() {
            public void onBrowserEvent( Event event ) {
                placeManager.goTo( new DefaultPlaceRequest( "RepoList" ) );
            }
        } );
        homeLink.appendChild( home.getElement() );
    }

    @EventHandler("signout")
    public void signout( final ClickEvent e ) {
        redirect( GWT.getModuleBaseURL() + "uf_logout" );
    }

    @EventHandler("settings")
    public void settings( final ClickEvent e ) {
        placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_name", identity.getName() ) );
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

    @Override
    public int getOrder() {
        return 1;
    }
}

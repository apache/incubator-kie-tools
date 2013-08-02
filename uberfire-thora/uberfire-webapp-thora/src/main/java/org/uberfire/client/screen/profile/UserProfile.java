package org.uberfire.client.screen.profile;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.screen.repository.RepositoryUtil;
import org.uberfire.client.widgets.gravatar.GravatarImage;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.shared.repository.NewRepositoryInfo;
import org.uberfire.shared.repository.RepositoryAppService;
import org.uberfire.shared.repository.RepositoryInfo;
import org.uberfire.shared.user.UserAppService;
import org.uberfire.shared.user.UserProfileModel;

@WorkbenchScreen(identifier = "UserProfile")
@Templated("user-profile.html")
@Dependent
public class UserProfile extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<UserAppService> userService;

    @Inject
    private Caller<RepositoryAppService> repositoryAppService;

    @Inject
    private Identity identity;

    @DataField
    @Inject
    private Image bigImage;

    @DataField
    @Inject
    private Button newRepo;

    @DataField
    private Element fullName = DOM.createSpan();

    @DataField
    private Element userName = DOM.createSpan();

    @DataField
    private Element memberSince = DOM.createSpan();

    @DataField
    private Element email = DOM.createSpan();

    @DataField
    private Element url = DOM.createSpan();

    @DataField
    private Element publicRepos = DOM.createSpan();

    @DataField
    private Element newRepoArea = DOM.createDiv();

    @DataField
    private Element contribsPanel = DOM.createDiv();

    @DataField
    private Element empty = DOM.createDiv();

    @DataField
    private UListElement contribs = Document.get().createULElement();

    @DataField
    private UListElement repoList = Document.get().createULElement();

    @DataField
    private Element memberSinceArea = DOM.createDiv();

    @DataField
    private Element urlArea = DOM.createDiv();

    @DataField
    private HeadingElement userNameArea = Document.get().createHElement( 4 );

    private String currentUser = "";

    @WorkbenchPartTitle
    public String getTitle() {
        if ( identity.getName().equals( currentUser ) ) {
            return "My Profile";
        }

        return "User Profile: " + currentUser;
    }

    @OnStartup
    public void onStartup( final PlaceRequest placeRequest ) {
        final String userEmailParam = placeRequest.getParameter( "user_email", null );
        final String userNameParam = placeRequest.getParameter( "user_name", null );

        currentUser = userNameParam;
        if ( !userNameParam.equals( identity.getName() ) ) {
            newRepoArea.removeFromParent();
        }

        if ( userEmailParam != null ) {
            fullName.setInnerText( userNameParam );
            memberSinceArea.getStyle().setDisplay( Style.Display.NONE );
            urlArea.getStyle().setDisplay( Style.Display.NONE );
            userNameArea.getStyle().setDisplay( Style.Display.NONE );
            email.setInnerText( userEmailParam );
            publicRepos.setInnerText( "0" );
            bigImage.setUrl( new GravatarImage( userEmailParam, 210 ).getUrl() );
            return;
        }

        userName.setInnerText( userNameParam );
        userService.call( new RemoteCallback<UserProfileModel>() {
            @Override
            public void callback( UserProfileModel response ) {
                userNameArea.getStyle().clearDisplay();
                fullName.setInnerText( response.getFullName() );
                if ( response.getMemberSince() == null || response.getMemberSince().isEmpty() ) {
                    memberSinceArea.getStyle().setDisplay( Style.Display.NONE );
                } else {
                    memberSinceArea.getStyle().clearDisplay();
                    memberSince.setInnerText( response.getMemberSince() );
                }

                if ( response.getWebsite() == null || response.getWebsite().isEmpty() ) {
                    urlArea.getStyle().setDisplay( Style.Display.NONE );
                } else {
                    urlArea.getStyle().clearDisplay();
                    url.setInnerText( response.getWebsite() );
                }

                email.setInnerText( response.getEmail() );
                publicRepos.setInnerText( String.valueOf( response.getPublicRepos() ) );
                bigImage.setUrl( new GravatarImage( response.getEmail(), 210 ).getUrl() );

                buildContribs( response.getLatestContributions() );

            }
        } ).getUserProfile( currentUser );

        repositoryAppService.call( new RemoteCallback<List<RepositoryInfo>>() {
            @Override
            public void callback( final List<RepositoryInfo> response ) {
                for ( final RepositoryInfo activeRepo : response ) {
                    repoList.appendChild( RepositoryUtil.newRepo( placeManager, activeRepo, false ) );
                }
            }
        } ).getUserRepositories( currentUser );
    }

    private void buildContribs( final List<RepositoryInfo> latestContributions ) {
        if ( latestContributions.size() == 0 ) {
            empty.getStyle().setDisplay( Style.Display.BLOCK );
            contribsPanel.getStyle().setDisplay( Style.Display.NONE );
            return;
        }
        empty.getStyle().setDisplay( Style.Display.NONE );
        contribsPanel.getStyle().setDisplay( Style.Display.BLOCK );

        contribs.removeFromParent();
        contribs = Document.get().createULElement();
        contribs.addClassName( "unstyled" );

        for ( final RepositoryInfo latestContribution : latestContributions ) {
            final LIElement li = Document.get().createLIElement();
            final Element content = DOM.createElement( "p" );
            final Element icon = DOM.createElement( "i" );
            icon.addClassName( "icon-book" );
            content.appendChild( icon );

            final Anchor owner = new Anchor( latestContribution.getOwner() );
            DOM.sinkEvents( owner.getElement(), Event.ONCLICK );
            DOM.setEventListener( owner.getElement(), new EventListener() {
                public void onBrowserEvent( Event event ) {
                    placeManager.goTo( new DefaultPlaceRequest( "UserProfile" ).addParameter( "user_name", latestContribution.getOwner() ) );
                }
            } );
            content.appendChild( owner.getElement() );

            final Element divider = DOM.createSpan();
            divider.setInnerText( " / " );
            content.appendChild( divider );

            final Anchor repo = new Anchor( latestContribution.getName() );
            DOM.sinkEvents( repo.getElement(), Event.ONCLICK );
            DOM.setEventListener( repo.getElement(), new EventListener() {
                public void onBrowserEvent( Event event ) {
                    placeManager.goTo( new DefaultPlaceRequest( "RepoBrowser" ).addParameter( "repo", latestContribution.getName() ) );
                }
            } );
            content.appendChild( repo.getElement() );

            final Element lineBreak = DOM.createElement( "br" );
            content.appendChild( lineBreak );

            final Element descr = DOM.createSpan();
            descr.setInnerText( latestContribution.getDescription() );
            content.appendChild( descr );

            content.appendChild( DOM.createElement( "hr" ) );
            li.appendChild( content );
            contribs.appendChild( li );
        }
        contribsPanel.appendChild( contribs );
    }

    public void onNewRepoInfo( @Observes final NewRepositoryInfo event ) {
        repoList.appendChild( RepositoryUtil.newRepo( placeManager, event.getRepositoryInfo(), false ) );
    }

    @EventHandler("newRepo")
    public void newRepo( ClickEvent e ) {
        placeManager.goTo( new DefaultPlaceRequest( "NewRepo" ) );
    }

}

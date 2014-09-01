package org.kie.workbench.common.screens.social.hp.client.userpage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Image;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.screens.social.hp.client.UserHomepageSelectedEvent;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.MainPresenter;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.header.HeaderPresenter;
import org.kie.workbench.common.screens.social.hp.predicate.UserTimeLineOnlyUserActivityPredicate;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.security.Identity;

@ApplicationScoped
@WorkbenchScreen(identifier = "UserHomePageMainPresenter")
public class UserHomePageMainPresenter {

    public interface View extends UberView<UserHomePageMainPresenter> {

        void setHeader( final HeaderPresenter header );

        void setMain( MainPresenter main );
    }

    @Inject
    private View view;

    @Inject
    private HeaderPresenter header;

    @Inject
    private MainPresenter mainPresenter;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private Identity loggedUser;

    @Inject
    private PlaceManager placeManager;

    @AfterInitialization
    public void loadContent() {
        initHeader();
    }

    private void initHeader() {
        view.setHeader( header );
        view.setMain( mainPresenter );
    }

    @OnOpen
    public void onOpen() {
        setupHeader( loggedUser.getName() );
        setupMain( loggedUser.getName() );
    }

    public void watchUserHomepageSelectedEvent( @Observes UserHomepageSelectedEvent event ) {
        setupHeader( event.getSocialUserName() );
        setupMain( event.getSocialUserName() );
    }

    private void setupHeader( String username ) {
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                generateConnectionsList( socialUser );
            }
        } ).findSocialUser( username );
    }

    private void setupMain( String username ) {
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                String title = !socialUser.getRealName().isEmpty() ? socialUser.getRealName() : socialUser.getUserName();
                title += " Recent Activities";
                SimpleSocialTimelineWidgetModel model = new SimpleSocialTimelineWidgetModel( socialUser,
                                                                                             title,
                                                                                             new UserTimeLineOnlyUserActivityPredicate( socialUser ),
                                                                                             placeManager,
                                                                                             new SocialPaged( 10 ) );
                mainPresenter.setup( model );
            }
        } ).findSocialUser( username );
    }

    private void generateConnectionsList( SocialUser socialUser ) {
        for ( final String follower : socialUser.getFollowingName() ) {
            header.clear();
            socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
                public void callback( SocialUser socialUser ) {
                    Image followerImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
                    followerImage.addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            Window.alert( "GOTO -> page" );
                        }
                    } );
                    header.addConnection( followerImage );
                }
            } ).findSocialUser( follower );
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "UserHomePageMainPresenter";
    }

    @WorkbenchPartView
    public UberView<UserHomePageMainPresenter> getView() {
        return view;
    }

}

package org.kie.workbench.common.screens.social.hp.client.userpage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.userbox.UserBoxView;
import org.kie.uberfire.social.activities.model.SocialFileSelectedEvent;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserServiceAPI;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.LoadUserPageEvent;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserEditedEvent;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserHomepageSelectedEvent;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.MainPresenter;
import org.kie.workbench.common.screens.social.hp.client.userpage.main.header.HeaderPresenter;
import org.kie.workbench.common.screens.social.hp.client.util.IconLocator;
import org.kie.workbench.common.screens.social.hp.predicate.UserTimeLineOnlyUserActivityPredicate;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Identity;

@ApplicationScoped
@WorkbenchScreen(identifier = "UserHomePageMainPresenter")
public class UserHomePageMainPresenter {

    private PlaceRequest place;

    public interface View extends UberView<UserHomePageMainPresenter> {

        void setHeader( final HeaderPresenter header );

        void setMain( MainPresenter main );
    }

    @Inject
    private IconLocator iconLocator;

    @Inject
    private Event<SocialFileSelectedEvent> socialFileSelectedEvent;
    
    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private View view;

    @Inject
    private HeaderPresenter header;

    @Inject
    private MainPresenter mainPresenter;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    Caller<SocialUserServiceAPI> socialUserServiceAPI;

    @Inject
    private Identity loggedUser;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<UserHomepageSelectedEvent> userHomepageSelectedEvent;


    //control race conditions due to assync system (cdi x UF lifecycle)
    private String lastUserOnpage;

    @AfterInitialization
    public void loadContent() {
        initHeader();
    }

    private void initHeader() {
        view.setHeader( header );
        view.setMain( mainPresenter );
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
        this.lastUserOnpage = loggedUser.getName();
        setupHeader( loggedUser.getName() );
        setupMain( loggedUser.getName() );
    }

    public void watchLoadUserPageEvent( @Observes LoadUserPageEvent event ) {
        this.lastUserOnpage = event.getSocialUserName();
        setupHeader( event.getSocialUserName() );
        setupMain( event.getSocialUserName() );
    }

    public void watchUserHomepageSelectedEvent( @Observes UserHomepageSelectedEvent event ) {
        this.lastUserOnpage = event.getSocialUserName();
        setupHeader( event.getSocialUserName() );
        setupMain( event.getSocialUserName() );
    }

    public void watchUserHomepageSelectedEvent( @Observes UserEditedEvent event ) {
        this.lastUserOnpage = event.getSocialUserName();
        setupHeader( event.getSocialUserName() );
        setupMain( event.getSocialUserName() );
    }

    private boolean isThisUserStillCurrentActiveUser( SocialUser socialUser ) {
        return socialUser.getUserName().equalsIgnoreCase( lastUserOnpage );
    }

    private void setupHeader( String username ) {
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                if ( isThisUserStillCurrentActiveUser( socialUser ) ) {
                    generateConnectionsList( socialUser );
                }
            }
        } ).findSocialUser( username );
    }

    private void setupMain( String username ) {
        final SocialPaged socialPaged = new SocialPaged( 5 );
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                if ( isThisUserStillCurrentActiveUser( socialUser ) ) {
                    setupMainWidget( socialUser, socialPaged );
                }
            }
        } ).findSocialUser( username );
    }

    private void setupMainWidget( SocialUser socialUser,
                                  SocialPaged socialPaged ) {
        String title = ( socialUser != null && socialUser.getRealName() != null && !socialUser.getRealName().isEmpty() ) ? socialUser.getRealName() : socialUser.getUserName();
        title += "'s Recent Activities";
        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( place, title ) );
        SimpleSocialTimelineWidgetModel model = new SimpleSocialTimelineWidgetModel( socialUser, new UserTimeLineOnlyUserActivityPredicate( socialUser ), placeManager, socialPaged )
                    .withIcons( iconLocator.getResourceTypes() )
                    .withOnlyMorePagination( new NavLink( "(more...)" ) )
                    .withLinkCommand( generateLinkCommand() );
        mainPresenter.setup( model );
    }

    private ParameterizedCommand<String> generateLinkCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                placeManager.goTo( "AuthoringPerspective" );
                socialFileSelectedEvent.fire( new SocialFileSelectedEvent( parameter ) );
            }
        };
    }

    private void generateConnectionsList( final SocialUser socialUser ) {
        header.clear();
        for ( final String follower : socialUser.getFollowingName() ) {
            socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
                public void callback( final SocialUser follower ) {
                    if ( isThisUserStillCurrentActiveUser( socialUser ) ) {
                        setupFollowerWidget( follower );
                    }
                }
            } ).findSocialUser( follower );
        }
        if ( isThisUserStillCurrentActiveUser( socialUser ) & thereIsNoFollowers( socialUser ) ) {
            header.noConnection();
        }
    }

    private void setupFollowerWidget( SocialUser socialUser ) {
        Image followerImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );

        UserBoxView.RelationType relationType = findRelationTypeWithLoggedUser( socialUser );

        header.addConnection( socialUser, relationType, followerImage, onClickEvent(), generateFollowUnfollowCommand( relationType ) );
    }

    private ParameterizedCommand<String> onClickEvent() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                userHomepageSelectedEvent.fire( new UserHomepageSelectedEvent( parameter ) );
            }
        };
    }

    private ParameterizedCommand<String> generateFollowUnfollowCommand( final UserBoxView.RelationType relationType ) {

        return new ParameterizedCommand<String>() {
            @Override
            public void execute( final String parameter ) {
                if ( relationType == UserBoxView.RelationType.CAN_FOLLOW ) {
                    socialUserServiceAPI.call().userFollowAnotherUser( loggedUser.getName(), parameter );
                } else {
                    socialUserServiceAPI.call().userUnfollowAnotherUser( loggedUser.getName(), parameter );
                }
                userHomepageSelectedEvent.fire( new UserHomepageSelectedEvent( lastUserOnpage ) );
            }
        };
    }

    private UserBoxView.RelationType findRelationTypeWithLoggedUser( SocialUser socialUser ) {
        if ( socialUser.getUserName().equalsIgnoreCase( loggedUser.getName() ) ) {
            return UserBoxView.RelationType.ME;
        } else {
            return socialUser.getFollowersName().contains( loggedUser.getName() ) ?
                    UserBoxView.RelationType.UNFOLLOW : UserBoxView.RelationType.CAN_FOLLOW;
        }
    }

    private boolean thereIsNoFollowers( SocialUser socialUser ) {
        return socialUser.getFollowingName() == null || socialUser.getFollowingName().isEmpty();
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

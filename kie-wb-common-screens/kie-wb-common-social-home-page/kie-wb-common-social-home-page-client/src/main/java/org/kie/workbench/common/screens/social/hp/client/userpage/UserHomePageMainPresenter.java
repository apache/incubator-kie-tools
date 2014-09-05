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
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
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
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.lifecycle.OnStartup;
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
    
    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
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

    public void watchUserHomepageSelectedEvent( @Observes UserEditedEvent event ) {
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
        final SocialPaged socialPaged = new SocialPaged( 5 );
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                String title = (socialUser!=null&&socialUser.getRealName()!=null&&!socialUser.getRealName().isEmpty())  ? socialUser.getRealName() : socialUser.getUserName();
                title += "'s Recent Activities";
                changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( place, title ) );
                SimpleSocialTimelineWidgetModel model = new SimpleSocialTimelineWidgetModel( socialUser, new UserTimeLineOnlyUserActivityPredicate( socialUser ), placeManager, socialPaged ).withIcons( iconLocator.getResourceTypes() ).withOnlyMorePagination( new NavLink( "(more...)" ) );
                mainPresenter.setup( model );
            }
        } ).findSocialUser( username );
    }

    private void generateConnectionsList( SocialUser socialUser ) {
        header.clear();
        for ( final String follower : socialUser.getFollowingName() ) {
            socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
                public void callback( SocialUser socialUser ) {
                    Image followerImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
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

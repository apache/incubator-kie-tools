package org.kie.workbench.common.screens.social.hp.client.userpage;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserServiceAPI;
import org.kie.workbench.common.screens.social.hp.client.UserHomepageSelectedEvent;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.EditUserForm;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.SideUserInfoPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.security.Identity;

@ApplicationScoped
@WorkbenchScreen(identifier = "UserHomePageSidePresenter")
public class UserHomePageSidePresenter {

    public interface View extends UberView<UserHomePageSidePresenter> {

        void setupUserMenu( String userName,
                            SideUserInfoPresenter widget );

        void setupAllPeopleMenu( List<String> users,
                                 ParameterizedCommand onSelect );
    }

    @Inject
    private View view;

    @Inject
    private Event<UserHomepageSelectedEvent> selectedEvent;

    @Inject
    private PlaceManager placeManager;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private Caller<SocialUserServiceAPI> socialUserService;

    @Inject
    private Identity loggedUser;

    @Inject
    SideUserInfoPresenter sideUserInfoPresenter;

    @Inject
    EditUserForm editUserForm;

    @OnOpen
    public void onOpen() {
        refreshPage( loggedUser.getName() );
    }

    public void watchUserHomepageSelectedEvent( @Observes UserHomepageSelectedEvent event ) {
        refreshPage( event.getSocialUserName() );
    }

    private void refreshPage( final String username ) {
        socialUserRepositoryAPI.call( new RemoteCallback<List<SocialUser>>() {
            public void callback( List<SocialUser> users ) {
                List<String> userNames = new ArrayList<String>();
                SocialUser userOnPage = null;
                for ( SocialUser user : users ) {
                    userNames.add( user.getUserName() );
                    if ( user.getUserName().equalsIgnoreCase( username ) ) {
                        userOnPage = user;
                    }
                }
                setupAllPeopleMenu( userNames );
                if ( userOnPage != null ) {
                    setupUserMenu( userOnPage );
                }

            }
        } ).findAllUsers();
    }

    private void setupAllPeopleMenu( List<String> userNames ) {
        view.setupAllPeopleMenu( userNames, new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                selectedEvent.fire( new UserHomepageSelectedEvent( parameter ) );
            }
        } );
    }

    private void setupUserMenu( SocialUser userOnPage ) {
        String userName = userOnPage.getRealName().isEmpty()?userOnPage.getUserName(): userOnPage.getRealName();
        view.setupUserMenu(userName , setupSideUserInfoPresenter( userOnPage ) );
    }

    private SideUserInfoPresenter setupSideUserInfoPresenter( SocialUser socialUser ) {
        Anchor anchor = generateActionLink( socialUser );
        sideUserInfoPresenter.setup( socialUser, GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.BIG ), anchor );
        return sideUserInfoPresenter;
    }

    private Anchor generateActionLink( final SocialUser socialUser ) {
        Anchor anchor = GWT.create( Anchor.class );
        if ( socialUser.getUserName().equalsIgnoreCase( loggedUser.getName() ) ) {
            createLoggedUserActionLink( socialUser, anchor );
        } else {
            createAnotherUserActionLink( socialUser, anchor );
        }
        return anchor;
    }

    private void createAnotherUserActionLink( final SocialUser socialUser,
                                              Anchor button ) {
        if ( loggedUserFollowSelectedUser( socialUser ) ) {
            generateUnFollowButton( socialUser, button );
        } else {
            generateFollowButton( socialUser, button );
        }
    }

    private void generateFollowButton( final SocialUser socialUser,
                                       Anchor button ) {
        button.setText( "Follow this user" );
        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                socialUserService.call().userFollowAnotherUser( loggedUser.getName(), socialUser.getUserName() );
                selectedEvent.fire( new UserHomepageSelectedEvent( socialUser.getUserName() ) );
            }
        } );
    }

    private void generateUnFollowButton( final SocialUser socialUser,
                                         Anchor button ) {
        button.setText( "Unfollow this user" );
        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                socialUserService.call().userUnfollowAnotherUser( loggedUser.getName(), socialUser.getUserName() );
                selectedEvent.fire( new UserHomepageSelectedEvent( socialUser.getUserName() ) );
            }
        } );
    }

    private boolean loggedUserFollowSelectedUser( SocialUser socialUser ) {
        return socialUser.getFollowersName().contains( loggedUser.getName() );
    }

    private void createLoggedUserActionLink( final SocialUser socialUser,
                                             Anchor button ) {
        button.setText( "Edit my infos" );
        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                editUserForm.show( socialUser, new ParameterizedCommand<SocialUser>() {
                    @Override
                    public void execute( SocialUser socialUser ) {
                        socialUserService.call().update( socialUser );
                        refreshPage( socialUser.getUserName() );
                    }
                } );
            }
        } );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "";
    }

    @WorkbenchPartView
    public UberView<UserHomePageSidePresenter> getView() {
        return view;
    }

}

/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.userpage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserImageRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserServiceAPI;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.LoadUserPageEvent;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserEditedEvent;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserHomepageSelectedEvent;
import org.kie.workbench.common.screens.social.hp.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.EditUserForm;
import org.kie.workbench.common.screens.social.hp.client.userpage.side.SideUserInfoPresenter;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
@WorkbenchScreen( identifier = "UserHomePageSidePresenter" )
public class UserHomePageSidePresenter {

    private PlaceRequest place;

    public interface View extends UberView<UserHomePageSidePresenter> {

        void setupUserInfo( String userName,
                            SideUserInfoPresenter widget );

        void setupSearchPeopleMenu( Set<String> userNames,
                                    ParameterizedCommand<String> parameterizedCommand,
                                    String suggestText );

        void setupHomeLink( Anchor anchor );

        void clear();
    }

    @Inject
    private View view;

    @Inject
    private Event<UserEditedEvent> userEditedEvent;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;

    @Inject
    private PlaceManager placeManager;

    @Inject
    Event<UserHomepageSelectedEvent> selectedEvent;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    Caller<SocialUserServiceAPI> socialUserService;

    @Inject
    User loggedUser;

    @Inject
    SideUserInfoPresenter sideUserInfoPresenter;

    @Inject
    EditUserForm editUserForm;

    Map<String, SocialUser> users;

    //control race conditions due to async system (cdi x UF lifecycle)
    private String lastUserOnpage;

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
        this.lastUserOnpage = loggedUser.getIdentifier();
        socialUserRepositoryAPI.call( new RemoteCallback<List<SocialUser>>() {
            public void callback( final List<SocialUser> users ) {
                setUsers( users );
                setupSearchPeopleMenu();
                refreshPage( lastUserOnpage );
            }
        } ).findAllUsers();
    }

    private void setUsers( final List<SocialUser> socialUsers ) {
        this.users = new HashMap<String, SocialUser>( socialUsers.size() );
        for ( final SocialUser user : socialUsers ) {
            users.put( user.getUserName(), user );
        }
    }

    @WorkbenchMenu
    public Menus buildMenu() {
        return MenuFactory
                .newTopLevelCustomMenu( new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push( MenuFactory.CustomMenuBuilder element ) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<Button>() {

                            @Override
                            public Button build() {
                                return new Button() {
                                    {
                                        setIcon( IconType.HOME );
                                        setTitle( Constants.INSTANCE.Home() );
                                        setSize( ButtonSize.SMALL );
                                        addClickHandler( new ClickHandler() {
                                            @Override
                                            public void onClick( ClickEvent event ) {
                                                refreshCacheAndGeneratesSelectEvent( loggedUser.getIdentifier() );
                                            }
                                        } );
                                    }
                                };
                            }
                        };
                    }
                } ).endMenu().build();
    }

    public void watchUserHomepageSelectedEvent( @Observes UserHomepageSelectedEvent event ) {
        this.lastUserOnpage = event.getSocialUserName();
        refreshPage( event.getSocialUserName() );
    }

    public void watchLoadUserPageEvent( @Observes LoadUserPageEvent event ) {
        this.lastUserOnpage = event.getSocialUserName();
        refreshPage( event.getSocialUserName() );
    }

    private boolean isThisUserStillCurrentActiveUser( String socialUser ) {
        return socialUser.equalsIgnoreCase( lastUserOnpage );
    }

    protected void refreshPage( final String username ) {
        if ( isThisUserStillCurrentActiveUser( username ) && usersWereRetrieved() ) {
            view.clear();
            refreshPageWidgets( username );
        }
    }

    private boolean usersWereRetrieved() {
        return users != null;
    }

    protected void refreshPageWidgets( final String username ) {
        final SocialUser userOnPage = users.get( username );
        if ( userOnPage != null ) {
            setupUserMenu( userOnPage );
        }
    }

    private void setupSearchPeopleMenu() {
        view.setupSearchPeopleMenu( users.keySet(), new ParameterizedCommand<String>() {
            @Override
            public void execute( String parameter ) {
                refreshCacheAndGeneratesSelectEvent( parameter );
            }
        }, "user login..." );
    }

    private void setupUserMenu( SocialUser userOnPage ) {
        String userName = ( userOnPage != null && userOnPage.getRealName() != null && !userOnPage.getRealName().isEmpty() ) ? userOnPage.getRealName() : userOnPage.getUserName();
        view.setupUserInfo( userName, setupSideUserInfoPresenter( userOnPage ) );
        final String title = userName + Constants.INSTANCE.UserProfile();
        changeTitleWidgetEvent.fire( new ChangeTitleWidgetEvent( this.place, title ) );
    }

    private SideUserInfoPresenter setupSideUserInfoPresenter( SocialUser socialUser ) {
        Button followUnfollow = generateActionLink( socialUser );
        sideUserInfoPresenter.setup( socialUser,
                                     GravatarBuilder.generate( socialUser, SocialUserImageRepositoryAPI.ImageSize.BIG ),
                                     followUnfollow );
        return sideUserInfoPresenter;
    }

    private Button generateActionLink( final SocialUser socialUser ) {
        Button followUnfollow = GWT.create( Button.class );

        if ( socialUser.getUserName().equalsIgnoreCase( loggedUser.getIdentifier() ) ) {
            createLoggedUserActionLink( socialUser, followUnfollow );
        } else {
            createAnotherUserActionLink( socialUser, followUnfollow );
        }
        return followUnfollow;
    }

    private void createAnotherUserActionLink( final SocialUser socialUser,
                                              Button followUnfollow ) {
        if ( loggedUserFollowSelectedUser( socialUser ) ) {
            generateUnFollowButton( socialUser, followUnfollow );
        } else {
            generateFollowButton( socialUser, followUnfollow );
        }
    }

    private void generateFollowButton( final SocialUser socialUser,
                                       Button button ) {
        button.setText( "Follow" );
        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                followUser( socialUser );
            }
        } );
    }

    private void generateUnFollowButton( final SocialUser socialUser,
                                         Button button ) {
        button.setText( "Unfollow" );
        button.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                unfollowUser( socialUser );
            }
        } );
    }

    void followUser( final SocialUser socialUser ) {
        socialUserService.call( new RemoteCallback<Object>() {
            @Override
            public void callback( Object o ) {
                refreshCacheAndGeneratesSelectEvent( socialUser.getUserName() );
            }
        } ).userFollowAnotherUser( loggedUser.getIdentifier(), socialUser.getUserName() );
    }

    void unfollowUser( final SocialUser socialUser ) {
        socialUserService.call( new RemoteCallback<Object>() {
            @Override
            public void callback( Object o ) {
                refreshCacheAndGeneratesSelectEvent( socialUser.getUserName() );
            }
        } ).userUnfollowAnotherUser( loggedUser.getIdentifier(),
                                     socialUser.getUserName() );
    }

    void refreshCacheAndGeneratesSelectEvent( final String userName ) {
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( final SocialUser user ) {
                users.put( user.getUserName(), user );
                selectedEvent.fire( new UserHomepageSelectedEvent( userName ) );
            }
        } ).findSocialUser( userName );
    }

    private boolean loggedUserFollowSelectedUser( SocialUser socialUser ) {
        return socialUser.getFollowersName().contains( loggedUser.getIdentifier() );
    }

    private void createLoggedUserActionLink( final SocialUser socialUser,
                                             final Button followUnfollow ) {
        followUnfollow.setText( "Edit" );
        followUnfollow.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                editUserForm.show( socialUser, new ParameterizedCommand<SocialUser>() {
                    @Override
                    public void execute( SocialUser socialUser ) {
                        socialUserService.call().update( socialUser );
                        refreshPage( socialUser.getUserName() );
                        userEditedEvent.fire( new UserEditedEvent( socialUser.getUserName() ) );
                    }
                } );
            }
        } );
    }

    protected void setLastUserOnpage( String lastUserOnpage ) {
        this.lastUserOnpage = lastUserOnpage;
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

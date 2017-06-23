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

package org.kie.workbench.common.screens.social.hp.client.homepage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.security.shared.api.identity.User;
import org.ext.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.SocialTimelineWidget;
import org.ext.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserServiceAPI;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.LoadUserPageEvent;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserHomepageSelectedEvent;
import org.kie.workbench.common.screens.social.hp.client.homepage.header.HeaderPresenter;
import org.kie.workbench.common.screens.social.hp.client.homepage.main.MainPresenter;
import org.kie.workbench.common.screens.social.hp.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.social.hp.client.util.IconLocator;
import org.kie.workbench.common.screens.social.hp.service.RepositoryListService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
@WorkbenchScreen(identifier = "SocialHomePageMainPresenter")
public class SocialHomePageMainPresenter {

    public interface View extends UberView<SocialHomePageMainPresenter> {

        void setHeader( final HeaderPresenter header );

        void setMain( MainPresenter main );
    }

    @Inject
    private View view;

    @Inject
    private Event<UserHomepageSelectedEvent> selectedEvent;

    @Inject
    private Event<LoadUserPageEvent> loadUserPageEvent;

    @Inject
    private HeaderPresenter header;

    @Inject
    private MainPresenter main;

    @Inject
    private Caller<RepositoryListService> repositoryService;

    @Inject
    private Caller<OrganizationalUnitService> organizationalUnitService;

    @Inject
    Caller<SocialEventTypeRepositoryAPI> eventTypeRepositoryAPI;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private User loggedUser;

    @Inject
    private IconLocator iconLocator;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<SocialUserServiceAPI> socialUserService;

    @Inject
    private DefaultSocialLinkCommandGenerator linkCommandGenerator;

    @PostConstruct
    public void init() {
        view.setHeader( header );
        view.setMain( main );
        loadContent();
    }

    @OnOpen
    public void onOpen() {
        setupPage();
    }

    private void setupPage() {
        initHeader();
        initMain();
    }

    private void initMain() {
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            @Override
            public void callback( SocialUser socialUser ) {
                updateMainTimeline( "", socialUser );

            }
        } ).findSocialUser( loggedUser.getIdentifier() );
    }

    private void initHeader() {

        header.setOnSelectCommand( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String param ) {

                socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
                    @Override
                    public void callback( SocialUser socialUser ) {
                        updateMainTimeline( param, socialUser );

                    }
                } ).findSocialUser( loggedUser.getIdentifier() );

            }
        } );
        createHeaderMenuList();
    }

    private void updateMainTimeline( String param,
                                     final SocialUser socialUser ) {
        if ( param.contains( Constants.INSTANCE.AllRepositories() ) ) {
            param = "";
        }
        SocialTimelineWidget socialTimelineWidget = GWT.create( SocialTimelineWidget.class );
        List<ClientResourceType> resourceTypes = iconLocator.getResourceTypes();
        SocialTimelineWidgetModel model = new SocialTimelineWidgetModel( socialUser, placeManager, resourceTypes )
                .withUserClickCommand( generateUserClickCommand() )
                .withFollowUnfollowCommand( generateFollowUnfollowCommand() )
                .withLinkCommand( generateLinkCommand() );
        Map<String, String> globals = new HashMap();
        globals.put( "filter", param );
        model.droolsQuery( globals, "filterTimelineRecentAssets", "10" );
        socialTimelineWidget.init( model );
        main.setSocialWidget( socialTimelineWidget );
    }

    private ParameterizedCommand<LinkCommandParams> generateLinkCommand() {
        return linkCommandGenerator.generateLinkCommand();
    }

    private boolean loggedUserFollowSelectedUser( SocialUser socialUser ) {
        return socialUser.getFollowersName().contains( loggedUser.getIdentifier() );
    }

    private ParameterizedCommand<String> generateFollowUnfollowCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( final String parameter ) {
                socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
                    @Override
                    public void callback( SocialUser socialUser ) {
                        if ( loggedUserFollowSelectedUser( socialUser ) ) {
                            socialUserService.call().userUnfollowAnotherUser( loggedUser.getIdentifier(), socialUser.getUserName() );
                        } else {
                            socialUserService.call().userFollowAnotherUser( loggedUser.getIdentifier(), socialUser.getUserName() );
                        }
                        setupPage();
                    }
                } ).findSocialUser( parameter );
            }
        };
    }

    private ParameterizedCommand<String> generateUserClickCommand() {
        return new ParameterizedCommand<String>() {
            @Override
            public void execute( final String parameter ) {
                placeManager.goTo( "UserHomePagePerspective" );
                loadUserPageEvent.fire( new LoadUserPageEvent( parameter ) );
            }
        };
    }

    private void createHeaderMenuList() {
        final List<String> reposNames = new ArrayList<String>();
        reposNames.add( Constants.INSTANCE.AllRepositories() );
        repositoryService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> repositories ) {
                for ( String repository : repositories ) {
                    reposNames.add( repository );
                }
                header.setUpdatesMenuList( reposNames );
            }

        } ).getRepositories();
    }

    public void loadContent() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.LatestChanges();
    }

    @WorkbenchPartView
    public UberView<SocialHomePageMainPresenter> getView() {
        return view;
    }

}

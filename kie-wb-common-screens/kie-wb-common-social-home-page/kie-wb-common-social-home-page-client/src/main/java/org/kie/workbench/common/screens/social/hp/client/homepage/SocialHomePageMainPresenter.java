package org.kie.workbench.common.screens.social.hp.client.homepage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryInfo;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.SocialTimelineWidget;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.screens.social.hp.client.homepage.header.HeaderPresenter;
import org.kie.workbench.common.screens.social.hp.client.homepage.main.MainPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.security.Identity;

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
    private HeaderPresenter header;

    @Inject
    private MainPresenter main;

    @Inject
    private Caller<RepositoryService> repositoryService;

    @Inject
    private Caller<OrganizationalUnitService> organizationalUnitService;

    @Inject
    Caller<SocialEventTypeRepositoryAPI> eventTypeRepositoryAPI;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private Identity loggedUser;

    @Inject
    private PlaceManager placeManager;


    @AfterInitialization
    public void init() {
        initHeader();

        initMain();
    }

    private void initMain() {
        view.setMain( main );

    }

    private void initHeader() {

        view.setHeader( header );

        header.setOnSelectCommand( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String param ) {

                socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
                    public void callback( SocialUser socialUser ) {
                        setupHeaderOnSelect( param, socialUser );

                    }
                } ).findSocialUser( loggedUser.getName() );

            }
        } );
        createHeaderMenuList();
        // widgets from UX (not yet implemented)
//        header.setNumberOfItemsLabel( "(12)" );
//        header.setViewAllCommand( new Command() {
//            @Override
//            public void execute() {
//                Window.alert( "setViewAllCommand" );
//            }
//        } );
    }

    private void setupHeaderOnSelect( String param,
                                      SocialUser socialUser ) {
        if ( param.contains( "Latest" ) ) {
            SocialTimelineWidget socialTimelineWidget = GWT.create( SocialTimelineWidget.class );
            SocialTimelineWidgetModel model = new SocialTimelineWidgetModel( "Latest Changes", socialUser, placeManager );
            socialTimelineWidget.init( model );
            main.setSocialWidget( socialTimelineWidget );
        } else {
            SocialTimelineWidget socialTimelineWidget = GWT.create( SocialTimelineWidget.class );
            SocialTimelineWidgetModel model = new SocialTimelineWidgetModel( "Latest Changes", socialUser, placeManager );
            Map<String, String> globals = new HashMap();
            globals.put( "filter", param );
            model.droolsQuery( globals, "filterTimelineRecentAssets" );
            socialTimelineWidget.init( model );
            main.setSocialWidget( socialTimelineWidget );
        }
    }

    private void createHeaderMenuList() {
        final List<String> reposNames = new ArrayList<String>();
        reposNames.add( "Latest Changes" );
        repositoryService.call( new RemoteCallback<Collection<Repository>>() {
            public void callback( Collection<Repository> repositories ) {
                for ( Repository repository : repositories ) {
                    reposNames.add( repository.getAlias() );
                }
                header.setUpdatesMenuList( reposNames );
            }

        } ).getRepositories();
    }

    @AfterInitialization
    public void loadContent() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "SocialHomePageMainPresenter";
    }

    @WorkbenchPartView
    public UberView<SocialHomePageMainPresenter> getView() {
        return view;
    }

}

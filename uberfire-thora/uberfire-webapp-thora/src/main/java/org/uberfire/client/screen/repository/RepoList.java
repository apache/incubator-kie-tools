package org.uberfire.client.screen.repository;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.shared.repository.NewRepositoryInfo;
import org.uberfire.shared.repository.RepositoryAppService;
import org.uberfire.shared.repository.RepositoryInfo;

@WorkbenchScreen(identifier = "RepoList")
@Templated("repo-list.html")
@Dependent
public class RepoList extends Composite {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<RepositoryAppService> repositoryAppService;

    @DataField
    private UListElement repoList = Document.get().createULElement();

    @DataField
    @Inject
    private Button newRepo;

    @WorkbenchPartTitle
    public String getTitle() {
        return "Home";
    }

    @OnStartup
    public void setup() {
        repositoryAppService.call( new RemoteCallback<List<RepositoryInfo>>() {
            @Override
            public void callback( final List<RepositoryInfo> response ) {
                for ( final RepositoryInfo activeRepo : response ) {
                    repoList.appendChild( RepositoryUtil.newRepo( placeManager, activeRepo, true ) );
                }
            }
        } ).getAllRepositories();
    }

    public void onNewRepoInfo( @Observes final NewRepositoryInfo event ) {
        repoList.appendChild( RepositoryUtil.newRepo( placeManager, event.getRepositoryInfo(), true ) );
    }

    @EventHandler("newRepo")
    public void newRepo( ClickEvent e ) {
        placeManager.goTo( new DefaultPlaceRequest( "NewRepo" ) );
    }

}

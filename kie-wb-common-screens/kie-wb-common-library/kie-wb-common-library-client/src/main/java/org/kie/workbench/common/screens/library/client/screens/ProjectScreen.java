/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.library.api.LibraryContextSwitchEvent;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.events.AssetDetailEvent;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryBreadcrumbs;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@WorkbenchScreen( identifier = LibraryPlaces.PROJECT_SCREEN,
        owningPerspective = LibraryPerspective.class )
public class ProjectScreen {

    public interface View extends UberElement<ProjectScreen> {

        void clearAssets();

        void addAsset( String assetName,
                       String assetType,
                       IsWidget assetIcon,
                       Command details,
                       Command select );

        void noRightsPopup();
    }

    private View view;

    private PlaceManager placeManager;

    private LibraryBreadcrumbs libraryBreadcrumbs;

    private Event<LibraryContextSwitchEvent> libraryContextSwitchEvent;

    private SessionInfo sessionInfo;

    private AuthorizationManager authorizationManager;

    private TranslationService ts;

    private Caller<LibraryService> libraryService;

    private Classifier assetClassifier;

    private Event<AssetDetailEvent> assetDetailEvent;

    private Project project;

    private List<FolderItem> assets;

    @Inject
    public ProjectScreen( final View view,
                          final PlaceManager placeManager,
                          final LibraryBreadcrumbs libraryBreadcrumbs,
                          final Event<LibraryContextSwitchEvent> libraryContextSwitchEvent,
                          final SessionInfo sessionInfo,
                          final AuthorizationManager authorizationManager,
                          final TranslationService ts,
                          final Caller<LibraryService> libraryService,
                          final Classifier assetClassifier,
                          final Event<AssetDetailEvent> assetDetailEvent ) {
        this.view = view;
        this.placeManager = placeManager;
        this.libraryBreadcrumbs = libraryBreadcrumbs;
        this.libraryContextSwitchEvent = libraryContextSwitchEvent;
        this.sessionInfo = sessionInfo;
        this.authorizationManager = authorizationManager;
        this.ts = ts;
        this.libraryService = libraryService;
        this.assetClassifier = assetClassifier;
        this.assetDetailEvent = assetDetailEvent;
    }

    public void onStartup( @Observes final ProjectDetailEvent projectDetailEvent ) {
        this.project = projectDetailEvent.getProjectSelected();
        loadProjectInfo();
    }

    private void loadProjectInfo() {
        libraryService.call( new RemoteCallback<List<FolderItem>>() {
            @Override
            public void callback( List<FolderItem> assetsList ) {
                assets = assetsList;
                loadProject( assets );
            }
        } ).getProjectAssets( project );
    }

    private void loadProject( List<FolderItem> assets ) {
        setupAssets( assets );
        setupToolBar();
    }

    private void setupToolBar() {
        libraryBreadcrumbs.setupLibraryBreadCrumbsForProject( project );
    }

    private void setupAssets( List<FolderItem> assets ) {
        view.clearAssets();

        assets.stream().forEach( asset -> {
            if ( !asset.getType().equals( FolderItemType.FOLDER ) ) {
                final ClientResourceType assetResourceType = assetClassifier.findResourceType( asset );
                final String assetName = Utils.getBaseFileName( asset.getFileName(), assetResourceType.getSuffix() );

                view.addAsset( assetName,
                               assetResourceType.getDescription(),
                               assetResourceType.getIcon(),
                               detailsCommand( asset ),
                               selectCommand( assetName, ( (Path) asset.getItem() ) ) );
            }
        } );
    }

    Command selectCommand( final String assetName,
                           final Path assetPath ) {
        return () -> {
            placeManager.goTo( LibraryPlaces.ASSET_PERSPECTIVE );
            assetDetailEvent.fire( new AssetDetailEvent( project, assetPath ) );
        };
    }

    boolean hasAccessToPerspective( String perspectiveId ) {
        ResourceRef resourceRef = new ResourceRef( perspectiveId, ActivityResourceType.PERSPECTIVE );
        return authorizationManager.authorize( resourceRef, sessionInfo.getIdentity() );
    }

    private Command detailsCommand( FolderItem asset ) {
        // TODO Show asset details
        return () -> {};
    }

    public void updateAssetsBy( String filter ) {
        if ( assets != null ) {
            List<FolderItem> filteredAssets = filterAssets( assets, filter );
            setupAssets( filteredAssets );
        }
    }

    List<FolderItem> filterAssets( final List<FolderItem> assets,
                                   final String filter ) {
        return assets.stream()
                .filter( a -> a.getFileName().toUpperCase().startsWith( filter.toUpperCase() ) )
                .collect( Collectors.toList() );
    }

    public void newAsset() {
        if ( hasAccessToPerspective( LibraryPlaces.AUTHORING ) ) {
            placeManager.goTo( new DefaultPlaceRequest( LibraryPlaces.AUTHORING ) );
            libraryContextSwitchEvent.fire( new LibraryContextSwitchEvent( LibraryContextSwitchEvent.EventType.PROJECT_SELECTED,
                                                                           project.getRootPath(),
                                                                           () -> libraryBreadcrumbs.setupLibraryBreadCrumbsForProject( project ) ) );
        } else {
            view.noRightsPopup();
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation( LibraryConstants.LibraryScreen );
    }

    @WorkbenchPartView
    public UberElement<ProjectScreen> getView() {
        return view;
    }
}

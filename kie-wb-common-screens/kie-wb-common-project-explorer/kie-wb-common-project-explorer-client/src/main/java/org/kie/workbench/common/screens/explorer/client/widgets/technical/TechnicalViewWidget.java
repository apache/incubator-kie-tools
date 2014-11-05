/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.technical;

import com.github.gwtbootstrap.client.ui.Button;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.event.Observes;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.client.widgets.BranchChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.BranchSelector;
import org.kie.workbench.common.screens.explorer.client.widgets.View;
import org.kie.workbench.common.screens.explorer.client.widgets.ViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorOptions;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.uberfire.client.common.BusyPopup;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Technical View implementation
 */
@ApplicationScoped
public class TechnicalViewWidget extends Composite implements View {

    interface TechnicalViewImplBinder
            extends
            UiBinder<Widget, TechnicalViewWidget> {

    }

    private static TechnicalViewImplBinder uiBinder = GWT.create( TechnicalViewImplBinder.class );

    @UiField
    Explorer explorer;


    @UiField(provided = true)
    @Inject
    BranchSelector branchSelector;
    
    @UiField
    Button openProjectEditorButton;
    
    
    
    @Inject
    PlaceManager placeManager;

    private final NavigatorOptions techOptions = new NavigatorOptions() {{
        showFiles( true );
        showHiddenFiles( false );
        showDirectories( true );
        allowUpLink( true );
        showItemAge( false );
        showItemMessage( false );
        showItemLastUpdater( false );
    }};

    private ViewPresenter presenter;

    @PostConstruct
    public void init() {
        //Cannot create and bind UI until after injection points have been initialized
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final ViewPresenter presenter ) {
        this.presenter = presenter;
        explorer.init( Explorer.Mode.EXPANDED, techOptions, Explorer.NavType.BREADCRUMB, presenter );
        openProjectEditorButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                placeManager.goTo("projectScreen");
            }
        });
    }
    
    //@TODO: we need to remove these two when we remove the projectScreen from here
    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        enableToolsMenuItems( (KieProject) event.getProject() );
    }

    private void enableToolsMenuItems( final KieProject project ) {
        final boolean enabled = ( project != null );
        openProjectEditorButton.setEnabled(enabled);
    }

    @Override
    public void setContent( final Set<OrganizationalUnit> organizationalUnits,
                            final OrganizationalUnit activeOrganizationalUnit,
                            final Set<Repository> repositories,
                            final Repository activeRepository,
                            final Set<Project> projects,
                            final Project activeProject,
                            final FolderListing folderListing,
                            final Map<FolderItem, List<FolderItem>> siblings ) {
        explorer.setupHeader( organizationalUnits, activeOrganizationalUnit,
                              repositories, activeRepository,
                              projects, activeProject );
        explorer.loadContent( folderListing, siblings );

        branchSelector.setRepository(activeRepository);

    }

    @Override
    public void setItems( final FolderListing activeFolderListing ) {
        explorer.loadContent( activeFolderListing );
    }

    @Override
    public void setOptions( final Set<Option> options ) {

        techOptions.showHiddenFiles( options.contains( Option.INCLUDE_HIDDEN_ITEMS ) );

        if ( options.contains( Option.TREE_NAVIGATOR ) ) {
            explorer.setNavType( Explorer.NavType.TREE, techOptions );
        } else {
            explorer.setNavType( Explorer.NavType.BREADCRUMB, techOptions );
        }
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public Explorer getExplorer() {
        return explorer;
    }

    public void addBranchChangeHandler(BranchChangeHandler branchChangeHandler) {
        branchSelector.addBranchChangeHandler(branchChangeHandler);
    }
}

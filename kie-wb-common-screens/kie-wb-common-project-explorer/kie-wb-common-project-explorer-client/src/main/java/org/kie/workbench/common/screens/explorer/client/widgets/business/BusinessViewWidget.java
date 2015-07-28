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
package org.kie.workbench.common.screens.explorer.client.widgets.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.resources.images.ProjectExplorerImageResources;
import org.kie.workbench.common.screens.explorer.client.utils.Classifier;
import org.kie.workbench.common.screens.explorer.client.utils.Utils;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewImpl;
import org.kie.workbench.common.screens.explorer.client.widgets.BranchChangeHandler;
import org.kie.workbench.common.screens.explorer.client.widgets.BranchSelector;
import org.kie.workbench.common.screens.explorer.client.widgets.View;
import org.kie.workbench.common.screens.explorer.client.widgets.ViewPresenter;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorOptions;
import org.kie.workbench.common.screens.explorer.client.widgets.tagSelector.TagSelector;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.accordion.TriggerWidget;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

/**
 * Business View implementation
 */
@ApplicationScoped
public class BusinessViewWidget extends BaseViewImpl implements View {

    interface BusinessViewImplBinder
            extends
            UiBinder<Widget, BusinessViewWidget> {

    }

    private static BusinessViewImplBinder uiBinder = GWT.create( BusinessViewImplBinder.class );

    private static final String ID_CLEANUP_PATTERN = "[^a-zA-Z0-9]";

    @UiField
    Explorer explorer;

    @UiField
    PanelGroup itemsContainer;

    @UiField(provided = true)
    @Inject
    BranchSelector branchSelector;

    @UiField(provided = true)
    @Inject
    TagSelector tagSelector;

    @Inject
    Classifier classifier;

    @Inject
    PlaceManager placeManager;

    @Inject
    User user;

    //TreeSet sorts members upon insertion
    private final Set<FolderItem> sortedFolderItems = new TreeSet<FolderItem>( Sorters.ITEM_SORTER );

    private final NavigatorOptions businessOptions = new NavigatorOptions() {{
        showFiles( false );
        showHiddenFiles( false );
        showDirectories( true );
        allowUpLink( false );
        showItemAge( false );
        showItemMessage( false );
        showItemLastUpdater( false );
    }};

    private ViewPresenter presenter;

    @PostConstruct
    public void init() {
        //Cannot create and bind UI until after injection points have been initialized
        initWidget( uiBinder.createAndBindUi( this ) );
        itemsContainer.setId( DOM.createUniqueId() );
    }

    @Override
    public void init( final ViewPresenter presenter ) {
        this.presenter = presenter;
        explorer.init( Explorer.Mode.COLLAPSED, businessOptions, Explorer.NavType.TREE, presenter );
    }

    @Override
    public void setContent( final Set<OrganizationalUnit> organizationalUnits,
                            final OrganizationalUnit organizationalUnit,
                            final Set<Repository> repositories,
                            final Repository repository,
                            final Set<Project> projects,
                            final Project project,
                            final FolderListing folderListing,
                            final Map<FolderItem, List<FolderItem>> siblings ) {
        explorer.setupHeader( organizationalUnits, organizationalUnit,
                              repositories, repository,
                              projects, project );
        explorer.loadContent( folderListing, siblings );

        branchSelector.setRepository( repository );

        setItems( folderListing );
    }

    @Override
    public void setItems( final FolderListing folderListing ) {
        renderItems( folderListing );
    }

    @Override
    public void renderItems( FolderListing folderListing ) {
        tagSelector.loadContent( presenter.getActiveContentTags(), presenter.getCurrentTag() );
        itemsContainer.clear();
        sortedFolderItems.clear();
        for ( final FolderItem content : folderListing.getContent() ) {
            if ( !content.getType().equals( FolderItemType.FOLDER ) ) {
                sortedFolderItems.add( content );
            }
        }

        if ( !sortedFolderItems.isEmpty() ) {
            final Map<ClientResourceType, Collection<FolderItem>> resourceTypeGroups = classifier.group( sortedFolderItems );
            final TreeMap<ClientResourceType, Collection<FolderItem>> sortedResourceTypeGroups = new TreeMap<ClientResourceType, Collection<FolderItem>>( Sorters.RESOURCE_TYPE_GROUP_SORTER );
            sortedResourceTypeGroups.putAll( resourceTypeGroups );

            for ( final Map.Entry<ClientResourceType, Collection<FolderItem>> entry : sortedResourceTypeGroups.entrySet() ) {
                final LinkedGroup itemsNavList = new LinkedGroup();
                itemsNavList.getElement().getStyle().setMarginBottom( 0, Style.Unit.PX );
                final PanelCollapse collapse = new PanelCollapse();
                collapse.setIn( true );
                collapse.setId( getCollapseId( entry.getKey() ) );
                final PanelBody body = new PanelBody();
                body.getElement().getStyle().setPadding( 0, Style.Unit.PX );
                collapse.add( body );
                body.add( itemsNavList );

                for ( FolderItem folderItem : entry.getValue() ) {
                    itemsNavList.add( makeItemNavLink( entry.getKey(),
                                                       folderItem ) );
                }

                itemsContainer.add( new Panel() {{
                    add( makeTriggerWidget( entry.getKey(), collapse ) );
                    add( collapse );
                }} );
            }
        } else {
            itemsContainer.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    private TriggerWidget makeTriggerWidget( final ClientResourceType resourceType,
                                             final PanelCollapse collapse ) {
        final String description = getResourceTypeDescription( resourceType );
        if ( resourceType.getIcon() != null ) {
            return new TriggerWidget( resourceType.getIcon(), description, !collapse.isIn() ) {{
                setDataToggle( Toggle.COLLAPSE );
                setDataParent( itemsContainer.getId() );
                setDataTargetWidget( collapse );
            }};
        }
        return new TriggerWidget( description, !collapse.isIn() ) {{
            setDataToggle( Toggle.COLLAPSE );
            setDataParent( itemsContainer.getId() );
            setDataTargetWidget( collapse );
        }};
    }

    @Override
    public void setOptions( final ActiveOptions options ) {
    }

    @Override
    public void setNavType( Explorer.NavType navType ) {
        explorer.setNavType( navType, businessOptions );
    }

    @Override
    public void hideTagFilter() {
        tagSelector.hide();
        if ( presenter.getActiveContent() != null ) {
            renderItems( presenter.getActiveContent() );
        }
    }

    @Override
    public void showTagFilter() {
        tagSelector.show();
    }

    @Override
    public void hideHeaderNavigator() {
        explorer.hideHeaderNavigator();
    }

    @Override
    public Explorer getExplorer() {
        return explorer;
    }

    private String getResourceTypeDescription( final ClientResourceType resourceType ) {
        String description = resourceType.getDescription();
        description = ( description == null || description.isEmpty() ) ? ProjectExplorerConstants.INSTANCE.miscellaneous_files() : description;
        return description;
    }

    private IsWidget makeItemNavLink( final ClientResourceType resourceType,
                                      final FolderItem folderItem ) {
        String _fileName = folderItem.getFileName();
        if ( !( resourceType instanceof AnyResourceType ) ) {
            _fileName = Utils.getBaseFileName( _fileName );
        }
        _fileName = _fileName.replaceAll( " ", "\u00a0" );
        final String fileName = _fileName;

        final LinkedGroupItem navLink = new LinkedGroupItem() {{
            setText( fileName );
            addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    presenter.itemSelected( folderItem );
                }
            } );
        }};

        Image lockImage;
        if ( folderItem.getLockedBy() == null ) {
            lockImage = new Image( ProjectExplorerImageResources.INSTANCE.lockEmpty() );
        } else if ( folderItem.getLockedBy().equals( user.getIdentifier() ) ) {
            lockImage = new Image( ProjectExplorerImageResources.INSTANCE.lockOwned() );
            lockImage.setTitle( ProjectExplorerConstants.INSTANCE.lockOwnedHint() );
        } else {
            lockImage = new Image( ProjectExplorerImageResources.INSTANCE.lock() );
            lockImage.setTitle( ProjectExplorerConstants.INSTANCE.lockHint() + " " + folderItem.getLockedBy() );
        }

        navLink.getWidget( 0 )
                .getElement()
                .setInnerHTML( "<span>" + lockImage.toString() + " " + fileName + "</span>" );

        return navLink;
    }

    private String getCollapseId( ClientResourceType resourceType ) {
        return resourceType != null ? resourceType.getShortName().replaceAll( ID_CLEANUP_PATTERN, "" ) : "";
    }

    public void addBranchChangeHandler( BranchChangeHandler branchChangeHandler ) {
        branchSelector.addBranchChangeHandler( branchChangeHandler );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

}

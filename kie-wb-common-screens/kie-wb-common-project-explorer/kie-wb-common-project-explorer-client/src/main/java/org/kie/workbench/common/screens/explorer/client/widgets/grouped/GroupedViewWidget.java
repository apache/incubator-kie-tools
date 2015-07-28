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
package org.kie.workbench.common.screens.explorer.client.widgets.grouped;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.Divider;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.WellNavList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
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
import org.kie.workbench.common.screens.explorer.client.widgets.business.TriggerWidget;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.Explorer;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorOptions;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderItemType;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.kie.workbench.common.screens.explorer.utils.Sorters;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

/**
 * Grouped View implementation
 */
@ApplicationScoped
public class GroupedViewWidget extends BaseViewImpl implements View {

    interface GroupedViewBinder
            extends
            UiBinder<Widget, GroupedViewWidget> {

    }

    private static GroupedViewBinder uiBinder = GWT.create( GroupedViewBinder.class );

    private static final String ID_CLEANUP_PATTERN = "[^a-zA-Z0-9]";

    @UiField
    Explorer explorer;

    @UiField
    WellNavList itemsContainer;

    @UiField(provided = true)
    @Inject
    BranchSelector branchSelector;

    @UiField
    Button openProjectEditorButton;

    @Inject
    Classifier classifier;

    @Inject
    PlaceManager placeManager;

    @Inject
    User user;

    private Map<String, List<FolderItem>> itemsByTag = new HashMap<String, List<FolderItem>>();

    private final NavigatorOptions groupedOptions = new NavigatorOptions() {{
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
    }

    @Override
    public void init( final ViewPresenter presenter ) {
        this.presenter = presenter;
        explorer.init( Explorer.Mode.COLLAPSED, groupedOptions, Explorer.NavType.TREE, presenter );
        openProjectEditorButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                placeManager.goTo( "projectScreen" );
            }
        } );
    }

    //@TODO: we need to remove these two when we remove the projectScreen from here
    public void onProjectContextChanged( @Observes final ProjectContextChangeEvent event ) {
        enableToolsMenuItems( (KieProject) event.getProject() );
    }

    private void enableToolsMenuItems( final KieProject project ) {
        final boolean enabled = ( project != null );
        openProjectEditorButton.setEnabled( enabled );
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

    protected void addItemByTag( String tag,
                                 FolderItem item ) {
        List<FolderItem> items = itemsByTag.get( tag );
        if ( items == null ) {
            itemsByTag.put( tag, items = new ArrayList<FolderItem>() );
        }
        items.add( item );
    }

    @Override
    public void setItems( final FolderListing folderListing ) {
        itemsContainer.clear();

        itemsByTag.clear();

        for ( final FolderItem content : folderListing.getContent() ) {
            if ( !content.getType().equals( FolderItemType.FOLDER ) ) {
                if ( content.getTags() == null || content.getTags().isEmpty() ) {
                    addItemByTag( "", content );
                    continue;
                }
                for ( String tag : content.getTags() ) {
                    addItemByTag( tag, content );
                }
            }
        }

        if ( !itemsByTag.isEmpty() ) {
            List<String> tags = new ArrayList<String>( itemsByTag.keySet() );

            Collections.sort( tags, new Comparator<String>() {
                @Override
                public int compare( String o1,
                                    String o2 ) {
                    return o1.compareTo( o2 );
                }
            } );

            for ( int i = 0; i < tags.size(); i++ ) {
                if ( i > 0 ) {
                    itemsContainer.add( new Divider() );
                }
                String tag = tags.get( i );
                List<FolderItem> items = itemsByTag.get( tag );

                String id = getCollapseId( tag, null );
                CollapseTrigger tagTrigger = new CollapseTrigger( "#" + id );

                tagTrigger.setWidget( new TriggerWidget( tag.equals( "" ) ? ProjectExplorerConstants.INSTANCE.untagged() : tag ) );

                Collapse tagContent = new Collapse();
                tagContent.setExistTrigger( true );
                tagContent.setId( id );

                NavList content = new NavList();

                addChildItems( tag, items, content );

                tagContent.add( content );
                itemsContainer.add( tagTrigger );
                itemsContainer.add( tagContent );
            }

        } else {
            itemsContainer.add( new Label( ProjectExplorerConstants.INSTANCE.noItemsExist() ) );
        }
    }

    protected void addChildItems( String tag,
                                  List<FolderItem> items,
                                  ComplexPanel content ) {
        final Map<ClientResourceType, Collection<FolderItem>> resourceTypeGroups = classifier.group( items );
        final TreeMap<ClientResourceType, Collection<FolderItem>> sortedResourceTypeGroups = new TreeMap<ClientResourceType, Collection<FolderItem>>( Sorters.RESOURCE_TYPE_GROUP_SORTER );
        sortedResourceTypeGroups.putAll( resourceTypeGroups );

        Map<String, Collapse> collapses = new HashMap<String, Collapse>();

        final Iterator<Map.Entry<ClientResourceType, Collection<FolderItem>>> itr = sortedResourceTypeGroups.entrySet().iterator();
        while ( itr.hasNext() ) {
            final Map.Entry<ClientResourceType, Collection<FolderItem>> e = itr.next();

            ClientResourceType resourceType = e.getKey();

            final CollapseTrigger collapseTrigger = new CollapseTrigger( "#" + getCollapseId( tag, resourceType ) );
            final String description = getResourceTypeDescription( resourceType );
            final IsWidget icon = resourceType.getIcon();
            if ( icon == null ) {
                collapseTrigger.setWidget( new TriggerWidget( description ) );
                ;
            } else {
                SimplePanel iconPanel = new SimplePanel();
                iconPanel.getElement().appendChild( DOM.clone( icon.asWidget().getElement(), true ) );
                collapseTrigger.setWidget( new TriggerWidget( iconPanel, description ) );
            }

            final Collapse collapse = new Collapse();
            collapse.setExistTrigger( true );
            final String collapseId = getCollapseId( tag, e.getKey() );
            collapse.setId( collapseId );

            final NavList itemsNavList = new NavList();
            collapse.add( itemsNavList );
            for ( FolderItem folderItem : e.getValue() ) {
                itemsNavList.add( makeItemNavLink( e.getKey(),
                                                   folderItem ) );
            }
            collapse.setDefaultOpen( false );

            Collapse oldCollapse = collapses.get( collapseId );
            if ( oldCollapse != null ) {
                final String classAttr = oldCollapse.getWidget().getElement().getAttribute( "class" );
                collapse.getWidget().getElement().setAttribute( "class", classAttr );
            }
            collapses.put( collapseId, collapse );

            content.add( collapseTrigger );
            content.add( collapse );
            if ( itr.hasNext() ) {
                content.add( new Divider() );
            }
        }

    }

    @Override
    public void setOptions( final Set<Option> options ) {
        if ( options.contains( Option.TREE_NAVIGATOR ) ) {
            explorer.setNavType( Explorer.NavType.TREE, groupedOptions );
        } else {
            explorer.setNavType( Explorer.NavType.BREADCRUMB, groupedOptions );
        }
        if ( options.contains( Option.NO_CONTEXT_NAVIGATION ) ) {
            explorer.hideHeaderNavigator();
        }
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
        String fileName = folderItem.getFileName();
        if ( !( resourceType instanceof AnyResourceType ) ) {
            fileName = Utils.getBaseFileName( fileName );
        }
        fileName = fileName.replaceAll( " ", "\u00a0" );
        final NavLink navLink = new NavLink( fileName );
        navLink.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.itemSelected( folderItem );
            }
        } );

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

    private String getCollapseId( String tag,
                                  ClientResourceType resourceType ) {
        String id = tag;

        if ( id.equals( "" ) ) {
            id = "untagged";
        }

        if ( resourceType != null ) {
            id += "-" + resourceType.getShortName().replaceAll( ID_CLEANUP_PATTERN, "" );
        }

        return id;
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

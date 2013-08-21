package org.kie.workbench.common.screens.group.manager.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.group.manager.client.resources.i18n.GroupManagerConstants;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.BusyPopup;

/**
 * The Group Manager View implementation
 */
@ApplicationScoped
public class GroupManagerViewImpl extends Composite implements GroupManagerView {

    interface GroupManagerViewBinder
            extends
            UiBinder<Widget, GroupManagerViewImpl> {

    }

    private static final Comparator<Group> GROUP_COMPARATOR = new Comparator<Group>() {
        @Override
        public int compare( final Group o1,
                            final Group o2 ) {
            return o1.getName().toLowerCase().compareTo( o2.getName().toLowerCase() );
        }
    };

    private static final Comparator<Repository> REPOSITORY_COMPARATOR = new Comparator<Repository>() {
        @Override
        public int compare( final Repository o1,
                            final Repository o2 ) {
            return o1.getAlias().toLowerCase().compareTo( o2.getAlias().toLowerCase() );
        }
    };

    private static GroupManagerViewBinder uiBinder = GWT.create( GroupManagerViewBinder.class );

    @UiField
    ListBox lstGroups;

    @UiField
    ListBox lstGroupRepositories;

    @UiField
    ListBox lstAvailableRepositories;

    @UiField
    Button btnAddGroup;

    @UiField
    Button btnDeleteGroup;

    @UiField
    Button btnAddRepository;

    @UiField
    Button btnRemoveRepository;

    private GroupManagerPresenter presenter;

    private List<Group> sortedGroups = new ArrayList<Group>();
    private List<Repository> sortedGroupRepositories = new ArrayList<Repository>();
    private List<Repository> sortedAvailableRepositories = new ArrayList<Repository>();
    private List<Repository> sortedAllRepositories = new ArrayList<Repository>();

    public GroupManagerViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        lstGroups.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( final ChangeEvent event ) {
                final int selectedGroupIndex = lstGroups.getSelectedIndex();
                if ( selectedGroupIndex == -1 ) {
                    return;
                }
                if ( sortedGroups.isEmpty() ) {
                    return;
                }
                final Group selectedGroup = sortedGroups.get( selectedGroupIndex );
                presenter.groupSelected( selectedGroup );
                btnDeleteGroup.setEnabled( true );
                btnAddRepository.setEnabled( false );
                btnRemoveRepository.setEnabled( false );
            }
        } );

        lstGroupRepositories.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( final ChangeEvent event ) {
                btnRemoveRepository.setEnabled( true );
            }
        } );

        lstAvailableRepositories.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( final ChangeEvent event ) {
                btnAddRepository.setEnabled( true );
            }
        } );
    }

    @Override
    public void init( final GroupManagerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void reset() {
        sortedGroups.clear();
        sortedGroupRepositories.clear();
        sortedAvailableRepositories.clear();
        sortedAllRepositories.clear();
        lstGroups.clear();
        lstGroupRepositories.clear();
        lstAvailableRepositories.clear();
        btnAddRepository.setEnabled( false );
        btnRemoveRepository.setEnabled( false );
        btnDeleteGroup.setEnabled( false );
    }

    @Override
    public void setGroups( final Collection<Group> groups ) {
        final int selectedGroupIndex = lstGroups.getSelectedIndex();
        final Group selectedGroup = ( selectedGroupIndex < 0 ? null : sortedGroups.get( selectedGroupIndex ) );

        lstGroups.clear();
        if ( !( groups == null || groups.isEmpty() ) ) {
            lstGroups.setEnabled( true );
            sortedGroups = sortGroups( groups );
            for ( Group group : sortedGroups ) {
                lstGroups.addItem( group.getName() );
            }
        } else {
            lstGroups.setEnabled( false );
            lstGroups.addItem( GroupManagerConstants.INSTANCE.NoGroupsDefined() );
        }

        if ( selectedGroup == null ) {
            lstGroupRepositories.clear();
            lstGroupRepositories.setEnabled( false );
            lstGroupRepositories.addItem( GroupManagerConstants.INSTANCE.NoGroupSelected() );
            lstAvailableRepositories.clear();
            lstAvailableRepositories.setEnabled( false );
            lstAvailableRepositories.addItem( GroupManagerConstants.INSTANCE.NoGroupSelected() );
            btnDeleteGroup.setEnabled( false );
        } else {
            lstGroups.setSelectedIndex( sortedGroups.indexOf( selectedGroup ) );
            presenter.groupSelected( selectedGroup );
            btnDeleteGroup.setEnabled( true );
        }

    }

    private List<Group> sortGroups( final Collection<Group> groups ) {
        final List<Group> sortedGroups = new ArrayList<Group>();
        sortedGroups.addAll( groups );
        Collections.sort( sortedGroups,
                          GROUP_COMPARATOR );
        return sortedGroups;
    }

    @Override
    public void setGroupRepositories( final Collection<Repository> repositories ) {
        lstGroupRepositories.clear();
        sortedGroupRepositories.clear();
        if ( !( repositories == null || repositories.isEmpty() ) ) {
            lstGroupRepositories.setEnabled( true );
            sortedGroupRepositories = sortRepositories( repositories );
            for ( Repository repository : sortedGroupRepositories ) {
                lstGroupRepositories.addItem( repository.getAlias() );
            }
        } else {
            lstGroupRepositories.setEnabled( false );
            lstGroupRepositories.addItem( GroupManagerConstants.INSTANCE.NoRepositoriesDefined() );
        }

        lstAvailableRepositories.clear();
        sortedAvailableRepositories.clear();
        sortedAvailableRepositories.addAll( sortedAllRepositories );
        sortedAvailableRepositories.removeAll( sortedGroupRepositories );
        if ( !( sortedAvailableRepositories == null || sortedAvailableRepositories.isEmpty() ) ) {
            lstAvailableRepositories.setEnabled( true );
            for ( Repository repo : sortedAvailableRepositories ) {
                lstAvailableRepositories.addItem( repo.getAlias() );
            }
        } else {
            lstAvailableRepositories.setEnabled( false );
            lstAvailableRepositories.addItem( GroupManagerConstants.INSTANCE.NoRepositoriesAvailable() );
        }

        btnAddRepository.setEnabled( false );
        btnRemoveRepository.setEnabled( false );
    }

    @Override
    public void setAllRepositories( final Collection<Repository> repositories ) {
        sortedAllRepositories = sortRepositories( repositories );
        presenter.loadGroups();
    }

    private List<Repository> sortRepositories( final Collection<Repository> repositories ) {
        final List<Repository> sortedRepositories = new ArrayList<Repository>();
        sortedRepositories.addAll( repositories );
        Collections.sort( sortedRepositories,
                          REPOSITORY_COMPARATOR );
        return sortedRepositories;
    }

    @Override
    public void addGroup( final Group newGroup ) {
        final Collection<Group> existingGroups = new ArrayList<Group>( sortedGroups );
        existingGroups.add( newGroup );
        setGroups( existingGroups );
    }

    @Override
    public void deleteGroup( final Group group ) {
        final Collection<Group> existingGroups = new ArrayList<Group>( sortedGroups );
        existingGroups.remove( group );
        setGroups( existingGroups );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @UiHandler("btnAddGroup")
    public void onClickAddGroupButton( final ClickEvent event ) {
        presenter.addNewGroup();
    }

    @UiHandler("btnDeleteGroup")
    public void onClickDeleteGroupButton( final ClickEvent event ) {
        final int selectedGroupIndex = lstGroups.getSelectedIndex();
        if ( selectedGroupIndex < 0 ) {
            return;
        }
        final Group group = sortedGroups.get( selectedGroupIndex );
        if ( Window.confirm( GroupManagerConstants.INSTANCE.ConfirmGroupDeletion0( group.getName() ) ) ) {
            presenter.deleteGroup( group );
        }
    }

    @UiHandler("btnAddRepository")
    public void onClickAddRepositoryButton( final ClickEvent event ) {
        final int selectedGroupIndex = lstGroups.getSelectedIndex();
        final Group selectedGroup = ( selectedGroupIndex < 0 ? null : sortedGroups.get( selectedGroupIndex ) );
        if ( selectedGroup == null ) {
            return;
        }
        final int selectedRepositoryIndex = lstAvailableRepositories.getSelectedIndex();
        final Repository selectedRepository = ( selectedRepositoryIndex < 0 ? null : sortedAvailableRepositories.get( selectedRepositoryIndex ) );
        if ( selectedRepository == null ) {
            return;
        }
        presenter.addGroupRepository( selectedGroup,
                                      selectedRepository );

    }

    @UiHandler("btnRemoveRepository")
    public void onClickRemoveRepositoryButton( final ClickEvent event ) {
        final int selectedGroupIndex = lstGroups.getSelectedIndex();
        final Group selectedGroup = ( selectedGroupIndex < 0 ? null : sortedGroups.get( selectedGroupIndex ) );
        if ( selectedGroup == null ) {
            return;
        }
        final int selectedRepositoryIndex = lstGroupRepositories.getSelectedIndex();
        final Repository selectedRepository = ( selectedRepositoryIndex < 0 ? null : sortedGroupRepositories.get( selectedRepositoryIndex ) );
        if ( selectedRepository == null ) {
            return;
        }
        presenter.removeGroupRepository( selectedGroup,
                                         selectedRepository );
    }

}

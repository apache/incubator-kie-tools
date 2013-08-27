package org.kie.workbench.common.screens.organizationalunit.manager.client.editor;

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
import org.kie.workbench.common.screens.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.BusyPopup;

/**
 * The Organizational Unit Manager View implementation
 */
@ApplicationScoped
public class OrganizationalUnitManagerViewImpl extends Composite implements OrganizationalUnitManagerView {

    interface OrganizationalUnitManagerViewBinder
            extends
            UiBinder<Widget, OrganizationalUnitManagerViewImpl> {

    }

    private static final Comparator<OrganizationalUnit> ORGANIZATIONAL_UNIT_COMPARATOR = new Comparator<OrganizationalUnit>() {
        @Override
        public int compare( final OrganizationalUnit o1,
                            final OrganizationalUnit o2 ) {
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

    private static OrganizationalUnitManagerViewBinder uiBinder = GWT.create( OrganizationalUnitManagerViewBinder.class );

    @UiField
    ListBox lstOrganizationalUnits;

    @UiField
    ListBox lstOrganizationalUnitRepositories;

    @UiField
    ListBox lstAvailableRepositories;

    @UiField
    Button btnAddOrganizationalUnit;

    @UiField
    Button btnDeleteOrganizationalUnit;

    @UiField
    Button btnAddRepository;

    @UiField
    Button btnRemoveRepository;

    private OrganizationalUnitManagerPresenter presenter;

    private List<OrganizationalUnit> sortedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
    private List<Repository> sortedOrganizationalUnitRepositories = new ArrayList<Repository>();
    private List<Repository> sortedAvailableRepositories = new ArrayList<Repository>();
    private List<Repository> sortedAllRepositories = new ArrayList<Repository>();

    public OrganizationalUnitManagerViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );

        lstOrganizationalUnits.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( final ChangeEvent event ) {
                final int selectedOrganizationalUnitIndex = lstOrganizationalUnits.getSelectedIndex();
                if ( selectedOrganizationalUnitIndex == -1 ) {
                    return;
                }
                if ( sortedOrganizationalUnits.isEmpty() ) {
                    return;
                }
                final OrganizationalUnit selectedOrganizationalUnit = sortedOrganizationalUnits.get( selectedOrganizationalUnitIndex );
                presenter.organizationalUnitSelected( selectedOrganizationalUnit );
                btnDeleteOrganizationalUnit.setEnabled( true );
                btnAddRepository.setEnabled( false );
                btnRemoveRepository.setEnabled( false );
            }
        } );

        lstOrganizationalUnitRepositories.addChangeHandler( new ChangeHandler() {
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
    public void init( final OrganizationalUnitManagerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void reset() {
        sortedOrganizationalUnits.clear();
        sortedOrganizationalUnitRepositories.clear();
        sortedAvailableRepositories.clear();
        sortedAllRepositories.clear();
        lstOrganizationalUnits.clear();
        lstOrganizationalUnitRepositories.clear();
        lstAvailableRepositories.clear();
        btnAddRepository.setEnabled( false );
        btnRemoveRepository.setEnabled( false );
        btnDeleteOrganizationalUnit.setEnabled( false );
    }

    @Override
    public void setOrganizationalUnits( final Collection<OrganizationalUnit> organizationalUnits ) {
        final int selectedOrganizationalUnitIndex = lstOrganizationalUnits.getSelectedIndex();
        final OrganizationalUnit selectedOrganizationalUnit = ( selectedOrganizationalUnitIndex < 0 ? null : sortedOrganizationalUnits.get( selectedOrganizationalUnitIndex ) );

        lstOrganizationalUnits.clear();
        if ( !( organizationalUnits == null || organizationalUnits.isEmpty() ) ) {
            lstOrganizationalUnits.setEnabled( true );
            sortedOrganizationalUnits = sortOrganizationalUnits( organizationalUnits );
            for ( OrganizationalUnit group : sortedOrganizationalUnits ) {
                lstOrganizationalUnits.addItem( group.getName() );
            }
        } else {
            lstOrganizationalUnits.setEnabled( false );
            lstOrganizationalUnits.addItem( OrganizationalUnitManagerConstants.INSTANCE.NoOrganizationalUnitsDefined() );
        }

        if ( selectedOrganizationalUnit == null ) {
            lstOrganizationalUnitRepositories.clear();
            lstOrganizationalUnitRepositories.setEnabled( false );
            lstOrganizationalUnitRepositories.addItem( OrganizationalUnitManagerConstants.INSTANCE.NoOrganizationalUnitSelected() );
            lstAvailableRepositories.clear();
            lstAvailableRepositories.setEnabled( false );
            lstAvailableRepositories.addItem( OrganizationalUnitManagerConstants.INSTANCE.NoOrganizationalUnitSelected() );
            btnDeleteOrganizationalUnit.setEnabled( false );
        } else {
            lstOrganizationalUnits.setSelectedIndex( sortedOrganizationalUnits.indexOf( selectedOrganizationalUnit ) );
            presenter.organizationalUnitSelected( selectedOrganizationalUnit );
            btnDeleteOrganizationalUnit.setEnabled( true );
        }

    }

    private List<OrganizationalUnit> sortOrganizationalUnits( final Collection<OrganizationalUnit> groups ) {
        final List<OrganizationalUnit> sortedOrganizationalUnits = new ArrayList<OrganizationalUnit>();
        sortedOrganizationalUnits.addAll( groups );
        Collections.sort( sortedOrganizationalUnits,
                          ORGANIZATIONAL_UNIT_COMPARATOR );
        return sortedOrganizationalUnits;
    }

    @Override
    public void setOrganizationalUnitRepositories( final Collection<Repository> repositories ) {
        lstOrganizationalUnitRepositories.clear();
        sortedOrganizationalUnitRepositories.clear();
        if ( !( repositories == null || repositories.isEmpty() ) ) {
            lstOrganizationalUnitRepositories.setEnabled( true );
            sortedOrganizationalUnitRepositories = sortRepositories( repositories );
            for ( Repository repository : sortedOrganizationalUnitRepositories ) {
                lstOrganizationalUnitRepositories.addItem( repository.getAlias() );
            }
        } else {
            lstOrganizationalUnitRepositories.setEnabled( false );
            lstOrganizationalUnitRepositories.addItem( OrganizationalUnitManagerConstants.INSTANCE.NoRepositoriesDefined() );
        }

        lstAvailableRepositories.clear();
        sortedAvailableRepositories.clear();
        sortedAvailableRepositories.addAll( sortedAllRepositories );
        sortedAvailableRepositories.removeAll( sortedOrganizationalUnitRepositories );
        if ( !( sortedAvailableRepositories == null || sortedAvailableRepositories.isEmpty() ) ) {
            lstAvailableRepositories.setEnabled( true );
            for ( Repository repo : sortedAvailableRepositories ) {
                lstAvailableRepositories.addItem( repo.getAlias() );
            }
        } else {
            lstAvailableRepositories.setEnabled( false );
            lstAvailableRepositories.addItem( OrganizationalUnitManagerConstants.INSTANCE.NoRepositoriesAvailable() );
        }

        btnAddRepository.setEnabled( false );
        btnRemoveRepository.setEnabled( false );
    }

    @Override
    public void setAllRepositories( final Collection<Repository> repositories ) {
        sortedAllRepositories = sortRepositories( repositories );
        presenter.loadOrganizationalUnits();
    }

    private List<Repository> sortRepositories( final Collection<Repository> repositories ) {
        final List<Repository> sortedRepositories = new ArrayList<Repository>();
        sortedRepositories.addAll( repositories );
        Collections.sort( sortedRepositories,
                          REPOSITORY_COMPARATOR );
        return sortedRepositories;
    }

    @Override
    public void addOrganizationalUnit( final OrganizationalUnit newOrganizationalUnit ) {
        final Collection<OrganizationalUnit> existingOrganizationalUnits = new ArrayList<OrganizationalUnit>( sortedOrganizationalUnits );
        existingOrganizationalUnits.add( newOrganizationalUnit );
        setOrganizationalUnits( existingOrganizationalUnits );
    }

    @Override
    public void deleteOrganizationalUnit( final OrganizationalUnit organizationalUnit ) {
        final Collection<OrganizationalUnit> existingOrganizationalUnits = new ArrayList<OrganizationalUnit>( sortedOrganizationalUnits );
        existingOrganizationalUnits.remove( organizationalUnit );
        setOrganizationalUnits( existingOrganizationalUnits );
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @UiHandler("btnAddOrganizationalUnit")
    public void onClickAddOrganizationalUnitButton( final ClickEvent event ) {
        presenter.addNewOrganizationalUnit();
    }

    @UiHandler("btnDeleteOrganizationalUnit")
    public void onClickDeleteOrganizationalUnitButton( final ClickEvent event ) {
        final int selectedOrganizationalUnitIndex = lstOrganizationalUnits.getSelectedIndex();
        if ( selectedOrganizationalUnitIndex < 0 ) {
            return;
        }
        final OrganizationalUnit organizationalUnit = sortedOrganizationalUnits.get( selectedOrganizationalUnitIndex );
        if ( Window.confirm( OrganizationalUnitManagerConstants.INSTANCE.ConfirmOrganizationalUnitDeletion0( organizationalUnit.getName() ) ) ) {
            presenter.deleteOrganizationalUnit( organizationalUnit );
        }
    }

    @UiHandler("btnAddRepository")
    public void onClickAddRepositoryButton( final ClickEvent event ) {
        final int selectedOrganizationalUnitIndex = lstOrganizationalUnits.getSelectedIndex();
        final OrganizationalUnit selectedOrganizationalUnit = ( selectedOrganizationalUnitIndex < 0 ? null : sortedOrganizationalUnits.get( selectedOrganizationalUnitIndex ) );
        if ( selectedOrganizationalUnit == null ) {
            return;
        }
        final int selectedRepositoryIndex = lstAvailableRepositories.getSelectedIndex();
        final Repository selectedRepository = ( selectedRepositoryIndex < 0 ? null : sortedAvailableRepositories.get( selectedRepositoryIndex ) );
        if ( selectedRepository == null ) {
            return;
        }
        presenter.addOrganizationalUnitRepository( selectedOrganizationalUnit,
                                                   selectedRepository );

    }

    @UiHandler("btnRemoveRepository")
    public void onClickRemoveRepositoryButton( final ClickEvent event ) {
        final int selectedOrganizationalUnitIndex = lstOrganizationalUnits.getSelectedIndex();
        final OrganizationalUnit selectedOrganizationalUnit = ( selectedOrganizationalUnitIndex < 0 ? null : sortedOrganizationalUnits.get( selectedOrganizationalUnitIndex ) );
        if ( selectedOrganizationalUnit == null ) {
            return;
        }
        final int selectedRepositoryIndex = lstOrganizationalUnitRepositories.getSelectedIndex();
        final Repository selectedRepository = ( selectedRepositoryIndex < 0 ? null : sortedOrganizationalUnitRepositories.get( selectedRepositoryIndex ) );
        if ( selectedRepository == null ) {
            return;
        }
        presenter.removeOrganizationalUnitRepository( selectedOrganizationalUnit,
                                                      selectedRepository );
    }

}

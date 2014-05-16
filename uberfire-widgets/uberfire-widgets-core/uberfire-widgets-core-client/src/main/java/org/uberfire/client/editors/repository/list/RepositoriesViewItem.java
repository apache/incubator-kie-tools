package org.uberfire.client.editors.repository.list;

import java.util.Collection;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.backend.repositories.PublicURI;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.navigator.CommitNavigator;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

public class RepositoriesViewItem extends Composite {

    interface RepositoriesViewItemBinder
            extends
            UiBinder<Widget, RepositoriesViewItem> {

    }

    private static RepositoriesViewItemBinder uiBinder = GWT.create( RepositoriesViewItemBinder.class );

    @UiField
    public InlineHTML ownerReference;

    @UiField
    public InlineHTML repoName;

    @UiField
    public InlineHTML repoDesc;

    @UiField
    public InlineHTML gitDaemonURI;

    @UiField
    public Button myGitCopyButton;

    @UiField
    public FlowPanel linksPanel;

    @UiField
    public ListBox branchesDropdown;

    private RemoveRepositoryCmd cmdRemoveRepository;

    private UpdateRepositoryCmd cmdUpdateRepository;

    public RepositoriesViewItem( final String repositoryName,
                                 final String owner,
                                 final List<PublicURI> publicURIs,
                                 final String description,
                                 final String currentBranch,
                                 final Collection<String> branches,
                                 final RemoveRepositoryCmd cmdRemoveRepository,
                                 final UpdateRepositoryCmd cmdUpdateRepository) {
        initWidget( uiBinder.createAndBindUi( this ) );

        this.cmdRemoveRepository = cmdRemoveRepository;
        this.cmdUpdateRepository = cmdUpdateRepository;
        if ( owner != null && !owner.isEmpty() ) {
            ownerReference.setText( owner + " / " );
        }
        repoName.setText( repositoryName );
        repoDesc.setText( description );
        int count = 0;
        if ( publicURIs.size() > 0 ) {
            linksPanel.add( new InlineHTML() {{
                setText( CoreConstants.INSTANCE.AvailableProtocols() );
                getElement().getStyle().setPaddingLeft( 10, Style.Unit.PX );
            }} );
        }
        for ( final PublicURI publicURI : publicURIs ) {
            if ( count == 0 ) {
                gitDaemonURI.setText( publicURI.getURI() );
            }
            final String protocol = publicURI.getProtocol() == null ? "default" : publicURI.getProtocol();
            final Anchor anchor = new Anchor( protocol );
            anchor.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    gitDaemonURI.setText( publicURI.getURI() );
                }
            } );
            if ( count != 0 ) {
                anchor.getElement().getStyle().setPaddingLeft( 5, Style.Unit.PX );
            }
            linksPanel.add( anchor );
            count++;
        }

        final String uriId = "view-uri-for-" + repositoryName;
        gitDaemonURI.getElement().setId( uriId );
        myGitCopyButton.getElement().setAttribute( "data-clipboard-target", uriId );
        myGitCopyButton.getElement().setAttribute( "data-clipboard-text", gitDaemonURI.getText() );

        myGitCopyButton.getElement().setId( "view-button-" + uriId );

        // populate branches
        int index = 0;
        for (String branch : branches) {
            branchesDropdown.addItem(branch, branch);
            if (currentBranch.equals(branch)) {
                branchesDropdown.setSelectedIndex(index);
            }
            index++;
        }

        glueCopy( myGitCopyButton.getElement() );
    }

    @UiHandler("btnRemoveRepository")
    public void onClickButtonRemoveRepository( final ClickEvent event ) {
        if ( cmdRemoveRepository != null ) {
            cmdRemoveRepository.execute();
        }
    }

    @UiHandler("btnChangeBranch")
    public void onClickButtonUpdateRepository( final ClickEvent event ) {
        if ( cmdUpdateRepository != null ) {
            final String branch = branchesDropdown.getValue( branchesDropdown.getSelectedIndex() );
            cmdUpdateRepository.add("branch", branch);
            cmdUpdateRepository.execute();
        }
    }

    public void update(final Repository repository) {
        if (this.cmdUpdateRepository != null) {
            this.cmdUpdateRepository.setRepository(repository);
        }

        if (this.cmdRemoveRepository != null) {
            this.cmdRemoveRepository.setRepository(repository);
        }
    }

    public static native void glueCopy( final com.google.gwt.user.client.Element element ) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

}

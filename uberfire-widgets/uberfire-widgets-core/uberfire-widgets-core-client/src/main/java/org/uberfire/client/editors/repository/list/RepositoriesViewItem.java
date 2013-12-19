package org.uberfire.client.editors.repository.list;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
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
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.navigator.CommitNavigator;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Created with IntelliJ IDEA.
 * Date: 24/09/13
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
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

    private Command cmdRemoveRepository;

    public RepositoriesViewItem( final String repositoryName,
                                 final String owner,
                                 final List<PublicURI> publicURIs,
                                 final String description,
                                 final Command cmdRemoveRepository ) {
        initWidget( uiBinder.createAndBindUi( this ) );

        this.cmdRemoveRepository = cmdRemoveRepository;
        if ( owner != null && !owner.isEmpty() ) {
            ownerReference.setText( owner + " / " );
        }
        repoName.setText( repositoryName );
        repoDesc.setText( description );
        int count = 0;
        if ( publicURIs.size() > 0 ) {
            linksPanel.add( new InlineHTML() {{
                setText( "Available protocol(s): " );
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

        glueCopy( myGitCopyButton.getElement() );
    }

    @UiHandler("btnRemoveRepository")
    public void onClickButtonRemoveRepository( final ClickEvent event ) {
        if ( cmdRemoveRepository != null ) {
            cmdRemoveRepository.execute();
        }
    }

    public static native void glueCopy( final com.google.gwt.user.client.Element element ) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

}

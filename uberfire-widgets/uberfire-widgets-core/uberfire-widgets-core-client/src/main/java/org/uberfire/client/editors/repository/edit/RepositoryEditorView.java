/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.editors.repository.edit;

import java.util.List;
import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.backend.repositories.PublicURI;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.navigator.CommitNavigator;
import org.uberfire.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

public class RepositoryEditorView extends Composite
        implements
        RequiresResize,
        RepositoryEditorPresenter.View {

    interface RepositoryEditorViewBinder
            extends
            UiBinder<Widget, RepositoryEditorView> {

    }

    private static RepositoryEditorViewBinder uiBinder = GWT.create( RepositoryEditorViewBinder.class );

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
    public FlowPanel history;

    @UiField
    public Button loadMore;

    private CommitNavigator commitNavigator = null;

    private RepositoryEditorPresenter presenter;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final RepositoryEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    public void setRepositoryInfo( final String repositoryName,
                                   final String owner,
                                   final List<PublicURI> publicURIs,
                                   final String description,
                                   final List<VersionRecord> initialVersionList ) {
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

        if ( initialVersionList != null && !initialVersionList.isEmpty() ) {
            commitNavigator = new CommitNavigator() {{
                setOnRevertCommand( new ParameterizedCommand<VersionRecord>() {
                    @Override
                    public void execute( final VersionRecord record ) {
                        BusyPopup.showMessage( CoreConstants.INSTANCE.Reverting() );
                        presenter.revert( record );
                    }
                } );
                loadContent( initialVersionList );
            }};
            history.add( commitNavigator );
        } else {
            history.setVisible( false );
        }

        loadMore.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                if ( commitNavigator != null ) {
                    presenter.getLoadMoreHistory( commitNavigator.getLastIndex() );
                }
            }
        } );

        final String uriId = "uri-for-" + repositoryName;
        gitDaemonURI.getElement().setId( uriId );
        myGitCopyButton.getElement().setAttribute( "data-clipboard-target", uriId );
        myGitCopyButton.getElement().setAttribute( "data-clipboard-text", gitDaemonURI.getText() );

        myGitCopyButton.getElement().setId( "button-" + uriId );

        glueCopy( myGitCopyButton.getElement() );
    }

    @Override
    public void reloadHistory( final List<VersionRecord> versionList ) {
        commitNavigator.loadContent( versionList );
        BusyPopup.close();
    }

    @Override
    public void addHistory( List<VersionRecord> versionList ) {
        if ( commitNavigator != null ) {
            if ( !versionList.isEmpty() ) {
                commitNavigator.addContent( versionList );
            } else {
                loadMore.setEnabled( false );
            }
        }
    }

    @Override
    public void clear() {
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        setPixelSize( width, height );
    }

    public static native void glueCopy( final Element element ) /*-{
        var clip = new $wnd.ZeroClipboard(element);
    }-*/;

}
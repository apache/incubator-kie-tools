/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.widgets.metadata.client.widget;

import java.util.Date;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.widgets.client.discussion.DiscussionWidgetPresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.editor.commons.client.history.VersionHistoryPresenter;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.mvp.ParameterizedCommand;

public class OverviewWidgetViewImpl
        extends Composite
        implements OverviewScreenView,
                   RequiresResize {

    private static final int VERSION_HISTORY_TAB = 0;

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, OverviewWidgetViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    TextArea description;

    @UiField
    Label resourceType;

    @UiField
    Label projects;

    @UiField
    Label lastModifiedLabel;

    @UiField
    Label createdLabel;

    @UiField
    NavTabs navTabs;

    @UiField
    TabContent tabContent;

    @UiField(provided = true)
    DiscussionWidgetPresenter discussionArea;

    VersionHistoryPresenter versionHistory;
    MetadataWidget metadata;

    public OverviewWidgetViewImpl() {
    }

    @Inject
    public OverviewWidgetViewImpl( final BusyIndicatorView busyIndicatorView,
                                   final DiscussionWidgetPresenter discussionArea,
                                   final VersionHistoryPresenter versionHistory ) {

        this.metadata = new MetadataWidget( busyIndicatorView );

        this.discussionArea = discussionArea;

        this.versionHistory = versionHistory;

        versionHistory.setOnCurrentVersionRefreshed( new ParameterizedCommand<VersionRecord>() {
            @Override
            public void execute( VersionRecord record ) {
                metadata.setNote( record.comment() );
                setLastModified( record.author(), record.date() );
            }
        } );

        initWidget( uiBinder.createAndBindUi( this ) );

        final TabPane versionHistoryPane = new TabPane() {{
            add( versionHistory );
        }};

        final TabPane metadataPane = new TabPane() {{
            add( metadata );
        }};

        tabContent.add( versionHistoryPane );
        tabContent.add( metadataPane );

        navTabs.add( new TabListItem( MetadataConstants.INSTANCE.VersionHistory() ) {{
            addStyleName( "uf-dropdown-tab-list-item" );
            setDataTargetWidget( versionHistoryPane );
            setActive( true );
        }} );

        navTabs.add( new TabListItem( MetadataConstants.INSTANCE.Metadata() ) {{
            addStyleName( "uf-dropdown-tab-list-item" );
            setDataTargetWidget( metadataPane );
        }} );

        navTabs.getElement().setAttribute( "data-uf-lock", "false" );
        showVersionHistory();
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setReadOnly( boolean isReadOnly ) {
        description.setEnabled( !isReadOnly );
    }

    @Override
    public void setVersionHistory( Path path ) {
        versionHistory.init( path );
    }

    @Override
    public void setDescription( String description ) {
        this.description.setText( description );
    }

    @UiHandler("description")
    public void onDescriptionChange( KeyUpEvent event ) {
        presenter.onDescriptionEdited( description.getText() );
    }

    @Override
    public void setResourceType( ClientResourceType type ) {
        resourceType.setText( type.getDescription() );
    }

    @Override
    public void setProject( String project ) {
        projects.setText( project );
    }

    @Override
    public void setLastModified( String lastContributor,
                                 Date lastModified ) {
        lastModifiedLabel.setText( "By/" + lastContributor + " on " + DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( lastModified ) );
    }

    @Override
    public void setCreated( String creator,
                            Date dateCreated ) {
        createdLabel.setText( "By/" + creator + " on " + DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( dateCreated ) );
    }

    @Override
    public String getTitle( String fileName,
                            String fileType ) {
        return fileName + "- Business Rule";
    }

    @Override
    public void showVersionHistory() {
        ( (TabListItem) navTabs.getWidget( VERSION_HISTORY_TAB ) ).setActive( true );
    }

    @Override
    public void setMetadata( Metadata metadata,
                             boolean isReadOnly ) {
        this.metadata.setContent( metadata, isReadOnly );
        this.discussionArea.setContent( metadata );
    }

    @Override
    public void showBusyIndicator( String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public Widget asWidget() {
        return super.asWidget();
    }

    @Override
    public void showSavingIndicator() {
        showBusyIndicator( CommonConstants.INSTANCE.Saving() );
    }

    @Override
    public void showLoadingIndicator() {
        showBusyIndicator( CommonConstants.INSTANCE.Loading() );
    }

    @Override
    public void refresh( String version ) {
        versionHistory.refresh( version );
    }

    public void setForceUnlockHandler( final Runnable handler ) {
        this.metadata.setForceUnlockHandler( handler );
    }

    @Override
    public void onResize() {
        discussionArea.onResize();
    }

}

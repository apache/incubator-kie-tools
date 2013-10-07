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

package org.drools.workbench.screens.testscenario.client;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.guvnor.common.services.project.model.Dependency;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.workbench.events.NotificationEvent;

public class BulkRunTestScenarioEditorViewImpl
        extends ResizeComposite
        implements BulkRunTestScenarioEditorView {

    private String tabTitleLabel = TestScenarioConstants.INSTANCE.RunAllScenarios();

    interface BulkRunTestScenarioEditorViewImplBinder
            extends
            UiBinder<Widget, BulkRunTestScenarioEditorViewImpl> {

    }

    private Presenter presenter;

    private static BulkRunTestScenarioEditorViewImplBinder uiBinder = GWT.create( BulkRunTestScenarioEditorViewImplBinder.class );

    private final Event<NotificationEvent> notificationEvent;

/*    @UiField(provided = true)
    DependencyGrid dependencyGrid;*/

    @Inject
    public BulkRunTestScenarioEditorViewImpl( final Event<NotificationEvent> notificationEvent/*,
                                   DependencyGrid dependencyGrid */ ) {
        //this.dependencyGrid = dependencyGrid;
        initWidget( uiBinder.createAndBindUi( this ) );
        this.notificationEvent = notificationEvent;
    }

    @Override
    public void showSaveSuccessful( final String fileName ) {
        notificationEvent.fire( new NotificationEvent( "ProjectEditorResources.CONSTANTS.SaveSuccessful( fileName )" ) );
    }

    @Override
    public String getTitleWidget() {
        return tabTitleLabel;
    }

    @Override
    public void setDependencies( final List<Dependency> dependencies ) {
        //dependencyGrid.fillList( dependencies );
    }

    /*
        @Override
        public void addArtifactIdChangeHandler( ArtifactIdChangeHandler changeHandler ) {
            gavEditor.addArtifactIdChangeHandler( changeHandler );
        }
    */
    @Override
    public void setReadOnly() {
        //dependencyGrid.setReadOnly();

    }

    @Override
    public void setTitleText( final String titleText ) {
        tabTitleLabel = titleText;
    }

    @Override
    public void onResize() {
        setPixelSize( getParent().getOffsetWidth(),
                      getParent().getOffsetHeight() );
        super.onResize();
    }

    @Override
    public void showBusyIndicator( final String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @UiHandler("runAllButton")
    public void onRunAllButton( final ClickEvent e ) {
        presenter.onRunAllButton();
    }

    @Override
    public void setPresenter( final Presenter presenter ) {
        this.presenter = presenter;
    }

}

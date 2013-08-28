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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.widget.HasBusyIndicator;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;

public class BulkRunTestScenarioEditor
        implements IsWidget,
                   BulkRunTestScenarioEditorView.Presenter,
                   HasBusyIndicator {

    @Inject
    private BulkRunTestScenarioEditorView view;
    private Path path;

    @Inject
    private Caller<ScenarioTestEditorService> scenarioService;

    @PostConstruct
    public void init() {
        view.setPresenter( this );
    }

    public void init( final Path path,
                      final boolean isReadOnly ) {
        this.path = path;
        if ( isReadOnly ) {
            view.setReadOnly();
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onRunAllButton() {
        BusyPopup.showMessage( TestScenarioConstants.INSTANCE.BuildingAndRunningScenario() );
        scenarioService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( Void v ) {
                BusyPopup.close();
            }
        },
                              new HasBusyIndicatorDefaultErrorCallback( BulkRunTestScenarioEditor.this )
                            ).runAllScenarios( path );
    }

    @Override
    public void showBusyIndicator( String message ) {
        BusyPopup.showMessage( message );
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
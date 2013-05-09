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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.widget.HasBusyIndicator;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.mvp.Command;

public class BulkRunTestScenarioEditor
        implements IsWidget, BulkRunTestScenarioEditorView.Presenter, HasBusyIndicator {

    @Inject
    private BulkRunTestScenarioEditorView view;
    private Path path;

    @Inject
    private Caller<ScenarioTestEditorService> scenarioService;

    @PostConstruct
    public void init() {
        view.setPresenter(this);        
    }
    
    public void init(final Path path,
                     final boolean isReadOnly ) {
        this.path = path;
        if ( isReadOnly ) {
            view.setReadOnly();
        }
    }

    private void setTitle( final String titleText ) {
        if ( titleText == null || titleText.isEmpty() ) {
            view.setTitleText( TestScenarioConstants.INSTANCE.RunAllScenarios() );
        } else {
            view.setTitleText( titleText );
        }
    }

    public void save( final String commitMessage,
                      final Command callback,
                      final Metadata metadata ) {
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public String getTitle() {
        return view.getTitleWidget();
    }

    @Override
    public void onRunAllButton() {
        final FormStylePopup pop = new FormStylePopup();
        final TextBox sessionNameTextBox = new TextBox();        
        pop.addAttribute("session name" + ":", sessionNameTextBox);

        Button ok = new Button("OK");
        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if(sessionNameTextBox.getText() == null || "".equals(sessionNameTextBox.getText())) {
                    Window.alert(TestScenarioConstants.INSTANCE.PleaseInputSessionName());
                    return;
                }
                
                BusyPopup.showMessage(TestScenarioConstants.INSTANCE.BuildingAndRunningScenario());

                scenarioService.call(new RemoteCallback<Void>() {
                    @Override
                    public void callback(Void v) {
                        pop.hide();
                        BusyPopup.close();
                    }
                },
                        new HasBusyIndicatorDefaultErrorCallback(BulkRunTestScenarioEditor.this)
                ).runAllScenarios(path, sessionNameTextBox.getText());                        
            }
        });
        pop.addAttribute( "", ok);
        pop.show();                
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
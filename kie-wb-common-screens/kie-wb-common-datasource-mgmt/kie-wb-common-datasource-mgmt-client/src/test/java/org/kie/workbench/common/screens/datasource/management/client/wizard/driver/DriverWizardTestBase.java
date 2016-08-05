/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.wizard.driver;

import com.google.gwtmockito.GwtMock;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.editor.driver.DriverDefEditorHelper;
import org.kie.workbench.common.screens.datasource.management.client.editor.driver.DriverDefMainPanel;
import org.kie.workbench.common.screens.datasource.management.client.editor.driver.DriverDefMainPanelView;
import org.kie.workbench.common.screens.datasource.management.client.util.ClientValidationServiceMock;
import org.kie.workbench.common.screens.datasource.management.client.util.DataSourceManagementTestConstants;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.mockito.Mock;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Mockito.when;

public abstract class DriverWizardTestBase
        implements DataSourceManagementTestConstants {

    @GwtMock
    protected DriverDefPageView view;

    @GwtMock
    protected DriverDefMainPanelView mainPanelView;

    protected DriverDefMainPanel mainPanel;

    @Mock
    protected TranslationService translationService;

    protected DriverDefEditorHelper editorHelper;

    @Mock
    protected EventSourceMock<WizardPageStatusChangeEvent> statusChangeEvent;

    protected DriverDefPage defPage;

    protected DriverDef driverDef;

    /**
     * Initializes the wizard page.
     */
    protected void setup() {
        mainPanel = new DriverDefMainPanel( mainPanelView );
        driverDef = new DriverDef();
        editorHelper = new DriverDefEditorHelper( translationService, new ClientValidationServiceMock( ) );
        defPage = new DriverDefPage( view, mainPanel, editorHelper, statusChangeEvent );
        defPage.setDriverDef( driverDef );
    }

    /**
     * Emulates the user completing the page by entering valid values in all fields
     */
    protected void completeValidDefPage() {
        when( mainPanelView.getName() ).thenReturn( NAME );
        when( mainPanelView.getGroupId() ).thenReturn( GROUP_ID );
        when( mainPanelView.getArtifactId() ).thenReturn( ARTIFACT_ID );
        when( mainPanelView.getVersion() ).thenReturn( VERSION );
        when( mainPanelView.getDriverClass() ).thenReturn( DRIVER_CLASS );

        mainPanel.onNameChange();
        mainPanel.onGroupIdChange();
        mainPanel.onArtifactIdChange();
        mainPanel.onVersionChange();
        mainPanel.onDriverClassChange();
    }
}

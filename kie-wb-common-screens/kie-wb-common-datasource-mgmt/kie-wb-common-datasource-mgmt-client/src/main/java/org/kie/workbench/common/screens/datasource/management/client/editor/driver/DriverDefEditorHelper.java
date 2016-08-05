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

package org.kie.workbench.common.screens.datasource.management.client.editor.driver;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.validation.ClientValidationService;
import org.kie.workbench.common.screens.datasource.management.model.DriverDef;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

@Dependent
public class DriverDefEditorHelper {

    private TranslationService translationService;

    private DriverDef driverDef;

    private DriverDefMainPanel mainPanel;

    private DriverDefMainPanelView.Handler handler;

    private ClientValidationService validationService;

    private boolean nameValid = false;

    private boolean driverClassValid = false;

    private boolean groupIdValid = false;

    private boolean artifactIdValid = false;

    private boolean versionValid = false;

    @Inject
    public DriverDefEditorHelper( final TranslationService translationService,
            final ClientValidationService validationService ) {
        this.translationService = translationService;
        this.validationService = validationService;
    }

    public void init( final DriverDefMainPanel mainPanel ) {
        this.mainPanel = mainPanel;

        mainPanel.setHandler( new DriverDefMainPanelView.Handler() {
            @Override
            public void onNameChange() {
                DriverDefEditorHelper.this.onNameChange();
            }

            @Override
            public void onDriverClassChange() {
                DriverDefEditorHelper.this.onDriverClassChange();
            }

            @Override
            public void onGroupIdChange() {
                DriverDefEditorHelper.this.onGroupIdChange();
            }

            @Override
            public void onArtifactIdChange() {
                DriverDefEditorHelper.this.onArtifactIdChange();
            }

            @Override
            public void onVersionChange() {
                DriverDefEditorHelper.this.onVersionIdChange();
            }
        } );
    }

    public void setHandler( final DriverDefMainPanelView.Handler handler ) {
        this.handler = handler;
    }

    public void onNameChange() {
        final String newValue = mainPanel.getName().trim();
        validationService.isValidDriverName( newValue, new ValidatorCallback() {
            @Override
            public void onSuccess() {
                onNameChange( newValue, true );
            }

            @Override
            public void onFailure() {
                onNameChange( newValue, false );
            }
        } );
    }

    private void onNameChange( String newValue, boolean isValid ) {
        driverDef.setName( newValue );
        nameValid = isValid;
        if ( !nameValid ) {
            mainPanel.setNameErrorMessage(
                    getMessage( DataSourceManagementConstants.DriverDefEditor_InvalidNameMessage ) );
        } else {
            mainPanel.clearNameErrorMessage();
        }
        if ( handler != null ) {
            handler.onNameChange();
        }
    }

    public void onDriverClassChange() {
        final String newValue = mainPanel.getDriverClass().trim();
        validationService.isValidClassName( newValue, new ValidatorCallback() {
            @Override
            public void onSuccess() {
                onDriverClassChange( newValue, true );
            }

            @Override
            public void onFailure() {
                onDriverClassChange( newValue, false );
            }
        } );
    }

    private void onDriverClassChange( String newValue, boolean isValid ) {
        driverDef.setDriverClass( newValue );
        driverClassValid = isValid;
        if ( !driverClassValid ) {
            mainPanel.setDriverClassErrorMessage(
                    getMessage( DataSourceManagementConstants.DriverDefEditor_InvalidDriverClassMessage ) );
        } else {
            mainPanel.clearDriverClassErrorMessage();
        }
        if ( handler != null ) {
            handler.onDriverClassChange();
        }
    }

    public void onGroupIdChange() {
        final String newValue = mainPanel.getGroupId().trim();
        validationService.isValidGroupId( newValue, new ValidatorCallback() {
            @Override
            public void onSuccess() {
                onGroupIdChange( newValue, true );
            }

            @Override
            public void onFailure() {
                onGroupIdChange( newValue, false );
            }
        } );
    }

    private void onGroupIdChange( String newValue, boolean isValid ) {
        driverDef.setGroupId( newValue );
        groupIdValid = isValid;
        if ( !groupIdValid ) {
            mainPanel.setGroupIdErrorMessage(
                    getMessage( DataSourceManagementConstants.DriverDefEditor_InvalidGroupIdMessage ) );
        } else {
            mainPanel.clearGroupIdErrorMessage();
        }
        if ( handler != null ) {
            handler.onGroupIdChange();
        }
    }

    public void onArtifactIdChange() {
        final String newValue = mainPanel.getArtifactId().trim();
        validationService.isValidArtifactId( newValue, new ValidatorCallback() {
            @Override
            public void onSuccess() {
                onArtifactIdChange( newValue, true );
            }

            @Override
            public void onFailure() {
                onArtifactIdChange( newValue, false );
            }
        } );
    }

    private void onArtifactIdChange( String newValue, boolean isValid ) {
        driverDef.setArtifactId( newValue );
        artifactIdValid = isValid;
        if ( !artifactIdValid ) {
            mainPanel.setArtifactIdErrorMessage(
                    getMessage( DataSourceManagementConstants.DriverDefEditor_InvalidArtifactIdMessage ) );
        } else {
            mainPanel.clearArtifactIdErrorMessage();
        }
        if ( handler != null ) {
            handler.onArtifactIdChange();
        }
    }

    public void onVersionIdChange() {
        final String newValue = mainPanel.getVersion().trim();
        validationService.isValidVersionId( newValue, new ValidatorCallback() {
            @Override
            public void onSuccess() {
                onVersionIdChange( newValue, true );
            }

            @Override
            public void onFailure() {
                onVersionIdChange( newValue, false );
            }
        } );
    }

    private void onVersionIdChange( String newValue, boolean isValid ) {
        driverDef.setVersion( newValue );
        versionValid = isValid;
        if ( !versionValid ) {
            mainPanel.setVersionErrorMessage(
                    getMessage( DataSourceManagementConstants.DriverDefEditor_InvalidVersionMessage ) );
        } else {
            mainPanel.clearVersionErrorMessage();
        }
        if ( handler != null ) {
            handler.onVersionChange();
        }
    }

    public boolean isNameValid() {
        return nameValid;
    }

    public boolean isDriverClassValid() {
        return driverClassValid;
    }

    public boolean isGroupIdValid() {
        return groupIdValid;
    }

    public boolean isArtifactIdValid() {
        return artifactIdValid;
    }

    public boolean isVersionValid() {
        return versionValid;
    }

    public void setValid( boolean valid ) {
        this.nameValid = valid;
        this.driverClassValid = valid;
        this.groupIdValid = valid;
        this.artifactIdValid = valid;
        this.versionValid = valid;
    }

    public void setDriverDef( DriverDef driverDef ) {
        this.driverDef = driverDef;
        mainPanel.clear();
        mainPanel.setName( driverDef.getName() );
        mainPanel.setDriverClass( driverDef.getDriverClass() );
        mainPanel.setGroupId( driverDef.getGroupId() );
        mainPanel.setArtifactId( driverDef.getArtifactId() );
        mainPanel.setVersion( driverDef.getVersion() );
    }

    public String getMessage( String messageKey ) {
        return translationService.getTranslation( messageKey );
    }

    public String getMessage( String messageKey, Object... args ) {
        return translationService.format( messageKey, args );
    }
}

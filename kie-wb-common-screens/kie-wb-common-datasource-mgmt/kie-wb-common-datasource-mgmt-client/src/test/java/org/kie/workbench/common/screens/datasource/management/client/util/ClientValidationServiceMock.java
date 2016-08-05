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

package org.kie.workbench.common.screens.datasource.management.client.util;

import org.kie.workbench.common.screens.datasource.management.client.validation.ClientValidationService;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

public class ClientValidationServiceMock
        extends ClientValidationService
        implements DataSourceManagementTestConstants {

    public ClientValidationServiceMock() {
        super( null );
    }

    @Override
    public void isValidGroupId( String groupId, ValidatorCallback callback ) {
        if ( GROUP_ID.equals( groupId ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    @Override
    public void isValidArtifactId( String artifactId, ValidatorCallback callback ) {
        if ( ARTIFACT_ID.equals( artifactId ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    @Override
    public void isValidVersionId( String versionId, ValidatorCallback callback ) {
        if ( VERSION.equals( versionId ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    @Override
    public void isValidClassName( String className, ValidatorCallback callback ) {
        if ( DRIVER_CLASS.equals( className ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    @Override
    public void isValidDriverName( String driverName, ValidatorCallback callback ) {
        if ( NAME.equals( driverName ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    @Override
    public void isValidDataSourceName( String dataSourceName, ValidatorCallback callback ) {
        if ( NAME.equals( dataSourceName ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    @Override
    public void isValidConnectionURL( String connectionURL, ValidatorCallback callback ) {
        if ( CONNECTION_URL.equals( connectionURL ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

}

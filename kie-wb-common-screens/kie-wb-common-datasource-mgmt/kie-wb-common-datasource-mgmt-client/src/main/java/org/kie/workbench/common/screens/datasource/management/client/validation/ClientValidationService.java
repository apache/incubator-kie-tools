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

package org.kie.workbench.common.screens.datasource.management.client.validation;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

@ApplicationScoped
public class ClientValidationService {

    private Caller<ValidationService> validationService;

    @Inject
    public ClientValidationService( final Caller<ValidationService> validationService ) {
        this.validationService = validationService;
    }

    public void isValidGroupId( final String groupId, final ValidatorCallback callback ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                if ( Boolean.TRUE.equals( result  ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        } ).validateGroupId( groupId );
    }

    public void isValidArtifactId( final String artifactId, final ValidatorCallback callback ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                if ( Boolean.TRUE.equals( result ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        } ).validateArtifactId( artifactId );
    }

    public void isValidVersionId( final String versionId, final ValidatorCallback callback ) {
        validationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( Boolean result ) {
                if ( Boolean.TRUE.equals( result ) ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        } ).validateGAVVersion( versionId );
    }

    public void isValidClassName( final String className, final ValidatorCallback callback ) {
        String[] identifiers = className.split( "\\.", -1 );
        validationService.call( new RemoteCallback<Map<String, Boolean>>() {
            @Override
            public void callback( Map<String, Boolean> results ) {
                Boolean result = false;
                for ( Boolean subResult : results.values() ) {
                    if ( Boolean.FALSE.equals( subResult ) ) {
                        result = false;
                        break;
                    } else {
                        result = true;
                    }
                }
                if ( result ) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }
        } ).evaluateJavaIdentifiers( identifiers );
    }

    public void isValidDriverName( final String driverName, final ValidatorCallback callback ) {
        isNotEmpty( driverName, callback );
    }

    public void isValidDataSourceName( final String dataSourceName, final ValidatorCallback callback ) {
        isNotEmpty( dataSourceName, callback );
    }

    public void isValidConnectionURL( String connectionURL, ValidatorCallback callback ) {
        isNotEmpty( connectionURL, callback );
    }

    public void isNotEmpty( String value, ValidatorCallback callback ) {
        if ( !isEmpty( value ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    public boolean isEmpty( String value ) {
        return value == null || value.trim().isEmpty();
    }

}

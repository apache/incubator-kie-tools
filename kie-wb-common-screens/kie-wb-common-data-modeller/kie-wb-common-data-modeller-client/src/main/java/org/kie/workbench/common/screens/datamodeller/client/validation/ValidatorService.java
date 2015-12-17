/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.validation;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerErrorCallback;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

@ApplicationScoped
public class ValidatorService {

    private Caller<ValidationService> validationService;

    public static final String MANAGED_PROPERTY_EXISTS = "MANAGED_PROPERTY_EXISTS";

    public static final String UN_MANAGED_PROPERTY_EXISTS = "UN_MANAGED_PROPERTY_EXISTS";

    @Inject
    public ValidatorService( Caller<ValidationService> validationService ) {
        this.validationService = validationService;
    }

    // TODO Generify this!!
    public void isValidIdentifier( final String identifier,
            final ValidatorCallback callback ) {
        validationService.call(
                new RemoteCallback<Map<String, Boolean>>() {
                    @Override
                    public void callback( Map<String, Boolean> evaluated ) {
                        Boolean b = evaluated.get( identifier );
                        if ( Boolean.TRUE.equals( b ) ) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    }
                },
                new DataModelerErrorCallback( "An error occurred during the server validation process" )
        )
                .evaluateJavaIdentifiers( new String[]{ identifier } );
    }

    public void isValidPackageIdentifier( String identifier,
            final ValidatorCallback callback ) {
        String[] packageTerms = DataModelerUtils.getPackageTerms( identifier );
        validationService.call(
                new RemoteCallback<Map<String, Boolean>>() {
                    @Override
                    public void callback( Map<String, Boolean> evaluatedTerms ) {
                        // TODO the service is prepared for term-based evaluation, but for now the callback
                        // doesn't support returning params
                        boolean nok = evaluatedTerms.containsValue( Boolean.FALSE );
                        if ( nok ) {
                            callback.onFailure();
                        } else {
                            callback.onSuccess();
                        }
                    }
                },
                new DataModelerErrorCallback( "An error occurred during the server validation process" )
        )
                .evaluateJavaIdentifiers( packageTerms );
    }

    public void isValidTimerInterval( String expression, final ValidatorCallback callback ) {

        if ( expression == null || (expression.length() > 0 && "".equals( expression.trim() )) ) {
            callback.onFailure();
        } else {
            validationService.call( new RemoteCallback<Boolean>() {
                @Override
                public void callback( final Boolean value ) {
                    if ( Boolean.TRUE.equals( value ) ) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                }
            } ).isTimerIntervalValid( expression );
        }
    }

    public void isUniqueEntityName( String packageName,
            String name,
            DataModel model,
            ValidatorCallback callback ) {
        Boolean b = Boolean.TRUE;
        String className = assembleClassName( packageName, name );
        for ( DataObject d : model.getDataObjects() ) {
            if ( d.getClassName().equals( className ) ) {
                b = Boolean.FALSE;
                break;
            }
        }
        if ( b ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    // TODO add a validation in order to avoid cyclic extensions

    public void isUniqueAttributeName( String name,
            DataObject object,
            ValidatorWithReasonCallback callback ) {
        for ( ObjectProperty prop : object.getProperties() ) {
            if ( prop.getName().equalsIgnoreCase( name ) ) {
                callback.onFailure( MANAGED_PROPERTY_EXISTS );
                return;
            }
        }
        for ( ObjectProperty unmanagedProp : object.getUnmanagedProperties() ) {
            if ( unmanagedProp.getName().equalsIgnoreCase( name ) ) {
                callback.onFailure( UN_MANAGED_PROPERTY_EXISTS );
                return;
            }
        }
        callback.onSuccess();
    }

    public void canExtend( DataModelerContext context,
            String siblingCandidateName,
            String parentCandidateName,
            ValidatorCallback callback ) {
        if ( context.getHelper().isAssignableFrom( siblingCandidateName, parentCandidateName ) ) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }

    public void isValidPosition( String position,
            ValidatorCallback callback ) {
        int i = -1;
        if ( position == null || position.length() == 0 ) {
            i = 0;  // null or empty String is allowed
        }
        try {
            i = Integer.parseInt( position, 10 );
        } catch ( NumberFormatException e ) {
        }
        if ( i < 0 ) {
            callback.onFailure();
        } else {
            callback.onSuccess();
        }
    }

    public boolean isReferencedByCurrentObject( DataObject referencedObject,
            DataObject currentObject ) {

        if ( currentObject.getSuperClassName() != null && currentObject.getSuperClassName().equals( referencedObject.getClassName() )) return true;

        if ( currentObject.getProperties() != null ) {
            for ( ObjectProperty propertyTO : currentObject.getProperties() ) {
                if ( propertyTO.getClassName().equals( referencedObject.getClassName() ) ) return true;
            }
        }
        return false;
    }

}

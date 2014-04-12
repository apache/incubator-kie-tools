/**
 * Copyright 2012 JBoss Inc
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerErrorCallback;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.services.shared.validation.ValidatorCallback;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

@ApplicationScoped
public class ValidatorService {

    @Inject
    private Caller<ValidationService> validationService;

    public ValidatorService() {
    }

    // TODO Generify this!!
    public void isValidIdentifier( final String identifier,
                                   final ValidatorCallback callback ) {
        validationService.call(
                new RemoteCallback<Map<String, Boolean>>() {
                    @Override
                    public void callback( Map<String, Boolean> evaluated ) {
                        boolean b = evaluated.get( identifier );
                        if ( b ) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    }
                },
                new DataModelerErrorCallback( "An error occurred during the server validation process" )
                              )
                .evaluateIdentifiers( new String[]{ identifier } );
    }

    public void isValidPackageIdentifier( String identifier,
                                          final ValidatorCallback callback ) {
        String[] packageTerms = DataModelerUtils.getInstance().getPackageTerms( identifier );
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
                .evaluateIdentifiers( packageTerms );
    }

    public void isValidTimerInterval( String expression, final ValidatorCallback callback ) {

        //TODO, complete this validation.
        if ("kaka".equals( expression ) ) {
            callback.onFailure();
        } else {
            callback.onSuccess();
        }
    }

    public void isUniqueEntityName( String packageName,
                                    String name,
                                    DataModelTO model,
                                    ValidatorCallback callback ) {
        Boolean b = Boolean.TRUE;
        String className = assembleClassName( packageName, name );
        for ( DataObjectTO d : model.getDataObjects() ) {
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
                                       DataObjectTO object,
                                       ValidatorCallback callback ) {
        for ( ObjectPropertyTO prop : object.getProperties() ) {
            if ( prop.getName().equalsIgnoreCase( name ) ) {
                callback.onFailure();
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

    public Collection<String> getDataObjectExternalReferences( DataModelerContext context,
                                     DataObjectTO object,
                                     DataModelTO model ) {
        Collection<String> self = new ArrayList<String>(1);
        self.add( object.getClassName() );
        Collection<String> refs = context.getHelper().getDataObjectReferences( object.getClassName() );
        refs.removeAll( self );
        return refs;
    }
}

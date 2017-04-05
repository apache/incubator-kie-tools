/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.validation;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PersistenceDescriptorValidationMessage
        extends ValidationMessage {

    private List<String> params = new ArrayList<>( );

    public PersistenceDescriptorValidationMessage( @MapsTo( "id" ) long id,
                                                   @MapsTo( "level" ) Level level,
                                                   @MapsTo( "text" ) String text,
                                                   @MapsTo( "params" ) List< String > params ) {
        super( level, text );
        setId( id );
            this.params = params;
    }

    public PersistenceDescriptorValidationMessage( long id,
                                                   Level level,
                                                   String text ) {
        this( id, level, text, new ArrayList<>( ) );
    }

    public List< String > getParams( ) {
        return params;
    }
}
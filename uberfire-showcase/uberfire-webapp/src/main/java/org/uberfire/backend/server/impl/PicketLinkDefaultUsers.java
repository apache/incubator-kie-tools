/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.picketlink.authentication.event.PreAuthenticateEvent;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;

@ApplicationScoped
public class PicketLinkDefaultUsers {

    @Inject
    private PartitionManager partitionManager;

    private boolean done = false;

    /**
     * Creates example users so people can log in while trying out the app.
     */
    public synchronized void create( @Observes PreAuthenticateEvent event ) {
        if ( done ) {
            return;
        }

        done = true;

        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final RelationshipManager relationshipManager = partitionManager.createRelationshipManager();

        User admin = new User( "admin" );

        admin.setEmail( "john@doe.com" );
        admin.setFirstName( "John" );
        admin.setLastName( "Doe" );

        User nonAdmin = new User( "joe" );

        nonAdmin.setEmail( "joe@doe.com" );
        nonAdmin.setFirstName( "Joe" );
        nonAdmin.setLastName( "Doe" );

        identityManager.add( admin );
        identityManager.add( nonAdmin );
        identityManager.updateCredential( admin, new Password( "admin" ) );
        identityManager.updateCredential( nonAdmin, new Password( "joe" ) );

        Role roleSimple = new Role( "simple" );
        Role roleAdmin = new Role( "admin" );

        identityManager.add( roleSimple );
        identityManager.add( roleAdmin );

        relationshipManager.add( new Grant( admin, roleSimple ) );
        relationshipManager.add( new Grant( admin, roleAdmin ) );

        relationshipManager.add( new Grant( nonAdmin, roleSimple ) );
    }

}

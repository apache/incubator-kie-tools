/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.backend.server.impl;

import java.util.concurrent.atomic.AtomicBoolean;
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

    private final AtomicBoolean hasInitialized = new AtomicBoolean( false );

    public void onPreAuthenticateEvent( @Observes PreAuthenticateEvent event ) {
        if ( !hasInitialized.getAndSet( true ) ) {
            setup();
        }
    }

    private void setup() {
        final IdentityManager identityManager = partitionManager.createIdentityManager();
        final RelationshipManager relationshipManager = partitionManager.createRelationshipManager();

        final User admin = new User( "admin" );
        final User director = new User( "director" );
        final User user = new User( "user" );
        final User guest = new User( "guest" );

        identityManager.add( admin );
        identityManager.add( director );
        identityManager.add( user );
        identityManager.add( guest );

        identityManager.updateCredential( admin, new Password( "admin" ) );
        identityManager.updateCredential( director, new Password( "director" ) );
        identityManager.updateCredential( user, new Password( "user" ) );
        identityManager.updateCredential( guest, new Password( "guest" ) );

        final Role roleAdmin = new Role( "admin" );
        final Role roleAnalyst = new Role( "analyst" );

        identityManager.add( roleAdmin );
        identityManager.add( roleAnalyst );

        relationshipManager.add( new Grant( admin, roleAnalyst ) );
        relationshipManager.add( new Grant( admin, roleAdmin ) );

        relationshipManager.add( new Grant( director, roleAnalyst ) );

        relationshipManager.add( new Grant( user, roleAnalyst ) );
    }

}

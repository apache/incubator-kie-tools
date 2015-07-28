/**
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.picketlink.authentication.event.PreAuthenticateEvent;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;

@ApplicationScoped
public class PicketLinkDefaultUsers {

    @Inject
    private IdentityManager identityManager;

    @Inject
    private RelationshipManager relationshipManager;

    @Inject
    @Named( "socialUserPersistenceAPI" )
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    private boolean done = false;

    /**
     * Creates example users so people can log in while trying out the app.
     */
    public synchronized void create( @Observes PreAuthenticateEvent event ) {
        if ( done ) {
            return;
        }

        done = true;

        final Role roleSimple = new Role( "simple" );
        final Role roleAdmin = new Role( "admin" );

        identityManager.add( roleSimple );
        identityManager.add( roleAdmin );

        createUser( "admin", "Luke", "Skywalker", roleAdmin, roleSimple );
        createUser( "kenobi", "Obi-Wan", "Kenobi", roleAdmin, roleSimple );
        createUser( "han", "Han", "Solo", roleSimple );
        createUser( "darth", "Darth", "Vader", roleSimple );
    }

    private void createUser( final String userName, final String firstName, final String lastName, final Role... roles ) {
        final User user = new User( userName );
        user.setEmail( String.format( "%s@%s.com", firstName.toLowerCase(), lastName.toLowerCase() ) );
        user.setFirstName( firstName );
        user.setLastName( lastName );

        identityManager.add( user );
        identityManager.updateCredential( user, new Password( userName ) );

        for ( Role role : roles ) {
            relationshipManager.add( new Grant( user, role ) );
        }
        socialUserPersistenceAPI.updateUsers( fromUser( user ) );
        //Forces cache to sync
        socialUserPersistenceAPI.getSocialUser( userName );
    }

    private static SocialUser fromUser( final User user ) {
        final SocialUser socialUser = new SocialUser( user.getLoginName() );
        socialUser.setEmail( user.getEmail() );
        socialUser.setRealName( user.getFirstName() + " " + user.getLastName() );
        return socialUser;
    }

}

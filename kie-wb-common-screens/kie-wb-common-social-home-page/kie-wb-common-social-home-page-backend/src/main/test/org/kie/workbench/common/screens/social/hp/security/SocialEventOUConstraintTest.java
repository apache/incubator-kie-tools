/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.social.hp.security;

import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SocialEventOUConstraintTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private RepositoryServiceImpl repositoryService;

    @Mock
    private User identity;

    @InjectMocks
    private SocialEventOUConstraint socialEventOUConstraint;

    private SocialUser socialUser = new SocialUser( "dora" );


    @Before
    public void setUp() throws Exception {
        Collection<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
        final OrganizationalUnitImpl ou = new OrganizationalUnitImpl( "ouname", "owner", "groupid" );
        ous.add( ou );
        when( organizationalUnitService.getOrganizationalUnits() ).thenReturn( ous );
        when( authorizationManager.authorize( ou, identity ) ).thenReturn( true );
    }


    @Test
    public void init() throws Exception {
        socialEventOUConstraint.init();
        assertFalse( socialEventOUConstraint.getAuthorizedOrganizationUnits().isEmpty() );
    }

    @Test
    public void hasRestrictionsTest() throws Exception {

        final SocialActivitiesEvent event = new SocialActivitiesEvent( socialUser, OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT, new Date() )
                .withLink( "otherName", "otherName", SocialActivitiesEvent.LINK_TYPE.CUSTOM );

        socialEventOUConstraint.init();
        assertTrue( socialEventOUConstraint.hasRestrictions( event ) );

    }

    @Test
    public void hasNoRestrictionsTest() throws Exception {

        final SocialActivitiesEvent event = new SocialActivitiesEvent( socialUser, OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT, new Date() )
                .withLink( "name", "ouname", SocialActivitiesEvent.LINK_TYPE.CUSTOM );

        socialEventOUConstraint.init();
        assertFalse( socialEventOUConstraint.hasRestrictions( event ) );
    }

    @Test
    public void hasNoRestrictionsForOtherSocialEventsTest() throws Exception {

        final SocialActivitiesEvent VSFotherType = new SocialActivitiesEvent( socialUser, "type", new Date() )
                .withLink( "link", "link", SocialActivitiesEvent.LINK_TYPE.VFS );
        final SocialActivitiesEvent customEventOtherType = new SocialActivitiesEvent( socialUser, "type", new Date() )
                .withLink( "link", "link", SocialActivitiesEvent.LINK_TYPE.CUSTOM );

        assertFalse( socialEventOUConstraint.hasRestrictions( VSFotherType ) );
        assertFalse( socialEventOUConstraint.hasRestrictions( customEventOtherType ) );

    }
}
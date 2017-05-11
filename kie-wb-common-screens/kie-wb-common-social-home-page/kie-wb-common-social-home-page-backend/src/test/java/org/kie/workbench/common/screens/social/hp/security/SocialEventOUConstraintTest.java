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
package org.kie.workbench.common.screens.social.hp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SocialEventOUConstraintTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private RepositoryServiceImpl repositoryService;

    @Mock
    private UserCDIContextHelper userCDIContextHelper;

    private SocialEventOUConstraint socialEventOUConstraint;

    private SocialUser socialUser = new SocialUser("dora");
    private User user = new UserImpl("bento");

    @Before
    public void setUp() throws Exception {
        Collection<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
        final OrganizationalUnitImpl ou = new OrganizationalUnitImpl("ouname",
                                                                     "owner",
                                                                     "groupid");
        ous.add(ou);
        when(organizationalUnitService.getOrganizationalUnits()).thenReturn(ous);
        when(authorizationManager.authorize(ou,
                                            user)).thenReturn(true);
        when(userCDIContextHelper.getUser()).thenReturn(user);
        when(userCDIContextHelper.thereIsALoggedUserInScope()).thenReturn(true);

        socialEventOUConstraint = new SocialEventOUConstraint(organizationalUnitService,
                                                              authorizationManager,
                                                              repositoryService,
                                                              userCDIContextHelper);
    }

    @Test
    public void init() throws Exception {
        socialEventOUConstraint.init();
        assertFalse(socialEventOUConstraint.getAuthorizedOrganizationUnits().isEmpty());
    }

    @Test
    public void hasRestrictionsTest() throws Exception {

        final SocialActivitiesEvent event = new SocialActivitiesEvent(socialUser,
                                                                      OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                      new Date())
                .withLink("otherName",
                          "otherName",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        socialEventOUConstraint.init();
        assertTrue(socialEventOUConstraint.hasRestrictions(event));
    }

    @Test
    public void hasNoRestrictionsTest() throws Exception {

        final SocialActivitiesEvent event = new SocialActivitiesEvent(socialUser,
                                                                      OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                      new Date())
                .withLink("name",
                          "ouname",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        socialEventOUConstraint.init();
        assertFalse(socialEventOUConstraint.hasRestrictions(event));
    }

    @Test
    public void hasRestrictionsBecauseThrowsAnExceptionTest() throws Exception {

        final SocialActivitiesEvent event = new SocialActivitiesEvent(socialUser,
                                                                      OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                      new Date())
                .withLink("name",
                          "ouname",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        SocialEventOUConstraint spy = spy(socialEventOUConstraint);
        when(spy.isOUSocialEvent(event)).thenThrow(RuntimeException.class);

        spy.init();
        assertTrue(spy.hasRestrictions(event));
    }

    @Test
    public void hasNoRestrictionsForOtherSocialEventsTest() throws Exception {

        final SocialActivitiesEvent vfsOtherType = new SocialActivitiesEvent(socialUser,
                                                                             "type",
                                                                             new Date())
                .withLink("link",
                          "link",
                          SocialActivitiesEvent.LINK_TYPE.VFS);
        final SocialActivitiesEvent customEventOtherType = new SocialActivitiesEvent(socialUser,
                                                                                     "type",
                                                                                     new Date())
                .withLink("link",
                          "link",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        socialEventOUConstraint.init();

        assertFalse(socialEventOUConstraint.hasRestrictions(vfsOtherType));
        assertFalse(socialEventOUConstraint.hasRestrictions(customEventOtherType));
    }

    @Test
    public void ifThereIsNoLoggedUserInScopeShouldNotHaveRestrictions() throws Exception {

        when(userCDIContextHelper.thereIsALoggedUserInScope()).thenReturn(false);

        final SocialActivitiesEvent restrictedEvent = new SocialActivitiesEvent(socialUser,
                                                                                OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                                new Date())
                .withLink("otherName",
                          "otherName",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        socialEventOUConstraint.init();

        assertFalse(socialEventOUConstraint.hasRestrictions(restrictedEvent));
    }
}
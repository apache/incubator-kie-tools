package org.kie.workbench.common.screens.social.hp.security;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.impl.OrganizationalUnitImpl;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.impl.git.GitRepository;
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
public class SocialEventRepositoryConstraintTest {

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private RepositoryServiceImpl repositoryService;

    @Mock
    private UserCDIContextHelper userCDIContextHelper;

    @Mock
    private ConfiguredRepositories configuredRepositories;

    private SocialEventRepositoryConstraint socialEventRepositoryConstraint;

    private SocialUser socialUser = new SocialUser("dora");
    private Repository returnRepo;
    private GitRepository repository;
    private User user = new UserImpl("bento");

    @Before
    public void setUp() throws Exception {

        Collection<OrganizationalUnit> ous = new ArrayList<OrganizationalUnit>();
        final OrganizationalUnitImpl ou = new OrganizationalUnitImpl("ouname",
                                                                     "owner",
                                                                     "groupid");
        final OrganizationalUnitImpl ouSpy = spy(ou);
        Collection<Repository> repositories = new ArrayList<Repository>();
        repository = new GitRepository("repo");
        repositories.add(repository);
        ous.add(ouSpy);

        when(ouSpy.getRepositories()).thenReturn(repositories);
        when(organizationalUnitService.getOrganizationalUnits()).thenReturn(ous);
        when(authorizationManager.authorize(ou,
                                            user)).thenReturn(true);
        when(authorizationManager.authorize(repository,
                                            user)).thenReturn(true);
        when(userCDIContextHelper.getUser()).thenReturn(user);
        when(userCDIContextHelper.thereIsALoggedUserInScope()).thenReturn(true);

        socialEventRepositoryConstraint = createSocialEventRepositoryContraint();
    }

    @Test
    public void init() throws Exception {
        socialEventRepositoryConstraint.init();
        assertFalse(socialEventRepositoryConstraint.getAuthorizedRepositories().isEmpty());
    }

    @Test
    public void hasRestrictionsTest() throws Exception {

        final SocialActivitiesEvent event = new SocialActivitiesEvent(socialUser,
                                                                      OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                      new Date())
                .withLink("otherName",
                          "otherName",
                          SocialActivitiesEvent.LINK_TYPE.VFS);

        socialEventRepositoryConstraint.init();

        assertTrue(socialEventRepositoryConstraint.hasRestrictions(event));
    }

    @Test
    public void hasNoRestrictionsTest() throws Exception {
        returnRepo = repository;

        final SocialActivitiesEvent vfsEvent = new SocialActivitiesEvent(socialUser,
                                                                         "type",
                                                                         new Date());
        final SocialActivitiesEvent projectEvent = new SocialActivitiesEvent(socialUser,
                                                                             OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                             new Date())
                .withLink("otherName",
                          "otherName",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        this.socialEventRepositoryConstraint.init();

        assertFalse(this.socialEventRepositoryConstraint.hasRestrictions(vfsEvent));
        assertFalse(this.socialEventRepositoryConstraint.hasRestrictions(projectEvent));
    }

    @Test
    public void hasRestrictionsBecauseThrowsAnExceptionTest() throws Exception {

        returnRepo = repository;
        SocialEventRepositoryConstraint socialEventRepositoryConstraintSpy = spy(this.socialEventRepositoryConstraint);

        final SocialActivitiesEvent vfsEvent = new SocialActivitiesEvent(socialUser,
                                                                         "type",
                                                                         new Date());

        socialEventRepositoryConstraintSpy.init();

        assertFalse(socialEventRepositoryConstraintSpy.hasRestrictions(vfsEvent));

        when(socialEventRepositoryConstraintSpy.getEventRepository(vfsEvent)).thenThrow(RuntimeException.class);

        assertTrue(socialEventRepositoryConstraintSpy.hasRestrictions(vfsEvent));
    }

    @Test
    public void hasNoRestrictionsForOtherSocialEventsTest() throws Exception {

        final SocialActivitiesEvent customEventOtherType = new SocialActivitiesEvent(socialUser,
                                                                                     "type",
                                                                                     new Date())
                .withLink("link",
                          "link",
                          SocialActivitiesEvent.LINK_TYPE.CUSTOM);

        assertFalse(socialEventRepositoryConstraint.hasRestrictions(customEventOtherType));
    }

    @Test
    public void ifThereIsNoLoggedUserInScopeShouldNotHaveRestrictions() throws Exception {

        when(userCDIContextHelper.thereIsALoggedUserInScope()).thenReturn(false);

        final SocialActivitiesEvent restrictedEvent = new SocialActivitiesEvent(socialUser,
                                                                                OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT,
                                                                                new Date())
                .withLink("otherName",
                          "otherName",
                          SocialActivitiesEvent.LINK_TYPE.VFS);

        socialEventRepositoryConstraint.init();

        assertFalse(socialEventRepositoryConstraint.hasRestrictions(restrictedEvent));
    }

    private SocialEventRepositoryConstraint createSocialEventRepositoryContraint() {
        return new SocialEventRepositoryConstraint(
                organizationalUnitService,
                authorizationManager,
                configuredRepositories,
                userCDIContextHelper) {
            @Override
            Repository getEventRepository(SocialActivitiesEvent event) {
                return returnRepo;
            }
        };
    }
}
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.profile;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractProfileManagerTest {

    private static final String DEF_SET_ID = "ds1";
    private static final String PROFILE_DEFAULT_ID = "pDefault";
    private static final String PROFILE_DOMAIN_ID = "pDomain";

    @Mock
    private Annotation domainQualifier;

    @Mock
    private Profile defaultProfile;

    @Mock
    private Profile domainProfile;

    private AbstractProfileManagerStub tested;
    private List<Profile> profiles;

    @Before
    public void setup() throws Exception {
        when(defaultProfile.getProfileId()).thenReturn(PROFILE_DEFAULT_ID);
        when(domainProfile.getProfileId()).thenReturn(PROFILE_DOMAIN_ID);
        profiles = Arrays.asList(defaultProfile, domainProfile);
        tested = new AbstractProfileManagerStub();
    }

    @Test
    public void testGetAllProfiles() {
        Collection<Profile> allProfiles = tested.getAllProfiles();
        assertEquals(2, allProfiles.size());
        assertTrue(allProfiles.contains(defaultProfile));
        assertTrue(allProfiles.contains(domainProfile));
    }

    @Test
    public void testGetProfileById() {
        assertEquals(defaultProfile, tested.getProfile(PROFILE_DEFAULT_ID));
        assertEquals(domainProfile, tested.getProfile(PROFILE_DOMAIN_ID));
        assertNull(tested.getProfile("someOtherId"));
    }

    @Test
    public void testGetProfilesByDomain() {
        Collection<Profile> domainProfiles = tested.getProfiles(DEF_SET_ID);
        assertEquals(2, domainProfiles.size());
        assertTrue(domainProfiles.contains(defaultProfile));
        assertTrue(domainProfiles.contains(domainProfile));
    }

    @Test
    public void testGetProfileByDomainAndId() {
        assertEquals(defaultProfile, tested.getProfile(DEF_SET_ID, PROFILE_DEFAULT_ID));
        assertEquals(domainProfile, tested.getProfile(DEF_SET_ID, PROFILE_DOMAIN_ID));
    }

    private class AbstractProfileManagerStub extends AbstractProfileManager {

        @Override
        protected Function<String, Annotation> getQualifier() {
            return AbstractProfileManagerTest.this::getQualifier;
        }

        @Override
        protected Iterable<Profile> getAllProfileInstances() {
            return AbstractProfileManagerTest.this.getAllProfileInstances();
        }

        @Override
        protected Iterable<Profile> selectProfileInstances(Annotation... qualifiers) {
            return AbstractProfileManagerTest.this.selectProfileInstances(qualifiers);
        }
    }

    private Annotation getQualifier(String dsId) {
        return DEF_SET_ID.equals(dsId) ? domainQualifier : null;
    }

    private Iterable<Profile> getAllProfileInstances() {
        return profiles;
    }

    private Iterable<Profile> selectProfileInstances(Annotation... qualifiers) {
        List<Profile> result = new ArrayList<>();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.equals(DefinitionManager.DEFAULT_QUALIFIER)) {
                result.add(defaultProfile);
            } else if (qualifier.equals(domainQualifier)) {
                result.add(domainProfile);
            }
        }
        return result;
    }
}

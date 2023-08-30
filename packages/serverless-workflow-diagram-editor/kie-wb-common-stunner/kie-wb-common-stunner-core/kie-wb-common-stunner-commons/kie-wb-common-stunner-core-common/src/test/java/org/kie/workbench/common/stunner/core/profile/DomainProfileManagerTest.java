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

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DomainProfileManagerTest {

    private static final FullProfile DEFAULT_PROFILE = new FullProfile();
    private static final String DEF_SET_ID = "ds1";
    private static final String DEF1 = "d1";
    private static final String DEF2 = "d2";
    private static final String PROFILE_DOMAIN_ID = "pDomain";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private TypeDefinitionSetRegistry definitionSets;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private ProfileManager profileManager;

    @Mock
    private Object definitionSet;

    @Mock
    private DomainProfile domainProfile;

    @Mock
    private Metadata metadata;

    private DomainProfileManager tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(metadata.getProfileId()).thenReturn(DEFAULT_PROFILE.getProfileId());
        when(domainProfile.getProfileId()).thenReturn(PROFILE_DOMAIN_ID);
        Predicate<String> domainProfileFilter = DEF1::equals;
        when(domainProfile.definitionAllowedFilter()).thenReturn(domainProfileFilter);
        when(definitionManager.definitionSets()).thenReturn(definitionSets);
        when(definitionSets.getDefinitionSetById(eq(DEF_SET_ID))).thenReturn(definitionSet);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getDefinitions(eq(definitionSet)))
                .thenReturn(Stream.of(DEF1, DEF2).collect(Collectors.toSet()));
        when(profileManager.getProfile(eq(DEF_SET_ID), eq(DEFAULT_PROFILE.getProfileId())))
                .thenReturn(DEFAULT_PROFILE);
        when(profileManager.getProfile(eq(DEF_SET_ID), eq(PROFILE_DOMAIN_ID)))
                .thenReturn(domainProfile);
        tested = new DomainProfileManager(definitionManager,
                                          profileManager,
                                          DEFAULT_PROFILE);
    }

    @Test
    public void testGetAllDefinitions() {
        // Using the default profile
        when(metadata.getProfileId()).thenReturn(DEFAULT_PROFILE.getProfileId());
        List<String> allDefinitions = tested.getAllDefinitions(metadata);
        assertEquals(2, allDefinitions.size());
        assertTrue(allDefinitions.contains(DEF1));
        assertTrue(allDefinitions.contains(DEF2));
        // Using the domain profile
        when(metadata.getProfileId()).thenReturn(PROFILE_DOMAIN_ID);
        List<String> domainDefinitions = tested.getAllDefinitions(metadata);
        assertEquals(1, domainDefinitions.size());
        assertTrue(domainDefinitions.contains(DEF1));
        assertFalse(domainDefinitions.contains(DEF2));
    }

    @Test
    public void testIsDefinitionAllowed() {
        // Using the default profile
        when(metadata.getProfileId()).thenReturn(DEFAULT_PROFILE.getProfileId());
        assertTrue(tested.isDefinitionIdAllowed(metadata).test(DEF1));
        assertTrue(tested.isDefinitionIdAllowed(metadata).test(DEF2));
        // Using the domain profile
        when(metadata.getProfileId()).thenReturn(PROFILE_DOMAIN_ID);
        assertTrue(tested.isDefinitionIdAllowed(metadata).test(DEF1));
        assertFalse(tested.isDefinitionIdAllowed(metadata).test(DEF2));
    }
}

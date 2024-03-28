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


package org.kie.workbench.common.stunner.core.client.api;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.stubs.ManagedInstanceStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClientProfileManagerTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private Profile profile1;

    @Mock
    private Profile profile2;

    private ClientProfileManager tested;
    private ManagedInstance<Profile> profileInstances;

    @Before
    public void setup() {
        profileInstances = spy(new ManagedInstanceStub<>(profile1, profile2));
        tested = new ClientProfileManager(definitionUtils,
                                          profileInstances);
    }

    @Test
    public void testGetQualifier() {
        tested.getQualifier().apply("q1");
        verify(definitionUtils, times(1)).getQualifier(eq("q1"));
        tested.getQualifier().apply("q2");
        verify(definitionUtils, times(1)).getQualifier(eq("q2"));
    }

    @Test
    public void testGetAllProfileInstances() {
        Iterable<Profile> profiles = tested.getAllProfileInstances();
        Iterator<Profile> iterator = profiles.iterator();
        assertEquals(profile1, iterator.next());
        assertEquals(profile2, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSelectProfileInstances() {
        Annotation qualifier = mock(Annotation.class);
        tested.selectProfileInstances(qualifier);
        verify(profileInstances, times(1)).select(eq(qualifier));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(profileInstances, times(1)).destroyAll();
    }
}

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.profile;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.MockInstanceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BackendProfileManagerTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private Profile profile1;

    @Mock
    private Profile profile2;

    private BackendProfileManager tested;
    private Instance<Profile> profileInstances;

    @Before
    public void setup() {
        profileInstances = spy(new MockInstanceImpl<>(profile1, profile2));
        tested = new BackendProfileManager(definitionUtils,
                                           profileInstances);
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(profileInstances, times(1)).destroy(eq(profile1));
        verify(profileInstances, times(1)).destroy(eq(profile2));
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
}

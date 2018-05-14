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

package org.kie.workbench.common.stunner.core.client.preferences;

import java.lang.annotation.Annotation;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ManagedInstanceStub;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StunnerPreferencesRegistriesTest {

    private static final String DEF_SET_ID = "ds1";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private StunnerPreferencesRegistry preferencesRegistry;
    private ManagedInstance<StunnerPreferencesRegistry> preferencesRegistries;

    @Mock
    private Annotation qualifier;

    @Mock
    private StunnerPreferences preferences;

    private StunnerPreferencesRegistries tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        preferencesRegistries = spy(new ManagedInstanceStub<>(preferencesRegistry));
        when(preferencesRegistry.get()).thenReturn(preferences);
        when(definitionUtils.getQualifier(eq(DEF_SET_ID))).thenReturn(qualifier);
        tested = new StunnerPreferencesRegistries(definitionUtils,
                                                  preferencesRegistries);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGet() {
        StunnerPreferences instance = tested.get(DEF_SET_ID);
        verify(preferencesRegistries, times(1)).select(eq(qualifier));
        assertEquals(preferences, instance);
    }
}

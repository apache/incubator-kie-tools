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

package org.kie.workbench.common.stunner.core.lookup.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommonDomainLookupsTest {

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private Function<String, DomainLookupsCache> cacheBuilder;

    @Mock
    private DomainLookupsCache cache;

    private CommonDomainLookups tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(cacheBuilder.apply(anyString())).thenReturn(cache);
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        tested = new CommonDomainLookups(definitionUtils,
                                         definitionsRegistry,
                                         ruleManager,
                                         cacheBuilder);
    }

    @Test
    public void testSetTheDomain() {
        String domain = "d1";
        assertEquals(tested, tested.setDomain(domain));
        verify(cacheBuilder, times(1)).apply(eq(domain));
        assertEquals(cache, tested.getCache());
    }

    @Test
    public void testLookupMorphBaseDefinitions() {
        String def1Id = "def1";
        String def2Id = "def2";
        String def3Id = "def3";
        String def4Id = "def4";
        String morphBase1 = "morphBase1";
        String morphBase2 = "morphBase2";
        Object def1 = mock(Object.class);
        Object def2 = mock(Object.class);
        Object def3 = mock(Object.class);
        Object def4 = mock(Object.class);
        MorphDefinition morphDefinition1 = mock(MorphDefinition.class);
        MorphDefinition morphDefinition2 = mock(MorphDefinition.class);
        MorphDefinition morphDefinition3 = mock(MorphDefinition.class);
        when(definitionsRegistry.getDefinitionById(eq(def1Id))).thenReturn(def1);
        when(definitionsRegistry.getDefinitionById(eq(def2Id))).thenReturn(def2);
        when(definitionsRegistry.getDefinitionById(eq(def3Id))).thenReturn(def3);
        when(definitionsRegistry.getDefinitionById(eq(def4Id))).thenReturn(def4);
        when(definitionUtils.getMorphDefinition(eq(def1))).thenReturn(morphDefinition1);
        when(definitionUtils.getMorphDefinition(eq(def2))).thenReturn(morphDefinition2);
        when(definitionUtils.getMorphDefinition(eq(def3))).thenReturn(morphDefinition3);
        when(morphDefinition1.getDefault()).thenReturn(morphBase1);
        when(morphDefinition2.getDefault()).thenReturn(morphBase2);
        when(morphDefinition3.getDefault()).thenReturn(morphBase2);
        Set<String> result = tested.lookupMorphBaseDefinitions(new HashSet<String>(3) {{
            add(def1Id);
            add(def2Id);
            add(def3Id);
            add(def4Id);
        }});
        assertTrue(result.contains(morphBase1));
        assertTrue(result.contains(morphBase2));
        assertTrue(result.contains(def4Id));
    }

    @Test
    public void testDestroy() {
        tested.setDomain("someDomain");
        tested.destroy();
        verify(cache, times(1)).clear();
        assertNull(tested.getCache());
    }
}

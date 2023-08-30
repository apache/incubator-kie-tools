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


package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BindableDefinitionSetAdapterImplTest {

    private static final Annotation QUALIFIER = new SomeQualifier() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return SomeQualifier.class;
        }
    };
    private static final SomeDefSet INSTANCE = new SomeDefSet();
    private static final DefinitionSetAdapterBindings BINDINGS = new DefinitionSetAdapterBindings()
            .setGraphFactory(GraphFactory.class)
            .setQualifier(QUALIFIER)
            .setDefinitionIds(new HashSet<>(Arrays.asList("def1", "def2")));

    private BindableDefinitionSetAdapterImpl<Object> tested;

    @Mock
    private StunnerTranslationService translationService;

    @Before
    public void setUp() {
        tested = BindableDefinitionSetAdapterImpl.create(translationService);
        tested.setBindings(SomeDefSet.class, BINDINGS);
    }

    @Test
    public void testGetId() {
        String id = tested.getId(INSTANCE);
        assertEquals("org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapterImplTest", id);
    }

    @Test
    public void testGetDomain() {
        String domain = tested.getDomain(INSTANCE);
        assertEquals("org.kie.workbench.common.stunner.core.definition.adapter.binding", domain);
    }

    @Test
    public void testGetDescription() {
        when(translationService.getDefinitionSetDescription(eq("org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapterImplTest")))
                .thenReturn("defSetDescriptionValue");
        String description = tested.getDescription(INSTANCE);
        assertEquals("defSetDescriptionValue", description);
    }

    @Test
    @SuppressWarnings("all")
    public void testGetGraphFactory() {
        Class<? extends ElementFactory> graphFactoryType = tested.getGraphFactoryType(INSTANCE);
        assertEquals(GraphFactory.class, graphFactoryType);
    }

    @Test
    public void testGetQualifier() {
        Annotation qualifier = tested.getQualifier(INSTANCE);
        assertEquals(QUALIFIER, qualifier);
    }

    @Test
    public void testGetSvgNodeId() {
        when(translationService.getDefinitionSetSvgNodeId(eq("org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapterImplTest")))
                .thenReturn(Optional.of("svg1"));
        Optional<String> svgNodeId = tested.getSvgNodeId(INSTANCE);
        assertTrue(svgNodeId.isPresent());
        assertEquals("svg1", svgNodeId.get());
    }

    @Test
    public void testGetDefinitions() {
        Set<String> definitions = tested.getDefinitions(INSTANCE);
        assertEquals(2, definitions.size());
        assertTrue(definitions.contains("def1"));
        assertTrue(definitions.contains("def2"));
    }

    private static class SomeDefSet {

    }

    private @interface SomeQualifier {

    }
}

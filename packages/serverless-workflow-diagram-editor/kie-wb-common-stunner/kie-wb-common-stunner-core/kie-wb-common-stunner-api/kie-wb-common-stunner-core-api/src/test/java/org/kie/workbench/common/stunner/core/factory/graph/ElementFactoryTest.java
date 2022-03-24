/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.factory.graph;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ElementFactoryTest extends TestCase {

    @Test
    public void testIsDelegateFactory() {
        ElementFactory factory = new ElementFactory() {
            @Override
            public Class<? extends ElementFactory> getFactoryType() {
                return null;
            }

            @Override
            public Element build(String uuid, Object definition) {
                return null;
            }

            @Override
            public boolean accepts(Object source) {
                return false;
            }
        };
        assertFalse(factory.isDelegateFactory());
    }

    @Test
    public void testBuild() {
        final String UUID = "UUID";
        final String DEFINITION = "Definition String";

        Element element = mock(Element.class);
        ElementFactory<String, Definition<String>, Element<Definition<String>>> factory = new ElementFactory() {
            @Override
            public Class<? extends ElementFactory> getFactoryType() {
                return null;
            }

            @Override
            public Element build(String uuid, Object definition) {
                return element;
            }

            @Override
            public boolean accepts(Object source) {
                return false;
            }
        };
        assertEquals(element, factory.build(UUID, DEFINITION, null));
    }
}
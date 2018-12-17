/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.AbstractGraphDefinitionTypesTest;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ParentsTypeMatcherTest extends AbstractGraphDefinitionTypesTest {

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    private ParentsTypeMatcher newPredicate(final Class<?> parentType) {
        return new ParentsTypeMatcher(graphHandler.definitionManager)
                .forParentType(parentType);
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testMissingNodeA() {
        assertFalse(newPredicate(ParentDefinition.class)
                            .test(nodeA,
                                  null));
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testMissingNodeB() {
        assertFalse(newPredicate(ParentDefinition.class)
                            .test(null,
                                  nodeB));
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void testMissingNodes() {
        assertFalse(newPredicate(ParentDefinition.class)
                            .test(null,
                                  null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithParentSubprocess() {
        assertTrue(newPredicate(ParentDefinition.class).test(nodeA, nodeB));
        assertTrue(newPredicate(DefinitionA.class).test(nodeA, nodeB));
        assertFalse(newPredicate(RootDefinition.class).test(nodeA, nodeB));
        assertFalse(newPredicate(ParentDefinition.class).test(nodeA, nodeC));
        assertFalse(newPredicate(RootDefinition.class).test(nodeA, nodeC));
        assertFalse(newPredicate(ParentDefinition.class).test(nodeB, nodeC));
        assertFalse(newPredicate(RootDefinition.class).test(nodeB, nodeC));

        //now set root node as parent
        graphHandler.removeChild(parentNode, nodeA);
        graphHandler.removeChild(parentNode, nodeB);
        graphHandler.setChild(rootNode, nodeA);
        graphHandler.setChild(rootNode, nodeB);
        assertTrue(newPredicate(RootDefinition.class).test(nodeA, nodeC));
        assertTrue(newPredicate(RootDefinition.class).test(nodeB, nodeC));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithNoParents() {
        graphHandler
                .removeChild(parentNode, nodeA)
                .removeChild(parentNode, nodeB);
        assertTrue(newPredicate(RootDefinition.class).test(nodeA, nodeB));

        graphHandler.setChild(rootNode, nodeA);
        assertFalse(newPredicate(RootDefinition.class).test(nodeA, nodeB));

        graphHandler.removeChild(rootNode, nodeA);
        graphHandler.setChild(rootNode, nodeB);
        assertFalse(newPredicate(RootDefinition.class).test(nodeA, nodeB));

        graphHandler.setChild(rootNode, nodeA);
        assertTrue(newPredicate(RootDefinition.class).test(nodeA, nodeB));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithNoParent() {
        graphHandler.removeChild(parentNode, nodeA);
        assertFalse(newPredicate(RootDefinition.class).test(nodeA, nodeC));

        graphHandler.setChild(rootNode, nodeA);
        assertTrue(newPredicate(RootDefinition.class).test(nodeA, nodeC));
    }
}

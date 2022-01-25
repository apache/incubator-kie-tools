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

package org.kie.workbench.common.stunner.core.graph;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class HashCodeAndEqualityTest {

    @Test
    public void testGraphEquals() {
        GraphImpl<String> a = new GraphImpl<String>("Graph",
                                                    new GraphNodeStoreImpl());
        GraphImpl<String> b = new GraphImpl<String>("Graph",
                                                    new GraphNodeStoreImpl());
        assertEquals(a,
                     b);

        NodeImpl<String> node = new NodeImpl<>("Node");
        a.addNode(node);
        assertNotEquals(a,
                        b);
        b.addNode(node);
        assertEquals(a,
                     b);
        assertEquals(a,
                     a);
    }

    @Test
    public void testGraphHashCode() {
        GraphImpl<String> a = new GraphImpl<String>("Graph",
                                                    new GraphNodeStoreImpl());
        GraphImpl<String> b = new GraphImpl<String>("Graph",
                                                    new GraphNodeStoreImpl());
        assertEquals(a.hashCode(),
                     b.hashCode());

        NodeImpl<String> node = new NodeImpl<>("Node");
        a.addNode(node);
        assertNotEquals(a.hashCode(),
                        b.hashCode());
        b.addNode(node);
        assertEquals(a.hashCode(),
                     b.hashCode());
        assertEquals(a.hashCode(),
                     a.hashCode());
    }
}

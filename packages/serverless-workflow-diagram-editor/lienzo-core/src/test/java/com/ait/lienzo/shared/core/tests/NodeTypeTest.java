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

package com.ait.lienzo.shared.core.tests;

import com.ait.lienzo.shared.core.types.NodeType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeTypeTest {

    @Test
    public void testEquals() {
        NodeType group = NodeType.GROUP;
        assertFalse(group.equals(null));
        assertTrue(group.equals(group));

        NodeType layer = NodeType.LAYER;
        assertFalse(layer.equals(group));
        assertFalse(group.equals(layer));

        NodeTypeExtension something = NodeTypeExtension.SOMETHING;
        assertFalse(group.equals(something));
        assertFalse(something.equals(group));

        NodeTypeExtension overrideGroup = NodeTypeExtension.GROUP;
        assertTrue(overrideGroup.equals(group));
        assertTrue(group.equals(overrideGroup));
    }

    private static class NodeTypeExtension extends NodeType {

        public static NodeTypeExtension SOMETHING = new NodeTypeExtension("Something");
        public static NodeTypeExtension GROUP = new NodeTypeExtension("Group");

        protected NodeTypeExtension(String value) {
            super(value);
        }
    }
}

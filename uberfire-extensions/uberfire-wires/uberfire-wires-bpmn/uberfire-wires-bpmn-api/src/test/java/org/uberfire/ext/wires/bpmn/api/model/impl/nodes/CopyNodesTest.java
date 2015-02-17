/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.bpmn.api.model.impl.nodes;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CopyNodesTest {

    @Test
    public void testCopyStartProcessNode() {
        final StartProcessNode node = new StartProcessNode();
        final StartProcessNode copy = node.copy();

        assertNotNull( copy );
        assertFalse( node == copy );
        assertEquals( node.getContent().getId(),
                      copy.getContent().getId() );
        assertEquals( node.getContent().getTitle(),
                      copy.getContent().getTitle() );
        assertEquals( node.getContent().getDescription(),
                      copy.getContent().getDescription() );
        assertEquals( node.getContent().getRoles(),
                      copy.getContent().getRoles() );
        assertEquals( node.getContent().getProperties(),
                      copy.getContent().getProperties() );
    }

    @Test
    public void testCopyEndProcessNode() {
        final EndProcessNode node = new EndProcessNode();
        final EndProcessNode copy = node.copy();

        assertNotNull( copy );
        assertFalse( node == copy );
        assertEquals( node.getContent().getId(),
                      copy.getContent().getId() );
        assertEquals( node.getContent().getTitle(),
                      copy.getContent().getTitle() );
        assertEquals( node.getContent().getDescription(),
                      copy.getContent().getDescription() );
        assertEquals( node.getContent().getRoles(),
                      copy.getContent().getRoles() );
        assertEquals( node.getContent().getProperties(),
                      copy.getContent().getProperties() );
    }

    @Test
    public void testCopyProcessNode() {
        final ProcessNode node = new ProcessNode();
        final ProcessNode copy = node.copy();

        assertNotNull( copy );
        assertFalse( node == copy );
        assertEquals( node.getContent().getId(),
                      copy.getContent().getId() );
        assertEquals( node.getContent().getTitle(),
                      copy.getContent().getTitle() );
        assertEquals( node.getContent().getDescription(),
                      copy.getContent().getDescription() );
        assertEquals( node.getContent().getRoles(),
                      copy.getContent().getRoles() );
        assertEquals( node.getContent().getProperties(),
                      copy.getContent().getProperties() );
    }

}

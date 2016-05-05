/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraph;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;

import static junit.framework.Assert.assertNotNull;
import static org.jgroups.util.Util.assertTrue;
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
    public void testCopyProcessNode1() {
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

    @Test
    public void testCopyProcessNode2() {
        final ProcessNode process = new ProcessNode();
        final StartProcessNode startProcessNode = new StartProcessNode();
        final EndProcessNode endProcessNode = new EndProcessNode();
        process.addNode( startProcessNode );
        process.addNode( endProcessNode );

        final ProcessNode copyProcess = process.copy();

        assertNotNull( copyProcess );
        assertFalse( process == copyProcess );
        assertEquals( process.getContent().getId(),
                      copyProcess.getContent().getId() );
        assertEquals( process.getContent().getTitle(),
                      copyProcess.getContent().getTitle() );
        assertEquals( process.getContent().getDescription(),
                      copyProcess.getContent().getDescription() );
        assertEquals( process.getContent().getRoles(),
                      copyProcess.getContent().getRoles() );
        assertEquals( process.getContent().getProperties(),
                      copyProcess.getContent().getProperties() );

        assertEquals( process.size(),
                      copyProcess.size() );

        final BpmnGraphNode copyStartProcessNode = getNode( copyProcess,
                                                            StartProcessNode.class );
        assertNotNull( copyStartProcessNode );
        assertTrue( copyStartProcessNode instanceof StartProcessNode );
        assertFalse( startProcessNode == copyStartProcessNode );

        final BpmnGraphNode copyEndProcessNode = getNode( copyProcess,
                                                          EndProcessNode.class );
        assertNotNull( copyEndProcessNode );
        assertTrue( copyEndProcessNode instanceof EndProcessNode );
        assertFalse( endProcessNode == copyEndProcessNode );
    }

    private BpmnGraphNode getNode( final BpmnGraph process,
                                   final Class clazz ) {
        for ( BpmnGraphNode node : process ) {
            if ( node.getClass().equals( clazz ) ) {
                return node;
            }
        }
        return null;
    }

}

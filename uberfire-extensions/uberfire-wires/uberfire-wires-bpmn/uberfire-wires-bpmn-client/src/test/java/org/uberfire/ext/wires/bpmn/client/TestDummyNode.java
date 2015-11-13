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
package org.uberfire.ext.wires.bpmn.client;

import java.util.Collections;
import java.util.HashSet;

import org.uberfire.ext.wires.bpmn.api.model.BpmnEdge;
import org.uberfire.ext.wires.bpmn.api.model.BpmnGraphNode;
import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.Role;
import org.uberfire.ext.wires.bpmn.api.model.impl.content.DefaultContentImpl;
import org.uberfire.ext.wires.bpmn.api.model.impl.roles.DefaultRoleImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.impl.GraphNodeImpl;

/**
 * A TestDummyNode that cannot be added to a Process
 */
public class TestDummyNode extends GraphNodeImpl<Content, BpmnEdge> implements BpmnGraphNode {

    public TestDummyNode() {
        setContent( new DefaultContentImpl( "dummy",
                                            "dummy",
                                            "dummy",
                                            new HashSet<Role>() {{
                                                add( new DefaultRoleImpl( "dummy" ) );
                                            }},
                                            Collections.EMPTY_SET ) );
    }

    @Override
    public TestDummyNode copy() {
        final TestDummyNode copy = new TestDummyNode();
        copy.setContent( this.getContent().copy() );
        return copy;
    }

}

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
package org.uberfire.ext.wires.bpmn.client;

import java.util.Collections;
import java.util.List;

import org.uberfire.ext.wires.bpmn.api.model.Content;
import org.uberfire.ext.wires.bpmn.api.model.impl.content.DefaultContentImpl;
import org.uberfire.ext.wires.bpmn.beliefs.graph.Edge;
import org.uberfire.ext.wires.bpmn.beliefs.graph.GraphNode;

/**
 * A TestDummyNode that cannot be added to a Process
 */
public class TestDummyNode implements GraphNode<Content> {

    private final Content content = new DefaultContentImpl( "dummy",
                                                            "dummy",
                                                            "dummy",
                                                            Collections.EMPTY_SET,
                                                            Collections.EMPTY_SET );

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId( int id ) {
        //Stub
    }

    @Override
    public List<Edge> getInEdges() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Edge> getOutEdges() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public void setContent( Content content ) {
        //Stub
    }
}

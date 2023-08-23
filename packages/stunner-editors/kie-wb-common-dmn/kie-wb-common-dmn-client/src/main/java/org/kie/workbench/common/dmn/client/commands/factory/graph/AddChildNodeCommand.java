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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

@Portable
public class AddChildNodeCommand extends org.kie.workbench.common.stunner.core.graph.command.impl.AddChildNodeCommand {

    public AddChildNodeCommand(final @MapsTo("parentUUID") String parentUUID,
                               final @MapsTo("candidate") Node candidate,
                               final @MapsTo("location") Point2D location) {
        super(parentUUID,
              candidate,
              location);
    }

    public AddChildNodeCommand(final Node<?, Edge> parent,
                               final Node candidate) {
        super(parent,
              candidate);
    }

    @Override
    protected RegisterNodeCommand getRegisterNodeCommand(Node candidate) {
        return new RegisterNodeCommand(candidate);
    }

    @Override
    protected SetChildrenCommand getSetChildNodeCommand(final Node<?, Edge> parent,
                                                        final Node candidate) {
        return new SetChildrenCommand(parent,
                                      candidate);
    }
}

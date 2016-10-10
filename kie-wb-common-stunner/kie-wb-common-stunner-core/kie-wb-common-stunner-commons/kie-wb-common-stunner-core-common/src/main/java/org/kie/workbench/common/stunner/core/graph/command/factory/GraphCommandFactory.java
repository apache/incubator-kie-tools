/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.factory;

import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.impl.*;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public interface GraphCommandFactory {

    AddNodeCommand ADD_NODE( Graph target,
                             Node candidate );

    AddChildNodeCommand ADD_CHILD_NODE( Graph target,
                                        Node parent,
                                        Node candidate );

    AddDockedNodeCommand ADD_DOCKED_NODE( Graph target,
                                          Node parent,
                                          Node candidate );

    AddEdgeCommand ADD_EDGE( Node target,
                             Edge edge );

    SafeDeleteNodeCommand SAFE_DELETE_NODE( Graph target,
                                            Node candidate );

    DeleteNodeCommand DELETE_NODE( Graph target,
                                   Node candidate );

    DeleteEdgeCommand DELETE_EDGE( Edge<? extends View,
            Node> edge );

    ClearGraphCommand CLEAR_GRAPH( Graph target );

    ClearGraphCommand CLEAR_GRAPH( Graph target,
                                   String rootUUID );

    AddChildEdgeCommand ADD_CHILD_EDGE( Node parent,
                                        Node candidate );

    AddParentEdgeCommand ADD_PARENT_EDGE( Node parent,
                                          Node candidate );

    DeleteChildEdgeCommand DELETE_CHILD_EDGE( Node parent,
                                              Node candidate );

    DeleteParentEdgeCommand DELETE_PARENT_EDGE( Node parent,
                                                Node candidate );

    AddDockEdgeCommand ADD_DOCK_EDGE( Node parent,
                                      Node candidate );

    DeleteDockEdgeCommand DELETE_DOCK_EDGE( Node parent,
                                            Node candidate );

    SetConnectionSourceNodeCommand SET_SOURCE_NODE( Node<? extends View<?>, Edge> sourceNode,
                                                    Edge<? extends View<?>, Node> edge,
                                                    int magnetIndex );

    SetConnectionTargetNodeCommand SET_TARGET_NODE( Node<? extends View<?>, Edge> targetNode,
                                                    Edge<? extends View<?>, Node> edge,
                                                    int magnetIndex );

    UpdateElementPositionCommand UPDATE_POSITION( Element element,
                                                  Double x,
                                                  Double y );

    UpdateElementPropertyValueCommand UPDATE_PROPERTY_VALUE( Element element,
                                                             String propertyId,
                                                             Object value );

    MorphNodeCommand MORPH_NODE( Node<Definition, Edge> candidate,
                                 MorphDefinition morphDefinition,
                                 String morphTarget );

}

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


package org.kie.workbench.common.stunner.core.graph.content.view;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;

/**
 * Indicates that the view for the node/edge represents a physical connector that will the drawn in the canvas.
 * @param <W> The Definition of the connector's graphical view representation.
 */
public interface ViewConnector<W> extends View<W>,
                                          HasControlPoints {

    Optional<Connection> getSourceConnection();

    Optional<Connection> getTargetConnection();

    void setSourceConnection(final Connection connection);

    void setTargetConnection(final Connection connection);
}

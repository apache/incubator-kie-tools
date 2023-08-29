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


package org.kie.workbench.common.stunner.core.registry.diagram;

import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;

/**
 * Base registry type for Diagrams.
 * @param <D> The type of the Diagram.
 */
public interface DiagramRegistry<D extends Diagram> extends DynamicRegistry<D> {

    /**
     * Returns the Diagram of type <code>D</code> that matches the UUID.
     * @param uuid The uuid for Diagram.
     */
    D getDiagramByUUID(final String uuid);

    /**
     * Updates the diagram on this registry.
     */
    void update(final D diagram);
}

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


package org.kie.workbench.common.stunner.core.rule.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;

@Portable
public final class EdgeOccurrences extends AbstractOccurrences {

    private final String connectorRole;
    private final EdgeCardinalityContext.Direction direction;

    public EdgeOccurrences(final @MapsTo("name") String name,
                           final @MapsTo("connectorRole") String connectorRole,
                           final @MapsTo("role") String role,
                           final @MapsTo("direction") EdgeCardinalityContext.Direction direction,
                           final @MapsTo("minOccurrences") int minOccurrences,
                           final @MapsTo("maxOccurrences") int maxOccurrences) {
        super(name,
              role,
              minOccurrences,
              maxOccurrences);
        this.connectorRole = connectorRole;
        this.direction = direction;
    }

    public String getConnectorRole() {
        return connectorRole;
    }

    public EdgeCardinalityContext.Direction getDirection() {
        return direction;
    }
}

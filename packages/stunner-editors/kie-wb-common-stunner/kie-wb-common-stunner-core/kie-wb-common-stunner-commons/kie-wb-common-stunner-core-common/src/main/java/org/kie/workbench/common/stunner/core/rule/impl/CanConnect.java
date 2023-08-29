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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public final class CanConnect extends AbstractRule {

    /**
     * Permitted connections
     */
    @Portable
    public static class PermittedConnection {

        private final String startRole;
        private final String endRole;

        public PermittedConnection(final @MapsTo("startRole") String startRole,
                                   final @MapsTo("endRole") String endRole) {
            this.startRole = startRole;
            this.endRole = endRole;
        }

        /**
         * Role of the start Element that can accept this Connection
         * @return
         */
        public String getStartRole() {
            return startRole;
        }

        /**
         * Role of then end Element that can accept this Connection
         * @return
         */
        public String getEndRole() {
            return endRole;
        }
    }

    private final String role;
    private final List<PermittedConnection> permittedConnections;

    public CanConnect(final @MapsTo("name") String name,
                      final @MapsTo("role") String role,
                      final @MapsTo("permittedConnections") List<PermittedConnection> permittedConnections) {
        super(name);
        this.role = role;
        this.permittedConnections = permittedConnections;
    }

    public String getRole() {
        return role;
    }

    public List<CanConnect.PermittedConnection> getPermittedConnections() {
        return permittedConnections;
    }
}

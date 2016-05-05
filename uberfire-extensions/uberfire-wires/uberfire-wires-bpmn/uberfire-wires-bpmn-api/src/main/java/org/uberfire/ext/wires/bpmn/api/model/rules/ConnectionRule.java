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
package org.uberfire.ext.wires.bpmn.api.model.rules;

import java.util.Set;

import org.uberfire.ext.wires.bpmn.api.model.Role;

/**
 * Rule to restrict how Elements can be connected. Connections can be restricted to Elements with certain Roles.
 */
public interface ConnectionRule extends RuleByRole {

    /**
     * The Connections that are permitted by the Rule. The source and target of the connection must have the Role defined in the PermittedConnection
     * @return
     */
    Set<PermittedConnection> getPermittedConnections();

    /**
     * Permitted connections
     */
    public static interface PermittedConnection {

        /**
         * Role of the start Element that can accept this Connection
         * @return
         */
        Role getStartRole();

        /**
         * Role of then end Element that can accept this Connection
         * @return
         */
        Role getEndRole();
    }

}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api;

import org.jboss.errai.security.shared.api.Group;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;

import java.util.Collection;


/**
 * <p>The Groups Manager.</p>
 * 
 * <p>NOTE: Some user management security systems do not provide support for roles and groups, only one of them. 
 *  Consider that UF roles are the ones defined in the deployment descriptor (web.xml) and present in the <code>org.uberfire.ext.security.server.RolesRegistry</code>. 
 *  So take care when searching or getting a group, if it's name is registered as a role, it must NOT be considered for management here.
 *  This behavior has to be done by each security management provider implementation, depending on the external security system being used.</p>
 * 
 * @since 0.8.0
 */
public interface GroupManager extends AbstractEntityManager<Group, GroupManagerSettings> {

    /**
     * <p>Assign the a group to a given collection of users.</p>.
     * @param name The group name.
     * @param users The collection of user identifiers. The group will be assigned to each one.
     * @throws SecurityManagementException
     */
    void assignUsers(String name, Collection<String> users) throws SecurityManagementException;

}

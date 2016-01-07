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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * <p>Supported features for the User System Management services.</p>
 * <p>The backend and UI features for users management available are based on these capability statuses. 
 * If a capability is not supported by the current service provider implementation class, it will be not available from the user interface neither the backend method calls.</p>
 * 
 * @since 0.8.0
 */
@Portable
public enum Capability {
    
    // USERS features.
    CAN_SEARCH_USERS, CAN_ADD_USER, CAN_UPDATE_USER, CAN_READ_USER, CAN_DELETE_USER, 
    CAN_MANAGE_ATTRIBUTES, CAN_ASSIGN_GROUPS, CAN_ASSIGN_ROLES, CAN_CHANGE_PASSWORD,
    
    // GROUPS features.
    CAN_SEARCH_GROUPS, CAN_ADD_GROUP, CAN_UPDATE_GROUP, CAN_READ_GROUP, CAN_DELETE_GROUP,
    
    // ROLES features.
    CAN_SEARCH_ROLES, CAN_ADD_ROLE, CAN_UPDATE_ROLE, CAN_READ_ROLE, CAN_DELETE_ROLE;

    Capability() {
    }

}

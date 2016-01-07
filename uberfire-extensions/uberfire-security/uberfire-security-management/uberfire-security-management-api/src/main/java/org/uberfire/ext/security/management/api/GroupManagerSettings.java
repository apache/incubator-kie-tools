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

import java.util.Collection;

/**
 * <p>The settings for a groups entity manager.</p>
 *
 * @since 0.8.0
 */
public interface GroupManagerSettings extends Settings {

    /**
     * <p>Specify if the provider allows groups with no users assigned.</p>
     * <p>Usually realm based on property files, such as the default ones for Wildfly or EAP, does not allow empty users 
     * as the username is the key for the property entry.</p>
     * @return Allows groups with any user assigned.
     */
    boolean allowEmpty();

    /**
     * Return constrained groups for management 
     */
    Collection<String> getConstrainedGroups();

    /**
     * Sets constrained groups for management (usually the registered roles).
     */
    void setConstrainedGroups(Collection<String> constrainedGroups);
    
}

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

package org.uberfire.ext.security.management.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.GroupManagerSettings;

import java.util.Collection;
import java.util.Map;

/**
 * <p>Default settings for a group manager.</p>
 * 
 * @since 0.8.0
 */
@Portable
public class GroupManagerSettingsImpl extends SettingsImpl implements GroupManagerSettings {

    final boolean allowEmpty;
    Collection<String> constrainedGroups;

    public GroupManagerSettingsImpl(@MapsTo("capabilities") Map<Capability, CapabilityStatus> capabilities,
                                    @MapsTo("allowEmpty") boolean allowEmpty) {
        super(capabilities);
        this.allowEmpty = allowEmpty;
    }


    @Override
    public boolean allowEmpty() {
        return allowEmpty;
    }

    @Override
    public Collection<String> getConstrainedGroups() {
        return constrainedGroups;
    }

    @Override
    public void setConstrainedGroups(final Collection<String> constrainedGroups) {
        this.constrainedGroups = constrainedGroups;
    }
}

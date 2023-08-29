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


package org.kie.workbench.common.stunner.core.profile;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.ProfileManager;

public abstract class AbstractProfileManager implements ProfileManager {

    protected abstract Function<String, Annotation> getQualifier();

    protected abstract Iterable<Profile> getAllProfileInstances();

    protected abstract Iterable<Profile> selectProfileInstances(Annotation... qualifiers);

    @Override
    public Collection<Profile> getAllProfiles() {
        final List<Profile> result = new ArrayList<>();
        getAllProfileInstances().forEach(result::add);
        return result;
    }

    @Override
    public Profile getProfile(final String id) {
        return null != id ?
                getAllProfiles().stream().filter(profile -> profile.getProfileId().equals(id)).findFirst().orElse(null) :
                null;
    }

    @Override
    public Collection<Profile> getProfiles(final String definitionSetId) {
        final Annotation qualifier = getQualifier().apply(definitionSetId);
        final List<Profile> result = new ArrayList<>();
        selectProfileInstances(qualifier).forEach(result::add);
        result.add(getDefaultProfileInstance());
        return result;
    }

    @Override
    public Profile getProfile(final String definitionSetId,
                              final String id) {
        final Annotation qualifier = getQualifier().apply(definitionSetId);
        Profile profile = getProfile(id);
        if (null == profile) {
            profile = getDefaultDomainProfile(qualifier);
        }
        return profile;
    }

    private Profile getDefaultDomainProfile(final Annotation qualifier) {
        final Iterator<Profile> domainDefaultProfile =
                selectProfileInstances(qualifier, DefinitionManager.DEFAULT_QUALIFIER)
                        .iterator();
        if (domainDefaultProfile.hasNext()) {
            return domainDefaultProfile.next();
        }
        return getDefaultProfileInstance();
    }

    private Profile getDefaultProfileInstance() {
        return selectProfileInstances(DefinitionManager.DEFAULT_QUALIFIER).iterator().next();
    }
}

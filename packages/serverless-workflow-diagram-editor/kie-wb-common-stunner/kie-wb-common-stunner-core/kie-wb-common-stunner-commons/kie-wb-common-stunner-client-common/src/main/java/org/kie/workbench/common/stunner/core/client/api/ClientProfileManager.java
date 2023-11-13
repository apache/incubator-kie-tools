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


package org.kie.workbench.common.stunner.core.client.api;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import io.crysknife.client.ManagedInstance;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.profile.AbstractProfileManager;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class ClientProfileManager extends AbstractProfileManager {

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<Profile> profileInstances;

    @Inject
    public ClientProfileManager(final DefinitionUtils definitionUtils,
                                final @Any ManagedInstance<Profile> profileInstances) {
        this.definitionUtils = definitionUtils;
        this.profileInstances = profileInstances;
    }

    @PreDestroy
    public void destroy() {
        profileInstances.destroyAll();
    }

    @Override
    protected Function<String, Annotation> getQualifier() {
        return definitionUtils::getQualifier;
    }

    @Override
    protected Iterable<Profile> getAllProfileInstances() {
        return profileInstances;
    }

    @Override
    protected Iterable<Profile> selectProfileInstances(final Annotation... qualifiers) {
        return profileInstances.select(qualifiers);
    }
}

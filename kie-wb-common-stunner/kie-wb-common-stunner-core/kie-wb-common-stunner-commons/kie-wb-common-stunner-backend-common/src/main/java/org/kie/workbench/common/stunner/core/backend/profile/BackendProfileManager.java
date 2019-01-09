/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.profile;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.profile.AbstractProfileManager;
import org.kie.workbench.common.stunner.core.profile.Profile;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class BackendProfileManager extends AbstractProfileManager {

    private final DefinitionUtils definitionUtils;
    private final Instance<Profile> profileInstances;

    @Inject
    public BackendProfileManager(final DefinitionUtils definitionUtils,
                                 final @Any Instance<Profile> profileInstances) {
        this.definitionUtils = definitionUtils;
        this.profileInstances = profileInstances;
    }

    @PreDestroy
    public void destroy() {
        profileInstances.forEach(profileInstances::destroy);
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

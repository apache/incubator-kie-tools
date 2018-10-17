/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.definition.clone;

import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.soup.commons.util.Maps;

@ApplicationScoped
public class CloneManagerImpl implements CloneManager {

    private final Map<ClonePolicy, CloneProcess> cloneProcessMap;

    public CloneManagerImpl() {
        this(null, null, null);
    }

    @Inject
    public CloneManagerImpl(DeepCloneProcess deepCloneProcess, DefaultCloneProcess defaultCloneProcess, NoneCloneProcess noneCloneProcess) {
        this.cloneProcessMap = new Maps.Builder<ClonePolicy, CloneProcess>()
                .put(ClonePolicy.ALL, deepCloneProcess)
                .put(ClonePolicy.DEFAULT, defaultCloneProcess)
                .put(ClonePolicy.NONE, noneCloneProcess)
                .build();
    }

    private CloneProcess cloneProcess(ClonePolicy clonePolicy) {
        Objects.requireNonNull(clonePolicy, "ClonePolicy is required");
        return cloneProcessMap.get(clonePolicy);
    }

    @Override
    public <T> T clone(T source, ClonePolicy policy) {
        return cloneProcess(policy).clone(source);
    }

    @Override
    public <S, T> T clone(S source, T target, ClonePolicy policy) {
        return cloneProcess(policy).clone(source, target);
    }
}
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


package org.kie.workbench.common.stunner.core.definition.clone;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CloneManagerImpl implements CloneManager {

    private final Map<ClonePolicy, CloneProcess> cloneProcessMap;

    public CloneManagerImpl() {
        this(null, null, null);
    }

    @Inject
    public CloneManagerImpl(IDeepCloneProcess deepCloneProcess, DefaultCloneProcess defaultCloneProcess, NoneCloneProcess noneCloneProcess) {
        this.cloneProcessMap = Stream.of(new AbstractMap.SimpleEntry<>(ClonePolicy.ALL, deepCloneProcess),
                                         new AbstractMap.SimpleEntry<>(ClonePolicy.DEFAULT, defaultCloneProcess),
                                         new AbstractMap.SimpleEntry<>(ClonePolicy.NONE, noneCloneProcess))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

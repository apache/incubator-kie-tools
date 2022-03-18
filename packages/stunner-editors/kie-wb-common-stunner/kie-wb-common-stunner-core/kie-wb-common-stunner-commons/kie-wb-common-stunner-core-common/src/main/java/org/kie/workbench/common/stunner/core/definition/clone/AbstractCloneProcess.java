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

import java.util.Objects;

import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;

public abstract class AbstractCloneProcess implements CloneProcess {

    private final FactoryManager factoryManager;
    final AdapterManager adapterManager;

    public AbstractCloneProcess(FactoryManager factoryManager, AdapterManager adapterManager) {
        this.factoryManager = factoryManager;
        this.adapterManager = adapterManager;
    }

    @Override
    public <T> T clone(T source) {
        return clone(source, createEmptyClone(source));
    }

    @SuppressWarnings("unchecked")
    private <T> T createEmptyClone(T source) {
        Objects.requireNonNull(source, "Source cannot be null");
        return (T) factoryManager.newDefinition(adapterManager.forDefinition().getId(source).value());
    }
}

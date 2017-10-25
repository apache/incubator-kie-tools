/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.services.rest.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;

public class RuntimeFactory {

    private final Collection<RuntimeBuilder> builders = new ArrayList<>();

    private final Collection<RuntimeDestroyer> destroyers = new ArrayList<>();

    public RuntimeFactory() {
    }

    @Inject
    public RuntimeFactory(final Instance<RuntimeBuilder<?, ?>> builders,
                          final Instance<RuntimeDestroyer> destroyers) {
        builders.forEach(this.builders::add);
        destroyers.forEach(this.destroyers::add);
    }

    public Optional<Runtime> newRuntime(RuntimeConfig config) {
        return builders.stream()
                .filter(rb -> rb.supports(config))
                .findFirst().flatMap(rb -> {
                    return (Optional<Runtime>) rb.apply(config);
                });
    }

    public void destroyRuntime(RuntimeId runtimeId) {
        destroyers.stream()
                .filter(rd -> rd.supports(runtimeId))
                .findFirst().get().destroy(runtimeId);
    }
}

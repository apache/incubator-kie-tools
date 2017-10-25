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
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.RuntimeManager;

public class RuntimeManagerFactory {

    private final Collection<RuntimeManager> managers = new ArrayList<>();

    public RuntimeManagerFactory() {
    }

    @Inject
    public RuntimeManagerFactory(final Instance<RuntimeManager> managers) {
        managers.forEach(this.managers::add);
    }

    public void startRuntime(RuntimeId runtimeId) {
        managers.stream()
                .filter(p -> p.supports(runtimeId))
                .findFirst().get().start(runtimeId);
    }

    public void stopRuntime(RuntimeId runtimeId) {
        managers.stream()
                .filter(p -> p.supports(runtimeId))
                .findFirst().get().stop(runtimeId);
    }

    public void restartRuntime(RuntimeId runtimeId) {
        managers.stream()
                .filter(p -> p.supports(runtimeId))
                .findFirst().get().restart(runtimeId);
    }

    public void refreshRuntime(RuntimeId runtimeId) {
        managers.stream()
                .filter(p -> p.supports(runtimeId))
                .findFirst().get().refresh(runtimeId);
    }
}

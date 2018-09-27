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
package org.kie.workbench.common.services.verifier.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class VerifierWebWorkerRegistry {

    private Map<String, VerifierWebWorkerProvider> map = new HashMap<>();

    public VerifierWebWorkerRegistry() {

    }

    @Inject
    public VerifierWebWorkerRegistry(@Any final Instance<VerifierWebWorkerProvider> verifierWebWorkerProviders) {
        for (final VerifierWebWorkerProvider verifierWebWorkerProvider : verifierWebWorkerProviders) {
            map.put(verifierWebWorkerProvider.getId(), verifierWebWorkerProvider);
        }
    }

    public Optional<VerifierWebWorkerProvider> get(final String id) {
        if (map.containsKey(id)) {
            return Optional.of(map.get(id));
        } else {
            return Optional.empty();
        }
    }
}

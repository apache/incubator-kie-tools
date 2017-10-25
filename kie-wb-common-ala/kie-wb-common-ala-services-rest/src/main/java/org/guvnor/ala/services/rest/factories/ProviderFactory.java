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

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;

public class ProviderFactory {

    private final Collection<ProviderBuilder> builders = new ArrayList<>();

    public ProviderFactory() {
    }

    @Inject
    public ProviderFactory(final Instance<ProviderBuilder<?, ?>> builders) {
        builders.forEach(this.builders::add);
    }

    public Optional<Provider> newProvider(ProviderConfig config) {
        Optional<Provider> provider = builders.stream()
                .filter(pb -> pb.supports(config))
                .findFirst().flatMap(pb -> {
                    return (Optional<Provider>) pb.apply(config);
                });
        return provider;
    }
}

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

package org.guvnor.ala.docker.executor;

import java.util.Optional;
import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerProviderImpl;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderDestroyer;
import org.guvnor.ala.runtime.providers.ProviderId;

public class DockerProviderConfigExecutor
        implements ProviderBuilder<DockerProviderConfig, DockerProvider>,
                   ProviderDestroyer,
                   FunctionConfigExecutor<DockerProviderConfig, DockerProvider> {

    private RuntimeRegistry runtimeRegistry;

    @Inject
    public DockerProviderConfigExecutor(final RuntimeRegistry runtimeRegistry) {
        this.runtimeRegistry = runtimeRegistry;
    }

    @Override
    public Optional<DockerProvider> apply(final DockerProviderConfig dockerProviderConfig) {
        final DockerProviderImpl provider = new DockerProviderImpl(
                new DockerProviderConfigImpl(dockerProviderConfig.getName(),
                                             dockerProviderConfig.getHostIp()));
        runtimeRegistry.registerProvider(provider);
        return Optional.of(provider);
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerProviderConfig.class;
    }

    @Override
    public String outputId() {
        return "docker-provider";
    }

    @Override
    public boolean supports(final ProviderConfig config) {
        return config instanceof DockerProviderConfig;
    }

    @Override
    public boolean supports(final ProviderId providerId) {
        return providerId instanceof DockerProvider;
    }

    @Override
    public void destroy(final ProviderId providerId) {
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.wildfly.executor;

import java.util.Optional;
import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderDestroyer;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.config.impl.WildflyProviderConfigImpl;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.guvnor.ala.wildfly.model.WildflyProviderImpl;

public class WildflyProviderConfigExecutor
        implements ProviderBuilder<WildflyProviderConfig, WildflyProvider>,
                   ProviderDestroyer,
                   FunctionConfigExecutor<WildflyProviderConfig, WildflyProvider> {

    private RuntimeRegistry runtimeRegistry;

    @Inject
    public WildflyProviderConfigExecutor(final RuntimeRegistry runtimeRegistry) {
        this.runtimeRegistry = runtimeRegistry;
    }

    @Override
    public Optional<WildflyProvider> apply(final WildflyProviderConfig wildflyProviderConfig) {
        if (wildflyProviderConfig.getName() == null || wildflyProviderConfig.getName().isEmpty()) {
            throw new ProvisioningException("No name was provided for the WildflyProviderConfig.getName() " +
                                                    "configuration parameter. You might probably have to properly set " +
                                                    "the pipeline input parameter: " + ProviderConfig.PROVIDER_NAME);
        }
        Provider provider = runtimeRegistry.getProvider(wildflyProviderConfig.getName());
        WildflyProvider wildflyProvider;
        if (provider != null) {
            if (!(provider instanceof WildflyProvider)) {
                throw new ProvisioningException("The provider: " + wildflyProviderConfig.getName() +
                                                        " must be an instance of " + WildflyProviderConfig.class +
                                                        " but is: " + provider.getClass());
            } else {
                wildflyProvider = (WildflyProvider) provider;
            }
        } else {
            wildflyProvider = new WildflyProviderImpl(new WildflyProviderConfigImpl(wildflyProviderConfig.getName(),
                                                                                    wildflyProviderConfig.getHost(),
                                                                                    wildflyProviderConfig.getPort(),
                                                                                    wildflyProviderConfig.getManagementPort(),
                                                                                    wildflyProviderConfig.getUser(),
                                                                                    wildflyProviderConfig.getPassword()));
            runtimeRegistry.registerProvider(wildflyProvider);
        }
        return Optional.of(wildflyProvider);
    }

    @Override
    public Class<? extends Config> executeFor() {
        return WildflyProviderConfig.class;
    }

    @Override
    public String outputId() {
        return "wildfly-provider";
    }

    @Override
    public boolean supports(final ProviderConfig config) {
        return config instanceof WildflyProviderConfig;
    }

    @Override
    public boolean supports(final ProviderId providerId) {
        return providerId instanceof WildflyProvider;
    }

    @Override
    public void destroy(final ProviderId providerId) {
    }
}
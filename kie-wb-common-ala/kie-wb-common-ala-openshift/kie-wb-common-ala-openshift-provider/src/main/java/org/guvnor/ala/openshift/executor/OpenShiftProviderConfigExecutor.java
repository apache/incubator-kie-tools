/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.executor;

import java.util.Optional;

import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.openshift.model.OpenShiftProvider;
import org.guvnor.ala.openshift.model.OpenShiftProviderImpl;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderDestroyer;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * Stores the openshift provider in the runtime registry.
 */
public class OpenShiftProviderConfigExecutor implements ProviderBuilder<OpenShiftProviderConfig, OpenShiftProvider>, ProviderDestroyer, FunctionConfigExecutor<OpenShiftProviderConfig, OpenShiftProvider> {

    private RuntimeRegistry runtimeRegistry;

    @Inject
    public OpenShiftProviderConfigExecutor(final RuntimeRegistry runtimeRegistry) {
        this.runtimeRegistry = runtimeRegistry;
    }

    @Override
    public Optional<OpenShiftProvider> apply(final OpenShiftProviderConfig openshiftProviderConfig) {
        if (openshiftProviderConfig.getName() == null || openshiftProviderConfig.getName().isEmpty()) {
            throw new ProvisioningException("No name was provided for the OpenShiftProviderConfig.getName() " +
                                                    "configuration parameter. You might probably have to properly set " +
                                                    "the pipeline input parameter: " + ProviderConfig.PROVIDER_NAME);
        }
        Provider provider = runtimeRegistry.getProvider(openshiftProviderConfig.getName());
        OpenShiftProvider openshiftProvider;
        if (provider != null) {
            if (!(provider instanceof OpenShiftProvider)) {
                throw new ProvisioningException("The provider: " + openshiftProviderConfig.getName() +
                                                        " must be an instance of " + OpenShiftProviderConfig.class +
                                                        " but is: " + provider.getClass());
            } else {
                openshiftProvider = (OpenShiftProvider) provider;
            }
        } else {
            openshiftProvider = new OpenShiftProviderImpl(openshiftProviderConfig.getName(), openshiftProviderConfig);
            runtimeRegistry.registerProvider(openshiftProvider);
        }
        return Optional.of(openshiftProvider);
    }

    @Override
    public Class<? extends Config> executeFor() {
        return OpenShiftProviderConfig.class;
    }

    @Override
    public String outputId() {
        return OpenShiftProvider.CONTEXT_KEY;
    }

    @Override
    public boolean supports(final ProviderConfig config) {
        return config instanceof OpenShiftProviderConfig;
    }

    @Override
    public boolean supports(final ProviderId providerId) {
        return providerId instanceof OpenShiftProvider;
    }

    @Override
    public void destroy(final ProviderId providerId) {
    }

}

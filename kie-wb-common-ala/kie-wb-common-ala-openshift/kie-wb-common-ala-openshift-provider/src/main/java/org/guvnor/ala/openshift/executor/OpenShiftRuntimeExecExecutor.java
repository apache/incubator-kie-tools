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
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.exceptions.RuntimeOperationException;
import org.guvnor.ala.openshift.access.OpenShiftAccessInterface;
import org.guvnor.ala.openshift.access.OpenShiftClient;
import org.guvnor.ala.openshift.access.exceptions.OpenShiftClientException;
import org.guvnor.ala.openshift.config.OpenShiftRuntimeConfig;
import org.guvnor.ala.openshift.model.OpenShiftProvider;
import org.guvnor.ala.openshift.model.OpenShiftRuntime;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeEndpoint;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeInfo;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeState;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.ala.util.RuntimeConfigHelper.buildRuntimeName;

/**
 * Responsible for the "create" and "destroy" lifecycle aspects of the openshift runtime.
 * @param <T> OpenShiftRuntimeConfig
 */
public class OpenShiftRuntimeExecExecutor<T extends OpenShiftRuntimeConfig> implements RuntimeBuilder<T, OpenShiftRuntime>,
                                                                                       RuntimeDestroyer,
                                                                                       FunctionConfigExecutor<T, OpenShiftRuntime> {

    private final RuntimeRegistry runtimeRegistry;
    private final OpenShiftAccessInterface openshift;
    private static final Logger LOG = LoggerFactory.getLogger(OpenShiftRuntimeExecExecutor.class);

    @Inject
    public OpenShiftRuntimeExecExecutor(final RuntimeRegistry runtimeRegistry,
                                        final OpenShiftAccessInterface openshift) {
        this.runtimeRegistry = runtimeRegistry;
        this.openshift = openshift;
    }

    @Override
    public Optional<OpenShiftRuntime> apply(final OpenShiftRuntimeConfig config) {
        final Optional<OpenShiftRuntime> runtime = create(config);
        if (runtime.isPresent()) {
            runtimeRegistry.registerRuntime(runtime.get());
        }
        return runtime;
    }

    private Optional<OpenShiftRuntime> create(final OpenShiftRuntimeConfig runtimeConfig) throws ProvisioningException {

        final Optional<OpenShiftProvider> _openshiftProvider = runtimeRegistry.getProvider(runtimeConfig.getProviderId(),
                                                                                           OpenShiftProvider.class);
        if (!_openshiftProvider.isPresent()) {
            return Optional.empty();
        }
        OpenShiftProvider openshiftProvider = _openshiftProvider.get();
        OpenShiftClient openshiftClient = openshift.getOpenShiftClient(openshiftProvider);

        LOG.info("Creating runtime...");
        OpenShiftRuntimeState runtimeState;
        try {
            runtimeState = openshiftClient.create(runtimeConfig);
        } catch (OpenShiftClientException ex) {
            throw new ProvisioningException(ex.getMessage(),
                                            ex);
        }

        final String id = runtimeConfig.getRuntimeId().toString();
        LOG.info("Created runtime: " + id);

        OpenShiftRuntimeEndpoint endpoint = openshiftClient.getRuntimeEndpoint(id);

        return Optional.of(new OpenShiftRuntime(id,
                                                buildRuntimeName(runtimeConfig,
                                                                 id),
                                                runtimeConfig,
                                                openshiftProvider,
                                                endpoint,
                                                new OpenShiftRuntimeInfo(runtimeConfig),
                                                runtimeState));
    }

    @Override
    public Class<? extends Config> executeFor() {
        return OpenShiftRuntimeConfig.class;
    }

    @Override
    public String outputId() {
        return OpenShiftRuntime.CONTEXT_KEY;
    }

    @Override
    public boolean supports(final RuntimeConfig config) {
        return config instanceof OpenShiftRuntimeConfig;
    }

    @Override
    public boolean supports(final RuntimeId runtimeId) {
        return runtimeId instanceof OpenShiftRuntime || runtimeRegistry.getRuntimeById(runtimeId.getId()) instanceof OpenShiftRuntime;
    }

    @Override
    public void destroy(final RuntimeId runtimeId) {
        final Optional<OpenShiftProvider> _openshiftProvider = runtimeRegistry.getProvider(runtimeId.getProviderId(),
                                                                                           OpenShiftProvider.class);
        if (!_openshiftProvider.isPresent()) {
            return;
        }
        OpenShiftProvider openshiftProvider = _openshiftProvider.get();
        try {
            LOG.info("Destroying runtime: " + runtimeId.getId());
            openshift.getOpenShiftClient(openshiftProvider).destroy(runtimeId.getId());
            LOG.info("Destroyed runtime: " + runtimeId.getId());
        } catch (OpenShiftClientException ex) {
            throw new RuntimeOperationException("Error Destroying runtime: " + runtimeId.getId(),
                                                ex);
        }
        runtimeRegistry.deregisterRuntime(runtimeId);
    }
}

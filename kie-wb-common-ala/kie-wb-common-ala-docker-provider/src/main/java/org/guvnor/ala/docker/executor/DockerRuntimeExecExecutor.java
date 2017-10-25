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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.DockerRuntimeExecConfig;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.model.DockerRuntimeEndpoint;
import org.guvnor.ala.docker.model.DockerRuntimeInfo;
import org.guvnor.ala.docker.model.DockerRuntimeState;
import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.util.RuntimeConfigHelper.buildRuntimeName;

public class DockerRuntimeExecExecutor<T extends DockerRuntimeConfig>
        implements RuntimeBuilder<T, DockerRuntime>,
                   RuntimeDestroyer,
                   FunctionConfigExecutor<T, DockerRuntime> {

    private final RuntimeRegistry runtimeRegistry;
    private final DockerAccessInterface docker;
    protected static final Logger LOG = LoggerFactory.getLogger(DockerRuntimeExecExecutor.class);

    @Inject
    public DockerRuntimeExecExecutor(final RuntimeRegistry runtimeRegistry,
                                     final DockerAccessInterface docker) {
        this.runtimeRegistry = runtimeRegistry;
        this.docker = docker;
    }

    @Override
    public Optional<DockerRuntime> apply(final DockerRuntimeConfig config) {
        final Optional<DockerRuntime> runtime = create(config);
        runtime.ifPresent(runtimeRegistry::registerRuntime);
        return runtime;
    }

    private Optional<DockerRuntime> create(final DockerRuntimeConfig runtimeConfig) throws ProvisioningException {
        if (runtimeConfig.isPull()) {
            try {
                LOG.info("Pulling Docker Image: " + runtimeConfig.getImage());
                docker.getDockerClient(runtimeConfig.getProviderId()).pull(runtimeConfig.getImage());
            } catch (DockerException | InterruptedException ex) {
                LOG.error(ex.getMessage(),
                          ex);
                throw new ProvisioningException("Error Pulling Docker Image: " + runtimeConfig.getImage() + "with error: " + ex.getMessage());
            }
        }

        final String[] ports = {runtimeConfig.getPort()};
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();

        final Optional<DockerProvider> _dockerProvider = runtimeRegistry.getProvider(runtimeConfig.getProviderId(),
                                                                                     DockerProvider.class);

        if (!_dockerProvider.isPresent()) {
            return Optional.empty();
        }
        final DockerProvider dockerProvider = _dockerProvider.get();
        final List<PortBinding> randomPort = new ArrayList<>();
        final PortBinding randomPortBinding = PortBinding.randomPort(dockerProvider.getConfig().getHostIp());

        randomPort.add(randomPortBinding);
        portBindings.put(runtimeConfig.getPort(),
                         randomPort);

        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();

        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(runtimeConfig.getImage())
                .exposedPorts(ports)
                .build();

        final ContainerCreation creation;
        try {
            creation = docker.getDockerClient(runtimeConfig.getProviderId()).createContainer(containerConfig);
            docker.getDockerClient(runtimeConfig.getProviderId()).startContainer(creation.id());
        } catch (DockerException | InterruptedException ex) {
            LOG.error(ex.getMessage(),
                      ex);
            throw new ProvisioningException("Error Creating Docker Container with image: " + runtimeConfig.getImage() + "with error: " + ex.getMessage(),
                                            ex);
        }

        final String id = creation.id();
        String shortId = id.substring(0,
                                      12);
        String host = "";
        try {
            docker.getDockerClient(runtimeConfig.getProviderId()).inspectContainer(id);
            host = docker.getDockerClient(runtimeConfig.getProviderId()).getHost();
        } catch (DockerException | InterruptedException ex) {
            throw new ProvisioningException("Error Getting Docker Container info: " + id + "with error: " + ex.getMessage(),
                                            ex);
        }
        DockerRuntimeEndpoint dockerRuntimeEndpoint = new DockerRuntimeEndpoint();
        dockerRuntimeEndpoint.setHost(host);
        dockerRuntimeEndpoint.setPort(Integer.valueOf(runtimeConfig.getPort()));
        dockerRuntimeEndpoint.setContext("");
        return Optional.of(new DockerRuntime(shortId,
                                             buildRuntimeName(runtimeConfig,
                                                              shortId),
                                             runtimeConfig,
                                             dockerProvider,
                                             dockerRuntimeEndpoint,
                                             new DockerRuntimeInfo(),
                                             new DockerRuntimeState(RUNNING,
                                                                    new Date().toString())));
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerRuntimeExecConfig.class;
    }

    @Override
    public String outputId() {
        return "docker-runtime";
    }

    @Override
    public boolean supports(final RuntimeConfig config) {
        return config instanceof DockerRuntimeConfig;
    }

    @Override
    public boolean supports(final RuntimeId runtimeId) {
        return runtimeId instanceof DockerRuntime
                || runtimeRegistry.getRuntimeById(runtimeId.getId()) instanceof DockerRuntime;
    }

    @Override
    public void destroy(final RuntimeId runtimeId) {
        try {
            LOG.info("Killing Container: " + runtimeId.getId());
            docker.getDockerClient(runtimeId.getProviderId()).killContainer(runtimeId.getId());
            LOG.info("Removing Container: " + runtimeId.getId());
            docker.getDockerClient(runtimeId.getProviderId()).removeContainer(runtimeId.getId());
            runtimeRegistry.deregisterRuntime(runtimeId);
        } catch (DockerException | InterruptedException ex) {
            LOG.debug(ex.getMessage(),
                      ex);
            try {
                // Trying to remove the container if it cannot be killed
                LOG.info("Attempting to Remove Container without Killing: " + runtimeId.getId());
                docker.getDockerClient(runtimeId.getProviderId()).removeContainer(runtimeId.getId());
                runtimeRegistry.deregisterRuntime(runtimeId);
            } catch (DockerException | InterruptedException ex2) {
                LOG.error(ex.getMessage(),
                          ex2);
                throw new ProvisioningException("Error destroying Docker Runtime: " + ex.getMessage(),
                                                ex2);
            }
        }
    }
}

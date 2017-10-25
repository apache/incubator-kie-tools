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

package org.guvnor.ala.docker.service;

import javax.inject.Inject;

import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ContainerState;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.model.DockerRuntimeState;
import org.guvnor.ala.exceptions.RuntimeOperationException;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.STOPPED;

public class DockerRuntimeManager implements RuntimeManager {

    private final RuntimeRegistry runtimeRegistry;
    private final DockerAccessInterface docker;
    protected static final Logger LOG = LoggerFactory.getLogger(DockerRuntimeManager.class);

    @Inject
    public DockerRuntimeManager(final RuntimeRegistry runtimeRegistry,
                                final DockerAccessInterface docker) {
        this.runtimeRegistry = runtimeRegistry;
        this.docker = docker;
    }

    @Override
    public boolean supports(final RuntimeId runtimeId) {
        return runtimeId instanceof DockerRuntime
                || runtimeRegistry.getRuntimeById(runtimeId.getId()) instanceof DockerRuntime;
    }

    @Override
    public void start(RuntimeId runtimeId) throws RuntimeOperationException {
        DockerRuntime runtime = (DockerRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            LOG.info("Starting container: " + runtimeId.getId());
            docker.getDockerClient(runtime.getProviderId()).startContainer(runtime.getId());
            refresh(runtimeId);
        } catch (DockerException | InterruptedException ex) {
            LOG.error("Error Starting container: " + runtimeId.getId(),
                      ex);
            throw new RuntimeOperationException("Error Starting container: " + runtimeId.getId(),
                                                ex);
        }
    }

    @Override
    public void stop(RuntimeId runtimeId) throws RuntimeOperationException {
        DockerRuntime runtime = (DockerRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            LOG.info("Stopping container: " + runtimeId.getId());
            docker.getDockerClient(runtime.getProviderId()).stopContainer(runtime.getId(),
                                                                          1);
            refresh(runtimeId);
        } catch (DockerException | InterruptedException ex) {
            LOG.error("Error Stopping container: " + runtimeId.getId(),
                      ex);
            throw new RuntimeOperationException("Error Stopping container: " + runtimeId.getId(),
                                                ex);
        }
    }

    @Override
    public void restart(RuntimeId runtimeId) throws RuntimeOperationException {
        DockerRuntime runtime = (DockerRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            docker.getDockerClient(runtime.getProviderId()).restartContainer(runtime.getId());
            refresh(runtimeId);
        } catch (DockerException | InterruptedException ex) {
            LOG.error("Error Restarting container: " + runtimeId.getId(),
                      ex);
            throw new RuntimeOperationException("Error Restarting container: " + runtimeId.getId(),
                                                ex);
        }
    }

    @Override
    public void refresh(RuntimeId runtimeId) throws RuntimeOperationException {
        DockerRuntime runtime = (DockerRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            ContainerInfo containerInfo = docker.getDockerClient(runtime.getProviderId()).inspectContainer(runtime.getId());
            ContainerState state = containerInfo.state();
            String stateString = STOPPED;
            if (state.running() && !state.paused()) {
                stateString = RUNNING;
            } else if (state.paused()) {
                stateString = "Paused";
            } else if (state.restarting()) {
                stateString = "Restarting";
            } else if (state.oomKilled()) {
                stateString = "Killed";
            }

            DockerRuntime newRuntime = new DockerRuntime(runtime.getId(),
                                                         runtime.getName(),
                                                         runtime.getConfig(),
                                                         runtime.getProviderId(),
                                                         runtime.getEndpoint(),
                                                         runtime.getInfo(),
                                                         new DockerRuntimeState(stateString,
                                                                                state.startedAt().toString()));
            runtimeRegistry.registerRuntime(newRuntime);
        } catch (DockerException | InterruptedException ex) {
            LOG.error("Error Refreshing container: " + runtimeId.getId(),
                      ex);
            throw new RuntimeOperationException("Error Refreshing container: " + runtimeId.getId(),
                                                ex);
        }
    }

    @Override
    public void pause(RuntimeId runtimeId) throws RuntimeOperationException {
        DockerRuntime runtime = (DockerRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            docker.getDockerClient(runtime.getProviderId()).pauseContainer(runtime.getId());
            refresh(runtimeId);
        } catch (DockerException | InterruptedException ex) {
            LOG.error("Error Pausing container: " + runtimeId.getId(),
                      ex);
            throw new RuntimeOperationException("Error Pausing container: " + runtimeId.getId(),
                                                ex);
        }
    }
}

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
package org.guvnor.ala.docker.access.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Info;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.lifecycle.Disposable;

public class DockerAccessInterfaceImpl
        implements DockerAccessInterface,
                   Disposable {

    protected static final Logger LOG = LoggerFactory.getLogger(DockerAccessInterfaceImpl.class);
    private final Map<String, DockerClient> clientMap = new ConcurrentHashMap<>();

    @Override
    public DockerClient getDockerClient(final ProviderId providerId) throws DockerException, InterruptedException {
        if (!clientMap.containsKey(providerId.getId())) {
            clientMap.put(providerId.getId(),
                          buildClient(providerId));
        }
        return clientMap.get(providerId.getId());
    }

    private DockerClient buildClient(final ProviderId providerId) throws DockerException, InterruptedException {
        DefaultDockerClient dockerClient;
        if (providerId.getId().equals("local")) {
            try {
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    dockerClient = DefaultDockerClient.builder().uri(DefaultDockerClient.DEFAULT_UNIX_ENDPOINT).build();
                    // This test the docker client connection to see if the client was built properly
                    Info info = dockerClient.info();
                    LOG.info("Connected to Docker Client Info: " + info);
                    return dockerClient;
                }

                try {
                    dockerClient = DefaultDockerClient.fromEnv().build();
                    Info info = dockerClient.info();
                    LOG.info("Connected to Docker Client Info: " + info);
                    return dockerClient;
                } catch (DockerCertificateException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (DockerException | InterruptedException ex) {
                try {
                    dockerClient = DefaultDockerClient.fromEnv().build();
                    Info info = dockerClient.info();
                    LOG.info("Connected to Docker Client Info: " + info);
                    return dockerClient;
                } catch (DockerCertificateException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("Couldn't create Docker Client, for providerId = " + providerId.getId());
    }

    @Override
    public void dispose() {
        clientMap.values().forEach(DockerClient::close);
    }
}

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
package org.guvnor.ala.docker.access;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.uberfire.commons.lifecycle.Disposable;

/**
 * This interface abstracts the Docker client interactions
 */
public interface DockerAccessInterface extends Disposable {

    /*
     * Get the Docker Client for the provided ProviderId
     * @param ProviderId 
     * @return DockerClient for the ProviderId, if it doesn't exist it creates a new DockerClient
     * @see DockerClient
     */
    DockerClient getDockerClient(final ProviderId providerId) throws DockerException, InterruptedException;
}

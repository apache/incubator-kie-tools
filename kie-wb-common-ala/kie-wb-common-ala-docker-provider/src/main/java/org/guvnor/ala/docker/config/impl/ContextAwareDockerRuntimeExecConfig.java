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
package org.guvnor.ala.docker.config.impl;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.DockerRuntimeExecConfig;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.runtime.providers.ProviderId;

public class ContextAwareDockerRuntimeExecConfig implements
                                                 ContextAware,
                                                 DockerRuntimeExecConfig,
                                                 CloneableConfig<DockerRuntimeExecConfig> {

    @JsonIgnore
    private Map<String, ?> context;
    private ProviderId providerId;
    private String image;
    private String port;
    private boolean pull;

    public ContextAwareDockerRuntimeExecConfig() {
    }

    public ContextAwareDockerRuntimeExecConfig(final ProviderId providerId,
                                               final String image,
                                               final String port,
                                               final boolean pull) {
        this.providerId = providerId;
        this.image = image;
        this.port = port;
        this.pull = pull;
    }

    @Override
    @JsonIgnore
    public void setContext(final Map<String, ?> context) {
        this.context = context;
        final DockerRuntimeConfig dockerRuntimeConfiguration = (DockerRuntimeConfig) context.get("docker-runtime-config");
        this.providerId = dockerRuntimeConfiguration.getProviderId();
        this.image = dockerRuntimeConfiguration.getImage();
        this.port = dockerRuntimeConfiguration.getPort();
        this.pull = dockerRuntimeConfiguration.isPull();
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public boolean isPull() {
        return pull;
    }

    public void setProviderId(ProviderId providerId) {
        this.providerId = providerId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setPull(boolean pull) {
        this.pull = pull;
    }

    @Override
    public String toString() {
        return "ContextAwareDockerRuntimeExecConfig{" + "providerId=" + providerId + ", image=" + image + ", port=" + port + ", pull=" + pull + '}';
    }

    @Override
    public DockerRuntimeExecConfig asNewClone(final DockerRuntimeExecConfig source) {
        return new ContextAwareDockerRuntimeExecConfig(source.getProviderId(),
                                                       source.getImage(),
                                                       source.getPort(),
                                                       source.isPull());
    }
}
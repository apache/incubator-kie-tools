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

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.runtime.providers.ProviderId;

public class DockerRuntimeConfigImpl
        implements DockerRuntimeConfig,
                   CloneableConfig<DockerRuntimeConfig> {

    private String runtimeName;
    private String image;
    private String port;
    private boolean pull;
    private ProviderId providerId;

    public DockerRuntimeConfigImpl() {
        //No-args constructor for enabling marshalling to work, please do not remove.
    }

    public DockerRuntimeConfigImpl(final ProviderId providerId,
                                   final String image,
                                   final String port,
                                   final boolean pull) {
        this.providerId = providerId;
        this.image = image;
        this.port = port;
        this.pull = pull;
        this.runtimeName = image;
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

    @Override
    public ProviderId getProviderId() {
        return providerId;
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

    public void setProviderId(ProviderId providerId) {
        this.providerId = providerId;
    }

    @Override
    public String getRuntimeName() {
        return runtimeName;
    }

    @Override
    public String toString() {
        return "DockerRuntimeConfigImpl{" + "image=" + image + ", port=" + port + ", pull=" + pull + ", providerId=" + providerId + '}';
    }

    @Override
    public DockerRuntimeConfig asNewClone(final DockerRuntimeConfig source) {
        return new DockerRuntimeConfigImpl(source.getProviderId(),
                                           source.getImage(),
                                           source.getPort(),
                                           source.isPull());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DockerRuntimeConfigImpl that = (DockerRuntimeConfigImpl) o;

        if (pull != that.pull) {
            return false;
        }
        if (runtimeName != null ? !runtimeName.equals(that.runtimeName) : that.runtimeName != null) {
            return false;
        }
        if (image != null ? !image.equals(that.image) : that.image != null) {
            return false;
        }
        if (port != null ? !port.equals(that.port) : that.port != null) {
            return false;
        }
        return providerId != null ? providerId.equals(that.providerId) : that.providerId == null;
    }

    @Override
    public int hashCode() {
        int result = runtimeName != null ? runtimeName.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (pull ? 1 : 0);
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        return result;
    }
}
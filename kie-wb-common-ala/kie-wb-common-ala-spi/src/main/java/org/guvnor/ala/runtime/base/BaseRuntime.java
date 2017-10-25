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

package org.guvnor.ala.runtime.base;

import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.pipeline.execution.RegistrableOutput;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeEndpoint;
import org.guvnor.ala.runtime.RuntimeInfo;
import org.guvnor.ala.runtime.RuntimeState;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * BaseRuntime implementation to be extended by each Runtime Provider
 */
public abstract class BaseRuntime
        implements Runtime,
                   RegistrableOutput {

    private String id;
    private String name;
    private RuntimeConfig config;
    private ProviderId providerId;
    private RuntimeEndpoint endpoint;
    private RuntimeInfo info;
    private RuntimeState state;

    /**
     * No-args constructor for enabling marshalling to work, please do not remove.
     */
    public BaseRuntime() {
    }

    public BaseRuntime(final String id,
                       final String name,
                       final RuntimeConfig config,
                       final ProviderId providerId,
                       final RuntimeEndpoint endpoint,
                       final RuntimeInfo info,
                       final RuntimeState state) {
        this.id = id;
        this.name = name;
        this.config = config;
        this.providerId = providerId;
        this.endpoint = endpoint;
        this.info = info;
        this.state = state;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RuntimeConfig getConfig() {
        return config;
    }

    @Override
    public RuntimeInfo getInfo() {
        return info;
    }

    @Override
    public RuntimeState getState() {
        return state;
    }

    @Override
    public RuntimeEndpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    @Override
    public String toString() {
        return "BaseRuntime{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", config=" + config +
                ", providerId=" + providerId +
                ", endpoint=" + endpoint +
                ", info=" + info +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseRuntime that = (BaseRuntime) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (config != null ? !config.equals(that.config) : that.config != null) {
            return false;
        }
        if (providerId != null ? !providerId.equals(that.providerId) : that.providerId != null) {
            return false;
        }
        if (endpoint != null ? !endpoint.equals(that.endpoint) : that.endpoint != null) {
            return false;
        }
        if (info != null ? !info.equals(that.info) : that.info != null) {
            return false;
        }
        return state != null ? state.equals(that.state) : that.state == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (config != null ? config.hashCode() : 0);
        result = 31 * result + (providerId != null ? providerId.hashCode() : 0);
        result = 31 * result + (endpoint != null ? endpoint.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }
}

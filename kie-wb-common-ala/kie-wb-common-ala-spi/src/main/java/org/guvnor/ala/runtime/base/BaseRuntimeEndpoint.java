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

import java.util.Objects;

import org.guvnor.ala.runtime.RuntimeEndpoint;

/**
 * BaseRuntimeEndpoint implementation to be extended by each Runtime Provider
 */
public class BaseRuntimeEndpoint
        implements RuntimeEndpoint {

    private String protocol;
    private String host;
    private Integer port;
    private String context;

    /**
     * No-args constructor for enabling marshalling to work, please do not remove.
     */
    public BaseRuntimeEndpoint() {
    }

    public BaseRuntimeEndpoint(final String protocol,
                               final String host,
                               final Integer port,
                               final String context) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    @Override
    public String getContext() {
        return context;
    }

    public void setContext(final String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", context='" + context + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.protocol);
        hash = 79 * hash + Objects.hashCode(this.host);
        hash = 79 * hash + Objects.hashCode(this.port);
        hash = 79 * hash + Objects.hashCode(this.context);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BaseRuntimeEndpoint other = (BaseRuntimeEndpoint) obj;
        if (!Objects.equals(this.protocol,
                            other.protocol)) {
            return false;
        }
        if (!Objects.equals(this.host,
                            other.host)) {
            return false;
        }
        if (!Objects.equals(this.port,
                            other.port)) {
            return false;
        }
        if (!Objects.equals(this.context,
                            other.context)) {
            return false;
        }
        return true;
    }
}

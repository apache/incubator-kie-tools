/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.wildfly.config.impl;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;

public class WildflyProviderConfigImpl
        implements WildflyProviderConfig,
                   CloneableConfig<WildflyProviderConfig> {

    private String name;
    private String host;
    private String port;
    private String managementPort;
    private String user;
    private String password;

    public WildflyProviderConfigImpl() {
        this.name = WildflyProviderConfig.super.getName();
        this.host = WildflyProviderConfig.super.getHost();
        this.port = WildflyProviderConfig.super.getPort();
        this.managementPort = WildflyProviderConfig.super.getManagementPort();
        this.user = WildflyProviderConfig.super.getUser();
        this.password = WildflyProviderConfig.super.getPassword();
    }

    public WildflyProviderConfigImpl(final String name,
                                     final String host,
                                     final String port,
                                     final String managementPort,
                                     final String user,
                                     final String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.managementPort = managementPort;
        this.user = user;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public String getManagementPort() {
        return managementPort;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public WildflyProviderConfig asNewClone(final WildflyProviderConfig origin) {
        return new WildflyProviderConfigImpl(origin.getName(),
                                             origin.getHost(),
                                             origin.getPort(),
                                             origin.getManagementPort(),
                                             origin.getUser(),
                                             origin.getPassword());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WildflyProviderConfigImpl that = (WildflyProviderConfigImpl) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (host != null ? !host.equals(that.host) : that.host != null) {
            return false;
        }
        if (port != null ? !port.equals(that.port) : that.port != null) {
            return false;
        }
        if (managementPort != null ? !managementPort.equals(that.managementPort) : that.managementPort != null) {
            return false;
        }
        if (user != null ? !user.equals(that.user) : that.user != null) {
            return false;
        }
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (managementPort != null ? managementPort.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
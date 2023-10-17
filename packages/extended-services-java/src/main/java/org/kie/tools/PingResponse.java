/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.kie.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@JsonPropertyOrder({ "version", "proxy", "kieSandboxUrl", "started" })
public class PingResponse {

    private final String version;
    private final ProxyConfig proxyConfig;
    private final String kieSandboxUrl;
    private final boolean started;

    @Inject
    public PingResponse(@ConfigProperty(name = "extended.services.version") String version,
                        ProxyConfig proxyConfig,
                        @ConfigProperty(name = "kie.sandbox.url") String kieSandboxUrl) {
        this.version = version;
        this.proxyConfig = proxyConfig;
        this.kieSandboxUrl = kieSandboxUrl;
        this.started = true;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("proxy")
    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    @JsonProperty("kieSandboxUrl")
    public String getKieSandboxUrl() {
        return kieSandboxUrl;
    }

    @JsonProperty("started")
    public boolean getStarted() {
        return started;
    }
}

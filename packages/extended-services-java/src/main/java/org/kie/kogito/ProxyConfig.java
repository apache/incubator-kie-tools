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

package org.kie.kogito;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProxyConfig {

    private final String ip;
    private final String port;
    private final boolean insecure_skip_verify;

    public ProxyConfig(@ConfigProperty(name = "quarkus.http.host") String ip,
                       @ConfigProperty(name = "quarkus.http.port") String port) {
        this.ip = ip;
        this.port = port;
        this.insecure_skip_verify = false;
    }

    @JsonProperty("ip")
    public String getIP() {
        return ip;
    }

    @JsonProperty("port")
    public String getPort() {
        return port;
    }

    @JsonProperty("insecureSkipVerify")
    public boolean getInsecureSkipVerify() {
        return insecure_skip_verify;
    }
}

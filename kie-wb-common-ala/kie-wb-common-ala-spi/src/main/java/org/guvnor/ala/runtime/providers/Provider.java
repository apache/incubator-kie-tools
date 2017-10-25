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
package org.guvnor.ala.runtime.providers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.config.ProviderConfig;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS;

/**
 * A provider represents a running entity that allows the provisioning of runtimes.
 * Such as: Docker, Kubernetes, Application Servers (Wildfly, Tomcat, etc)
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Provider<C extends ProviderConfig> extends ProviderId {

    /*
     * Get the Provider Configuration that was used to Configure this provider instance
     * @return ProviderConfig with the Provider configuration
     * @see ProviderConfig
    */
    C getConfig();
}

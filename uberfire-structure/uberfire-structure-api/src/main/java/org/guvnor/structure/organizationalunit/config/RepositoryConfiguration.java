/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.guvnor.structure.organizationalunit.config;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepositoryConfiguration {

    private Map<String, Object> environment;

    public RepositoryConfiguration() {
        this.environment = new HashMap<>();
    }

    public RepositoryConfiguration(@MapsTo("environment") Map<String, Object> environment) {
        this.environment = environment;
    }

    public void add(String key,
                    Object value) {
        this.environment.put(key,
                             value);
    }

    public <T> T get(Class<T> clazz,
                     String key) {
        return (T) this.environment.get(key);
    }

    public <T> T get(Class<T> clazz,
                     String key,
                     T defaultValue) {
        return (T) this.environment.getOrDefault(key,
                                                 defaultValue);
    }

    public Map<String, Object> getEnvironment() {
        return this.environment;
    }
}

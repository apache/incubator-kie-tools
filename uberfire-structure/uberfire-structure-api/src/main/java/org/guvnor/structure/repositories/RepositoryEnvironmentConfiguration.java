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

package org.guvnor.structure.repositories;

import org.jboss.errai.common.client.api.annotations.Portable;

import static org.guvnor.structure.repositories.EnvironmentParameters.CRYPT_PREFIX;

@Portable
public class RepositoryEnvironmentConfiguration {

    private String name;
    private Object value;

    /**
     * Please use the constructor with parameters.
     * This constructor is here to keep the class Portable.
     */
    @Deprecated
    public RepositoryEnvironmentConfiguration() {
    }

    public RepositoryEnvironmentConfiguration(final String name,
                                              final Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isSecuredConfigurationItem() {
        return name.startsWith(CRYPT_PREFIX);
    }
}

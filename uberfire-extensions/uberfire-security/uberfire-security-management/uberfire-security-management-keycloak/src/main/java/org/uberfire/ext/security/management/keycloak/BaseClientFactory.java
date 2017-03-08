/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import org.uberfire.ext.security.management.keycloak.client.ClientFactory;
import org.uberfire.ext.security.management.keycloak.client.Keycloak;

/**
 * Base client factory that provides the client instance..
 * @since 0.9.0
 */
public abstract class BaseClientFactory implements ClientFactory {

    protected static final String DEFAULT_AUTH_SERVER = "http://localhost:8080/auth";
    protected Keycloak client;

    @Override
    public Keycloak get() {
        assert client != null;
        return client;
    }
}

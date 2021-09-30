/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.kieserver;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Kie Server Connection information
 *
 */
@Portable
public class KieServerConnectionInfo {

    private Optional<String> location;
    private Optional<String> user;
    private Optional<String> password;
    private Optional<String> token;
    private boolean replaceQuery;

    public KieServerConnectionInfo(@MapsTo("location") Optional<String> location,
                                   @MapsTo("user") Optional<String> user,
                                   @MapsTo("password") Optional<String> password,
                                   @MapsTo("token") Optional<String> token,
                                   @MapsTo("replaceQuery") boolean replaceQuery) {
        this.location = location;
        this.user = user;
        this.password = password;
        this.token = token;
        this.replaceQuery = replaceQuery;
    }

    public Optional<String> getLocation() {
        return location;
    }

    public Optional<String> getUser() {
        return user;
    }

    public Optional<String> getPassword() {
        return password;
    }

    public Optional<String> getToken() {
        return token;
    }
    
    public boolean isReplaceQuery() {
        return replaceQuery;
    }

}
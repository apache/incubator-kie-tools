/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 */

package org.uberfire.ssh.service.backend.auth.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;

@Portable
public class SSHUser implements User {

    private String name;
    private final Map<String, String> properties = new HashMap<String, String>();

    public SSHUser(String name) {
        this.name = name;
    }

    public SSHUser(@MapsTo("name") String name, @MapsTo("properties") Map<String, String> properties) {
        this.name = name;
        this.properties.putAll(properties);
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public Set<Role> getRoles() {
        return Collections.emptySet();
    }

    @Override
    public Set<Group> getGroups() {
        return Collections.emptySet();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    @Override
    public void removeProperty(String name) {
        properties.remove(name);
    }

    @Override
    public String getProperty(String name) {
        return properties.get(name);
    }
}

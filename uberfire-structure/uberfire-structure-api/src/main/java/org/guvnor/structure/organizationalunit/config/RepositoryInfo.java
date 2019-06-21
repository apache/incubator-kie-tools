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

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.repositories.EnvironmentParameters;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class RepositoryInfo {

    private static final String CONTRIBUTORS = "contributors";
    private static final String SECURITY_GROUPS = "security:groups";
    private String name;
    private boolean deleted;
    private RepositoryConfiguration configuration;

    public RepositoryInfo(@MapsTo("name") String name,
                          @MapsTo("deleted") boolean deleted,
                          @MapsTo("configuration") RepositoryConfiguration configuration) {

        this.name = name;
        this.deleted = deleted;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Contributor> getContributors() {
        return this.configuration.get(List.class,
                                      CONTRIBUTORS,
                                      new ArrayList<Contributor>());
    }

    public String getScheme() {
        return this.configuration.get(String.class,
                                      EnvironmentParameters.SCHEME,
                                      "");
    }

    public List<String> getSecurityGroups() {
        return this.configuration.get(List.class,
                                      SECURITY_GROUPS,
                                      new ArrayList());
    }

    public boolean isAvoidIndex() {
        return this.configuration.get(Boolean.class,
                                      EnvironmentParameters.AVOID_INDEX,
                                      false);
    }

    public String getSpace() {
        return this.configuration.get(String.class,
                                      EnvironmentParameters.SPACE,
                                      "");
    }

    public RepositoryConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RepositoryConfiguration configuration) {
        this.configuration = configuration;
    }
}

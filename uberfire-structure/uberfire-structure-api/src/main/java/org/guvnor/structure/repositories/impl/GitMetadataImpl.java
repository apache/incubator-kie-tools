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

package org.guvnor.structure.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.repositories.GitMetadata;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Represents information about a repository. It contains the origin name
 * the forks it has, the repository name.
 */
@Portable
public class GitMetadataImpl implements GitMetadata {

    private String origin;
    private List<String> forks;
    private String name;

    public GitMetadataImpl(String name) {
        this(name,
             "",
             new ArrayList<>());
    }

    public GitMetadataImpl(String name,
                           String origin) {
        this(name,
             origin,
             new ArrayList<>());
    }

    public GitMetadataImpl(String name,
                           List<String> forks) {
        this(name,
             "",
             forks);
    }

    public GitMetadataImpl(@MapsTo("name") String name,
                           @MapsTo("origin") String origin,
                           @MapsTo("forks") List<String> forks) {
        this.name = checkNotEmpty("name",
                                  name);
        this.origin = checkNotNull("origin",
                                   origin);
        this.forks = checkNotNull("forks",
                                  forks);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getForks() {
        return new ArrayList<>(forks);
    }

    @Override
    public String getOrigin() {
        return origin;
    }
}
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
 */

package org.kie.workbench.common.screens.archetype.mgmt.backend.config;

import java.net.URI;

import javax.inject.Inject;

import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.util.URIUtil;

public class ArchetypeConfigStorageImpl implements ArchetypeConfigStorage {

    public static final String ARCHETYPES_SPACE_NAME = ".archetypes";
    private static final String FILE_FORMAT = "json";

    private final ObjectStorage objectStorage;

    @Inject
    public ArchetypeConfigStorageImpl(final ObjectStorage objectStorage) {
        this.objectStorage = objectStorage;
    }

    @Override
    public void setup() {
        objectStorage.init(getRootURI());
    }

    @Override
    public Archetype loadArchetype(final String alias) {
        return objectStorage.read(buildArchetypePath(alias));
    }

    @Override
    public void saveArchetype(final Archetype archetype) {
        objectStorage.write(buildArchetypePath(archetype.getAlias()),
                            archetype);
    }

    @Override
    public void deleteArchetype(final String alias) {
        objectStorage.delete(buildArchetypePath(alias));
    }

    private URI getRootURI() {
        return URI.create(SpacesAPI.resolveConfigFileSystemPath(SpacesAPI.Scheme.DEFAULT,
                                                                ARCHETYPES_SPACE_NAME));
    }

    private String buildArchetypePath(final String alias) {
        return String.format("/config/%s.%s", encode(alias), FILE_FORMAT);
    }

    private String encode(final String text) {
        return URIUtil.encodeQueryString(text);
    }
}

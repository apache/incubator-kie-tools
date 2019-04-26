/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.organizationalunit.config;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;

@ApplicationScoped
public class SpaceConfigStorageRegistryImpl implements SpaceConfigStorageRegistry {

    private Instance<SpaceConfigStorage> spaceConfigStorages;

    private Map<String, SpaceConfigStorage> storageBySpaceName = new HashMap<>();

    public SpaceConfigStorageRegistryImpl() {
    }

    @Inject
    public SpaceConfigStorageRegistryImpl(final Instance<SpaceConfigStorage> spaceConfigStorages) {
        this.spaceConfigStorages = spaceConfigStorages;
    }

    @Override
    public SpaceConfigStorage get(final String spaceName) {
        return storageBySpaceName.computeIfAbsent(spaceName, name -> {
            final SpaceConfigStorage spaceConfigStorage = spaceConfigStorages.get();
            spaceConfigStorage.setup(spaceName);
            return spaceConfigStorage;
        });
    }
}

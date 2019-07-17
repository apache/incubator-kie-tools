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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageBatch;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class SpaceConfigStorageRegistryImpl implements SpaceConfigStorageRegistry {

    private Instance<SpaceConfigStorage> spaceConfigStorages;

    private Map<String, SpaceConfigStorage> storageBySpaceName = new ConcurrentHashMap<>();

    public SpaceConfigStorageRegistryImpl() {
    }

    @Inject
    public SpaceConfigStorageRegistryImpl(final Instance<SpaceConfigStorage> spaceConfigStorages) {
        this.spaceConfigStorages = spaceConfigStorages;
    }

    @Override
    public SpaceConfigStorage get(final String spaceName) {

        return storageBySpaceName.computeIfAbsent(spaceName,
                                                  name -> {
                                                      final SpaceConfigStorage spaceConfigStorage = spaceConfigStorages.get();
                                                      spaceConfigStorage.setup(spaceName);
                                                      return spaceConfigStorage;
                                                  });
    }

    @Override
    public SpaceConfigStorageBatch getBatch(String spaceName) {

        Optional<SpaceConfigStorage> optional = Optional.ofNullable(get(spaceName));

        if (optional.isPresent()) {
            return new SpaceStorageBatchImpl(optional.get());
        }

        throw new IllegalArgumentException("Cannot find Space '" + spaceName + "'");
    }

    @Override
    public void remove(String spaceName) {
        if (this.exist(spaceName)) {
            this.storageBySpaceName.get(spaceName).close();
            this.storageBySpaceName.remove(spaceName);
        }
    }

    @Override
    public boolean exist(String spaceName) {
        return this.storageBySpaceName.containsKey(spaceName);
    }

    public static class SpaceStorageBatchImpl implements SpaceConfigStorageBatch {

        private SpaceConfigStorage spaceConfigStorage;

        public SpaceStorageBatchImpl(SpaceConfigStorage spaceConfigStorage) {
            this.spaceConfigStorage = spaceConfigStorage;
        }

        @Override
        public <T> T run(final Function<SpaceConfigStorageBatchContext, T> function) {
            checkNotNull("function", function);

            final SpaceConfigStorageBatchContext context = ActiveSpaceConfigStorageBatchContextRegistry.getCurrentBatch(spaceConfigStorage, this);

            try {
                if (isMine(context)) {
                    spaceConfigStorage.startBatch();
                }

                return function.apply(context);
            } finally {
                if (isMine(context)) {
                    spaceConfigStorage.endBatch();
                    ActiveSpaceConfigStorageBatchContextRegistry.clearCurrentBatch();
                }
            }
        }

        private boolean isMine(SpaceConfigStorageBatchContext context) {
            return context.getOwner().equals(this);
        }
    }
}

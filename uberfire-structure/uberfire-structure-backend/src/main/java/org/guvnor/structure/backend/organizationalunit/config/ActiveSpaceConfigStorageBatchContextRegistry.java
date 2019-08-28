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
import java.util.concurrent.ConcurrentHashMap;

import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageBatch;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;

public class ActiveSpaceConfigStorageBatchContextRegistry {

    private static Map<Long, SpaceConfigStorageBatchContextImpl> activeContexts = new ConcurrentHashMap<>();

    public static SpaceConfigStorageBatch.SpaceConfigStorageBatchContext getCurrentBatch(final SpaceConfigStorage spaceConfigStorage,
                                                                                         final SpaceConfigStorageBatch ownerBatch) {

        return activeContexts.computeIfAbsent(getContextId(), contextId -> new SpaceConfigStorageBatchContextImpl(spaceConfigStorage, ownerBatch));
    }

    public static void clearCurrentBatch() {
        activeContexts.remove(getContextId());
    }

    private static Long getContextId() {
        return Thread.currentThread().getId();
    }

    private static class SpaceConfigStorageBatchContextImpl implements SpaceConfigStorageBatch.SpaceConfigStorageBatchContext {

        private final SpaceConfigStorage spaceConfigStorage;
        private final Object owner;
        private SpaceInfo info;

        public SpaceConfigStorageBatchContextImpl(SpaceConfigStorage spaceConfigStorage, Object owner) {
            this.spaceConfigStorage = spaceConfigStorage;
            this.owner = owner;
        }

        @Override
        public SpaceInfo getSpaceInfo() {
            if (info == null) {
                info = spaceConfigStorage.loadSpaceInfo();
            }

            return info;
        }

        @Override
        public Object getOwner() {
            return owner;
        }

        @Override
        public void saveSpaceInfo() {
            spaceConfigStorage.saveSpaceInfo(info);
        }
    }
}

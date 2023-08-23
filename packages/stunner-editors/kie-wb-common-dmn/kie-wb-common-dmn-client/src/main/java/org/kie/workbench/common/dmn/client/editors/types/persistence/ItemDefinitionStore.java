/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;

/**
 * Stores all Item Definitions loaded in the {@link DataTypesPage}.
 * <p>
 * All entries are indexed by the UUID from the correspondent Data Type.
 */
@ApplicationScoped
public class ItemDefinitionStore {

    private Map<String, ItemDefinition> itemDefinitions = new HashMap<>();

    public ItemDefinition get(final String uuid) {
        return itemDefinitions.get(uuid);
    }

    public void index(final String uuid,
                      final ItemDefinition itemDefinition) {
        itemDefinitions.put(uuid, itemDefinition);
    }

    public void clear() {
        itemDefinitions.clear();
    }

    int size() {
        return itemDefinitions.size();
    }

    public void unIndex(final String uuid) {
        itemDefinitions.remove(uuid);
    }
}

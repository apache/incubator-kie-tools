/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.engine.index;

import java.util.HashMap;
import java.util.Map;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.engine.index.spi.DataSetIndexRegistry;
import org.dashbuilder.dataset.uuid.UUIDGenerator;

public class TransientDataSetIndexRegistry implements DataSetIndexRegistry {

    protected UUIDGenerator uuidGenerator;
    protected Map<String,DataSetIndex> indexMap = new HashMap<String, DataSetIndex>();

    public TransientDataSetIndexRegistry(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    public DataSetIndex put(DataSet dataSet) {
        if (dataSet == null) {
            return null;
        }
        String uuid = dataSet.getUUID();
        if (uuid == null || uuid.length() == 0) {
            uuid = uuidGenerator.newUuidBase64();
            dataSet.setUUID(uuid);
        }

        DataSetIndex dsIndex = new DataSetStaticIndex(dataSet);
        indexMap.put(uuid, dsIndex);
        return dsIndex;
    }

    public DataSetIndex get(String uuid) {
        DataSetIndex index = indexMap.get(uuid);
        if (index != null) {
            index.reuseHit();
        }
        return index;
    }

    public DataSetIndex remove(String uuid) {
        return indexMap.remove(uuid);
    }
}


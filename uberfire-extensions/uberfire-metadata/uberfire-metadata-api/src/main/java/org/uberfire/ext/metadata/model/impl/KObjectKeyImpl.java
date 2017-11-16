/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.metadata.model.impl;

import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.ext.metadata.model.schema.MetaType;

public class KObjectKeyImpl implements KObjectKey {

    private String key;
    private String id;
    private MetaType type;
    private String clusterId;
    private String segmentId;

    public KObjectKeyImpl(String key,
                          String id,
                          String type,
                          String clusterId,
                          String segmentId) {
        this.key = key;
        this.id = id;
        this.type = () -> type;
        this.clusterId = clusterId;
        this.segmentId = segmentId;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public MetaType getType() {
        return this.type;
    }

    @Override
    public String getClusterId() {
        return this.clusterId;
    }

    @Override
    public String getSegmentId() {
        return this.segmentId;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}

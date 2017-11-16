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

import java.util.List;

import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;

public class KObjectImpl implements KObject {

    private String id;
    private String type;
    private String clusterId;
    private String segmentId;
    private String key;
    private List<KProperty<?>> properties;
    private boolean fullText;

    public KObjectImpl(String id,
                       String type,
                       String clusterId,
                       String segmentId,
                       String key,
                       List<KProperty<?>> properties,
                       boolean fullText) {
        this.id = id;
        this.type = type;
        this.clusterId = clusterId;
        this.segmentId = segmentId;
        this.key = key;
        this.properties = properties;
        this.fullText = fullText;
    }

    @Override
    public boolean fullText() {
        return this.fullText;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public MetaType getType() {
        return () -> this.type;
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

    @Override
    public Iterable<KProperty<?>> getProperties() {
        return this.properties;
    }
}

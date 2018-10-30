/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.model.schema;

import java.util.Collection;
import java.util.Optional;

/**
 *
 */
public interface MetaObject {

    String META_OBJECT_ID = "id";
    String META_OBJECT_CLUSTER_ID = "cluster.id";
    String META_OBJECT_TYPE = "type";
    String META_OBJECT_KEY = "key";
    String META_OBJECT_SEGMENT_ID = "segment.id";
    String META_OBJECT_FULL_TEXT = "fullText";

    MetaType getType();

    Collection<MetaProperty> getProperties();

    Optional<MetaProperty> getProperty(final String name);

    void addProperty(final MetaProperty metaProperty);
}

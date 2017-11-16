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

package org.uberfire.ext.metadata.metamodel;

import java.util.Collections;

import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.model.impl.MetaObjectImpl;
import org.uberfire.ext.metadata.model.schema.MetaObject;

public class NullMetaModelStore implements MetaModelStore {

    private static MetaObject EMPTY = new MetaObjectImpl(() -> "",
                                                         Collections.EMPTY_SET);

    @Override
    public void add(final MetaObject metaObject) {
    }

    @Override
    public void update(final MetaObject metaObject) {
    }

    @Override
    public MetaObject getMetaObject(final String type) {
        return EMPTY;
    }

    @Override
    public void dispose() {
    }
}

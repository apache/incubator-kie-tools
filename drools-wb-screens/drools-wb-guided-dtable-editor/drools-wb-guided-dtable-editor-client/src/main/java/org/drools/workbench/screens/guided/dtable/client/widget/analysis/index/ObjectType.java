/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasKeys;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyDefinition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;
import org.uberfire.commons.validation.PortablePreconditions;

public class ObjectType
        implements HasKeys {

    private final static KeyDefinition TYPE = KeyDefinition.newKeyDefinition().withId( "type" ).build();

    private final UUIDKey uuidKey = new UUIDKey( this );
    private final String type;
    private final ObjectFields fields = new ObjectFields();

    public ObjectType( final String type ) {
        this.type = PortablePreconditions.checkNotNull( "type", type );
    }

    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public String getType() {
        return type;
    }

    public ObjectFields getFields() {
        return fields;
    }

    public static Matchers type() {
        return new Matchers( TYPE );
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key( TYPE,
                         type )
        };
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                TYPE
        };
    }
}

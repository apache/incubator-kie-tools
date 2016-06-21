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
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Key;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.UUIDKey;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatchers;

public class ObjectType
        implements HasKeys {

    private final UUIDKey uuidKey = new UUIDKey( this );
    private final String type;
    private final Fields fields = new Fields();

    public ObjectType( final String type ) {
        this.type = type;
    }

    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    public String getType() {
        return type;
    }

    public Fields getFields() {
        return fields;
    }

    public static Matchers type() {
        return new Matchers( "type" );
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key( "type",
                         type )
        };
    }

    public static String[] keyIDs() {
        return new String[]{
                UUIDKey.UNIQUE_UUID,
                "type"
        };
    }
}

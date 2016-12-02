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
package org.drools.workbench.services.verifier.api.client.index;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.keys.IndexKey;
import org.drools.workbench.services.verifier.api.client.index.keys.Key;
import org.drools.workbench.services.verifier.api.client.index.keys.UUIDKey;
import org.drools.workbench.services.verifier.api.client.index.keys.UpdatableKey;
import org.drools.workbench.services.verifier.api.client.maps.KeyDefinition;
import org.drools.workbench.services.verifier.api.client.maps.util.HasIndex;
import org.drools.workbench.services.verifier.api.client.maps.util.HasKeys;
import org.uberfire.commons.validation.PortablePreconditions;

public class Column
        implements HasKeys,
                   HasIndex {

    private final UUIDKey uuidKey;

    private UpdatableKey<Column> indexKey;

    public Column( final int columnIndex,
                   final AnalyzerConfiguration configuration ) {
        PortablePreconditions.checkNotNull( "columnIndex",
                                            columnIndex );
        this.indexKey = new UpdatableKey<>( IndexKey.INDEX_ID,
                                            columnIndex );
        this.uuidKey = configuration.getUUID( this );
    }

    public static Matchers index() {
        return new Matchers( IndexKey.INDEX_ID );
    }

    public static KeyDefinition[] keyDefinitions() {
        return new KeyDefinition[]{
                UUIDKey.UNIQUE_UUID,
                IndexKey.INDEX_ID
        };

    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                indexKey
        };
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public int getIndex() {
        return (int) indexKey.getSingleValueComparator();
    }

    @Override
    public void setIndex( final int index ) {
        if ( indexKey.getSingleValue()
                .equals( index ) ) {
            return;
        } else {

            final UpdatableKey<Column> oldKey = indexKey;
            final UpdatableKey<Column> newKey = new UpdatableKey<>( IndexKey.INDEX_ID,
                                                                    index );
            indexKey = newKey;

            oldKey.update( newKey,
                           this );
        }

    }
}

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
package org.drools.workbench.services.verifier.api.client.index.keys;

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.services.verifier.api.client.maps.KeyChangeListener;
import org.drools.workbench.services.verifier.api.client.maps.KeyDefinition;

public class UpdatableKey<T>
        extends Key {

    private List<KeyChangeListener<T>> keyChangeListeners = new ArrayList<>();

    public UpdatableKey( final KeyDefinition keyDefinition,
                         final Comparable value ) {
        super( keyDefinition,
               value );
    }

    public UpdatableKey( final KeyDefinition keyDefinition,
                         final Values values ) {
        super( keyDefinition,
               values );
    }

    public void update( final UpdatableKey newKey,
                        final T t ) {
        for ( final KeyChangeListener<T> keyChangeListener : keyChangeListeners ) {
            keyChangeListener.update( this,
                                      newKey,
                                      t );
        }
    }

    public void addKeyChangeListener( final KeyChangeListener<T> keyChangeListener ) {
        keyChangeListeners.add( keyChangeListener );
    }

    public void removeListener( final KeyChangeListener<T> keyChangeListener ) {
        keyChangeListeners.remove( keyChangeListener );
    }
}

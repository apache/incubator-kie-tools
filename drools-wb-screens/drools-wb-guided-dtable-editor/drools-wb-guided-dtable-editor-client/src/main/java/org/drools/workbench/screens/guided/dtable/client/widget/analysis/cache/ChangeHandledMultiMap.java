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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;

public class ChangeHandledMultiMap<T>
        extends MultiMap<Value, T> {

    private List<MultiMapChangeHandler<T>> changeHandlers = new ArrayList<>();

    private MultiMapChangeHandler.ChangeSet<T> changeSet = new MultiMapChangeHandler.ChangeSet<>();
    private int                                counter   = 0;

    public void addChangeListener( final MultiMapChangeHandler<T> changeHandler ) {
        changeHandlers.add( changeHandler );
    }

    @Override
    public void put( final Value value,
                     final int index,
                     final T t ) {
        addToCounter();

        super.put( value,
                   index,
                   t );

        addToChangeSet( value,
                        t );

        fire();
    }

    @Override
    public boolean put( final Value value,
                        final T t ) {
        addToCounter();

        final boolean put = super.put( value,
                                       t );

        addToChangeSet( value,
                        t );

        fire();

        return put;
    }

    @Override
    public void putAllValues( final Value value,
                              final Collection<T> ts ) {
        addToCounter();

        for ( final T t : ts ) {
            addToChangeSet( value,
                            t );
        }

        super.putAllValues( value,
                            ts );
        fire();
    }

    @Override
    public Collection<T> remove( final Value value ) {
        addToCounter();
        for ( final T t : get( value ) ) {
            addRemovedToChangeSet( value, t );
        }

        final Collection<T> remove = super.remove( value );

        fire();

        return remove;
    }

    @Override
    public void removeValue( final Value value,
                             final T t ) {
        addToCounter();

        super.removeValue( value,
                           t );

        addRemovedToChangeSet( value,
                               t );

        fire();
    }

    @Override
    public void merge( final MultiMap<Value, T> other ) {
        addToCounter();

        super.merge( other );

        fire();
    }

    @Override
    public void clear() {
        addToCounter();

        for ( final Value value : map.keySet() ) {
            for ( final T t : map.get( value ) ) {
                addRemovedToChangeSet( value,
                                       t );
            }
        }

        super.clear();

        fire();
    }

    @Override
    public boolean addAllValues( final Value value,
                                 final Collection<T> ts ) {
        addToCounter();

        final boolean b = super.addAllValues( value,
                                              ts );

        for ( final T t : ts ) {
            addToChangeSet( value, t );
        }

        fire();

        return b;
    }

    public void addToCounter() {
        counter++;
    }

    public void fire() {
        if ( counter == 1 ) {
            for ( final MultiMapChangeHandler<T> changeHandler : changeHandlers ) {
                changeHandler.onChange( changeSet );
            }

            changeSet = new MultiMapChangeHandler.ChangeSet<>();
        }

        counter--;
    }

    private void addToChangeSet( final Value value,
                                 final T t ) {

        if ( !changeHandlers.isEmpty() ) {
            changeSet.added.put( value, t );
        }
    }

    private void addRemovedToChangeSet( final Value value,
                                        final T t ) {
        if ( !changeHandlers.isEmpty() ) {
            changeSet.removed.put( value,
                                   t );
        }
    }

    private void addUpdatedToChangeSet( final Value value,
                                        final T t ) {
        if ( !changeHandlers.isEmpty() ) {
            changeSet.updated.put( value,
                                   t );
        }
    }

    public void move( final Set<Value> oldKeys,
                      final Set<Value> newKeys,
                      final T t ) {

        addToCounter();

        for ( final Value oldKey : oldKeys ) {
            final ArrayList<T> list = get( oldKey );
            list.remove( t );

            if ( list.isEmpty() ) {
                super.remove( oldKey );
            }
        }

        for ( final Value newKey : newKeys ) {
            super.put( newKey,
                       t );

            addUpdatedToChangeSet( newKey,
                                   t );
        }

        fire();
    }
}

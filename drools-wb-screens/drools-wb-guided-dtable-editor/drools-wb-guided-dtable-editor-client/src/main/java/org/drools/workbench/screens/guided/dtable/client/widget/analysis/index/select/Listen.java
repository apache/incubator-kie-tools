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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select;

import java.util.ArrayList;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.ChangeHandledMultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMapChangeHandler;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Listen<T>
        extends Select<T> {

    private final ArrayList<AllListener<T>>   allListeners   = new ArrayList<>();
    private final ArrayList<FirstListener<T>> firstListeners = new ArrayList<>();
    private final ArrayList<LastListener<T>>  lastListeners  = new ArrayList<>();

    private Entry<T>           first;
    private Entry<T>           last;
    private MultiMap<Value, T> all;

    public Listen( final MultiMap<Value, T> map,
                   final Matcher matcher ) {
        super( map,
               matcher );

        checkNotNull( "map",
                      map );
        if ( map instanceof ChangeHandledMultiMap ) {

            (( ChangeHandledMultiMap ) map).addChangeListener( new MultiMapChangeHandler<T>() {
                @Override
                public void onChange( final ChangeSet<T> changeSet ) {

                    if ( hasNoListeners() ) {
                        return;
                    }

                    final ChangeHelper<T> changeHelper = new ChangeHelper<T>( changeSet,
                                                                              matcher );

                    if ( !firstListeners.isEmpty() ) {
                        if ( first == null || changeHelper.firstChanged( first ) ) {
                            first = firstEntry();
                            notifyFirstListeners();
                        }
                    }

                    if ( !lastListeners.isEmpty() ) {
                        if ( last == null || changeHelper.lastChanged( last ) ) {
                            last = lastEntry();
                            notifyLastListeners();
                        }
                    }

                    if ( !allListeners.isEmpty() ) {
                        all = asMap();
                        notifyAllListeners();
                    }
                    }

            } );
        } else {
            throw new IllegalArgumentException( "Can not listend to this map." );
        }
    }

    /**
     * Well not *all*, just the AllListeners.
     */
    private void notifyAllListeners() {
        for ( final AllListener<T> allListener : allListeners ) {
            allListener.onAllChanged( all.allValues() );
        }
    }

    private void notifyFirstListeners() {
        for ( final FirstListener<T> firstListener : firstListeners ) {
            firstListener.onFirstChanged( first.getValue() );
        }
    }

    private void notifyLastListeners() {
        for ( final LastListener<T> lastListener : lastListeners ) {
            lastListener.onLastChanged( last.getValue() );
        }
    }

    private boolean hasNoListeners() {
        return allListeners.isEmpty() && firstListeners.isEmpty() && lastListeners.isEmpty();
    }

    public void first( final FirstListener<T> firstListener ) {
        if ( first == null ) {
            this.first = firstEntry();
        }

        firstListeners.add( firstListener );
    }

    public void last( final LastListener<T> lastListener ) {
        if ( last == null ) {
            this.last = lastEntry();
        }
        lastListeners.add( lastListener );
    }

    public void all( final AllListener<T> allListener ) {
        if ( all == null ) {
            this.all = asMap();
        }
        this.allListeners.add( allListener );
    }

}

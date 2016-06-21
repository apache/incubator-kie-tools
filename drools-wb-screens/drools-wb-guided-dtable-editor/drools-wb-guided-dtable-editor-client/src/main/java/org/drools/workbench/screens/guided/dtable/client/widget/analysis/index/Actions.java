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

import java.util.Collection;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.KeyTreeMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ExactMatcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.KeyMatcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Listen;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Select;

public class Actions {

    private final KeyTreeMap<Action> map = new KeyTreeMap<>(Action.keyIDs());

    public Actions() {

    }

    public Actions( final Collection<Action> actions ) {
        for ( final Action action : actions ) {
            add( action );
        }
    }

    void add( final Action action ) {
        map.put( action );
    }

    public void merge( final Actions actions ) {
        map.merge( actions.map );
    }

    public Where<ActionSelect, ActionListen> where( final Matcher matcher ) {
        return new Where<>( new ActionSelect( matcher ),
                            new ActionListen( matcher ) );
    }

    public <KeyType> MapBy<KeyType, Action> mapBy( final KeyMatcher matcher ) {
        return new MapBy<>( map.get( matcher.getId() ) );
    }

    public void remove( final Column column ) {
        final ExactMatcher matcher = Action.columnUUID().is( column.getUuidKey() );
        for ( final Action action : where( matcher ).select().all() ) {
            action.getUuidKey().retract();
        }
    }

    public class ActionSelect
            extends Select<Action> {

        public ActionSelect( final Matcher matcher ) {
            super( map.get( matcher.getId() ),
                   matcher );
        }
    }

    public class ActionListen
            extends Listen<Action> {

        public ActionListen( final Matcher matcher ) {
            super( map.get( matcher.getId() ),
                   matcher );
        }
    }
}

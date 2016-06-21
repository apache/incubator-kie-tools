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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.IndexedKeyTreeMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.Matcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Listen;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.select.Select;

public class Columns {

    private final IndexedKeyTreeMap<Column> map = new IndexedKeyTreeMap<>();

    public Columns() {

    }

    public Columns( final Collection<Column> columns ) {
        for ( final Column column : columns ) {
            add( column );
        }
    }

    public void add( final Column column ) {
        map.put( column,
                 column.getIndex() );
    }

    public void merge( final Columns columns ) {
        map.merge( columns.map );
    }

    public Where<ColumnSelect, ColumnListen> where( final Matcher matcher ) {
        return new Where<>( new ColumnSelect( matcher ),
                            new ColumnListen( matcher ) );
    }

    public void remove( final Column column ) {
        column.getUuidKey().retract();
    }

    public class ColumnSelect
            extends Select<Column> {

        public ColumnSelect( final Matcher matcher ) {
            super( map.get( matcher.getId() ),
                   matcher );
        }
    }

    public class ColumnListen
            extends Listen<Column> {

        public ColumnListen( final Matcher matcher ) {
            super( map.get( matcher.getId() ),
                   matcher );
        }
    }
}

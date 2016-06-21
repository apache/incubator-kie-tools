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

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.MultiMap;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Value;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.ExactMatcher;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.NullOrEmptyMatcher;

public class NullOrEmptyMatcherSearch<T> {

    private NullOrEmptyMatcher matcher;
    private MultiMap<Value, T> map;

    public NullOrEmptyMatcherSearch( final NullOrEmptyMatcher matcher,
                                     final MultiMap<Value, T> map ) {
        this.matcher = matcher;
        this.map = map;
    }

    public MultiMap<Value, T> search() {
        if ( matcher.isNegate() ) {
            return searchEmpty( searchNull() );
        } else {
            final MultiMap<Value, T> nullSearch = searchNull();

            nullSearch.merge( searchEmpty( map ) );

            return nullSearch;
        }

    }

    private MultiMap<Value, T> searchEmpty( final MultiMap<Value, T> multiMap ) {
        return new ExactMatcherSearch<>( new ExactMatcher( matcher.getId(),
                                                           "",
                                                           matcher.isNegate() ),
                                         multiMap ).search();
    }

    private MultiMap<Value, T> searchNull() {
        return new ExactMatcherSearch<>( new ExactMatcher( matcher.getId(),
                                                           null,
                                                           matcher.isNegate() ),
                                         map ).search();
    }
}

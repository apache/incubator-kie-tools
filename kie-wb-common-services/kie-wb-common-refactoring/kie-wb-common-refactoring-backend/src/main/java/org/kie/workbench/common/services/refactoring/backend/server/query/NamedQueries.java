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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class NamedQueries {

    private Map<String, NamedQuery> namedQueries = new HashMap<String, NamedQuery>();

    public NamedQueries() {
        //Make proxyable
    }

    @Inject
    public NamedQueries( @Any final Instance<NamedQuery> namedQueries ) {
        for (NamedQuery namedQuery : namedQueries) {
            this.namedQueries.put( namedQuery.getName(), namedQuery );
        }
    }

    public Set<String> getQueries() {
        final Set<String> queryNames = new HashSet<String>();
        for (NamedQuery namedQuery : namedQueries.values()) {
            queryNames.add( namedQuery.getName() );
        }
        return queryNames;
    }

    public NamedQuery findNamedQuery( String queryName ) {
        if ( namedQueries.containsKey( queryName ) ) {
            return namedQueries.get( queryName );
        } else {
            throw new IllegalArgumentException( "Named Query '" + queryName + "' does not exist." );
        }
    }
}

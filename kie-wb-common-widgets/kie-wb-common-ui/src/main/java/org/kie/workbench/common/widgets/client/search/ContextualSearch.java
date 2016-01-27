/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.client.search;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.uberfire.client.workbench.events.PerspectiveChange;

@ApplicationScoped
public class ContextualSearch {

    private SearchBehavior searchBehavior;
    private SearchBehavior defaultSearchBehavior;
    private final Map<String, SearchBehavior> perspectiveSearchBehavior = new HashMap<String, SearchBehavior>();

    public SearchBehavior getSearchBehavior() {
        return searchBehavior == null ? defaultSearchBehavior : searchBehavior;
    }

    private void setSearchBehavior( final SearchBehavior searchBehavior ) {
        this.searchBehavior = searchBehavior;
    }

    public void setPerspectiveSearchBehavior( final String placeId, final SearchBehavior searchBehavior ) {
        this.perspectiveSearchBehavior.put( placeId, searchBehavior );
    }

    public void setDefaultSearchBehavior( final SearchBehavior searchBehavior ) {
        this.defaultSearchBehavior = searchBehavior;
    }

    public void onPerspectiveChange( @Observes final PerspectiveChange perspectiveChange ) {
        final String id = perspectiveChange.getPlaceRequest().getIdentifier();
        setSearchBehavior( perspectiveSearchBehavior.get( id ) );
    }

}

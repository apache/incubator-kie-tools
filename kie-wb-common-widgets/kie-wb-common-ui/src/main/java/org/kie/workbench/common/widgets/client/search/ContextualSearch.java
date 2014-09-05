/*
 * Copyright 2013 JBoss by Red Hat.
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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.uberfire.client.workbench.events.PerspectiveChange;

@ApplicationScoped
public class ContextualSearch {

    private SearchBehavior searchBehavior;
    private SearchBehavior defaultSearchBehavior;

    public SearchBehavior getSearchBehavior() {
        return searchBehavior;
    }

    public void setSearchBehavior( final SearchBehavior searchBehavior ) {
        this.searchBehavior = searchBehavior;
    }

    public void setDefaultSearchBehavior( final SearchBehavior searchBehavior ) {
        this.defaultSearchBehavior = searchBehavior;
    }

    public void onPerspectiveChange( @Observes final PerspectiveChange perspectiveChange ) {
        this.searchBehavior = defaultSearchBehavior;
    }

}

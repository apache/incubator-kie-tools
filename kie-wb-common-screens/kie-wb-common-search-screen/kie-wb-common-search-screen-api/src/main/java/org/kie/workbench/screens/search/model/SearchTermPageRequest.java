/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.screens.search.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.tables.PageRequest;

/**
 * A Query request.
 */
@Portable
public class SearchTermPageRequest extends PageRequest {

    private String term;

    public SearchTermPageRequest() {
    }

    public SearchTermPageRequest( final String term,
                                  final int startRowIndex,
                                  final Integer pageSize ) {
        super( startRowIndex, pageSize );
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm( final String term ) {
        this.term = term;
    }
}

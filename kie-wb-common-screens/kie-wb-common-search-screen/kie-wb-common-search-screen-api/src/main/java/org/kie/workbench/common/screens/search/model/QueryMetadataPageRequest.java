/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.search.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.paging.PageRequest;

/**
 * A Query request.
 */
@Portable
public class QueryMetadataPageRequest extends PageRequest {

    private Map<String, Object> metadata;
    private Date createdAfter;
    private Date createdBefore;
    private Date lastModifiedAfter;
    private Date lastModifiedBefore;

    public QueryMetadataPageRequest( @MapsTo("metadata") final Map<String, Object> metadata,
                                     @MapsTo("createdAfter") final Date createdAfter,
                                     @MapsTo("createdBefore") final Date createdBefore,
                                     @MapsTo("lastModifiedAfter") final Date lastModifiedAfter,
                                     @MapsTo("lastModifiedBefore") final Date lastModifiedBefore,
                                     @MapsTo("startRowIndex") final int startRowIndex,
                                     @MapsTo("pageSize") final Integer pageSize ) {
        super( startRowIndex, pageSize );
        this.metadata = new HashMap<String, Object>( metadata );
        this.createdAfter = createdAfter;
        this.createdBefore = createdBefore;
        this.lastModifiedAfter = lastModifiedAfter;
        this.lastModifiedBefore = lastModifiedBefore;
    }

    public Date getCreatedAfter() {
        return createdAfter;
    }

    public Date getCreatedBefore() {
        return createdBefore;
    }

    public Date getLastModifiedAfter() {
        return lastModifiedAfter;
    }

    public Date getLastModifiedBefore() {
        return lastModifiedBefore;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setCreatedAfter( Date createdAfter ) {
        this.createdAfter = createdAfter;
    }

    public void setCreatedBefore( Date createdBefore ) {
        this.createdBefore = createdBefore;
    }

    public void setLastModifiedAfter( Date lastModifiedAfter ) {
        this.lastModifiedAfter = lastModifiedAfter;
    }

    public void setLastModifiedBefore( Date lastModifiedBefore ) {
        this.lastModifiedBefore = lastModifiedBefore;
    }

    public void setMetadata( Map<String, Object> metadata ) {
        this.metadata = new HashMap<String, Object>( metadata );
    }
}

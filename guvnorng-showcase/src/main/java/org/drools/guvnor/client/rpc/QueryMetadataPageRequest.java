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
package org.drools.guvnor.client.rpc;

import java.util.Date;
import java.util.List;

/**
 * A Query request.
 */
public class QueryMetadataPageRequest extends PageRequest {

    private List<MetaDataQuery> metadata;
    private Date                createdAfter;
    private Date                createdBefore;
    private Date                lastModifiedAfter;
    private Date                lastModifiedBefore;
    private boolean             searchArchived;

    // For GWT serialisation
    public QueryMetadataPageRequest() {
    }

    public QueryMetadataPageRequest(List<MetaDataQuery> metadata,
                                    Date createdAfter,
                                    Date createdBefore,
                                    Date lastModifiedAfter,
                                    Date lastModifiedBefore,
                                    boolean searchArchived,
                                    int startRowIndex,
                                    Integer pageSize) {
        super(startRowIndex, pageSize);
        this.metadata = metadata;
        this.createdAfter = createdAfter;
        this.createdBefore = createdBefore;
        this.lastModifiedAfter = lastModifiedAfter;
        this.lastModifiedBefore = lastModifiedBefore;
        this.searchArchived = searchArchived;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

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

    public List<MetaDataQuery> getMetadata() {
        return metadata;
    }

    public boolean isSearchArchived() {
        return searchArchived;
    }

    public void setCreatedAfter(Date createdAfter) {
        this.createdAfter = createdAfter;
    }

    public void setCreatedBefore(Date createdBefore) {
        this.createdBefore = createdBefore;
    }

    public void setLastModifiedAfter(Date lastModifiedAfter) {
        this.lastModifiedAfter = lastModifiedAfter;
    }

    public void setLastModifiedBefore(Date lastModifiedBefore) {
        this.lastModifiedBefore = lastModifiedBefore;
    }

    public void setMetadata(List<MetaDataQuery> metadata) {
        this.metadata = metadata;
    }

    public void setSearchArchived(boolean searchArchived) {
        this.searchArchived = searchArchived;
    }

}

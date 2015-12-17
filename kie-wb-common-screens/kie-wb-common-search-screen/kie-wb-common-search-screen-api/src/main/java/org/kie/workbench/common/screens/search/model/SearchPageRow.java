/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.search.model;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.paging.AbstractPathPageRow;

@Portable
public class SearchPageRow extends AbstractPathPageRow {

    private String description;
    private String abbreviatedDescription;
    private String creator;
    private Date createdDate;
    private String lastContributor;
    private Date lastModified;
    private boolean disabled;

    public SearchPageRow() {
        super();
    }

    public SearchPageRow( final Path path ) {
        super( path );
    }

    public SearchPageRow( final Path path,
                          final String creator,
                          final Date createdDate,
                          final String lastContributor,
                          final Date lastModified,
                          final String description ) {
        super( path );
        this.creator = creator;
        this.createdDate = createdDate;
        this.lastContributor = lastContributor;
        this.lastModified = lastModified;
        this.description = description;
    }

    public String getAbbreviatedDescription() {
        return abbreviatedDescription;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public String getLastContributor() {
        return lastContributor;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setAbbreviatedDescription( String abbreviatedDescription ) {
        this.abbreviatedDescription = abbreviatedDescription;
    }

    public void setCreatedDate( Date createdDate ) {
        this.createdDate = createdDate;
    }

    public void setCreator( String creator ) {
        this.creator = creator;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setLastContributor( String lastContributor ) {
        this.lastContributor = lastContributor;
    }

    public void setLastModified( Date lastModified ) {
        this.lastModified = lastModified;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled( final boolean disabled ) {
        this.disabled = disabled;
    }

}

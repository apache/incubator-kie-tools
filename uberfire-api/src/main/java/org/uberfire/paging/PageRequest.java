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

package org.uberfire.paging;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A generic request for paged data
 * @see PageResponse
 */
@Portable
public class PageRequest {

    protected int startRowIndex = 0;
    protected Integer pageSize = null; // null returns all pages

    // For serialisation
    public PageRequest() {
    }

    public PageRequest( int startRowIndex,
                        Integer pageSize ) {
        this.startRowIndex = startRowIndex;
        this.pageSize = pageSize;
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public Integer getPageSize() {
        return pageSize;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public void setPageSize( Integer pageSize ) {
        this.pageSize = pageSize;
    }

    public void setStartRowIndex( int startRowIndex ) {
        this.startRowIndex = startRowIndex;
    }

}

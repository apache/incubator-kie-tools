/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.paging;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * A Page of data for display in a PagedTable
 */
@Portable
public class PageResponse<T extends AbstractPageRow> {

    // Is totalRowSize exact or undetermined
    private boolean totalRowSizeExact;

    // Total number of rows in whole data-set (not just page)
    private int totalRowSize;

    private int startRowIndex;
    private List<T> pageRowList;
    private boolean lastPage;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public List<T> getPageRowList() {
        return pageRowList;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public int getTotalRowSize() {
        return totalRowSize;
    }

    public boolean isFirstPage() {
        return startRowIndex == 0L;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public boolean isTotalRowSizeExact() {
        return totalRowSizeExact;
    }

    public void setLastPage( boolean lastPage ) {
        this.lastPage = lastPage;
    }

    public void setPageRowList( List<T> assetPageRowList ) {
        this.pageRowList = assetPageRowList;
    }

    public void setStartRowIndex( int startRowIndex ) {
        this.startRowIndex = startRowIndex;
    }

    public void setTotalRowSize( int totalRowSize ) {
        this.totalRowSize = totalRowSize;
    }

    public void setTotalRowSizeExact( boolean totalRowSizeExact ) {
        this.totalRowSizeExact = totalRowSizeExact;
    }

}

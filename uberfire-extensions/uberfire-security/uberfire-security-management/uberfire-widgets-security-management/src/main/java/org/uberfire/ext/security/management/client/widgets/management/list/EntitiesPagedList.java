/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management.list;

import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Presenter class for listing entities, with automatic pagination feature management.</p>
 * <p>Notes:</p>
 * <ul>
 *     <li>
 *         <p>By default the <code>onChangePage</code> callback method is not fired, it's handled by this class itself.</p>
 *     </li>
 * </ul>
 *
 * @since 0.8.0
 */
@Dependent
@Alternative
public class EntitiesPagedList<T> extends EntitiesList<T> {

    protected Collection<T> entities;
    protected Callback<T> callback;
    protected int currentPage = -1;

    @Inject
    public EntitiesPagedList(LoadingBox loadingBox, View view) {
        super(loadingBox, view);
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API
     ****************************************************************************************************** */
    public void show(final Collection<T> entities, final Callback<T> callback) {
        if (this.currentPage == -1) this.currentPage = 1;
        this.entities = entities;
        this.callback = callback;
        show();
    }


    /*  ******************************************************************************************************
                                 PROTECTED PRESENTER API FOR IMPL WIDGETS
     ****************************************************************************************************** */

    protected void show() {
        if (callback != null && getEntities() != null) {
            final int size = getEntities().size();
            EntitiesPagedList.this.totalPages = size / pageSize;
            final int  start = ( currentPage - 1 ) * pageSize;
            final boolean hasMorePages = hasMorePages();
            final int end =  hasMorePages ? (start + pageSize) : size;
            final List<T> pageEntities = buildPageEntities(start, end);
            final PaginationConstraints paginationConstraints = buildPaginationConstraints(size);
            show(pageEntities, paginationConstraints, callback);
        }
    }



    /*  ******************************************************************************************************
                                 PROTECTED METHODS FOR INTERNAL PRESENTER LOGIC
     ****************************************************************************************************** */

    @Override
   void onGoToFirstPage() {
       if (this.currentPage > -1) {
           this.currentPage = 1;
           show();
       } else {
           super.onGoToFirstPage();
       }
   }

    @Override
    void onGoToPrevPage() {
        if (this.currentPage > -1) {
            this.currentPage = paginationConstraints.getCurrentPage() - 1;
            show();
        } else {
            super.onGoToPrevPage();
        }
    }

    @Override
    void onGoToNextPage() {
        if (this.currentPage > -1) {
            this.currentPage = paginationConstraints.getCurrentPage() + 1;
            show();
        } else {
            super.onGoToNextPage();
        }
    }

    @Override
    void onGoToLastPage() {
        if (this.currentPage > -1) {
            this.currentPage = totalPages + 1;
            show();
        } else {
            super.onGoToLastPage();
        }
    }

    @Override
    public void clear() {
        super.clear();
        entities = null;
        callback = null;
        currentPage = -1;
    }

    protected Collection<T> getEntities() {
        return entities;
    }

    protected List<T> buildPageEntities(final int  start, final int  end) {
        return new LinkedList<T>(getEntities()).subList(start, end);
    }

    protected PaginationConstraints buildPaginationConstraints(final int size) {
        final boolean hasMorePages = hasMorePages();
        final boolean isNotFirstPage = currentPage > 1;
        return new PaginationConstraints() {
            @Override
            public boolean isFirstPageEnabled() {
                return isNotFirstPage;
            }

            @Override
            public boolean isFirstPageVisible() {
                return isNotFirstPage;
            }

            @Override
            public boolean isPrevPageEnabled() {
                return isNotFirstPage;
            }

            @Override
            public boolean isPrevPageVisible() {
                return isNotFirstPage;
            }

            @Override
            public int getCurrentPage() {
                return currentPage;
            }

            @Override
            public boolean isNextPageEnabled() {
                return hasMorePages;
            }

            @Override
            public boolean isNextPageVisible() {
                return hasMorePages;
            }

            @Override
            public boolean isLastPageEnabled() {
                return hasMorePages;
            }

            @Override
            public boolean isLastPageVisible() {
                return hasMorePages;
            }

            @Override
            public Integer getTotal() {
                return size;
            }
        };
    }

    protected boolean hasMorePages() {
        return currentPage < EntitiesPagedList.this.totalPages;
    }

}

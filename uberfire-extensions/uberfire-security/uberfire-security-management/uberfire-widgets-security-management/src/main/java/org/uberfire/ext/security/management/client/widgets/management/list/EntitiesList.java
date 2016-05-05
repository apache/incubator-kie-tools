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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.api.AbstractEntityManager;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import java.util.Collection;

/**
 * <p>Presenter class for listing entities.</p>
 */
@Dependent
@Alternative
public class EntitiesList<T> implements IsWidget {

    protected static final int DEFAULT_PAGE_SIZE = 5;

    public interface View extends UberView<EntitiesList> {

        View configure(final String emptyEntitiesText, final PaginationConstraints paginationConstraints);
        View add(final int index, final String identifier, final String title, final HeadingSize titleSize,
                 final boolean canRead, final boolean canRemove,
                 final boolean canSelect, final boolean isSelected);
        View clear();

    }

    /**
     * <p>The pagination constraints for view's pager component.</p>
     */
    public interface PaginationConstraints {

        /**
         * <p>First page button status.</p>
         * @return The first page button will be enabled if <code>true</code>, otherwise disabled.
         */
        boolean isFirstPageEnabled();

        /**
         * <p>First page button visibility.</p>
         * @return The first page button will be visible if <code>true</code>, otherwise hidden.
         */
        boolean isFirstPageVisible();

        /**
         * <p>Previous page button status.</p>
         * @return The previous page button will be enabled if <code>true</code>, otherwise disabled.
         */
        boolean isPrevPageEnabled();

        /**
         * <p>Previous page button visibility.</p>
         * @return The previous page button will be visible if <code>true</code>, otherwise hidden.
         */
        boolean isPrevPageVisible();

        /**
         * <p>Current page.</p>
         * @return The current page.
         */
        int getCurrentPage();

        /**
         * <p>Next page button status.</p>
         * @return The next page button will be enabled if <code>true</code>, otherwise disabled.
         */
        boolean isNextPageEnabled();

        /**
         * <p>Next page button visibility.</p>
         * @return The next page button will be visible if <code>true</code>, otherwise hidden.
         */
        boolean isNextPageVisible();

        /**
         * <p>Last page button status.</p>
         * @return The last page button will be enabled if <code>true</code>, otherwise disabled.
         */
        boolean isLastPageEnabled();

        /**
         * <p>Last page button visibility.</p>
         * @return The last page button will be visible if <code>true</code>, otherwise hidden.
         */
        boolean isLastPageVisible();

        /**
         * <p>Show the total number of entities, if available.</p>
         * @return The total number of entities or <code>null</code> if not available.
         */
        Integer getTotal();
    }

    /**
     * <p>Callback methods for view's user actions.</p>
     */
    public interface Callback<T> {

        /**
         * <p>The title for the entity type to manage, such as "user" or "group".</p>
         * @return The entity type title.
         */
        String getEntityType();

        /**
         * <p>Allows enabling or disabling the entities read feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities list widget enables the read feature.</li>
         *              <li><code>false</code> - The entities list widget disables the read feature.</li>
         *          </ul>
         */
        boolean canRead();

        /**
         * <p>Allows enabling or disabling the entities delete feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities list widget enables the delete feature.</li>
         *              <li><code>false</code> - The entities list widget disables the delete feature.</li>
         *          </ul>
         */
        boolean canRemove();

        /**
         * <p>Allows enabling or disabling the entities selection feature.</p>
         * @return <p>Two possible values:</p>
         *          <ul>
         *              <li><code>true</code> - The entities list widget enables the selection feature.</li>
         *              <li><code>false</code> - The entities list widget disables the selection feature.</li>
         *          </ul>
         */
        boolean canSelect();

        /**
         * <p>Specify if the entity must be marked as selected..</p>
         */
        boolean isSelected(final String identifier);

        /**
         * <p>The entity identifier.</p>
         * @param entity The entity.
         * @return The entity identifier.
         */
        String getIdentifier(final T entity);

        /**
         * <p>The entity title.</p>
         * @param entity The title.
         * @return The entity identifier.
         */
        String getTitle(final T entity);

        /**
         * <p>Read an entity</p>
         * @param identifier The entity's identifier to read.
         */
        void onReadEntity(final String identifier);

        /**
         * <p>Remove an entity</p>
         * @param identifier The entity's identifier to remove.
         */
        void onRemoveEntity(final String identifier);

        /**
         * <p>Select or unselect an entity from the list.</p>
         * @param identifier The entity's identifier to remove.
         * @param isSelected If <code>true</code>, the entity has been selected, otherwise has been unselected.
         */
        void onSelectEntity(final String identifier, final boolean isSelected);

        /**
         * <p>Change current page.</p>
         * @param currentPage Current page.
         * @param goToPage The target page number to navigate.
         */
        void onChangePage(final int currentPage, final int goToPage);

    }

    LoadingBox loadingBox;
    public View view;

    int pageSize = DEFAULT_PAGE_SIZE;
    HeadingSize headingSize = HeadingSize.H3;
    Callback callback;
    PaginationConstraints paginationConstraints;
    int totalPages = -1;
    String emptyEntitiesText = null;

    @Inject
    public EntitiesList(LoadingBox loadingBox, View view) {
        this.loadingBox = loadingBox;
        this.view = view;
    }

    @PostConstruct
    protected void init() {
        view.init(this);
    }

    /*  ******************************************************************************************************
                                 PUBLIC PRESENTER API
     ****************************************************************************************************** */

    public void show(final AbstractEntityManager.SearchResponse<T> response, final Callback<T> callback) {

        if (callback != null && response != null) {
            show(response.getResults(), createPaginationCallback(response), callback);
        }
    }

    public void select(final String identifier) {
        doSelectEntity(identifier, true);
    }

    public void unselect(final String identifier) {
        doSelectEntity(identifier, false);
    }

    public void clear() {
        callback = null;
        paginationConstraints = null;
        totalPages = -1;
        emptyEntitiesText = null;
        view.clear();
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setEmptyEntitiesText(String emptyEntitiesText) {
        this.emptyEntitiesText = emptyEntitiesText;
    }

    public void setEntityTitleSize(HeadingSize headingSize) {
        this.headingSize = headingSize;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    /*  ******************************************************************************************************
                                 PACKAGE PROTECTED METHODS FOR USING AS CALLBACKS FOR THE VIEW
     ****************************************************************************************************** */

    String getEntityType() {
        return callback.getEntityType();
    }

    void onReadEntity(final String identifier) {
        callback.onReadEntity(identifier);
    }

    void onRemoveEntity(final String identifier) {
        callback.onRemoveEntity(identifier);
    }

    void onGoToFirstPage() {
        final int currentPage = paginationConstraints.getCurrentPage();
        callback.onChangePage(currentPage, 1);
    }

    void onGoToPrevPage() {
        final int currentPage = paginationConstraints.getCurrentPage();
        callback.onChangePage(currentPage, currentPage - 1);
    }

    void onGoToNextPage() {
        final int currentPage = paginationConstraints.getCurrentPage();
        callback.onChangePage(currentPage, currentPage + 1);
    }

    void onGoToLastPage() {
        final int currentPage = paginationConstraints.getCurrentPage();
        if (totalPages > 0) {
            callback.onChangePage(currentPage, totalPages + 1);
        }
    }

    void onSelectEntity(String identifier, int index, boolean isSelected) {
        doSelectEntity(identifier, isSelected);
    }


     /*  ******************************************************************************************************
                                     PROTECTED METHODS FOR INTERNAL PRESENTER LOGIC
         ****************************************************************************************************** */

    private void doSelectEntity(String identifier, boolean isSelected) {
        callback.onSelectEntity(identifier, isSelected);
    }

    protected void show(final Collection<T> entities, final PaginationConstraints paginationConstraints, final Callback<T> callback) {
        this.callback = callback;
        this.paginationConstraints = paginationConstraints;
        if (callback != null && entities != null) {
            showLoadingView();

            // Configure view and the paginator.
            view.configure(emptyEntitiesText, paginationConstraints);

            // Add the entities.
            if (!entities.isEmpty()) {
                int index = 0;
                for (final T entity : entities) {
                    final String id = callback.getIdentifier(entity);
                    final String title = callback.getTitle(entity);
                    final boolean isSelected = callback.isSelected(id);
                    view.add(index, id, title, headingSize, callback.canRead(), callback.canRemove(),
                            callback.canSelect(), isSelected);
                    index++;
                }
            }

            hideLoadingView();
        }
    }

    protected PaginationConstraints createPaginationCallback(final AbstractEntityManager.SearchResponse searchResponse) {
        if (searchResponse != null) {
            final int page = searchResponse.getPage();
            final int total = searchResponse.getTotal();

            // If the SPI is able to get max row count, calculate total pages.
            final int totalPagesRounded = (int) Math.ceil( total / (double ) searchResponse.getPageSize() );
            EntitiesList.this.totalPages = total > -1 ? total / searchResponse.getPageSize() : -1;
            final boolean hasNextPage = totalPages > -1 ? page < totalPagesRounded : searchResponse.hasNextPage();
            final boolean notInFistPage = page > 1;
            final boolean isLastPageButtonEnabled = EntitiesList.this.totalPages > -1 && hasNextPage;

            return  (new PaginationConstraints() {

                @Override
                public boolean isFirstPageEnabled() {
                    return notInFistPage;
                }

                @Override
                public boolean isFirstPageVisible() {
                    return notInFistPage;
                }

                @Override
                public boolean isPrevPageEnabled() {
                    return notInFistPage;
                }

                @Override
                public boolean isPrevPageVisible() {
                    return notInFistPage;
                }

                @Override
                public int getCurrentPage() {
                    return page;
                }

                @Override
                public boolean isNextPageEnabled() {
                    return hasNextPage;
                }

                @Override
                public boolean isNextPageVisible() {
                    return hasNextPage;
                }

                @Override
                public boolean isLastPageEnabled() {
                    return isLastPageButtonEnabled;
                }

                @Override
                public boolean isLastPageVisible() {
                    return isLastPageButtonEnabled;
                }

                @Override
                public Integer getTotal() {
                    return total > -1 ? total : null;
                }
            });
        }
        return null;
    }

    protected void showLoadingView() {
        loadingBox.show();

    }

    protected void hideLoadingView() {
        loadingBox.hide();
    }

}

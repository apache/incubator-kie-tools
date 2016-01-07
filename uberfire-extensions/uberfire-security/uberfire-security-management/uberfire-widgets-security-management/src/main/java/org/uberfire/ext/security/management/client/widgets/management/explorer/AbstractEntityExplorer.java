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

package org.uberfire.ext.security.management.client.widgets.management.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.LabelType;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementWidgetsConstants;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.list.EntitiesList;
import org.uberfire.ext.security.management.client.widgets.popup.LoadingBox;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractEntityExplorer<T> implements IsWidget {

    protected final static int PAGE_SIZE = 15;
    protected static final String SEARCH_PATTERN_ALL = "";
    
    ClientUserSystemManager userSystemManager;
    Event<OnErrorEvent> errorEvent;
    LoadingBox loadingBox;
    EntitiesList<T> entitiesList;
    public EntitiesExplorerView view;

    int pageSize = PAGE_SIZE;
    String searchPattern = SEARCH_PATTERN_ALL;
    int currentPage = 1;
    ExplorerViewContext context;
    Set<String> selected;

    @Inject
    public AbstractEntityExplorer(final ClientUserSystemManager userSystemManager,
                                  final Event<OnErrorEvent> errorEvent,
                                  final LoadingBox loadingBox,
                                  final EntitiesList<T> entitiesList,
                                  final EntitiesExplorerView view) {
        
        this.userSystemManager = userSystemManager;
        this.errorEvent = errorEvent;
        this.loadingBox = loadingBox;
        this.entitiesList = entitiesList;
        this.view = view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    protected abstract String getEmptyText();

    protected abstract String getEntityType();

    protected abstract String getTitle();

    protected abstract String getEntityId(final T entity);

    protected abstract String getEntityName(final T entity);
    
    protected abstract boolean canSearch();
    
    protected abstract boolean canCreate();

    protected abstract boolean canRead();

    protected abstract void showSearch();

    @PostConstruct
    public void init() {
        entitiesList.setPageSize(pageSize);
        entitiesList.setEmptyEntitiesText(getEmptyText());
        view.configure(getTitle(), entitiesList.view);
    }
    
    public void show() {
        show(null);
    }

    public void show(final ExplorerViewContext context) {
        // Configure the view context.
        this.context = context;
        if (this.context == null) this.context = new ExplorerViewContext();
        this.context.setParent(createParentContext());
        this.selected = this.context.getSelectedEntities();

        if (canSearch()) showSearch();
        else view.showMessage(LabelType.WARNING, UsersManagementWidgetsConstants.INSTANCE.doesNotHavePrivileges());
        
    }

    public Set<String> getSelectedEntities() {
        return nullSafe(selected);
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected final ErrorCallback<Message> errorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(final Message message, final Throwable throwable) {
            showError(throwable);
            return false;
        }
    };

    protected EntitiesList.Callback<T> createCallback() {
        return new EntitiesList.Callback<T>() {

            @Override
            public String getEntityType() {
                return AbstractEntityExplorer.this.getEntityType();
            }

            @Override
            public boolean canRead() {
                return AbstractEntityExplorer.this.context.canRead();
            }

            @Override
            public boolean canRemove() {
                return AbstractEntityExplorer.this.context.canDelete();
            }

            @Override
            public boolean canSelect() {
                return AbstractEntityExplorer.this.context.canSelect();
            }

            @Override
            public boolean isSelected(final String id) {
                return selected != null && AbstractEntityExplorer.this.selected.contains(id);
            }

            @Override
            public String getIdentifier(final T entity) {
                return AbstractEntityExplorer.this.getEntityId(entity);
            }

            @Override
            public String getTitle(final T entity) {
                return AbstractEntityExplorer.this.getEntityName(entity);
            }

            @Override
            public void onReadEntity(final String identifier) {
                fireReadEvent(identifier);
            }

            @Override
            public void onRemoveEntity(final String identifier) {
                // Not available from explorer widget.
            }

            @Override
            public void onSelectEntity(String identifier, boolean isSelected) {
                if (isSelected) {
                    if (selected == null) selected = new HashSet<String>(1);
                    AbstractEntityExplorer.this.selected.add(identifier);
                } else if (selected != null) {
                    AbstractEntityExplorer.this.selected.remove(identifier);
                }
            }

            @Override
            public void onChangePage(final int currentPage, final int goToPage) {
                AbstractEntityExplorer.this.currentPage = goToPage;
                AbstractEntityExplorer.this.showSearch();
            }
        };
    }

    protected void fireReadEvent(final String identifier) {
        
    }

    protected EntitiesExplorerView.ViewContext createParentContext() {
        final boolean canSelect = false;
    
        return new EntitiesExplorerView.ViewContext() {
            @Override
            public boolean canSearch() {
                return AbstractEntityExplorer.this.canSearch();
            }

            @Override
            public boolean canCreate() {
                return AbstractEntityExplorer.this.canCreate();
            }

            @Override
            public boolean canRead() {
                return AbstractEntityExplorer.this.canRead();
            }

            @Override
            public boolean canDelete() {
                // By default, the exlorer widget does not allow to delete.
                return false;
            }

            @Override
            public boolean canSelect() {
                return canSelect;
            }

            @Override
            public Set<String> getSelectedEntities() {
                return selected;
            }

            @Override
            public Set<String> getConstrainedEntities() {
                return new HashSet<String>();
            }

        };
    }

    protected EntitiesExplorerView.ViewCallback viewCallback = new EntitiesExplorerView.ViewCallback() {
        @Override
        public void onSearch(final String pattern) {
            AbstractEntityExplorer.this.searchPattern = pattern != null ? pattern : SEARCH_PATTERN_ALL;
            AbstractEntityExplorer.this.currentPage = 1;
            if (pattern == null || pattern.trim().length() == 0) view.clearSearch();
            showSearch();
        }

        @Override
        public void onRefresh()
        {
            currentPage = 1;
            showSearch();
        }

    };

    protected void showLoadingView() {
        loadingBox.show();
    }

    protected Set<String> nullSafe(final Set<String> set) {
        return set != null ? Collections.unmodifiableSet(set) : null;
    }
    
    protected void hideLoadingView() {
        loadingBox.hide();
    }

    protected void showError(final Throwable throwable) {
        final String msg = throwable != null ? throwable.getMessage() : UsersManagementWidgetsConstants.INSTANCE.genericError();
        showError(msg);
    }
    
    protected void showError(final String message) {
        hideLoadingView();
        errorEvent.fire(new OnErrorEvent(AbstractEntityExplorer.this, message));
    }

    public void clear() {
        context = null;
        searchPattern = SEARCH_PATTERN_ALL;
        currentPage = 1;
        view.clear();
    }

}

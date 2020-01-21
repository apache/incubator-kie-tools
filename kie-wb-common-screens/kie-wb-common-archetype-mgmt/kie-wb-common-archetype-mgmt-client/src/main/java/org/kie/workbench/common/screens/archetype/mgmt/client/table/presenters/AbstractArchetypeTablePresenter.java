/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.modal.AddArchetypeModalPresenter;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.config.ArchetypeTableConfiguration;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.item.ArchetypeItemPresenter;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.item.model.ArchetypeItem;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;

public abstract class AbstractArchetypeTablePresenter implements ArchetypeTablePresenter,
                                                                 HasBusyIndicator {

    protected static final String EMPTY = "";
    protected static final int PAGE_SIZE = 10;

    protected final View view;
    protected final ArchetypeListPresenter archetypeListPresenter;
    protected final BusyIndicatorView busyIndicatorView;
    protected final TranslationService ts;
    protected final AddArchetypeModalPresenter addArchetypeModalPresenter;
    protected final ArchetypePreferences archetypePreferences;
    protected final Caller<ArchetypeService> archetypeService;
    protected final PreferenceScopeFactory preferenceScopeFactory;
    protected final Promises promises;
    protected String searchFilter = EMPTY;
    protected int currentPage;
    protected int totalPages;
    protected ArchetypeTableConfiguration configuration;
    private boolean setup;
    private Runnable onChangedCallback;

    public AbstractArchetypeTablePresenter(final View view,
                                           final ArchetypeListPresenter archetypeListPresenter,
                                           final BusyIndicatorView busyIndicatorView,
                                           final TranslationService ts,
                                           final AddArchetypeModalPresenter addArchetypeModalPresenter,
                                           final ArchetypePreferences archetypePreferences,
                                           final Caller<ArchetypeService> archetypeService,
                                           final PreferenceScopeFactory preferenceScopeFactory,
                                           final Promises promises) {
        this.view = view;
        this.archetypeListPresenter = archetypeListPresenter;
        this.busyIndicatorView = busyIndicatorView;
        this.ts = ts;
        this.addArchetypeModalPresenter = addArchetypeModalPresenter;
        this.archetypePreferences = archetypePreferences;
        this.archetypeService = archetypeService;
        this.preferenceScopeFactory = preferenceScopeFactory;
        this.promises = promises;
    }

    public void onArchetypeListUpdatedEvent(@Observes final ArchetypeListUpdatedEvent event) {
        if (isSetup()) {
            loadList(false);
        }
    }

    public abstract Promise<Void> makeDefaultValue(String alias,
                                                   boolean updateList);

    public abstract Promise<Void> loadPreferences(PaginatedArchetypeList paginatedList);

    public abstract boolean canMakeChanges();

    @Override
    public void showBusyIndicator(final String message) {
        busyIndicatorView.showBusyIndicator(message);
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

    @Override
    public boolean isSetup() {
        return setup;
    }

    @Override
    public ArchetypePreferences getPreferences() {
        return archetypePreferences;
    }

    @Override
    public boolean isEmpty() {
        return !isSetup() || archetypeListPresenter.getObjectsList().isEmpty();
    }

    @Override
    public void reset() {
        setup = false;
    }

    @Override
    public Promise<Void> setup(final boolean isRefresh,
                               final Runnable onChangedCallback) {
        if (!isRefresh) {
            this.view.init(this);
            this.currentPage = 1;
            this.searchFilter = EMPTY;
            this.setup = true;
            this.onChangedCallback = onChangedCallback;
        }

        return loadList(isRefresh);
    }

    public View getView() {
        return view;
    }

    public void setCurrentPage(final int currentPage) {
        if (currentPage <= totalPages && currentPage > 0) {
            this.currentPage = currentPage;
            updateList();
        }
    }

    public void goToPreviousPage() {
        if (currentPage - 1 >= 1) {
            currentPage--;
            updateList();
        }
    }

    public void goToNextPage() {
        if (currentPage + 1 <= totalPages) {
            currentPage++;
            updateList();
        }
    }

    public void goToFirstPage() {
        currentPage = 1;
        updateList();
    }

    public void goToLastPage() {
        currentPage = totalPages;
        updateList();
    }

    public void search(final String searchText) {
        searchFilter = searchText;
        currentPage = 1;
        updateList();
    }

    public void addArchetype() {
        if (canMakeChanges()) {
            addArchetypeModalPresenter.show();
        }
    }

    public List<String> getIncluded() {
        return getSelectionMap()
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public boolean isShowIncludeColumn() {
        return getConfiguration().isShowIncludeColumn();
    }

    public boolean isShowStatusColumn() {
        return getConfiguration().isShowStatusColumn();
    }

    public boolean isShowDeleteAction() {
        return getConfiguration().isShowDeleteAction();
    }

    public boolean isShowValidateAction() {
        return getConfiguration().isShowValidateAction();
    }

    public void setSelected(final ArchetypeItem archetypeItem,
                            final boolean isSelected) {
        final Map<String, Boolean> selectionMap = getSelectionMap();
        if (selectionMap.containsKey(archetypeItem.getArchetype().getAlias())) {
            getSelectionMap().put(archetypeItem.getArchetype().getAlias(),
                                  isSelected);

            updateSelectionCounter();

            runOnChangedCallback();
        }
    }

    public void updateSelectionCounter() {
        view.setSelectionCounter(ts.format(ArchetypeManagementConstants.ArchetypeManagement_SelectionCounter,
                                           getIncluded().size(),
                                           getSelectionMap().size()));
    }

    protected void runOnChangedCallback() {
        if (onChangedCallback != null) {
            onChangedCallback.run();
        }
    }

    protected ParameterizedCommand<ArchetypePreferences> loadPreferencesSuccessCallback(final PaginatedArchetypeList paginatedList,
                                                                                        final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Void> resolve) {
        return preference -> {
            finishLoadList(paginatedList);
            resolve.onInvoke(promises.resolve());
        };
    }

    protected ParameterizedCommand<Throwable> loadPreferencesErrorCallback(final Promise.PromiseExecutorCallbackFn.RejectCallbackFn reject) {
        return error -> {
            hideBusyIndicator();
            reject.onInvoke(error);
        };
    }

    protected void configureView(final PaginatedArchetypeList paginatedList) {
        view.showAddAction(getConfiguration().isShowAddAction());
        view.showIncludeHeader(getConfiguration().isShowIncludeColumn());
        view.showStatusHeader(getConfiguration().isShowStatusColumn());
        view.showSelectionCounter(getConfiguration().isShowIncludeColumn());

        final boolean noResults = paginatedList.isEmpty();
        final boolean noArchetypesRegistered = noResults && searchFilter.equals(EMPTY);

        view.showNoResults(noResults && !noArchetypesRegistered);
        view.showPagination(!noResults);
        view.showToolbar(!noArchetypesRegistered);
        view.showEmpty(noArchetypesRegistered);
    }

    public Promise<Void> savePreferences(final PreferenceScope scope,
                                         final boolean updateList) {
        if (canMakeChanges()) {
            showBusyIndicator(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Loading));

            return promises.create((resolve, reject) ->
                                           archetypePreferences.save(scope,
                                                                     getSavePreferencesSuccessCallback(updateList, resolve),
                                                                     getSavePreferencesErrorCallback(reject)));
        }

        return promises.resolve();
    }

    public Promise<Void> savePreferences(final PreferenceScopeResolutionStrategyInfo strategyInfo,
                                         final boolean updateList) {
        if (canMakeChanges()) {
            showBusyIndicator(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Loading));

            return promises.create((resolve, reject) ->
                                           archetypePreferences.save(strategyInfo,
                                                                     getSavePreferencesSuccessCallback(updateList),
                                                                     getSavePreferencesErrorCallback(reject)));
        }

        return promises.resolve();
    }

    protected void updateList() {
        loadList(true);
    }

    private Command getSavePreferencesSuccessCallback(final boolean updateList,
                                                      final Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Void> resolve) {
        return () -> {
            hideBusyIndicator();
            if (updateList) {
                updateList();
            }
            resolve.onInvoke(promises.resolve());
        };
    }

    private ParameterizedCommand<Throwable> getSavePreferencesErrorCallback(final Promise.PromiseExecutorCallbackFn.RejectCallbackFn reject) {
        return error -> {
            hideBusyIndicator();
            reject.onInvoke(error);
        };
    }

    protected void finishLoadList(final PaginatedArchetypeList paginatedList) {
        final List<ArchetypeItem> archetypeItems =
                paginatedList.getArchetypes()
                        .stream()
                        .map(archetype -> new ArchetypeItem(archetype,
                                                            getSelectionMap().get(archetype.getAlias()),
                                                            archetype.getAlias().equals(archetypePreferences.getDefaultSelection())))
                        .collect(Collectors.toList());

        archetypeListPresenter.setup(view.getTableBody(),
                                     archetypeItems,
                                     (property, presenter) -> presenter.setup(property,
                                                                              this));
        updateSelectionCounter();

        hideBusyIndicator();

        view.show(true);
    }

    private Map<String, Boolean> getSelectionMap() {
        return archetypePreferences.getArchetypeSelectionMap();
    }

    private Command getSavePreferencesSuccessCallback(final boolean updateList) {
        return () -> {
            hideBusyIndicator();

            if (updateList) {
                updateList();
            }
        };
    }

    protected Promise<Void> loadList(boolean isRefresh) {
        if (!isRefresh) {
            view.show(false);
        }

        showBusyIndicator(ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_Loading));

        return promises.create(
                (resolve, reject) -> archetypeService.call((final PaginatedArchetypeList paginatedList) -> {
                    setupCounters(paginatedList.getTotal());

                    configureView(paginatedList);

                    if (isRefresh) {
                        finishLoadList(paginatedList);
                        resolve.onInvoke(promises.resolve());
                    } else {
                        resolve.onInvoke(loadPreferences(paginatedList));
                    }
                }, new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).list(Math.max(0, currentPage - 1),
                                                                                     PAGE_SIZE,
                                                                                     searchFilter));
    }

    void setupCounters(final int totalArchetypes) {
        this.currentPage = Math.max(1, this.currentPage);
        if (totalArchetypes < (currentPage - 1) * PAGE_SIZE + 1) {
            currentPage = totalArchetypes / PAGE_SIZE;
        }

        final int offset = (currentPage - 1) * PAGE_SIZE;
        final int fromCount = totalArchetypes > 0 ? offset + 1 : offset;
        final int toCount = resolveCounter(totalArchetypes,
                                           offset + PAGE_SIZE);
        final int totalCount = resolveCounter(totalArchetypes,
                                              0);

        final String indicatorText = ts.format(ArchetypeManagementConstants.ArchetypeManagement_ItemCountIndicatorText,
                                               fromCount,
                                               toCount,
                                               totalCount);

        view.setPageIndicator(indicatorText);

        totalPages = (int) Math.ceil(totalArchetypes / (float) PAGE_SIZE);

        final String totalText = ts.format(ArchetypeManagementConstants.ArchetypeManagement_OfN,
                                           Math.max(totalPages, 1));
        view.setTotalPages(totalText);

        view.setCurrentPage(currentPage);

        checkPaginationButtons();
    }

    private void checkPaginationButtons() {
        boolean isPreviousButtonEnabled = currentPage > 1;
        boolean isNextButtonEnabled = currentPage < totalPages;

        view.enablePreviousButton(isPreviousButtonEnabled);
        view.enableNextButton(isNextButtonEnabled);

        view.enableFirstButton(isPreviousButtonEnabled);
        view.enableLastButton(isNextButtonEnabled);
    }

    private int resolveCounter(final int counter,
                               final int otherCounter) {
        if (counter < otherCounter || otherCounter == 0) {
            return counter;
        } else {
            return otherCounter;
        }
    }

    ArchetypeTableConfiguration getConfiguration() {
        if (configuration == null) {
            configuration = initConfiguration();
        }

        return configuration;
    }

    public interface View extends UberElemental<AbstractArchetypeTablePresenter> {

        void showAddAction(boolean isVisible);

        void showIncludeHeader(boolean isVisible);

        void showStatusHeader(boolean isVisible);

        Element getTableBody();

        void enablePreviousButton(boolean isEnabled);

        void enableNextButton(boolean isEnabled);

        void enableFirstButton(boolean isEnabled);

        void enableLastButton(boolean isEnabled);

        void setCurrentPage(int currentPage);

        void setTotalPages(String totalText);

        void setPageIndicator(String indicatorText);

        void setSelectionCounter(String counterText);

        void showSelectionCounter(boolean isVisible);

        void showNoResults(boolean isVisible);

        void showPagination(boolean isVisible);

        void showEmpty(boolean isVisible);

        void showToolbar(boolean isVisible);

        void show(boolean isVisible);
    }

    @Dependent
    public static class ArchetypeListPresenter extends ListPresenter<ArchetypeItem, ArchetypeItemPresenter> {

        @Inject
        public ArchetypeListPresenter(final ManagedInstance<ArchetypeItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}

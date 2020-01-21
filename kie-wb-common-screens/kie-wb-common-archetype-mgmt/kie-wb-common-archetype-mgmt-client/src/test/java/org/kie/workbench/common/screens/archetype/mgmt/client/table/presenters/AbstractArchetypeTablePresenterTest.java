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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.client.modal.AddArchetypeModalPresenter;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.config.ArchetypeTableConfiguration;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.item.model.ArchetypeItem;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.impl.GlobalArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeListOperation;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeStatus;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.PaginatedArchetypeList;
import org.kie.workbench.common.screens.archetype.mgmt.shared.preferences.ArchetypePreferences;
import org.kie.workbench.common.screens.archetype.mgmt.shared.services.ArchetypeService;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.promise.SyncPromises;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractArchetypeTablePresenterTest {

    private AbstractArchetypeTablePresenter presenter;

    @Mock
    private AbstractArchetypeTablePresenter.View view;

    @Mock
    private AbstractArchetypeTablePresenter.ArchetypeListPresenter archetypeListPresenter;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private TranslationService ts;

    @Mock
    private AddArchetypeModalPresenter addArchetypeModalPresenter;

    @Mock
    private ArchetypePreferences archetypePreferences;

    @Mock
    private ArchetypeService archetypeService;

    @Mock
    private PreferenceScopeFactory preferenceScopeFactory;

    private Promises promises;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User user;

    @Before
    public void setup() {
        promises = new SyncPromises();

        presenter = spy(new GlobalArchetypeTablePresenter(view,
                                                          archetypeListPresenter,
                                                          busyIndicatorView,
                                                          ts,
                                                          addArchetypeModalPresenter,
                                                          archetypePreferences,
                                                          new CallerMock<>(archetypeService),
                                                          preferenceScopeFactory,
                                                          promises,
                                                          authorizationManager,
                                                          user));
    }

    @Test
    public void onArchetypeListUpdatedEventWhenIsNotSetupTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("setup")).set(false);

        presenter.onArchetypeListUpdatedEvent(new ArchetypeListUpdatedEvent(ArchetypeListOperation.ADD));

        verify(archetypeService, never()).list(any(),
                                               any(),
                                               any());
    }

    @Test
    public void onArchetypeListUpdatedEventWhenIsSetupTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("setup")).set(true);

        doReturn(promises.resolve()).when(presenter).loadList(false);

        presenter.onArchetypeListUpdatedEvent(new ArchetypeListUpdatedEvent(ArchetypeListOperation.ADD));

        verify(presenter).loadList(false);
    }

    @Test
    public void showBusyIndicatorTest() {
        final String msg = "Loading";

        presenter.showBusyIndicator(msg);

        verify(busyIndicatorView).showBusyIndicator(msg);
    }

    @Test
    public void hideBusyIndicatorTest() {
        presenter.hideBusyIndicator();

        verify(busyIndicatorView).hideBusyIndicator();
    }

    @Test
    public void isEmptyShouldBeTrueWhenIsNotSetupTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("setup")).set(false);

        assertTrue(presenter.isEmpty());
    }

    @Test
    public void isEmptyWhenTrueTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("setup")).set(true);

        doReturn(Collections.emptyList()).when(archetypeListPresenter).getObjectsList();

        assertTrue(presenter.isEmpty());
    }

    @Test
    public void resetTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("setup")).set(true);

        presenter.reset();

        assertFalse(presenter.isSetup());
    }

    @Test
    public void isEmptyWhenFalseTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("setup")).set(true);

        doReturn(Collections.singletonList(mock(ArchetypeItem.class))).when(archetypeListPresenter).getObjectsList();

        assertFalse(presenter.isEmpty());
    }

    @Test
    public void setupWhenIsNotRefreshTest() {
        doReturn(promises.resolve()).when(presenter).loadList(false);

        presenter.setup(false, () -> {
        });

        verify(view).init(presenter);
        verify(presenter).loadList(false);
    }

    @Test
    public void setupWhenIsRefreshTest() {
        doReturn(promises.resolve()).when(presenter).loadList(true);

        presenter.setup(true, () -> {
        });

        verify(view, never()).init(presenter);
        verify(presenter).loadList(true);
    }

    @Test
    public void setCurrentPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        doNothing().when(presenter).updateList();

        presenter.setCurrentPage(5);

        assertEquals(5, presenter.currentPage);
        verify(presenter).updateList();
    }

    @Test
    public void setCurrentPageOutRangeTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage")).set(10);
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        presenter.setCurrentPage(50);

        assertEquals(10, presenter.currentPage);
    }

    @Test
    public void goToPreviousPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage")).set(5);
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        doNothing().when(presenter).updateList();

        presenter.goToPreviousPage();

        assertEquals(4, presenter.currentPage);
        verify(presenter).updateList();
    }

    @Test
    public void goToPreviousPageDoNothingTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage")).set(1);
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        presenter.goToPreviousPage();

        assertEquals(1, presenter.currentPage);
        verify(view, never()).setCurrentPage(anyInt());
        verify(presenter, never()).updateList();
    }

    @Test
    public void goToNextPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage")).set(1);
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        doNothing().when(presenter).updateList();

        presenter.goToNextPage();

        assertEquals(2, presenter.currentPage);
        verify(presenter).updateList();
    }

    @Test
    public void goToNextPageDoNothingTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage")).set(10);
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        presenter.goToNextPage();

        assertEquals(10, presenter.currentPage);
        verify(view, never()).setCurrentPage(anyInt());
        verify(presenter, never()).updateList();
    }

    @Test
    public void goToFirstPageTest() {
        doNothing().when(presenter).updateList();

        presenter.goToFirstPage();

        assertEquals(1, presenter.currentPage);
    }

    @Test
    public void goToLastPageTest() throws NoSuchFieldException {
        new FieldSetter(presenter, AbstractArchetypeTablePresenter.class.getDeclaredField("totalPages")).set(10);

        doNothing().when(presenter).updateList();

        presenter.goToLastPage();

        assertEquals(10, presenter.currentPage);
    }

    @Test
    public void searchTest() {
        final String searchFilter = "keyword";

        doNothing().when(presenter).updateList();

        presenter.search(searchFilter);

        assertEquals(searchFilter, presenter.searchFilter);
        assertEquals(1, presenter.currentPage);
    }

    @Test
    public void addArchetypeWhenIsNotAllowedTest() {
        doReturn(false).when(presenter).canMakeChanges();

        presenter.addArchetype();

        verify(addArchetypeModalPresenter, never()).show();
    }

    @Test
    public void addArchetypeWhenIsAllowedTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.addArchetype();

        verify(addArchetypeModalPresenter).show();
    }

    @Test
    public void getIncludedWhenEmptyTest() {
        doReturn(Collections.emptyMap()).when(archetypePreferences).getArchetypeSelectionMap();

        final List<String> result = presenter.getIncluded();

        assertEquals(0, result.size());
    }

    @Test
    public void getIncludedWhenAllEnabledTest() {
        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("archetype 1", true);
        selectionMap.put("archetype 2", true);
        selectionMap.put("archetype 3", true);
        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        final List<String> result = presenter.getIncluded();

        assertEquals(3, result.size());
    }

    @Test
    public void getIncludedWhenAllDisabledTest() {
        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("archetype 1", false);
        selectionMap.put("archetype 2", false);
        selectionMap.put("archetype 3", false);
        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        final List<String> result = presenter.getIncluded();

        assertEquals(0, result.size());
    }

    @Test
    public void getIncludedMixedTest() {
        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("archetype 1", true);
        selectionMap.put("archetype 2", false);
        selectionMap.put("archetype 3", true);
        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        final List<String> result = presenter.getIncluded();

        assertEquals(2, result.size());
    }

    @Test
    public void isShowIncludeColumnWhenIsTrueTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withIncludeColumn()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowIncludeColumn();

        assertTrue(result);
    }

    @Test
    public void isShowIncludeColumnWhenIsFalseTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder().build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowIncludeColumn();

        assertFalse(result);
    }

    @Test
    public void isShowStatusColumnWhenIsTrueTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withStatusColumn()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowStatusColumn();

        assertTrue(result);
    }

    @Test
    public void isShowStatusColumnWhenIsFalseTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder().build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowStatusColumn();

        assertFalse(result);
    }

    @Test
    public void isShowDeleteActionWhenIsTrueTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withDeleteAction()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowDeleteAction();

        assertTrue(result);
    }

    @Test
    public void isShowDeleteActionWhenIsFalseTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder().build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowDeleteAction();

        assertFalse(result);
    }

    @Test
    public void isShowValidateActionWhenIsTrueTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withValidateAction()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowValidateAction();

        assertTrue(result);
    }

    @Test
    public void isShowValidateActionWhenIsFalseTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder().build();

        doReturn(config).when(presenter).getConfiguration();

        final boolean result = presenter.isShowValidateAction();

        assertFalse(result);
    }

    @Test
    public void setSelectedWhenKeyIsNotPresentTest() {
        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("archetype 1", true);
        selectionMap.put("archetype 2", false);
        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        presenter.setSelected(createArchetypeItem(),
                              true);

        verify(view, never()).setSelectionCounter(anyString());
    }

    @Test
    public void setSelectedWhenKeyIsPresentTest() throws NoSuchFieldException {
        final Runnable callback = () -> {
        };
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("onChangedCallback"))
                .set(callback);

        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("myArchetype", true);
        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        presenter.setSelected(createArchetypeItem(),
                              true);

        verify(view).setSelectionCounter(anyString());
    }

    @Test
    public void updateSelectionCounterTest() {
        doReturn(Collections.nCopies(1, "archetype")).when(presenter).getIncluded();

        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("archetype 1", true);
        selectionMap.put("archetype 2", false);
        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();

        presenter.updateSelectionCounter();

        verify(view).setSelectionCounter(anyString());
    }

    @Test
    public void loadPreferencesSuccessCallbackTest() {
        final PaginatedArchetypeList paginatedArchetypeList = mock(PaginatedArchetypeList.class);

        doNothing().when(presenter).finishLoadList(paginatedArchetypeList);

        presenter.loadPreferencesSuccessCallback(paginatedArchetypeList,
                                                 mock(Promise.PromiseExecutorCallbackFn.ResolveCallbackFn.class))
                .execute(archetypePreferences);

        verify(presenter).finishLoadList(paginatedArchetypeList);
    }

    @Test
    public void loadPreferencesErrorCallbackTest() {
        presenter.loadPreferencesErrorCallback(mock(Promise.PromiseExecutorCallbackFn.RejectCallbackFn.class))
                .execute(mock(Throwable.class));

        verify(busyIndicatorView).hideBusyIndicator();
    }

    @Test
    public void savePreferencesByStrategyInfoWhenAllowedTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.savePreferences(mock(PreferenceScopeResolutionStrategyInfo.class),
                                  true).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences).save(any(PreferenceScopeResolutionStrategyInfo.class),
                                          any(),
                                          any());
    }

    @Test
    public void savePreferencesByStrategyInfoWhenNotAllowedTest() {
        doReturn(false).when(presenter).canMakeChanges();

        presenter.savePreferences(mock(PreferenceScopeResolutionStrategyInfo.class),
                                  true).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences, never()).save(any(PreferenceScopeResolutionStrategyInfo.class),
                                                   any(),
                                                   any());
    }

    @Test
    public void savePreferencesByScopeWhenIsAllowedTest() {
        doReturn(true).when(presenter).canMakeChanges();

        presenter.savePreferences(mock(PreferenceScope.class),
                                  true).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences).save(any(PreferenceScope.class),
                                          any(),
                                          any());
    }

    @Test
    public void savePreferencesByScopeWhenIsNotAllowedTest() {
        doReturn(false).when(presenter).canMakeChanges();

        presenter.savePreferences(mock(PreferenceScope.class),
                                  true).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(archetypePreferences, never()).save(any(PreferenceScope.class),
                                                   any(),
                                                   any());
    }

    @Test
    public void updateListTest() {
        doReturn(promises.resolve()).when(presenter).loadList(true);

        presenter.updateList();

        verify(presenter).loadList(true);
    }

    @Test
    public void finishLoadListTest() {
        final List<Archetype> archetypes = new ArrayList<>();
        archetypes.add(createArchetypeWithAlias("archetype 1"));
        archetypes.add(createArchetypeWithAlias("archetype 2"));
        archetypes.add(createArchetypeWithAlias("archetype 3"));

        final Map<String, Boolean> selectionMap = new HashMap<>();
        selectionMap.put("archetype 1", true);
        selectionMap.put("archetype 2", true);
        selectionMap.put("archetype 3", true);

        final PaginatedArchetypeList paginatedArchetypeList =
                new PaginatedArchetypeList(archetypes,
                                           0,
                                           10,
                                           3);

        doReturn(selectionMap).when(archetypePreferences).getArchetypeSelectionMap();
        doReturn("archetype 1").when(archetypePreferences).getDefaultSelection();

        presenter.finishLoadList(paginatedArchetypeList);

        verify(archetypeListPresenter).setup(any(),
                                             any(),
                                             any());
        verify(view).setSelectionCounter(anyString());
        verify(busyIndicatorView).hideBusyIndicator();
        verify(view).show(true);
    }

    @Test
    public void configureViewWhenEmptyTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withAddAction()
                .withIncludeColumn()
                .withStatusColumn()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final PaginatedArchetypeList paginatedArchetypeList = new PaginatedArchetypeList(Collections.emptyList(),
                                                                                         0,
                                                                                         0,
                                                                                         0);

        presenter.configureView(paginatedArchetypeList);

        verify(view).showAddAction(true);
        verify(view).showIncludeHeader(true);
        verify(view).showStatusHeader(true);
        verify(view).showSelectionCounter(true);

        verify(view).showNoResults(false);
        verify(view).showPagination(false);
        verify(view).showToolbar(false);
        verify(view).showEmpty(true);
    }

    @Test
    public void configureViewWhenSearchEmptyTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("searchFilter"))
                .set("keyword");

        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withAddAction()
                .withIncludeColumn()
                .withStatusColumn()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final PaginatedArchetypeList paginatedArchetypeList = new PaginatedArchetypeList(Collections.emptyList(),
                                                                                         0,
                                                                                         0,
                                                                                         0);

        presenter.configureView(paginatedArchetypeList);

        verify(view).showAddAction(true);
        verify(view).showIncludeHeader(true);
        verify(view).showStatusHeader(true);
        verify(view).showSelectionCounter(true);

        verify(view).showNoResults(true);
        verify(view).showPagination(false);
        verify(view).showToolbar(true);
        verify(view).showEmpty(false);
    }

    @Test
    public void configureViewWhenPopulatedTest() {
        final ArchetypeTableConfiguration config = new ArchetypeTableConfiguration.Builder()
                .withAddAction()
                .withIncludeColumn()
                .withStatusColumn()
                .build();

        doReturn(config).when(presenter).getConfiguration();

        final PaginatedArchetypeList paginatedArchetypeList =
                new PaginatedArchetypeList(Collections.nCopies(10, mock(Archetype.class)),
                                           0,
                                           10,
                                           10);

        presenter.configureView(paginatedArchetypeList);

        verify(view).showAddAction(true);
        verify(view).showIncludeHeader(true);
        verify(view).showStatusHeader(true);
        verify(view).showSelectionCounter(true);

        verify(view).showNoResults(false);
        verify(view).showPagination(true);
        verify(view).showToolbar(true);
        verify(view).showEmpty(false);
    }

    @Test
    public void getPreferencesTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("archetypePreferences"))
                .set(archetypePreferences);

        assertEquals(archetypePreferences, presenter.getPreferences());
    }

    @Test
    public void getViewTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("view"))
                .set(view);

        assertEquals(view, presenter.getView());
    }

    @Test
    public void setupCountersWhenEmptyListTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage"))
                .set(10);

        presenter.setupCounters(0);

        assertEquals(0, presenter.currentPage);
        assertEquals(0, presenter.totalPages);

        verify(view).setPageIndicator(anyString());
        verify(view).setTotalPages(anyString());
        verify(view).setCurrentPage(0);
        verify(view).enablePreviousButton(false);
        verify(view).enableNextButton(false);
        verify(view).enableFirstButton(false);
        verify(view).enableLastButton(false);
    }

    @Test
    public void setupCountersWhenPopulatedListTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage"))
                .set(1);

        presenter.setupCounters(100);

        assertEquals(1, presenter.currentPage);
        assertEquals(10, presenter.totalPages);

        verify(view).setPageIndicator(anyString());
        verify(view).setTotalPages(anyString());
        verify(view).setCurrentPage(1);
        verify(view).enablePreviousButton(false);
        verify(view).enableNextButton(true);
        verify(view).enableFirstButton(false);
        verify(view).enableLastButton(true);
    }

    @Test
    public void setupCountersWhenInLastPageListTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage"))
                .set(10);

        presenter.setupCounters(100);

        assertEquals(10, presenter.currentPage);
        assertEquals(10, presenter.totalPages);

        verify(view).enablePreviousButton(true);
        verify(view).enableNextButton(false);
        verify(view).enableFirstButton(true);
        verify(view).enableLastButton(false);
    }

    @Test
    public void setupCountersWhenInFirstPageListTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage"))
                .set(1);

        presenter.setupCounters(100);

        assertEquals(1, presenter.currentPage);
        assertEquals(10, presenter.totalPages);

        verify(view).enablePreviousButton(false);
        verify(view).enableNextButton(true);
        verify(view).enableFirstButton(false);
        verify(view).enableLastButton(true);
    }

    @Test
    public void setupCountersWhenInMiddlePageListTest() throws NoSuchFieldException {
        new FieldSetter(presenter,
                        AbstractArchetypeTablePresenter.class.getDeclaredField("currentPage"))
                .set(5);

        presenter.setupCounters(100);

        assertEquals(5, presenter.currentPage);
        assertEquals(10, presenter.totalPages);

        verify(view).enablePreviousButton(true);
        verify(view).enableNextButton(true);
        verify(view).enableFirstButton(true);
        verify(view).enableLastButton(true);
    }

    private ArchetypeItem createArchetypeItem() {
        final Archetype archetype = createArchetypeWithAlias("myArchetype");
        return new ArchetypeItem(archetype,
                                 true,
                                 true);
    }

    private Archetype createArchetypeWithAlias(final String alias) {
        return new Archetype(alias,
                             mock(GAV.class),
                             new Date(),
                             ArchetypeStatus.VALID);
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.preferences.client.central.tree;

import java.util.ArrayList;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;
import org.uberfire.mocks.CallerMock;
import org.uberfire.preferences.shared.PreferenceScope;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TreeHierarchyStructurePresenterTest {

    @Mock
    private TreeHierarchyStructurePresenter.View view;

    @Mock
    private PreferenceBeanServerStore preferenceBeanServerStore;
    private Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller;

    @Mock
    private ManagedInstance<TreeHierarchyInternalItemPresenter> treeHierarchyInternalItemPresenterProvider;

    @Mock
    private ManagedInstance<TreeHierarchyLeafItemPresenter> treeHierarchyLeafItemPresenterProvider;

    @Mock
    private Event<HierarchyItemFormInitializationEvent> hierarchyItemFormInitializationEvent;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private PreferenceBeanStore store;

    @Mock
    private Event<NotificationEvent> notification;

    @Mock
    private PreferenceFormBeansInfo preferenceFormBeansInfo;

    private TreeHierarchyStructurePresenter presenter;

    @Before
    public void setup() {
        preferenceBeanServerStoreCaller = new CallerMock<>(preferenceBeanServerStore);

        presenter = spy(new TreeHierarchyStructurePresenter(view,
                                                            preferenceBeanServerStoreCaller,
                                                            treeHierarchyInternalItemPresenterProvider,
                                                            treeHierarchyLeafItemPresenterProvider,
                                                            hierarchyItemFormInitializationEvent,
                                                            placeManager,
                                                            store,
                                                            notification,
                                                            preferenceFormBeansInfo));

        doNothing().when(presenter).setupHierarchyItem(any());
        doReturn(new ArrayList<>()).when(presenter).getPreferencesToSave(any());
    }

    @Test
    public void initWithCustomScopeTest() {
        final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategyInfo = mock(PreferenceScopeResolutionStrategyInfo.class);

        presenter.init("identifier",
                       customScopeResolutionStrategyInfo,
                       null);

        verify(preferenceBeanServerStore).buildHierarchyStructureForPreference("identifier",
                                                                               customScopeResolutionStrategyInfo);
        verify(presenter).setupHierarchyItem(any());
        verify(view).init(presenter);
    }

    @Test
    public void initWithDefaultScopeResolutionStrategyTest() {
        presenter.init("identifier",
                       null,
                       null);

        verify(preferenceBeanServerStore).buildHierarchyStructureForPreference("identifier");
        verify(presenter).setupHierarchyItem(any());
        verify(view).init(presenter);
    }

    @Test
    public void saveWithCustomScopeTest() {
        final PreferenceScope scope = mock(PreferenceScope.class);
        final PreferencesCentralSaveEvent saveEvent = mock(PreferencesCentralSaveEvent.class);

        presenter.init("identifier",
                       null,
                       scope);

        presenter.saveEvent(saveEvent);

        verify(store).save(anyCollection(),
                           eq(scope),
                           any(),
                           any());
    }

    @Test
    public void saveWithCustomScopeResolutionStrategyTest() {
        final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategyInfo = mock(PreferenceScopeResolutionStrategyInfo.class);
        final PreferencesCentralSaveEvent saveEvent = mock(PreferencesCentralSaveEvent.class);

        presenter.init("identifier",
                       customScopeResolutionStrategyInfo,
                       null);

        presenter.saveEvent(saveEvent);

        verify(store).save(anyCollection(),
                           eq(customScopeResolutionStrategyInfo),
                           any(),
                           any());
    }

    @Test
    public void saveWithDefaultScopeResolutionStrategyTest() {
        final PreferencesCentralSaveEvent saveEvent = mock(PreferencesCentralSaveEvent.class);

        presenter.init("identifier",
                       null,
                       null);

        presenter.saveEvent(saveEvent);

        verify(store).save(anyCollection(),
                           any(),
                           any());
    }
}

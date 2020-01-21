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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.sections.archetypes;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.ArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.library.api.settings.SpaceScreenModel;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypesSectionPresenterTest {

    private ArchetypesSectionPresenter presenter;

    @Mock
    private Event<SettingsSectionChange<SpaceScreenModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<SpaceScreenModel> menuItem;

    private Promises promises;

    @Mock
    private ArchetypesSectionPresenter.View view;

    @Mock
    private ArchetypeTablePresenter archetypeTablePresenter;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private TranslationService ts;

    @Before
    public void setup() {
        promises = new SyncPromises();

        presenter = spy(new ArchetypesSectionPresenter(settingsSectionChangeEvent,
                                                       menuItem,
                                                       promises,
                                                       view,
                                                       archetypeTablePresenter,
                                                       notificationEvent,
                                                       ts));
    }

    @Test
    public void onArchetypeListUpdatedEventTest() {
        doReturn(true).when(archetypeTablePresenter).isSetup();

        final ArchetypeListUpdatedEvent event = mock(ArchetypeListUpdatedEvent.class);
        presenter.onArchetypeListUpdatedEvent(event);

        verify(settingsSectionChangeEvent).fire(any());
        verify(notificationEvent).fire(any());
    }

    @Test
    public void onArchetypeListUpdatedEventWhenTableIsNotSetup() {
        doReturn(false).when(archetypeTablePresenter).isSetup();

        final ArchetypeListUpdatedEvent event = mock(ArchetypeListUpdatedEvent.class);
        presenter.onArchetypeListUpdatedEvent(event);

        verify(settingsSectionChangeEvent, never()).fire(any());
        verify(notificationEvent, never()).fire(any());
    }

    @Test
    public void setupTest() {
        doReturn(promises.resolve()).when(archetypeTablePresenter).setup(eq(false),
                                                                         any());

        presenter.setup(any(SpaceScreenModel.class));

        verify(view).init(presenter);
    }

    @Test
    public void saveTest() {
        presenter.save(anyString(),
                       any());

        verify(archetypeTablePresenter).savePreferences(true);
    }
}

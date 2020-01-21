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

package org.kie.workbench.common.screens.archetype.mgmt.client.screens;

import javax.enterprise.event.Event;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.ArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeListOperation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypeManagementScreenPresenterTest {

    private ArchetypeManagementScreenPresenter presenter;

    @Mock
    private ArchetypeManagementScreenPresenter.View view;

    @Mock
    private TranslationService ts;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    @Mock
    private ArchetypeTablePresenter archetypeTablePresenter;

    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();

        presenter = spy(new ArchetypeManagementScreenPresenter(view,
                                                               ts,
                                                               notificationEvent,
                                                               archetypeTablePresenter,
                                                               promises));
    }

    @Test
    public void getViewTest() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void getTitleTest() {
        final String title = "title";

        doReturn(title).when(ts)
                .getTranslation(ArchetypeManagementConstants.ArchetypeManagement_ArchetypeManagementPerspectiveName);

        assertEquals(title, presenter.getTitle());
    }

    @Test
    public void onOpenTest() {
        doReturn(promises.resolve()).when(archetypeTablePresenter).setup(eq(false),
                                                                         any());

        presenter.onOpen();

        verify(archetypeTablePresenter).setup(eq(false),
                                              any());
    }

    @Test
    public void onCloseTest() {
        presenter.onClose();

        verify(archetypeTablePresenter).reset();
    }

    @Test
    public void onArchetypeListUpdatedEventWhenTableIsSetupTest() {
        doReturn(false).when(archetypeTablePresenter).isSetup();

        presenter.onArchetypeListUpdatedEvent(mock(ArchetypeListUpdatedEvent.class));

        verify(presenter, never()).notifyListUpdated(any());
    }

    @Test
    public void onArchetypeListUpdatedEventWhenTableIsNotSetupTest() {
        doReturn(true).when(archetypeTablePresenter).isSetup();
        doNothing().when(presenter).notifyListUpdated(any());

        presenter.onArchetypeListUpdatedEvent(mock(ArchetypeListUpdatedEvent.class));

        verify(presenter).notifyListUpdated(any());
    }

    @Test
    public void fireNotificationWhenArchetypeAddedTest() {
        fireNotificationOnArchetypeListUpdatedEvent(ArchetypeListOperation.ADD,
                                                    "Add",
                                                    ArchetypeManagementConstants.ArchetypeManagement_ArchetypeAddedMessage);
    }

    @Test
    public void fireNotificationWhenArchetypeDeletedTest() {
        fireNotificationOnArchetypeListUpdatedEvent(ArchetypeListOperation.DELETE,
                                                    "Delete",
                                                    ArchetypeManagementConstants.ArchetypeManagement_ArchetypeDeletedMessage);
    }

    @Test
    public void fireNotificationWhenArchetypeValidatedTest() {
        fireNotificationOnArchetypeListUpdatedEvent(ArchetypeListOperation.VALIDATE,
                                                    "Validate",
                                                    ArchetypeManagementConstants.ArchetypeManagement_ArchetypeValidatedMessage);
    }

    private void fireNotificationOnArchetypeListUpdatedEvent(final ArchetypeListOperation operation,
                                                             final String notificationMsg,
                                                             final String tsKey) {
        doReturn(notificationMsg).when(ts).getTranslation(tsKey);
        doReturn(true).when(archetypeTablePresenter).isSetup();

        presenter.onArchetypeListUpdatedEvent(new ArchetypeListUpdatedEvent(operation));

        verify(notificationEvent).fire(new NotificationEvent(notificationMsg,
                                                             NotificationEvent.NotificationType.SUCCESS));
    }
}
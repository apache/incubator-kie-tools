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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.resources.i18n.ArchetypeManagementConstants;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.ArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.ArchetypeListOperation;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
@WorkbenchScreen(identifier = ArchetypeManagementScreenPresenter.IDENTIFIER)
public class ArchetypeManagementScreenPresenter {

    public static final String IDENTIFIER = "ArchetypeManagementScreen";

    private final View view;
    private final TranslationService ts;
    private final Event<NotificationEvent> notificationEvent;
    private final ArchetypeTablePresenter archetypeTablePresenter;
    private final Promises promises;

    @Inject
    public ArchetypeManagementScreenPresenter(final View view,
                                              final TranslationService ts,
                                              final Event<NotificationEvent> notificationEvent,
                                              final ArchetypeTablePresenter archetypeTablePresenter,
                                              final Promises promises) {
        this.view = view;
        this.ts = ts;
        this.notificationEvent = notificationEvent;
        this.archetypeTablePresenter = archetypeTablePresenter;
        this.promises = promises;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ts.getTranslation(ArchetypeManagementConstants.ArchetypeManagement_ArchetypeManagementPerspectiveName);
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @OnOpen
    public void onOpen() {
        loadScreen();
    }

    @OnClose
    public void onClose() {
        archetypeTablePresenter.reset();
    }

    public void onArchetypeListUpdatedEvent(@Observes final ArchetypeListUpdatedEvent event) {
        if (archetypeTablePresenter.isSetup()) {
            notifyListUpdated(event.getOperation());
        }
    }

    public void notifyListUpdated(final ArchetypeListOperation operation) {
        final String translationKey;
        switch (operation) {
            case ADD:
                translationKey = ArchetypeManagementConstants.ArchetypeManagement_ArchetypeAddedMessage;
                break;
            case DELETE:
                translationKey = ArchetypeManagementConstants.ArchetypeManagement_ArchetypeDeletedMessage;
                break;
            case VALIDATE:
                translationKey = ArchetypeManagementConstants.ArchetypeManagement_ArchetypeValidatedMessage;
                break;
            default:
                throw new UnsupportedOperationException();
        }

        notificationEvent.fire(new NotificationEvent(ts.getTranslation(translationKey),
                                                     NotificationEvent.NotificationType.SUCCESS));
    }

    private void loadScreen() {
        archetypeTablePresenter.setup(false,
                                      () -> {
                                      }).then(v -> {
            view.setContent(archetypeTablePresenter.getView().getElement());
            return promises.resolve();
        });
    }

    public interface View extends UberElemental<ArchetypeManagementScreenPresenter> {

        void setContent(HTMLElement element);
    }
}

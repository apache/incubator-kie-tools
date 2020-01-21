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

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.archetype.mgmt.client.table.presenters.ArchetypeTablePresenter;
import org.kie.workbench.common.screens.archetype.mgmt.shared.events.ArchetypeListUpdatedEvent;
import org.kie.workbench.common.screens.library.api.settings.SpaceScreenModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.annotation.SpaceSettings;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.uberfire.client.promise.Promises;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
public class ArchetypesSectionPresenter extends Section<SpaceScreenModel> {

    private final View view;
    private final ArchetypeTablePresenter archetypeTablePresenter;
    private final Event<NotificationEvent> notificationEvent;
    private final TranslationService ts;

    @Inject
    public ArchetypesSectionPresenter(final Event<SettingsSectionChange<SpaceScreenModel>> settingsSectionChangeEvent,
                                      final MenuItem<SpaceScreenModel> menuItem,
                                      final Promises promises,
                                      final View view,
                                      final @SpaceSettings ArchetypeTablePresenter archetypeTablePresenter,
                                      final Event<NotificationEvent> notificationEvent,
                                      final TranslationService ts) {
        super(settingsSectionChangeEvent,
              menuItem,
              promises);

        this.view = view;
        this.archetypeTablePresenter = archetypeTablePresenter;
        this.notificationEvent = notificationEvent;
        this.ts = ts;
    }

    public void onArchetypeListUpdatedEvent(@Observes final ArchetypeListUpdatedEvent event) {
        if (archetypeTablePresenter.isSetup()) {
            fireResetEvent();
            notificationEvent.fire(
                    new NotificationEvent(ts.getTranslation(LibraryConstants.ArchetypeListUpdatedMessage),
                                          NotificationEvent.NotificationType.INFO));
        }
    }

    @Override
    public Promise<Void> setup(final SpaceScreenModel model) {
        this.view.init(this);

        return archetypeTablePresenter.setup(false,
                                             this::fireChangeEvent).then(v -> {
            view.showDescription(!archetypeTablePresenter.isEmpty());
            view.setTable(archetypeTablePresenter.getView().getElement());
            return promises.resolve();
        });
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {
        archetypeTablePresenter.savePreferences(true);
        return promises.resolve();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return archetypeTablePresenter.getPreferences().hashCode();
    }

    public interface View extends SectionView<ArchetypesSectionPresenter> {

        void setTable(HTMLElement element);

        void showDescription(boolean isVisible);
    }
}

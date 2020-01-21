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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.api.settings.SpaceScreenModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.annotation.SpaceSettings;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChangeType;
import org.kie.workbench.common.screens.library.client.settings.sections.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.stream.Collectors.toList;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Dependent
public class SettingsScreenPresenter implements HasBusyIndicator {

    private final View view;
    private final TranslationService ts;
    private final OrganizationalUnitController organizationalUnitController;
    private final WorkspaceProjectContext projectContext;
    private final BusyIndicatorView busyIndicatorView;
    private final SectionManager<SpaceScreenModel> sectionManager;
    private final SettingsSections settingsSections;
    private final Promises promises;
    private final Event<NotificationEvent> notificationEvent;

    @Inject
    public SettingsScreenPresenter(final View view,
                                   final TranslationService ts,
                                   final OrganizationalUnitController organizationalUnitController,
                                   final WorkspaceProjectContext projectContext,
                                   final BusyIndicatorView busyIndicatorView,
                                   final SectionManager<SpaceScreenModel> sectionManager,
                                   final @SpaceSettings SettingsSections settingsSections,
                                   final Promises promises,
                                   final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.ts = ts;
        this.organizationalUnitController = organizationalUnitController;
        this.projectContext = projectContext;
        this.busyIndicatorView = busyIndicatorView;
        this.sectionManager = sectionManager;
        this.settingsSections = settingsSections;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void postConstruct() {
        sectionManager.init(settingsSections.getList(),
                            view.getMenuItemsContainer(),
                            view.getContentContainer());
    }

    public Promise<Void> setupUsingCurrentSection() {
        this.view.init(this);

        showBusyIndicator(ts.getTranslation(LibraryConstants.Loading));

        view.enableActions(canUpdate());

        return setupSections(new SpaceScreenModel()).then(o -> {
            hideBusyIndicator();

            if (sectionManager.manages(sectionManager.getCurrentSection())) {
                return sectionManager.goToCurrentSection();
            } else {
                return sectionManager.goToFirstAvailable();
            }
        }).catch_(o -> promises.catchOrExecute(o, e -> {
            hideBusyIndicator();
            return promises.reject(e);
        }, i -> {
            notificationEvent.fire(new NotificationEvent(ts.getTranslation(LibraryConstants.SettingsLoadError), ERROR));
            hideBusyIndicator();
            return promises.resolve();
        }));
    }

    public View getView() {
        return view;
    }

    private OrganizationalUnit getOrganizationalUnit() {
        return projectContext.getActiveOrganizationalUnit()
                .orElseThrow(() -> new IllegalStateException("Cannot get library info without an active organizational unit."));
    }

    private boolean canUpdate() {
        return organizationalUnitController.canUpdateOrgUnit(getOrganizationalUnit());
    }

    @Override
    public void showBusyIndicator(final String message) {
        busyIndicatorView.showBusyIndicator(message);
    }

    @Override
    public void hideBusyIndicator() {
        busyIndicatorView.hideBusyIndicator();
    }

    public void onSettingsSectionChanged(@Observes final SettingsSectionChange<SpaceScreenModel> settingsSectionChange) {
        if (sectionManager.manages(settingsSectionChange.getSection())) {
            if (settingsSectionChange.getType() == SettingsSectionChangeType.CHANGE) {
                sectionManager.updateDirtyIndicator(settingsSectionChange.getSection());
            } else if (settingsSectionChange.getType() == SettingsSectionChangeType.RESET) {
                setupSection(new SpaceScreenModel(),
                             settingsSectionChange.getSection());
            }
        }
    }

    public void save() {
        if (canUpdate()) {
            sectionManager.validateAll().then(i -> {
                save(""); // Save with comments might be useful in the future when we have more sections
                return promises.resolve();
            }).catch_(o -> promises.catchOrExecute(o, e -> {
                hideBusyIndicator();
                return promises.reject(e);
            }, (final Section<SpaceScreenModel> section) -> {
                hideBusyIndicator();
                return sectionManager.goTo(section);
            }));
        }
    }

    public void reset() {
        setupUsingCurrentSection();
    }

    private void save(final String comment) {
        promises.reduceLazilyChaining(getSavingSteps(comment), this::executeSavingStep)
                .catch_(o -> promises.catchOrExecute(o, promises::reject, sectionManager::goTo));
    }

    private List<SettingsScreenPresenter.SavingStep> getSavingSteps(final String comment) {
        final Stream<SettingsScreenPresenter.SavingStep> saveSectionsSteps =
                sectionManager.getSections().stream().map(section -> chain -> section.save(comment, chain));

        final Stream<SettingsScreenPresenter.SavingStep> commonSavingSteps =
                Stream.of(chain -> saveSpaceScreenModel(),
                          chain -> sectionManager.resetAllDirtyIndicators(),
                          chain -> displaySuccessMessage());

        return Stream.concat(saveSectionsSteps, commonSavingSteps).collect(toList());
    }

    private Promise<Void> executeSavingStep(final Supplier<Promise<Void>> chain,
                                            final SettingsScreenPresenter.SavingStep savingStep) {

        return savingStep.execute(chain);
    }

    private Promise<Void> displaySuccessMessage() {
        hideBusyIndicator();
        notificationEvent.fire(new NotificationEvent(getSaveSuccessMessage(), SUCCESS));
        return promises.resolve();
    }

    Promise<Object> setupSections(final SpaceScreenModel model) {
        final List<Section<SpaceScreenModel>> sections = new ArrayList<>(sectionManager.getSections());

        return promises.all(sections, (final Section<SpaceScreenModel> section) -> setupSection(model, section)).then(i -> {
            if (sectionManager.isEmpty()) {
                return promises.reject("No sections available");
            } else {
                return promises.resolve();
            }
        });
    }

    private Promise<Void> saveSpaceScreenModel() {
        hideBusyIndicator();
        return promises.resolve();
    }

    Promise<Object> setupSection(final SpaceScreenModel model,
                                 final Section<SpaceScreenModel> section) {

        return section.setup(model)
                .then(i -> {
                    sectionManager.resetDirtyIndicator(section);
                    return promises.resolve();
                }).catch_(e -> {
                    sectionManager.remove(section);
                    notificationEvent.fire(new NotificationEvent(getSectionSetupErrorMessage(section), WARNING));
                    return promises.resolve();
                });
    }

    private String getSectionSetupErrorMessage(final Section<SpaceScreenModel> section) {
        return ts.format(LibraryConstants.SettingsSectionSetupError, section.getView().getTitle());
    }

    private String getSaveSuccessMessage() {
        return ts.format(LibraryConstants.SettingsSaveSuccess);
    }

    @FunctionalInterface
    private interface SavingStep {

        Promise<Void> execute(final Supplier<Promise<Void>> chain);
    }

    public interface View extends UberElement<SettingsScreenPresenter> {

        void enableActions(boolean isEnabled);

        HTMLElement getMenuItemsContainer();

        HTMLElement getContentContainer();
    }
}

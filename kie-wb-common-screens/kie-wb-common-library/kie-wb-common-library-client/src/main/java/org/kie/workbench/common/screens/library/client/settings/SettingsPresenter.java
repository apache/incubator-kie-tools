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

package org.kie.workbench.common.screens.library.client.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.sections.SettingsSections;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionManager;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.stream.Collectors.toList;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Dependent
public class SettingsPresenter {

    public interface View extends UberElemental<SettingsPresenter>,
                                  HasBusyIndicator {

        void showBusyIndicator();

        HTMLElement getMenuItemsContainer();

        String getSaveSuccessMessage();

        String getLoadErrorMessage();

        String getSectionSetupErrorMessage(final String title);

        void show();

        void hide();

        HTMLElement getContentContainer();
    }

    private final View view;
    private final Promises promises;
    private final Event<NotificationEvent> notificationEvent;
    private final SettingsSections settingsSections;
    private final SavePopUpPresenter savePopUpPresenter;

    private final Caller<ProjectScreenService> projectScreenService;
    private final WorkspaceProjectContext projectContext;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    private final SectionManager<ProjectScreenModel> sectionManager;

    private ObservablePath pathToPom;

    ObservablePath.OnConcurrentUpdateEvent concurrentPomUpdateInfo = null;
    ProjectScreenModel model;

    @Inject
    public SettingsPresenter(final View view,
                             final Promises promises,
                             final Event<NotificationEvent> notificationEvent,
                             final @Customizable SettingsSections settingsSections,
                             final SavePopUpPresenter savePopUpPresenter,
                             final Caller<ProjectScreenService> projectScreenService,
                             final WorkspaceProjectContext projectContext,
                             final ManagedInstance<ObservablePath> observablePaths,
                             final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                             final SectionManager<ProjectScreenModel> sectionManager) {
        this.view = view;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
        this.settingsSections = settingsSections;
        this.savePopUpPresenter = savePopUpPresenter;

        this.projectScreenService = projectScreenService;
        this.projectContext = projectContext;
        this.observablePaths = observablePaths;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.sectionManager = sectionManager;
    }

    @PostConstruct
    public void setup() {
        sectionManager.init(settingsSections.getList(),
                            view.getMenuItemsContainer(),
                            view.getContentContainer());

        setupUsingCurrentSection();
    }

    void setupUsingCurrentSection() {
        view.init(this);

        if (!projectContext.getActiveModule().isPresent()) {
            return;
        }

        view.showBusyIndicator();

        if (pathToPom != null) {
            pathToPom.dispose();
        }

        concurrentPomUpdateInfo = null;

        pathToPom = observablePaths.get().wrap(
                projectContext.getActiveModule().orElseThrow(() -> new RuntimeException("Can't get active module")).getPomXMLPath());

        pathToPom.onConcurrentUpdate(info -> concurrentPomUpdateInfo = info);

        promises.promisify(projectScreenService, s -> {
            return s.load(pathToPom);
        }).then(model -> {
            this.model = model;
            return setupSections(model);
        }).then(i -> {
            view.hideBusyIndicator();
            if (sectionManager.manages(sectionManager.getCurrentSection())) {
                return sectionManager.goToCurrentSection();
            } else {
                return sectionManager.goToFirstAvailable();
            }
        }).catch_(e -> promises.catchOrExecute(e, this::defaultErrorResolution, i -> {
            notificationEvent.fire(new NotificationEvent(view.getLoadErrorMessage(), ERROR));
            view.hideBusyIndicator();
            return promises.resolve();
        }));
    }

    Promise<Object> setupSections(final ProjectScreenModel model) {

        // Sections can be removed inside setupSection method, so we create
        // a new ArrayList containing a copy of the original sections
        final List<Section<ProjectScreenModel>> sections = new ArrayList<>(sectionManager.getSections());

        return promises.all(sections, (final Section<ProjectScreenModel> section) -> setupSection(model, section)).then(i -> {
            if (sectionManager.isEmpty()) {
                return promises.reject("No sections available");
            } else {
                return promises.resolve();
            }
        });
    }

    Promise<Object> setupSection(final ProjectScreenModel model,
                                 final Section<ProjectScreenModel> section) {

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

    String getSectionSetupErrorMessage(final Section<ProjectScreenModel> section) {
        return view.getSectionSetupErrorMessage(section.getView().getTitle());
    }

    public void showSaveModal() {
        sectionManager.validateAll().then(i -> {
            savePopUpPresenter.show(this::save);
            return promises.resolve();
        }).catch_(e -> promises.catchOrExecute(e, this::defaultErrorResolution, (final Section<ProjectScreenModel> section) -> {
            view.hideBusyIndicator();
            return sectionManager.goTo(section);
        }));
    }

    void save(final String comment) {
        promises.reduceLazilyChaining(getSavingSteps(comment), this::executeSavingStep)
                .catch_(e -> promises.catchOrExecute(e, this::defaultErrorResolution, sectionManager::goTo));
    }

    private Promise<Void> executeSavingStep(final Supplier<Promise<Void>> chain,
                                            final SavingStep savingStep) {

        return savingStep.execute(chain);
    }

    private List<SavingStep> getSavingSteps(final String comment) {

        final Stream<SavingStep> saveSectionsSteps =
                sectionManager.getSections().stream().map(section -> chain -> section.save(comment, chain));

        final Stream<SavingStep> commonSavingSteps =
                Stream.of(chain -> saveProjectScreenModel(comment, DeploymentMode.VALIDATED, chain),
                          chain -> sectionManager.resetAllDirtyIndicators(),
                          chain -> displaySuccessMessage());

        return Stream.concat(saveSectionsSteps, commonSavingSteps).collect(toList());
    }

    Promise<Void> displaySuccessMessage() {
        view.hideBusyIndicator();
        notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(), SUCCESS));
        return promises.resolve();
    }

    Promise<Void> saveProjectScreenModel(final String comment,
                                         final DeploymentMode mode,
                                         final Supplier<Promise<Void>> chain) {

        if (concurrentPomUpdateInfo != null) {
            handlePomConcurrentUpdate(comment, chain);
            return promises.reject(sectionManager.getCurrentSection());
        }

        return promises.promisify(projectScreenService, s -> {
            return s.save(pathToPom, model, comment, mode);
        }).then(workspaceProject -> {
            projectContext.updateProjectModule(workspaceProject.getMainModule());
            return promises.resolve();
        }).catch_(e -> promises.catchOrExecute(e, this::defaultErrorResolution, (final Promises.Error<Message> error) -> {
            DomGlobal.console.info(e);
            return handleSaveProjectScreenModelError(comment, chain, error.getThrowable());
        }));
    }

    Promise<Void> handleSaveProjectScreenModelError(final String comment,
                                                    final Supplier<Promise<Void>> chain,
                                                    final Throwable throwable) {

        if (throwable instanceof GAVAlreadyExistsException) {
            return handlePomConcurrentUpdate(comment, chain, (GAVAlreadyExistsException) throwable);
        } else {
            return defaultErrorResolution(throwable);
        }
    }

    void handlePomConcurrentUpdate(final String comment,
                                   final Supplier<Promise<Void>> chain) {

        newConcurrentUpdate(concurrentPomUpdateInfo.getPath(),
                            concurrentPomUpdateInfo.getIdentity(),
                            () -> forceSave(comment, chain),
                            () -> {
                            },
                            this::reset).show();
    }

    Promise<Void> handlePomConcurrentUpdate(final String comment,
                                            final Supplier<Promise<Void>> saveChain,
                                            final GAVAlreadyExistsException exception) {

        view.hideBusyIndicator();

        conflictingRepositoriesPopup.setContent(
                model.getPOM().getGav(),
                exception.getRepositories(),
                () -> forceSave(comment, saveChain));

        conflictingRepositoriesPopup.show();
        return promises.reject(sectionManager.getCurrentSection());
    }

    void forceSave(final String comment,
                   final Supplier<Promise<Void>> chain) {

        concurrentPomUpdateInfo = null;
        conflictingRepositoriesPopup.hide();
        saveProjectScreenModel(comment, DeploymentMode.FORCED, chain).then(i -> chain.get());
    }

    Promise<Void> defaultErrorResolution(final Throwable e) {
        new DefaultErrorCallback().error(null, e);
        view.hideBusyIndicator();
        return promises.resolve();
    }

    public void onSettingsSectionChanged(@Observes final SettingsSectionChange<ProjectScreenModel> settingsSectionChange) {

        if (!sectionManager.manages(settingsSectionChange.getSection())) {
            return;
        }

        sectionManager.updateDirtyIndicator(settingsSectionChange.getSection());
    }

    public boolean mayClose() {
        return !sectionManager.hasDirtySections();
    }

    public void reset() {
        setupUsingCurrentSection();
    }

    public View getView() {
        return view;
    }

    @FunctionalInterface
    private interface SavingStep {

        Promise<Void> execute(final Supplier<Promise<Void>> chain);
    }
}

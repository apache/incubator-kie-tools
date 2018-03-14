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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
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
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.uberfire.annotations.Customizable;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.DefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.stream.Collectors.toList;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@ApplicationScoped
public class SettingsPresenter {

    public interface View extends UberElemental<SettingsPresenter>,
                                  HasBusyIndicator {

        void showBusyIndicator();

        void setSection(final Section contentView);

        HTMLElement getMenuItemsContainer();

        String getSaveSuccessMessage();

        String getLoadErrorMessage();

        String getSectionSetupErrorMessage(final String title);

        void show();

        void hide();

        interface Section<T> extends UberElemental<T>,
                                     IsElement {

            String getTitle();
        }
    }

    private final View view;
    private final Promises promises;
    private final Event<NotificationEvent> notificationEvent;
    private final SettingsSections settingsSections;
    private final SavePopUpPresenter savePopUpPresenter;

    private final Caller<ProjectScreenService> projectScreenService;
    private final WorkspaceProjectContext projectContext;
    private final MenuItemsListPresenter menuItemsListPresenter;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private ObservablePath pathToPom;
    ObservablePath.OnConcurrentUpdateEvent concurrentPomUpdateInfo = null;

    ProjectScreenModel model;
    Section currentSection;
    Map<Section, Integer> originalHashCodes;
    List<Section> sections;

    @Inject
    public SettingsPresenter(final View view,
                             final Promises promises,
                             final Event<NotificationEvent> notificationEvent,
                             final @Customizable SettingsSections settingsSections,
                             final SavePopUpPresenter savePopUpPresenter,
                             final Caller<ProjectScreenService> projectScreenService,
                             final WorkspaceProjectContext projectContext,
                             final MenuItemsListPresenter menuItemsListPresenter,
                             final ManagedInstance<ObservablePath> observablePaths,
                             final ConflictingRepositoriesPopup conflictingRepositoriesPopup) {
        this.view = view;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
        this.settingsSections = settingsSections;
        this.savePopUpPresenter = savePopUpPresenter;

        this.projectScreenService = projectScreenService;
        this.projectContext = projectContext;
        this.menuItemsListPresenter = menuItemsListPresenter;
        this.observablePaths = observablePaths;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
    }

    @PostConstruct
    public void setup() {
        sections = new ArrayList<>(settingsSections.getList());
        currentSection = sections.get(0);
        setActiveMenuItem();
    }

    protected void setActiveMenuItem() {
        currentSection.getMenuItem().getView().getElement().classList.add("active");
    }

    public Promise<Void> setup(final Section activeSection) {
        currentSection = activeSection;

        view.init(this);
        view.showBusyIndicator();

        if (pathToPom != null) {
            pathToPom.dispose();
        }

        originalHashCodes = new HashMap<>();
        concurrentPomUpdateInfo = null;

        pathToPom = observablePaths.get()
                .wrap(projectContext.getActiveModule().orElseThrow(() -> new RuntimeException("Can't get active module"))
                              .getPomXMLPath());
        pathToPom.onConcurrentUpdate(info -> concurrentPomUpdateInfo = info);

        return promises.promisify(projectScreenService,
                                  s -> {
                                      return s.load(pathToPom);
                                  }).then(model -> {
            this.model = model;
            return setupSections(model);
        }).then(i -> {
            setupMenuItems();
            view.hideBusyIndicator();
            if (sections.contains(currentSection)) {
                return goTo(currentSection);
            } else {
                return goTo(sections.get(0));
            }
        }).catch_(e -> promises.catchOrExecute(e,
                                               this::defaultErrorResolution,
                                               i -> {
                                                   notificationEvent.fire(new NotificationEvent(view.getLoadErrorMessage(),
                                                                                                ERROR));
                                                   view.hideBusyIndicator();
                                                   return promises.resolve();
                                               }));
    }

    void setupMenuItems() {
        menuItemsListPresenter.setupWithPresenters(
                view.getMenuItemsContainer(),
                sections.stream().map(Section::getMenuItem).collect(toList()),
                (section, presenter) -> presenter.setup(section, this));
    }

    Promise<Object> setupSections(final ProjectScreenModel model) {
        // Sections can be removed inside setupSection method, so we create
        // a new ArrayList containing a copy of the original sections
        final List<Section> sections = new ArrayList<>(this.sections);

        final Promise<Object> setupResult = promises.all(sections, (final Section section) -> setupSection(model, section));

        if (this.sections.isEmpty()) {
            return promises.reject("No sections available");
        }

        return setupResult;
    }

    Promise<Object> setupSection(final ProjectScreenModel model,
                                 final Section section) {

        return promises.resolve().then(ignore -> section.setup(model)).catch_(e -> {
            sections.remove(section);
            notificationEvent.fire(new NotificationEvent(getSectionSetupErrorMessage(section), WARNING));
            return promises.reject(e);
        }).then(i -> {
            section.getMenuItem().setup(section, this);
            resetDirtyIndicator(section);
            return promises.resolve();
        }).catch_(e -> {
            DomGlobal.console.info(e);
            return promises.resolve();
        });
    }

    String getSectionSetupErrorMessage(final Section section) {
        return view.getSectionSetupErrorMessage(section.getView().getTitle());
    }

    public void showSaveModal() {
        promises.reduceLazily(sections, Section::validate).then(i -> {
            savePopUpPresenter.show(this::save);
            return promises.resolve();
        }).catch_(e -> promises.catchOrExecute(e, this::defaultErrorResolution, (final Section section) -> {
            view.hideBusyIndicator();
            return goTo(section);
        }));
    }

    void save(final String comment) {
        promises.reduceLazilyChaining(getSavingSteps(comment), this::executeSavingStep)
                .catch_(e -> promises.catchOrExecute(e, this::defaultErrorResolution, this::goTo));
    }

    private Promise<Void> executeSavingStep(final Supplier<Promise<Void>> chain,
                                            final SavingStep savingStep) {

        return savingStep.execute(chain);
    }

    private List<SavingStep> getSavingSteps(final String comment) {

        final Stream<SavingStep> saveSectionsSteps =
                sections.stream().map(section -> chain -> section.save(comment, chain));

        final Stream<SavingStep> commonSavingSteps =
                Stream.of(chain -> saveProjectScreenModel(comment, DeploymentMode.VALIDATED, chain),
                          chain -> promises.all(sections, this::resetDirtyIndicator),
                          chain -> displaySuccessMessage());

        return Stream.concat(saveSectionsSteps, commonSavingSteps).collect(toList());
    }

    Promise<Void> displaySuccessMessage() {
        view.hideBusyIndicator();
        notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(), SUCCESS));
        return promises.resolve();
    }

    Promise<Void> resetDirtyIndicator(final Section section) {
        originalHashCodes.put(section, section.currentHashCode());
        updateDirtyIndicator(section);
        return promises.resolve();
    }

    Promise<Void> saveProjectScreenModel(final String comment,
                                         final DeploymentMode mode,
                                         final Supplier<Promise<Void>> chain) {

        if (concurrentPomUpdateInfo != null) {
            handlePomConcurrentUpdate(comment, chain);
            return promises.reject(currentSection);
        }

        return promises.promisify(projectScreenService,
                                  s -> {
                                      return s.save(pathToPom,
                                                    model,
                                                    comment,
                                                    mode);
                                  })
                .then(ret -> {
                    projectContext.updateProjectModule(ret.getMainModule());
                    return promises.resolve();
                })
                .catch_(e -> promises.catchOrExecute(e,
                                                     this::defaultErrorResolution,
                                                     (final Promises.Error<Message> error) -> {
                                                         return handleSaveProjectScreenModelError(comment,
                                                                                                  chain,
                                                                                                  error.getThrowable());
                                                     }));
    }

    Promise<Void> handleSaveProjectScreenModelError(final String comment,
                                                    final Supplier<Promise<Void>> chain,
                                                    final Throwable throwable) {

        if (throwable instanceof GAVAlreadyExistsException) {
            return handlePomConcurrentUpdate(comment,
                                             chain,
                                             (GAVAlreadyExistsException) throwable);
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
        return promises.reject(currentSection);
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

    public void onSettingsSectionChanged(@Observes final SettingsSectionChange settingsSectionChange) {
        updateDirtyIndicator(settingsSectionChange.getSection());
    }

    void updateDirtyIndicator(final Section changedSection) {

        final boolean isDirty = Optional.ofNullable(originalHashCodes.get(changedSection))
                .map(originalHashCode -> !originalHashCode.equals(changedSection.currentHashCode()))
                .orElse(false);

        changedSection.setDirty(isDirty);
    }

    @OnOpen
    public void onOpen() {
        view.hide();
        setup(currentSection).then(i -> {
            view.show();
            return promises.resolve();
        });
    }

    public void reset() {
        setup(currentSection);
    }

    Promise<Void> goTo(final Section section) {
        currentSection = section;
        view.setSection(section.getView());
        return promises.resolve();
    }

    public View getView() {
        return view;
    }

    @Dependent
    public static class MenuItemsListPresenter extends ListPresenter<Section, MenuItem> {

        @Inject
        public MenuItemsListPresenter(final ManagedInstance<MenuItem> itemPresenters) {
            super(itemPresenters);
        }
    }

    public static abstract class Section {

        protected final Promises promises;
        private final Event<SettingsSectionChange> settingsSectionChangeEvent;
        private final SettingsPresenter.MenuItem menuItem;

        protected Section(final Event<SettingsSectionChange> settingsSectionChangeEvent,
                          final MenuItem menuItem,
                          final Promises promises) {

            this.promises = promises;
            this.settingsSectionChangeEvent = settingsSectionChangeEvent;
            this.menuItem = menuItem;
        }

        public abstract View.Section getView();

        public abstract int currentHashCode();

        public Promise<Void> save(final String comment, final Supplier<Promise<Void>> chain) {
            return promises.resolve();
        }

        public Promise<Object> validate() {
            return promises.resolve();
        }

        public Promise<Void> setup(final ProjectScreenModel model) {
            return promises.resolve();
        }

        public void fireChangeEvent() {
            settingsSectionChangeEvent.fire(new SettingsSectionChange(this));
        }

        public SettingsPresenter.MenuItem getMenuItem() {
            return menuItem;
        }

        public void setDirty(final boolean dirty) {
            menuItem.markAsDirty(dirty);
        }
    }

    @Dependent
    public static class MenuItem extends ListItemPresenter<Section, SettingsPresenter, SettingsPresenter.MenuItem.View> {

        private final SettingsPresenter.MenuItem.View view;

        private Section section;
        private SettingsPresenter settingsPresenter;

        @Inject
        public MenuItem(final SettingsPresenter.MenuItem.View view) {
            super(view);
            this.view = view;
        }

        public void showSection() {
            settingsPresenter.goTo(section);
        }

        public void markAsDirty(final boolean dirty) {
            view.markAsDirty(dirty);
        }

        @Override
        public MenuItem setup(final Section section,
                              final SettingsPresenter settingsPresenter) {

            this.section = section;
            this.settingsPresenter = settingsPresenter;

            this.view.init(this);
            this.view.setLabel(section.getView().getTitle());

            return this;
        }

        @Override
        public Section getObject() {
            return section;
        }

        public interface View extends UberElementalListItem<MenuItem>,
                                      IsElement {

            void setLabel(final String label);

            void markAsDirty(final boolean dirty);
        }
    }

    @FunctionalInterface
    private interface SavingStep {

        Promise<Void> execute(final Supplier<Promise<Void>> chain);
    }
}

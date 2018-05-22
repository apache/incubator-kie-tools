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

package org.kie.workbench.common.screens.library.client.settings.sections.generalsettings;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.generalsettings.GitUrlsPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.client.promise.Promises;

public class GeneralSettingsPresenter extends Section<ProjectScreenModel> {

    public interface View extends SectionView<GeneralSettingsPresenter> {

        String getName();

        String getDescription();

        String getGroupId();

        String getArtifactId();

        void setGitUrlsView(GitUrlsPresenter.View gitUrlsView);

        String getVersion();

        Boolean getConflictingGAVCheckDisabled();

        Boolean getChildGavEditEnabled();

        void setName(String name);

        void setDescription(String description);

        void setGroupId(String groupId);

        void setArtifactId(String artifactId);

        void setVersion(String version);

        void showError(String message);

        void setConflictingGAVCheckDisabled(boolean value);

        void setChildGavEditEnabled(boolean value);

        void hideError();

        String getEmptyNameMessage();

        String getInvalidNameMessage();

        String getEmptyGroupIdMessage();

        String getInvalidGroupIdMessage();

        String getEmptyArtifactIdMessage();

        String getInvalidArtifactIdMessage();

        String getEmptyVersionMessage();

        String getInvalidVersionMessage();

        String getDuplicatedProjectNameMessage();
    }



    private final View view;
    private final Caller<ValidationService> validationService;
    private final Caller<WorkspaceProjectService> projectService;
    private final GAVPreferences gavPreferences;
    private final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;
    private final GitUrlsPresenter gitUrlsPresenter;
    private final LibraryPlaces libraryPlaces;

    POM pom;

    @Inject
    public GeneralSettingsPresenter(final View view,
                                    final Promises promises,
                                    final MenuItem<ProjectScreenModel> menuItem,
                                    final Caller<ValidationService> validationService,
                                    final Caller<WorkspaceProjectService> projectService,
                                    final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                    final GAVPreferences gavPreferences,
                                    final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier,
                                    final GitUrlsPresenter gitUrlsPresenter,
                                    final LibraryPlaces libraryPlaces) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.validationService = validationService;
        this.projectService = projectService;
        this.gavPreferences = gavPreferences;
        this.projectScopedResolutionStrategySupplier = projectScopedResolutionStrategySupplier;
        this.gitUrlsPresenter = gitUrlsPresenter;
        this.libraryPlaces = libraryPlaces;
    }

    // Save

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        pom = model.getPOM();

        view.init(this);
        view.setName(pom.getName());
        view.setDescription(pom.getDescription());
        view.setGroupId(pom.getGav().getGroupId());
        view.setArtifactId(pom.getGav().getArtifactId());
        view.setVersion(pom.getGav().getVersion());

        gitUrlsPresenter.setup(model.getGitUrls());
        view.setGitUrlsView(gitUrlsPresenter.getView());

        return promises.create((resolve, reject) -> {
            gavPreferences.load(projectScopedResolutionStrategySupplier.get(), gavPreferences -> {
                view.setConflictingGAVCheckDisabled(gavPreferences.isConflictingGAVCheckDisabled());
                view.setChildGavEditEnabled(gavPreferences.isChildGAVEditEnabled());

                resolve.onInvoke(promises.resolve());
            }, reject::onInvoke);
        });
    }

    @Override
    public Promise<Object> validate() {
        view.hideError();

        return promises.all(

                validateStringIsNotEmpty(pom.getName(), view.getEmptyNameMessage())
                        .then(o -> executeValidation(s -> s.isProjectNameValid(pom.getName()), view.getInvalidNameMessage()))
                        .then(o -> executeValidation(projectService,
                                                     s -> s.spaceHasNoProjectsWithName(libraryPlaces.getActiveWorkspaceContext().getOrganizationalUnit(),
                                                                                       pom.getName()),
                                                     view.getDuplicatedProjectNameMessage()))
                        .catch_(this::showErrorAndReject),

                validateStringIsNotEmpty(pom.getGav().getGroupId(), view.getEmptyGroupIdMessage())
                        .then(o -> executeValidation(s -> s.validateGroupId(pom.getGav().getGroupId()), view.getInvalidGroupIdMessage()))
                        .catch_(this::showErrorAndReject),

                validateStringIsNotEmpty(pom.getGav().getArtifactId(), view.getEmptyArtifactIdMessage())
                        .then(o -> executeValidation(s -> s.validateArtifactId(pom.getGav().getArtifactId()), view.getInvalidArtifactIdMessage()))
                        .catch_(this::showErrorAndReject),

                validateStringIsNotEmpty(pom.getGav().getVersion(), view.getEmptyVersionMessage())
                        .then(o -> executeValidation(s -> s.validateGAVVersion(pom.getGav().getVersion()), view.getInvalidVersionMessage()))
                        .catch_(this::showErrorAndReject)
        );
    }

    Promise<Object> showErrorAndReject(final Object o) {
        return promises.catchOrExecute(o, e -> {
            view.showError(e.getMessage());
            return promises.reject(this);
        }, (final String errorMessage) -> {
            view.showError(errorMessage);
            return promises.reject(this);
        });
    }

    Promise<Boolean> validateStringIsNotEmpty(final String string,
                                              final String errorMessage) {

        return promises.create((resolve, reject) -> {
            if (string == null || string.isEmpty()) {
                reject.onInvoke(errorMessage);
            } else {
                resolve.onInvoke(true);
            }
        });
    }

    <T> Promise<Boolean> executeValidation(final Caller<T> caller,
                                           final Function<T, Boolean> call,
                                           final String errorMessage) {

        return promises
                .promisify(caller, call)
                .then(valid -> valid ? promises.resolve(true) : promises.reject(errorMessage));
    }

    Promise<Boolean> executeValidation(final Function<ValidationService, Boolean> call,
                                       final String errorMessage) {

        return executeValidation(validationService,
                                 call,
                                 errorMessage);
    }

    void setVersion(final String version) {
        pom.getGav().setVersion(version);
        fireChangeEvent();
    }

    void setArtifactId(final String artifactId) {
        pom.getGav().setArtifactId(artifactId);
        fireChangeEvent();
    }

    void setGroupId(final String groupId) {
        pom.getGav().setGroupId(groupId);
        fireChangeEvent();
    }

    void setDescription(final String description) {
        pom.setDescription(description);
        fireChangeEvent();
    }

    void setName(final String name) {
        pom.setName(name);
        fireChangeEvent();
    }

    void disableGavConflictCheck(final boolean value) {
        gavPreferences.setConflictingGAVCheckDisabled(value);
        fireChangeEvent();
    }

    void allowChildGavEdition(final boolean value) {
        gavPreferences.setChildGAVEditEnabled(value);
        fireChangeEvent();
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        return promises.create((resolve, reject) -> {
            gavPreferences.save(projectScopedResolutionStrategySupplier.get(),
                                () -> resolve.onInvoke(promises.resolve()),
                                (throwable) -> reject.onInvoke(this));
        });
    }

    @Override
    public int currentHashCode() {
        return pom.hashCode() +
                (gavPreferences.isChildGAVEditEnabled() ? 1 : 2) +
                (gavPreferences.isConflictingGAVCheckDisabled() ? 4 : 8);
    }

    public GitUrlsPresenter getGitUrlsPresenter() {
        return gitUrlsPresenter;
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }
}

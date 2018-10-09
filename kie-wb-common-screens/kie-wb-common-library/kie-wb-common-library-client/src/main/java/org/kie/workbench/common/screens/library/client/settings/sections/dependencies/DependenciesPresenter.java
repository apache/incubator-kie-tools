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

package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.List;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.Dependency;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.DependencySelectorPopup;
import org.kie.workbench.common.screens.projecteditor.client.forms.dependencies.EnhancedDependenciesManager;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependencies;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.uberfire.client.promise.Promises;

import static java.util.stream.Collectors.toList;

public class DependenciesPresenter extends Section<ProjectScreenModel>  {

    public interface View extends SectionView<DependenciesPresenter> {

        void add(DependenciesItemPresenter.View itemView);

        void setItems(List<DependenciesItemPresenter.View> itemViews);
    }

    private final View view;
    private final DependencySelectorPopup dependencySelectorPopup;
    private final EnhancedDependenciesManager enhancedDependenciesManager;
    private final ManagedInstance<DependenciesItemPresenter> presenters;

    private int currentHashCode = 0;

    ProjectScreenModel model;

    @Inject
    public DependenciesPresenter(final View view,
                                 final Promises promises,
                                 final MenuItem<ProjectScreenModel> menuItem,
                                 final DependencySelectorPopup dependencySelectorPopup,
                                 final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                 final EnhancedDependenciesManager enhancedDependenciesManager,
                                 final ManagedInstance<DependenciesItemPresenter> presenters) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.dependencySelectorPopup = dependencySelectorPopup;
        this.enhancedDependenciesManager = enhancedDependenciesManager;
        this.presenters = presenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        this.model = model;

        view.init(this);

        dependencySelectorPopup.addSelectionHandler(gav -> {
            final Dependency dependency = new Dependency(gav);
            dependency.setScope("compile");
            add(dependency);
        });

        return promises.create((resolve, reject) -> {
            enhancedDependenciesManager.init(model.getPOM(), dependencies -> {
                updateHashCode(dependencies);
                view.setItems(buildDependencyViews(model, dependencies));
                resolve.onInvoke(promises.resolve());
                fireChangeEvent();
            });

            enhancedDependenciesManager.update();
        });
    }

    @Override
    public Promise<Object> validate() {
        enhancedDependenciesManager.validateDependency();
        return promises.resolve();
    }

    private void updateHashCode(final EnhancedDependencies enhancedDependencies) {
        currentHashCode = enhancedDependencies.asList().hashCode() + model.getWhiteList().hashCode();
    }

    private List<DependenciesItemPresenter.View> buildDependencyViews(final ProjectScreenModel model,
                                                                      final EnhancedDependencies dependencies) {
        return dependencies.asList().stream()
                .map(dependency -> presenters.get().setup(dependency, model.getWhiteList(), this).getView())
                .collect(toList());
    }

    void add(final Dependency dependency) {
        enhancedDependenciesManager.addNew(dependency);
    }

    public void addNewDependency() {
        add(new Dependency());
        fireChangeEvent();
    }

    public void addAllToWhiteList(final Set<String> packages) {
        model.getWhiteList().addAll(packages);
        enhancedDependenciesManager.update();
    }

    public void removeAllFromWhiteList(final Set<String> packages) {
        model.getWhiteList().removeAll(packages);
        enhancedDependenciesManager.update();
    }

    public void addFromRepository() {
        dependencySelectorPopup.show();
    }

    public void remove(final EnhancedDependency enhancedDependency) {
        enhancedDependenciesManager.delete(enhancedDependency);
    }

    @Override
    public int currentHashCode() {
        return currentHashCode;
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }
}

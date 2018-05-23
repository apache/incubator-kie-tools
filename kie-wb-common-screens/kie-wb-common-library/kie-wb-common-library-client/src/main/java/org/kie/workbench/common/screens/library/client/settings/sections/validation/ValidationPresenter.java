/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.validation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.client.promise.Promises;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class ValidationPresenter extends Section<ProjectScreenModel>  {

    private final View view;
    private final ValidationListPresenter validationItemPresenters;

    private ModuleRepositories repositories;

    public interface View extends SectionView<ValidationPresenter> {

        Element getRepositoriesTable();
    }

    @Inject
    public ValidationPresenter(final ValidationPresenter.View view,
                               final Promises promises,
                               final MenuItem<ProjectScreenModel> menuItem,
                               final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                               final ValidationListPresenter validationItemPresenters) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.validationItemPresenters = validationItemPresenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        repositories = model.getRepositories();

        view.init(this);

        validationItemPresenters.setup(
                view.getRepositoriesTable(),
                repositories.getRepositories().stream().sorted(comparing(r -> r.getMetadata().getId())).collect(toList()),
                (repository, presenter) -> presenter.setup(repository, this));

        return promises.resolve();
    }

    @Override
    public int currentHashCode() {
        return repositories.getRepositories().hashCode();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Dependent
    public static class ValidationListPresenter extends ListPresenter<ModuleRepositories.ModuleRepository, ValidationItemPresenter> {

        @Inject
        public ValidationListPresenter(final ManagedInstance<ValidationItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}

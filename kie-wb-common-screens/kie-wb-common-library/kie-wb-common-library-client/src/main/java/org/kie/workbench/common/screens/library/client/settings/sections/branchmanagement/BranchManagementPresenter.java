/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.branchmanagement;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.util.sections.SectionView;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOption;
import org.kie.workbench.common.widgets.client.widget.ListPresenter;
import org.uberfire.client.promise.Promises;

import static java.util.stream.Collectors.toList;

public class BranchManagementPresenter extends Section<ProjectScreenModel> {

    public interface View extends SectionView<BranchManagementPresenter> {

        void showError(String message);

        void hideError();

        HTMLElement getBranchesSelectContainer();

        Element getRoleAccessTable();

        void showEmptyState();
    }

    private final View view;
    private final Caller<LibraryService> libraryService;
    private final LibraryPlaces libraryPlaces;
    private final KieSelectElement branchesSelect;
    private final RoleAccessListPresenter roleAccessListPresenter;
    private final ProjectController projectController;

    String selectedBranch;
    Map<String, BranchPermissions> branchPermissionsByBranch = new HashMap<>();

    @Inject
    public BranchManagementPresenter(final View view,
                                     final Promises promises,
                                     final MenuItem<ProjectScreenModel> menuItem,
                                     final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                     final Caller<LibraryService> libraryService,
                                     final LibraryPlaces libraryPlaces,
                                     final KieSelectElement branchesSelect,
                                     final RoleAccessListPresenter roleAccessListPresenter,
                                     final ProjectController projectController) {
        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.libraryService = libraryService;
        this.libraryPlaces = libraryPlaces;
        this.branchesSelect = branchesSelect;
        this.roleAccessListPresenter = roleAccessListPresenter;
        this.projectController = projectController;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {
        return projectController.getUpdatableBranches(libraryPlaces.getActiveWorkspace()).then(branches -> {
            view.init(this);

            if (branches.isEmpty()) {
                view.showEmptyState();
                return promises.resolve();
            }

            selectedBranch = libraryPlaces.getActiveWorkspace().getBranch().getName();

            branchesSelect.setup(branches.stream().map(Branch::getName).sorted(String::compareToIgnoreCase).map(p -> new KieSelectOption(p, p)).collect(toList()),
                                 selectedBranch,
                                 this::setBranch);

            return setup(libraryPlaces.getActiveWorkspace().getBranch().getName());
        });
    }

    Promise<Void> setup(final String branch) {
        this.selectedBranch = branch;

        if (branchPermissionsByBranch.containsKey(selectedBranch)) {
            setupRolesTable(branchPermissionsByBranch.get(selectedBranch));
            return promises.resolve();
        }

        return promises.promisify(libraryService, service -> {
            return service.loadBranchPermissions(libraryPlaces.getActiveSpace().getName(),
                                                 libraryPlaces.getActiveWorkspace().getRepository().getIdentifier(),
                                                 selectedBranch);
        }).then(result -> {
            branchPermissionsByBranch.put(selectedBranch, result);
            setupRolesTable(result);

            return promises.resolve();
        });
    }

    private void setupRolesTable(final BranchPermissions branchPermissions) {
        roleAccessListPresenter.setup(
                view.getRoleAccessTable(),
                branchPermissions.getPermissionsByRole().values().stream().sorted((o1, o2) -> o1.getRoleName().compareToIgnoreCase(o2.getRoleName())).collect(toList()),
                (property, presenter) -> presenter.setup(property, this));
    }

    @Override
    public Promise<Object> validate() {
        view.hideError();
        return promises.resolve();
    }

    void setBranch(final String branchName) {
        selectedBranch = branchName;
        setup(branchName);
        fireChangeEvent();
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {
        return promises.all(branchPermissionsByBranch.entrySet().stream().map(entry -> promises.promisify(libraryService, service -> {
            service.saveBranchPermissions(libraryPlaces.getActiveSpace().getName(),
                                          libraryPlaces.getActiveWorkspace().getRepository().getIdentifier(),
                                          entry.getKey(),
                                          entry.getValue());
        })).toArray(Promise[]::new));
    }

    @Override
    public int currentHashCode() {
        return branchPermissionsByBranch.hashCode();
    }

    @Override
    public SectionView<?> getView() {
        return view;
    }

    @Dependent
    public static class RoleAccessListPresenter extends ListPresenter<RolePermissions, RoleItemPresenter> {

        @Inject
        public RoleAccessListPresenter(final ManagedInstance<RoleItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}

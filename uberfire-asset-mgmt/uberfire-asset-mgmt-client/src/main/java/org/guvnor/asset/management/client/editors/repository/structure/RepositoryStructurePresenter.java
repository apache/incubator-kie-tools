/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.asset.management.client.editors.repository.structure;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModuleRow;
import org.guvnor.asset.management.client.editors.project.structure.widgets.ProjectModulesView;
import org.guvnor.asset.management.client.editors.project.structure.widgets.RepositoryStructureDataView;
import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.context.ProjectContextChangeHandler;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.ProjectWizard;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnFocus;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.guvnor.asset.management.client.editors.repository.structure.QueryDeletePopup.showDeletePopup;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentDelete;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentRename;

@Dependent
@WorkbenchScreen(identifier = "repositoryStructureScreen")
public class RepositoryStructurePresenter
        implements RepositoryStructureView.Presenter,
                   RepositoryStructureDataView.Presenter,
                   ProjectModulesView.Presenter,
                   ProjectContextChangeHandler {

    private RepositoryStructureView view;

    private Caller<POMService> pomService;

    private Caller<RepositoryStructureService> repositoryStructureService;

    private Event<ProjectContextChangeEvent> contextChangeEvent;

    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    private PlaceManager placeManager;

    private ProjectContext projectContext;

    private ProjectWizard wizard;

    private RepositoryStructureModel model;

    private ActionHistory history = new ActionHistory();

    private RepositoryStructureContext repositoryStructureContext = new RepositoryStructureContext();

    private ObservablePath pathToRepositoryStructure;

    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;

    private ListDataProvider<ProjectModuleRow> dataProvider = new ListDataProvider<ProjectModuleRow>();

    private RepositoryStructureMenu menus;

    private RepositoryManagedStatusUpdater repositoryManagedStatusUpdater;

    private RepositoryStructureTitle repositoryStructureTitle;

    public RepositoryStructurePresenter() {
        //Zero-parameter constructor for CDI proxies
    }

    @Inject
    public RepositoryStructurePresenter(final RepositoryStructureView view,
                                        final Caller<POMService> pomService,
                                        final Caller<RepositoryStructureService> repositoryStructureService,
                                        final RepositoryStructureTitle repositoryStructureTitle,
                                        final Event<ProjectContextChangeEvent> contextChangeEvent,
                                        final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                        final RepositoryStructureMenu menus,
                                        final PlaceManager placeManager,
                                        final ProjectContext projectContext,
                                        final ProjectWizard wizard,
                                        final RepositoryManagedStatusUpdater repositoryManagedStatusUpdater) {
        this.view = view;
        this.pomService = pomService;
        this.repositoryStructureService = repositoryStructureService;
        this.repositoryStructureTitle = repositoryStructureTitle;
        this.contextChangeEvent = contextChangeEvent;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.menus = menus;
        this.placeManager = placeManager;
        this.projectContext = projectContext;
        this.wizard = wizard;
        this.repositoryManagedStatusUpdater = repositoryManagedStatusUpdater;
        this.repositoryManagedStatusUpdater.bind(view,
                                                 history,
                                                 this);

        projectContext.addChangeHandler(this);

        view.setPresenter(this);
        view.getModulesView().setPresenter(this);
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        repositoryStructureTitle.init(placeRequest);
        makeMenuBar();
        processContextChange();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.RepositoryStructure();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view.asWidget();
    }

    @OnClose
    public void onClose() {
        concurrentUpdateSessionInfo = null;
        if (pathToRepositoryStructure != null) {
            pathToRepositoryStructure.dispose();
        }
    }

    @OnFocus
    public void onFocus() {
        //workaround.
        dataProvider.flush();
        dataProvider.refresh();
    }

    @Override
    public void onChange() {
        processContextChange();
    }

    private void processContextChange() {
        if (projectContext.getActiveRepository() == null) {
            clearView();
            view.setModulesViewVisible(false);
            enableActions(false);
        } else {

            final boolean repoOrBranchChanged = repositoryStructureContext.repositoryOrBranchChanged(projectContext.getActiveRepository(),
                                                                                                     projectContext.getActiveBranch());

            if (repoOrBranchChanged || repositoryStructureContext.activeProjectChanged(projectContext.getActiveProject())) {
                if (repoOrBranchChanged || history.alreadyUpToDate(projectContext.getActiveProject())) {
                    loadModel(projectContext.getActiveRepository(),
                              projectContext.getActiveBranch());
                }
                history.reset();
                repositoryStructureContext.reset(projectContext.getActiveRepository(),
                                                 projectContext.getActiveBranch(),
                                                 projectContext.getActiveProject());
            }
        }
    }

    @Override
    public void setModel(final RepositoryStructureModel model) {
        this.model = model;
        dataProvider.getList().clear();
        if (pathToRepositoryStructure != null) {
            destroyObservablePath(pathToRepositoryStructure);
        }
        concurrentUpdateSessionInfo = null;

        final boolean initialized = updateView();

        addStructureChangeListeners();
        updateEditorTitle(initialized);
    }

    private boolean updateView() {
        if (model == null) {
            this.model = new RepositoryStructureModel();
            view.setDataPresenterMode(RepositoryStructureDataView.ViewMode.CREATE_STRUCTURE);
            view.getModulesView().setMode(ProjectModulesView.ViewMode.MODULES_VIEW);
            initDataPresenter();
            view.setModulesViewVisible(false);

            menus.enableAssetsManagementMenu(false);
            pathToRepositoryStructure = null;

            return false;
        } else if (model.isMultiModule()) {
            view.setDataPresenterMode(RepositoryStructureDataView.ViewMode.EDIT_MULTI_MODULE_PROJECT);
            view.getModulesView().setMode(ProjectModulesView.ViewMode.MODULES_VIEW);
            initDataPresenter();
            view.setModulesViewVisible(true);

            pathToRepositoryStructure = createObservablePath(model.getPathToPOM());

            updateModulesList(model.getModules());
            menus.enableAssetsManagementMenu(true);

            return true;
        } else if (model.isSingleProject()) {
            view.setDataPresenterMode(RepositoryStructureDataView.ViewMode.EDIT_SINGLE_MODULE_PROJECT);
            view.getModulesView().setMode(ProjectModulesView.ViewMode.PROJECTS_VIEW);
            initDataPresenter();
            view.setModulesViewVisible(false);

            pathToRepositoryStructure = createObservablePath(model.getOrphanProjects().get(0).getPomXMLPath());
            menus.enableAssetsManagementMenu(true);

            return true;
        } else {
            view.setDataPresenterMode(RepositoryStructureDataView.ViewMode.EDIT_UNMANAGED_REPOSITORY);
            view.getModulesView().setMode(ProjectModulesView.ViewMode.PROJECTS_VIEW);
            view.setModulesViewVisible(true);

            menus.enableAssetsManagementMenu(false);
            initDataPresenter();
            updateProjectsList(model.getOrphanProjects());
            return true;
        }
    }

    private void initDataPresenter() {
        if (model == null) {
            return;
        }

        if (model.getPathToPOM() != null) {
            view.setDataPresenterModel(model.getPOM().getGav());
        } else if (model.isSingleProject()) {
            final Project project = model.getOrphanProjects().get(0);
            final POM pom = model.getOrphanProjectsPOM().get(project.getIdentifier());
            if (pom != null) {
                if (pom.getGav() != null) {
                    view.setDataPresenterModel(pom.getGav());
                }
            }
        }
    }

    //Package-protected to override for Unit Tests
    ObservablePath createObservablePath(final Path path) {
        return IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(path);
    }

    //Package-protected to override for Unit Tests
    void destroyObservablePath(final ObservablePath path) {
        path.dispose();
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        loadModel(projectContext.getActiveRepository(),
                  projectContext.getActiveBranch());
    }

    private void updateEditorTitle(final boolean initialized) {
        repositoryStructureTitle.updateEditorTitle(model,
                                                   initialized);
    }

    private void addStructureChangeListeners() {
        if (pathToRepositoryStructure != null) {

            pathToRepositoryStructure.onConcurrentUpdate(new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
                @Override
                public void execute(final ObservablePath.OnConcurrentUpdateEvent eventInfo) {
                    concurrentUpdateSessionInfo = eventInfo;
                }
            });

            pathToRepositoryStructure.onConcurrentRename(new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
                @Override
                public void execute(final ObservablePath.OnConcurrentRenameEvent info) {
                    newConcurrentRename(info.getSource(),
                                        info.getTarget(),
                                        info.getIdentity(),
                                        new Command() {
                                            @Override
                                            public void execute() {
                                                enableActions(false);
                                            }
                                        },
                                        new Command() {
                                            @Override
                                            public void execute() {
                                                reload();
                                            }
                                        }
                    ).show();
                }
            });

            pathToRepositoryStructure.onConcurrentDelete(new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
                @Override
                public void execute(final ObservablePath.OnConcurrentDelete info) {
                    newConcurrentDelete(info.getPath(),
                                        info.getIdentity(),
                                        new Command() {
                                            @Override
                                            public void execute() {
                                                enableActions(false);
                                            }
                                        },
                                        new Command() {
                                            @Override
                                            public void execute() {
                                                placeManager.closePlace("repositoryStructureScreen");
                                            }
                                        }
                    ).show();
                }
            });
        }
    }

    private void updateModulesList(final List<String> modules) {
        if (modules != null) {
            for (String module : model.getModules()) {
                dataProvider.getList().add(new ProjectModuleRow(module));
            }
        }
    }

    private void updateProjectsList(final List<Project> projects) {
        if (projects != null) {
            for (Project project : projects) {
                dataProvider.getList().add(new ProjectModuleRow(project.getProjectName()));
            }
        }
    }

    private void enableActions(final boolean value) {
        view.getModulesView().enableActions(value);
    }

    @Override
    public void clearView() {
        view.clearDataView();
        dataProvider.getList().clear();
        enableActions(true);
    }

    /**
     * *** Presenter interfaces *******
     */
    @Override
    public void onAddModule() {

        wizard.initialise(getPom());

        wizard.start(getModuleAddedSuccessCallback(),
                     false);
    }

    private POM getPom() {
        if (model.isMultiModule()) {
            return makeMultiModulePom();
        } else {
            final POM pom = new POM();
            pom.getGav().setGroupId(projectContext.getActiveOrganizationalUnit().getDefaultGroupId());
            return pom;
        }
    }

    private POM makeMultiModulePom() {
        final POM pom = new POM();
        final GAV parentGAV = view.getDataPresenterGav();

        pom.setParent(parentGAV);

        pom.getGav().setGroupId(parentGAV.getGroupId());
        pom.getGav().setVersion(parentGAV.getVersion());
        return pom;
    }

    private Callback<Project> getModuleAddedSuccessCallback() {
        //optimization to avoid reloading the complete model when a module is added.
        return new Callback<Project>() {
            @Override
            public void callback(final Project _project) {
                history.setLastAddedModule(_project);
                if (_project != null) {
                    //A new module was added.
                    if (model.isMultiModule()) {
                        view.showBusyIndicator(Constants.INSTANCE.Loading());
                        repositoryStructureService.call(new RemoteCallback<RepositoryStructureModel>() {
                                                            @Override
                                                            public void callback(RepositoryStructureModel _model) {
                                                                view.hideBusyIndicator();
                                                                if (_model != null) {
                                                                    model.setPOM(_model.getPOM());
                                                                    model.setPOMMetaData(_model.getPOMMetaData());
                                                                    model.setModules(_model.getModules());
                                                                    model.getModulesProject().put(_project.getProjectName(),
                                                                                                  _project);
                                                                    addToModulesList(_project);
                                                                }
                                                            }
                                                        },
                                                        new HasBusyIndicatorDefaultErrorCallback(view)).load(projectContext.getActiveRepository(),
                                                                                                             projectContext.getActiveBranch(),
                                                                                                             false);
                    } else {
                        view.showBusyIndicator(Constants.INSTANCE.Loading());
                        pomService.call(new RemoteCallback<POM>() {
                                            @Override
                                            public void callback(POM _pom) {
                                                view.hideBusyIndicator();
                                                model.getOrphanProjects().add(_project);
                                                model.getOrphanProjectsPOM().put(_project.getIdentifier(),
                                                                                 _pom);
                                                addToModulesList(_project);
                                            }
                                        },
                                        new HasBusyIndicatorDefaultErrorCallback(view)).load(_project.getPomXMLPath());
                    }
                }
            }
        };
    }

    @Override
    public void addDataDisplay(final HasData<ProjectModuleRow> display) {
        dataProvider.addDataDisplay(display);
    }

    @Override
    public void onDeleteModule(final ProjectModuleRow moduleRow) {
        final Project project = getSelectedModule(moduleRow.getName());

        if (project != null) {
            showDeletePopup(getDeleteMessage(moduleRow),
                            new Command() {
                                @Override
                                public void execute() {
                                    deleteSelectedModule(project);
                                }
                            });
        }
    }

    private String getDeleteMessage(final ProjectModuleRow moduleRow) {
        if (model.isMultiModule()) {
            return Constants.INSTANCE.ConfirmModuleDeletion(moduleRow.getName());
        } else {
            return Constants.INSTANCE.ConfirmProjectDeletion(moduleRow.getName());
        }
    }

    private void deleteSelectedModule(final Project project) {
        view.showBusyIndicator(Constants.INSTANCE.Deleting());
        history.setLastDeletedModule(project);
        repositoryStructureService.call(getModuleDeletedSuccessCallback(project),
                                        new HasBusyIndicatorDefaultErrorCallback(view)).delete(project.getPomXMLPath(),
                                                                                               "Module removed");
    }

    private RemoteCallback<Void> getModuleDeletedSuccessCallback(final Project _project) {
        //optimization to avoid reloading the complete model when a module is added.
        return new RemoteCallback<Void>() {
            @Override
            public void callback(Void response) {
                if (_project != null) {
                    //A project was deleted
                    if (model.isMultiModule()) {
                        view.showBusyIndicator(Constants.INSTANCE.Loading());
                        repositoryStructureService.call(new RemoteCallback<RepositoryStructureModel>() {
                                                            @Override
                                                            public void callback(RepositoryStructureModel _model) {
                                                                view.hideBusyIndicator();
                                                                if (_model != null) {
                                                                    model.setPOM(_model.getPOM());
                                                                    model.setPOMMetaData(_model.getPOMMetaData());
                                                                    model.setModules(_model.getModules());
                                                                    model.getModulesProject().remove(_project.getProjectName());
                                                                    removeFromModulesList(_project.getProjectName());
                                                                }
                                                            }
                                                        },
                                                        new HasBusyIndicatorDefaultErrorCallback(view)).load(projectContext.getActiveRepository(),
                                                                                                             projectContext.getActiveBranch(),
                                                                                                             false);
                    } else {
                        model.getOrphanProjects().remove(_project);
                        model.getOrphanProjectsPOM().remove(_project.getIdentifier());
                        removeFromModulesList(_project.getProjectName());
                    }
                }
            }
        };
    }

    @Override
    public void onEditModule(final ProjectModuleRow moduleRow) {
        final Project project = getSelectedModule(moduleRow.getName());
        if (project != null) {
            contextChangeEvent.fire(new ProjectContextChangeEvent(projectContext.getActiveOrganizationalUnit(),
                                                                  projectContext.getActiveRepository(),
                                                                  projectContext.getActiveBranch(),
                                                                  project));
            placeManager.goTo("projectScreen");
        }
    }

    @Override
    public void loadModel(final Repository repository,
                          final String branch) {
        view.showBusyIndicator(Constants.INSTANCE.Loading());
        clearView();
        repositoryStructureService.call(getLoadModelSuccessCallback(),
                                        new HasBusyIndicatorDefaultErrorCallback(view)).load(repository,
                                                                                             branch);
    }

    private RemoteCallback<RepositoryStructureModel> getLoadModelSuccessCallback() {
        return new RemoteCallback<RepositoryStructureModel>() {
            @Override
            public void callback(final RepositoryStructureModel model) {
                view.hideBusyIndicator();
                setModel(model);
            }
        };
    }

    @Override
    public void onInitRepositoryStructure() {
        //TODO add parameters validation
        if (model != null) {
            if (model.isMultiModule()) {
                doRepositoryStructureInitialization(DeploymentMode.VALIDATED);
            } else if (model.isSingleProject()) {
                repositoryManagedStatusUpdater.initSingleProject(projectContext.getActiveRepository(),
                                                                 projectContext.getActiveBranch());
            } else if (!model.isManaged()) {
                repositoryManagedStatusUpdater.updateNonManaged(projectContext.getActiveRepository(),
                                                                projectContext.getActiveBranch());
            }
        }
    }

    private void doRepositoryStructureInitialization(final DeploymentMode mode) {
        view.showBusyIndicator(Constants.INSTANCE.CreatingRepositoryStructure());
        repositoryStructureService.call(new RemoteCallback<Path>() {

                                            @Override
                                            public void callback(final Path response) {
                                                view.hideBusyIndicator();
                                                loadModel(projectContext.getActiveRepository(),
                                                          projectContext.getActiveBranch());
                                            }
                                        },
                                        new HasBusyIndicatorDefaultErrorCallback(view) {
                                            @Override
                                            public boolean error(final Message message,
                                                                 final Throwable throwable) {
                                                // The *real* Throwable is wrapped in an InvocationTargetException when ran as a Unit Test and invoked with Reflection.
                                                final Throwable _throwable = (throwable.getCause() == null ? throwable : throwable.getCause());
                                                if (_throwable instanceof GAVAlreadyExistsException) {
                                                    final GAVAlreadyExistsException gae = (GAVAlreadyExistsException) _throwable;
                                                    conflictingRepositoriesPopup.setContent(gae.getGAV(),
                                                                                            gae.getRepositories(),
                                                                                            new Command() {
                                                                                                @Override
                                                                                                public void execute() {
                                                                                                    conflictingRepositoriesPopup.hide();
                                                                                                    doRepositoryStructureInitialization(DeploymentMode.FORCED);
                                                                                                }
                                                                                            });
                                                    conflictingRepositoriesPopup.show();
                                                    return true;
                                                } else {
                                                    return super.error(message,
                                                                       _throwable);
                                                }
                                            }
                                        }).initRepositoryStructure(view.getDataPresenterGav(),
                                                                   projectContext.getActiveRepository(),
                                                                   mode);
    }

    private Project getSelectedModule(final String name) {
        if (model != null && name != null) {
            if (model.isMultiModule()) {
                return model.getModulesProject() != null ? model.getModulesProject().get(name) : null;
            } else if (model.getOrphanProjects() != null) {
                for (Project _project : model.getOrphanProjects()) {
                    if (name.equals(_project.getProjectName())) {
                        return _project;
                    }
                }
            }
        }
        return null;
    }

    private void removeFromModulesList(final String module) {
        if (module != null) {
            int index = -1;
            for (ProjectModuleRow row : dataProvider.getList()) {
                index++;
                if (module.equals(row.getName())) {
                    break;
                }
            }
            if (index >= 0 && (index == 0 || index < dataProvider.getList().size())) {
                dataProvider.getList().remove(index);
            }
        }
    }

    private void addToModulesList(final Project project) {
        ProjectModuleRow row = new ProjectModuleRow(project.getProjectName());
        if (!dataProvider.getList().contains(row)) {
            dataProvider.getList().add(row);
        }
    }

    private void makeMenuBar() {
        menus.init(new HasModel<RepositoryStructureModel>() {
            @Override
            public RepositoryStructureModel getModel() {
                return model;
            }
        });
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
}

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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentDelete;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentRename;
import static org.uberfire.ext.widgets.common.client.common.ConcurrentChangePopup.newConcurrentUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.RepositoryAction;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownHeader;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.util.CreationalCallback;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.async.AsyncBeanDef;
import org.jboss.errai.ioc.client.container.async.AsyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.ForceUnlockEvent;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.LockTarget;
import org.uberfire.client.mvp.LockTarget.TitleProvider;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.FileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.RenamePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    public static final String KIE_DEPLOYMENT_DESCRIPTOR_XML = "kie-deployment-descriptor.xml";
    protected ObservablePath pathToPomXML;
    protected CopyPopUpPresenter copyPopUpPresenter;
    protected SavePopUpPresenter savePopUpPresenter;
    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;
    private ProjectScreenView view;
    private User user;
    private Caller<ValidationService> validationService;
    private Caller<ProjectScreenService> projectScreenService;
    private ProjectNameValidator projectNameValidator;
    private Event<NotificationEvent> notificationEvent;
    private Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent;
    private PlaceManager placeManager;
    private Menus menus;
    private ProjectScreenModel model;
    private Integer originalHash;
    private PlaceRequest placeRequest;
    private BusyIndicatorView busyIndicatorView;
    private ObservablePath.OnConcurrentUpdateEvent concurrentUpdateSessionInfo = null;
    private ButtonGroup buildOptions;
    private Collection<Widget> buildExtensions;
    private WorkspaceProjectContext projectContext;
    private Instance<LockManager> lockManagerInstanceProvider;
    private Map<Widget, LockManager> lockManagers = new HashMap<>();
    private Runnable reloadRunnable;
    private TitleProvider titleProvider;
    private String title;
    private Event<ForceUnlockEvent> forceLockReleaseEvent;
    private ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    private Path kieDeploymentDescriptoPath;

    private Instance<ResourceTypeDefinition> resourceTypes;

    private BuildExecutor buildExecutor;

    public ProjectScreenPresenter() {
    }

    @Inject
    public ProjectScreenPresenter(final ProjectScreenView view,
                                  final WorkspaceProjectContext projectContext,
                                  final Caller<ProjectScreenService> projectScreenService,
                                  final User user,
                                  final Event<NotificationEvent> notificationEvent,
                                  final Event<ChangeTitleWidgetEvent> changeTitleWidgetEvent,
                                  final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent,
                                  final ProjectNameValidator projectNameValidator,
                                  final PlaceManager placeManager,
                                  final BusyIndicatorView busyIndicatorView,
                                  final Caller<ValidationService> validationService,
                                  final Instance<LockManager> lockManagerInstanceProvider,
                                  final Event<ForceUnlockEvent> forceLockReleaseEvent,
                                  final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                                  final CopyPopUpPresenter copyPopUpPresenter,
                                  final SavePopUpPresenter savePopUpPresenter,
                                  final GAVPreferences gavPreferences,
                                  final ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier,
                                  final Instance<ResourceTypeDefinition> resourceTypes,
                                  final BuildExecutor buildExecutor) {

        this.view = view;
        this.user = user;
        this.projectContextChangeEvent = projectContextChangeEvent;
        view.setPresenter(this);

        gavPreferences.load(projectScopedResolutionStrategySupplier.get(),
                            loadedGAVPreferences -> {
                                view.setGAVCheckDisabledSetting(loadedGAVPreferences.isConflictingGAVCheckDisabled());
                            },
                            throwable -> {
                                throw new RuntimeException(throwable);
                            });

        this.projectScreenService = projectScreenService;
        this.notificationEvent = notificationEvent;
        this.changeTitleWidgetEvent = changeTitleWidgetEvent;
        this.projectNameValidator = projectNameValidator;
        this.placeManager = placeManager;
        this.validationService = validationService;

        this.busyIndicatorView = busyIndicatorView;
        this.projectContext = projectContext;
        this.lockManagerInstanceProvider = lockManagerInstanceProvider;
        this.forceLockReleaseEvent = forceLockReleaseEvent;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;

        this.copyPopUpPresenter = copyPopUpPresenter;
        this.savePopUpPresenter = savePopUpPresenter;

        this.buildOptions = view.getBuildButtons();

        reloadRunnable = new Runnable() {

            @Override
            public void run() {
                ProjectScreenPresenter.this.reload();
            }
        };

        titleProvider = new TitleProvider() {

            @Override
            public String getTitle() {
                return (title == null) ? ProjectScreenPresenter.this.getTitle() : title;
            }
        };

        this.resourceTypes = resourceTypes;

        this.buildExecutor = buildExecutor;
        this.buildExecutor.init(view);

        menus = makeMenuBar();
    }

    protected boolean isDeploymentDescritorEditorAvailable(final Stream<ResourceTypeDefinition> resourceTypes,
                                                           final Path path) {
        return resourceTypes.anyMatch(r -> r.accept(path) && KIE_DEPLOYMENT_DESCRIPTOR_XML.equals(r.getPrefix() + "." + r.getSuffix()));
    }

    /**
     * This is in no way a permanent fix for the refresh issue.
     * We need something reusable for all the multi file editors we have and will have.
     */
    void onResourceUpdated(final @Observes ResourceUpdatedEvent resourceUpdatedEvent) {
        if (resourceUpdatedEvent.getSessionInfo().getIdentity().equals(user)) {
            if (resourceUpdatedEvent.getPath().equals(pathToPomXML)) {
                reloadRunnable.run();
            }
        }
    }

    private void configureBuildExtensions(final Module module,
                                          final ButtonGroup buildButtonGroup) {
        cleanExtensions();
        final DropDownMenu buildDropdown = getDropdown(buildButtonGroup);

        if (module == null || buildDropdown == null) {
            buildExtensions = null;
            return;
        }

        final Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>> pair = getBuildExtensions();
        Collection<BuildOptionExtension> allExtensions = pair.getK1();
        Collection<BuildOptionExtension> dependentScopedExtensions = pair.getK2();

        buildExtensions = new ArrayList<>(allExtensions.size());

        for (BuildOptionExtension ext : allExtensions) {
            for (Widget option : ext.getBuildOptions(module)) {
                if (option instanceof DropDownHeader ||
                        option instanceof AnchorListItem) {
                    buildExtensions.add(option);
                    buildDropdown.add(option);
                }
            }
        }

        destroyExtensions(dependentScopedExtensions);
    }

    private DropDownMenu getDropdown(final ButtonGroup buildButtonGroup) {
        for (int i = 0; i < buildButtonGroup.getWidgetCount(); i++) {
            final Widget widget = buildButtonGroup.getWidget(i);
            if (widget instanceof DropDownMenu) {
                return (DropDownMenu) widget;
            }
        }

        return null;
    }

    private void cleanExtensions() {
        if (buildExtensions != null && buildOptions != null) {
            final DropDownMenu dropdownMenu = ((DropDownMenu) buildOptions.getWidget(1));
            for (Widget ext : buildExtensions) {
                dropdownMenu.remove(ext);
            }
        }
    }

    protected Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>> getBuildExtensions() {
        AsyncBeanManager beanManager = IOC.getAsyncBeanManager();
        Collection<AsyncBeanDef<BuildOptionExtension>> beans = beanManager.lookupBeans(BuildOptionExtension.class);
        final Collection<BuildOptionExtension> dependentScoped = new ArrayList<>(beans.size());
        final Collection<BuildOptionExtension> instances = new ArrayList<>(beans.size());

        for (final AsyncBeanDef<BuildOptionExtension> bean : beans) {
            /*
             * We are assuming that extensions are not marked with @LoadAsync.
             * Thus getInstance will immediately invoke the callback.
             */
            bean.getInstance(new CreationalCallback<BuildOptionExtension>() {

                @Override
                public void callback(BuildOptionExtension extension) {
                    instances.add(extension);
                    if (bean.getScope().equals(Dependent.class)) {
                        dependentScoped.add(extension);
                    }
                }
            });
        }

        return new Pair<>(instances,
                                                                                            dependentScoped);
    }

    protected void destroyExtensions(Collection<BuildOptionExtension> extensions) {
        AsyncBeanManager beanManager = IOC.getAsyncBeanManager();

        for (BuildOptionExtension ext : extensions) {
            beanManager.destroyBean(ext);
        }
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        update();
    }

    private void update() {
        if (!projectContext.getActiveWorkspaceProject().isPresent()) {
            enableMenus(false);
            view.showNoProjectSelected();
            view.hideBusyIndicator();
        } else {
            enableMenus(true);
            view.showProjectEditor();
            showCurrentModuleInfoIfAny();
        }
    }

    @OnMayClose
    public boolean onMayClose() {
        if (isDirty()) {
            return view.confirmClose();
        }
        return true;
    }

    @OnClose
    public void onClose() {
        for (LockManager lockManager : lockManagers.values()) {
            lockManager.releaseLock();
            lockManagerInstanceProvider.destroy(lockManager);
        }
        lockManagers.clear();
    }

    private void showCurrentModuleInfoIfAny() {
        String rootPathURI = projectContext
                                           .getActiveWorkspaceProject()
                                           .map(proj -> proj.getRootPath().toURI())
                                           .orElseThrow(() -> new IllegalStateException("Cannot get root path URI without active project."));
        this.kieDeploymentDescriptoPath = PathFactory.newPath(KIE_DEPLOYMENT_DESCRIPTOR_XML,
                                                              rootPathURI + "/src/main/resources/META-INF/" + KIE_DEPLOYMENT_DESCRIPTOR_XML);
        setupPathToPomXML();
        init();
    }

    private void init() {
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        projectScreenService.call(
                new RemoteCallback<ProjectScreenModel>() {
                    @Override
                    public void callback(ProjectScreenModel model) {

                        concurrentUpdateSessionInfo = null;
                        ProjectScreenPresenter.this.model = model;

                        view.setPOM(model.getPOM());

                        validateGroupID(model.getPOM().getGav().getGroupId());
                        validateArtifactID(model.getPOM().getGav().getArtifactId());
                        validateVersion(model.getPOM().getGav().getVersion());

                        view.setDependencies(model.getPOM(),
                                             model.getWhiteList());
                        view.setPomMetadata(model.getPOMMetaData());
                        view.setPomMetadataUnlockHandler(getUnlockHandler(model.getPOMMetaData().getPath()));

                        view.setKModule(model.getKModule());
                        view.setKModuleMetadata(model.getKModuleMetaData());
                        view.setKModuleMetadataUnlockHandler(getUnlockHandler(model.getKModuleMetaData().getPath()));

                        view.setImports(model.getProjectImports());
                        view.setImportsMetadata(model.getProjectImportsMetaData());
                        view.setImportsMetadataUnlockHandler(getUnlockHandler(model.getProjectImportsMetaData().getPath()));

                        view.setRepositories(model.getRepositories());

                        view.setDeploymentDescriptorEnabled(isDeploymentDescritorEditorAvailable(StreamSupport.stream(resourceTypes.spliterator(),
                                                                                                                      false),
                                                                                                 kieDeploymentDescriptoPath));

                        view.hideBusyIndicator();
                        originalHash = model.hashCode();

                        if (menus != null) {
                            for (MenuItem mi : menus.getItemsMap().values()) {
                                mi.setEnabled(true);
                            }
                        }

                        updateEditorTitle();
                        updateCurrentView();
                    }
                },
                new CommandDrivenErrorCallback(view,
                                               new CommandBuilder()
                                                       .addNoSuchFileException(view,
                                                                               menus)
                                                       .addFileSystemNotFoundException(view,
                                                                                       menus)
                                                       .build())).load(pathToPomXML);

        if (model == null) {
            view.showGAVPanel();
        }

        configureBuildExtensions(projectContext.getActiveModule().orElse(null),
                                 buildOptions);
    }

    private void updateEditorTitle() {
        title = ProjectEditorResources.CONSTANTS.ProjectScreenWithName(model.getPOM().getGav().getGroupId() + ":" +
                                                                               model.getPOM().getGav().getArtifactId() + ":" +
                                                                               model.getPOM().getGav().getVersion());

        changeTitleWidgetEvent.fire(new ChangeTitleWidgetEvent(placeRequest,
                                                               title));
    }

    private void updateCurrentView() {
        if (view.showsGAVPanel()) {
            onGAVPanelSelected();
        } else if (view.showsGAVMetadataPanel()) {
            onGAVMetadataPanelSelected();
        } else if (view.showsKBasePanel()) {
            onKBasePanelSelected();
        } else if (view.showsKBaseMetadataPanel()) {
            onKBaseMetadataPanelSelected();
        } else if (view.showsImportsPanel()) {
            onImportsPanelSelected();
        } else if (view.showsImportsMetadataPanel()) {
            onImportsMetadataPanelSelected();
        } else if (view.showsRepositoriesPanel()) {
            onRepositoriesPanelSelected();
        } else if (view.showsDependenciesPanel()) {
            onDependenciesSelected();
        }
    }

    protected void setupPathToPomXML() {
        if (pathToPomXML != null) {
            pathToPomXML.dispose();
        }

        Path pomPath = getMainModulePomPath();
        pathToPomXML = IOC.getBeanManager().lookupBean(ObservablePath.class).getInstance().wrap(pomPath);

        pathToPomXML.onConcurrentUpdate(new ParameterizedCommand<ObservablePath.OnConcurrentUpdateEvent>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentUpdateEvent eventInfo) {
                concurrentUpdateSessionInfo = eventInfo;
            }
        });

        pathToPomXML.onConcurrentRename(new ParameterizedCommand<ObservablePath.OnConcurrentRenameEvent>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentRenameEvent info) {
                newConcurrentRename(info.getSource(),
                                    info.getTarget(),
                                    info.getIdentity(),
                                    new Command() {
                                        @Override
                                        public void execute() {
                                            enableMenus(false);
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

        pathToPomXML.onConcurrentDelete(new ParameterizedCommand<ObservablePath.OnConcurrentDelete>() {
            @Override
            public void execute(final ObservablePath.OnConcurrentDelete info) {
                newConcurrentDelete(info.getPath(),
                                    info.getIdentity(),
                                    new Command() {
                                        @Override
                                        public void execute() {
                                            enableMenus(false);
                                        }
                                    },
                                    new Command() {
                                        @Override
                                        public void execute() {
                                            placeManager.closePlace("projectScreen");
                                        }
                                    }
                ).show();
            }
        });
    }

    private Path getMainModulePomPath() {
        return projectContext.getActiveWorkspaceProject()
                                     .map(proj -> proj.getMainModule().getPomXMLPath())
                                     .orElseThrow(() -> new IllegalStateException("Cannot get main module POM path without active project."));
    }

    void enableMenus(final boolean enabled) {
        for (MenuItem mi : menus.getItems()) {
            mi.setEnabled(enabled);
        }
    }

    private void reload() {
        concurrentUpdateSessionInfo = null;
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        init();
    }

    private Menus makeMenuBar() {
        showCurrentModuleInfoIfAny();

        return MenuFactory
                .newTopLevelMenu(CommonConstants.INSTANCE.Save())
                .withPermission(Repository.RESOURCE_TYPE,
                                getRepository(),
                                RepositoryAction.UPDATE)
                .respondsWith(getSaveCommand(DeploymentMode.VALIDATED))
                .endMenu()
                .newTopLevelMenu(CommonConstants.INSTANCE.Delete())
                .withPermission(Repository.RESOURCE_TYPE,
                                getRepository(),
                                RepositoryAction.DELETE)
                .respondsWith(getDeleteCommand())
                .endMenu()
                .newTopLevelMenu(CommonConstants.INSTANCE.Copy())
                .withPermission(Repository.RESOURCE_TYPE,
                                getRepository(),
                                RepositoryAction.CREATE)
                .respondsWith(getCopyCommand())
                .endMenu()
                .newTopLevelMenu(CommonConstants.INSTANCE.Reimport())
                .withPermission(Repository.RESOURCE_TYPE,
                                getRepository(),
                                RepositoryAction.UPDATE)
                .respondsWith(getReImportCommand())
                .endMenu()
                .newTopLevelCustomMenu(new MenuFactory.CustomMenuBuilder() {
                    @Override
                    public void push(MenuFactory.CustomMenuBuilder element) {
                    }

                    @Override
                    public MenuItem build() {
                        return new BaseMenuCustom<IsWidget>() {

                            private boolean enabled;

                            @Override
                            public IsWidget build() {
                                return buildOptions;
                            }

                            @Override
                            public boolean isEnabled() {
                                return enabled;
                            }

                            @Override
                            public void setEnabled(boolean enabled) {
                                this.enabled = enabled;
                                ((Button) buildOptions.getWidget(0)).setEnabled(enabled);
                            }

                            @Override
                            public List<ResourceActionRef> getResourceActions() {
                                ResourceActionRef ref = new ResourceActionRef(Repository.RESOURCE_TYPE,
                                                                              getRepository(),
                                                                              RepositoryAction.BUILD);
                                return Collections.singletonList(ref);
                            }
                        };
                    }
                }).endMenu()
                .build();
    }

    private Repository getRepository() {
        return projectContext.getActiveWorkspaceProject().map(proj -> proj.getRepository()).orElse(null);
    }

    protected Command getReImportCommand() {
        return new Command() {
            @Override
            public void execute() {

                projectScreenService.call(
                        new RemoteCallback<Void>() {

                            @Override
                            public void callback(final Void o) {
                                busyIndicatorView.hideBusyIndicator();
                                notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ReimportSuccessful()));
                            }
                        },
                        new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).reImport(getMainModulePomPath());
            }
        };
    }

    private Command getDeleteCommand() {
        return new Command() {

            @Override
            public void execute() {

                busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Deleting());

                projectScreenService.call(
                                          new RemoteCallback<Void>() {

                                              @Override
                                              public void callback(final Void o) {
                                                  busyIndicatorView.hideBusyIndicator();
                                                  notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
                                                  placeManager.forceClosePlace(placeRequest);
                                              }
                                          },
                                          new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView))
                                    .delete(projectContext.getActiveWorkspaceProject()
                                                          .orElseThrow(() -> new IllegalStateException("Cannot call delete without an active project.")));
            }
        };
    }

    private Command getCopyCommand() {
        return new Command() {

            @Override
            public void execute() {

                copyPopUpPresenter.show(projectContext.getActiveWorkspaceProject()
                                                      .map(proj -> proj.getRootPath())
                                                      .orElseThrow(() -> new IllegalStateException("Cannot copy without an active project.")),
                                        projectNameValidator,
                                        new CommandWithFileNameAndCommitMessage() {

                                            @Override
                                            public void execute(final FileNameAndCommitMessage details) {

                                                busyIndicatorView.showBusyIndicator(CommonConstants.INSTANCE.Copying());
                                                projectScreenService.call(
                                                                          new RemoteCallback<Void>() {

                                                                              @Override
                                                                              public void callback(final Void o) {
                                                                                  copyPopUpPresenter.getView().hide();
                                                                                  busyIndicatorView.hideBusyIndicator();
                                                                                  notificationEvent.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemCopiedSuccessfully()));
                                                                              }
                                                                          },
                                                                          getCopyErrorCallback(copyPopUpPresenter.getView()))
                                                                    .copy(projectContext.getActiveWorkspaceProject()
                                                                                        .orElseThrow(() -> new IllegalStateException("Cannot copy without an active project.")),
                                                                          details.getNewFileName());
                                            }
                                        });
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getCopyErrorCallback(final CopyPopUpPresenter.View copyPopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                copyPopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    protected HasBusyIndicatorDefaultErrorCallback getRenameErrorCallback(final RenamePopUpPresenter.View renamePopupView) {
        return new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView) {

            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                renamePopupView.hide();
                return super.error(message,
                                   throwable);
            }
        };
    }

    private Command getCancelCommand() {
        return new Command() {
            @Override
            public void execute() {
            }
        };
    }

    Command getSaveCommand(final DeploymentMode mode) {
        return new Command() {
            @Override
            public void execute() {
                saveProject(new RemoteCallback<WorkspaceProject>() {
                                @Override
                                public void callback(final WorkspaceProject project) {
                                    view.hideBusyIndicator();
                                    notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.SaveSuccessful(pathToPomXML.getFileName()),
                                                                                 NotificationEvent.NotificationType.SUCCESS));
                                    originalHash = model.hashCode();
                                }
                            },
                            mode);
            }
        };
    }

    private Command getSaveAndExecuteCommand(final Command command,
                                             final DeploymentMode mode) {
        return new Command() {
            @Override
            public void execute() {
                saveProject(new RemoteCallback<WorkspaceProject>() {
                                @Override
                                public void callback(final WorkspaceProject project) {
                                    notificationEvent.fire(new NotificationEvent(ProjectEditorResources.CONSTANTS.SaveSuccessful(pathToPomXML.getFileName()),
                                                                                 NotificationEvent.NotificationType.SUCCESS));
                                    originalHash = model.hashCode();
                                    command.execute();
                                }
                            },
                            mode);
            }
        };
    }

    private void saveProject(final RemoteCallback<WorkspaceProject> callback,
                             final DeploymentMode mode) {
        if (concurrentUpdateSessionInfo != null) {
            newConcurrentUpdate(concurrentUpdateSessionInfo.getPath(),
                                concurrentUpdateSessionInfo.getIdentity(),
                                new Command() {
                                    @Override
                                    public void execute() {
                                        save(callback,
                                             mode);
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        //cancel?
                                    }
                                },
                                new Command() {
                                    @Override
                                    public void execute() {
                                        reload();
                                    }
                                }
            ).show();
        } else {
            save(callback,
                 mode);
        }
    }

    private void save(final RemoteCallback<WorkspaceProject> callback,
                      final DeploymentMode mode) {
        savePopUpPresenter.show(pathToPomXML,
                                new ParameterizedCommand<String>() {
                                    @Override
                                    public void execute(final String comment) {
                                        doSave(comment,
                                               callback,
                                               mode);
                                    }
                                });
        concurrentUpdateSessionInfo = null;
    }

    private void doSave(final String comment,
                        final RemoteCallback<WorkspaceProject> callback,
                        final DeploymentMode mode) {
        view.showBusyIndicator(CommonConstants.INSTANCE.Saving());

        //Instantiate a new instance on each "save" operation to pass in commit message
        final Map<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable> onSaveGavExistsHandler = new HashMap<Class<? extends Throwable>, CommandWithThrowableDrivenErrorCallback.CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class,
                new CommandWithThrowableDrivenErrorCallback.CommandWithThrowable() {
                    @Override
                    public void execute(final Throwable parameter) {

                        view.hideBusyIndicator();
                        conflictingRepositoriesPopup.setContent(model.getPOM().getGav(),
                                                                ((GAVAlreadyExistsException) parameter).getRepositories(),
                                                                () -> {
                                                                    conflictingRepositoriesPopup.hide();
                                                                    doSave(comment,
                                                                           callback,
                                                                           DeploymentMode.FORCED);
                                                                });
                        conflictingRepositoriesPopup.show();
                    }
                });
        }};

        projectScreenService.call(new RemoteCallback<WorkspaceProject>() {
                                      @Override
                                      public void callback(final WorkspaceProject project) {

                                          projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(project));

                                          update();

                                          if (callback != null) {
                                              callback.callback(project);
                                          }
                                      }
                                  },
                                  new CommandWithThrowableDrivenErrorCallback(view,
                                                                              onSaveGavExistsHandler)).save(pathToPomXML,
                                                                                                            model,
                                                                                                            comment,
                                                                                                            mode);
        updateEditorTitle();
    }

    @Override
    public void triggerBuild() {
        runBuild(() -> buildExecutor.triggerBuild());
    }

    @Override
    public void triggerBuildAndDeploy() {
        runBuild(() -> buildExecutor.triggerBuildAndDeploy());
    }

    private void runBuild(final Command buildCommand) {
        if (isDirty()) {
            view.showSaveBeforeContinue(getSaveAndExecuteCommand(buildCommand,
                                                                 DeploymentMode.VALIDATED),
                                        buildCommand,
                                        getCancelCommand());
        } else {
            buildCommand.execute();
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.ProjectScreen();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        if (menus == null) {
            menus = makeMenuBar();
        }
        return menus;
    }

    @Override
    public void onGAVPanelSelected() {
        view.showGAVPanel();
        acquireLockOnDemand(model.getPathToPOM(),
                            view.getPomPart());
    }

    @Override
    public void onGAVMetadataPanelSelected() {
        view.showGAVMetadataPanel();
        acquireLockOnDemand(model.getPathToPOM(),
                            view.getPomMetadataPart());
    }

    @Override
    public void onKBasePanelSelected() {
        view.showKBasePanel();
        acquireLockOnDemand(model.getPathToKModule(),
                            view.getKModulePart());
    }

    @Override
    public void onKBaseMetadataPanelSelected() {
        view.showKBaseMetadataPanel();
        acquireLockOnDemand(model.getPathToKModule(),
                            view.getKModuleMetadataPart());
    }

    @Override
    public void onImportsPanelSelected() {
        view.showImportsPanel();
        acquireLockOnDemand(model.getPathToImports(),
                            view.getImportsPart());
    }

    @Override
    public void onImportsMetadataPanelSelected() {
        view.showImportsMetadataPanel();
        acquireLockOnDemand(model.getPathToImports(),
                            view.getImportsMetadataPart());
    }

    @Override
    public void onRepositoriesPanelSelected() {
        view.showRepositoriesPanel();
        acquireLockOnDemand(model.getPathToRepositories(),
                            view.getRepositoriesPart());
    }

    @Override
    public void onDependenciesSelected() {
        view.showDependenciesPanel();
        acquireLockOnDemand(model.getPathToPOM(),
                            view.getDependenciesPart());
    }

    @Override
    public void onDeploymentDescriptorSelected() {
        placeManager.goTo(kieDeploymentDescriptoPath);
    }

    @Override
    public void onPersistenceDescriptorSelected() {

        Map<String, Object> attrs = new HashMap<>();
        attrs.put(PathFactory.VERSION_PROPERTY,
                  new Boolean(true));

        PathPlaceRequest placeRequest = new PathPlaceRequest(PathFactory.newPath("persistence.xml",
                                                                                 projectContext.getActiveWorkspaceProject()
                                                                                               .map(proj -> proj.getRootPath().toURI())
                                                                                               .orElseThrow(() -> new IllegalStateException("Cannot get root path URI without active project.")) +
                                                                                                    PersistenceDescriptorService.PERSISTENCE_DESCRIPTOR_PATH,
                                                                                 attrs));
        placeRequest.addParameter("createIfNotExists",
                                  "true");
        placeManager.goTo(placeRequest);
    }

    private boolean isDirty() {
        final Integer currentHash = model != null ? model.hashCode() : null;
        if (originalHash == null) {
            return currentHash != null;
        } else {
            return !originalHash.equals(currentHash);
        }
    }

    @Override
    public void validateGroupID(final String groupId) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                view.setValidGroupID(Boolean.TRUE.equals(result));
            }
        }).validateGroupId(groupId);
    }

    @Override
    public void validateArtifactID(final String artifactId) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                view.setValidArtifactID(Boolean.TRUE.equals(result));
            }
        }).validateArtifactId(artifactId);
    }

    @Override
    public void validateVersion(final String version) {
        validationService.call(new RemoteCallback<Boolean>() {
            @Override
            public void callback(final Boolean result) {
                view.setValidVersion(Boolean.TRUE.equals(result));
            }
        }).validateGAVVersion(version);
    }

    private void acquireLockOnDemand(final Path path,
                                     final Widget widget) {
        final LockManager lockManager = getOrCreateLockManager(widget);
        final LockTarget lockTarget = new LockTarget(path,
                                                     widget,
                                                     placeRequest,
                                                     titleProvider,
                                                     reloadRunnable);
        lockManager.init(lockTarget);
        lockManager.acquireLockOnDemand();
    }

    private LockManager getOrCreateLockManager(final Widget widget) {
        LockManager lockManager = lockManagers.get(widget);
        if (lockManager == null) {
            lockManager = lockManagerInstanceProvider.get();
            lockManagers.put(widget,
                             lockManager);
        }
        return lockManager;
    }

    private Runnable getUnlockHandler(final Path path) {
        return new Runnable() {

            @Override
            public void run() {
                forceLockReleaseEvent.fire(new ForceUnlockEvent(path));
            }
        };
    }

    @SuppressWarnings("unused")
    private void onLockChange(@Observes LockInfo lockInfo) {
        if (model == null) {
            return;
        }

        final Metadata pomMetaData = model.getPOMMetaData();
        if (pomMetaData != null && lockInfo.getFile().equals(pomMetaData.getPath())) {
            pomMetaData.setLockInfo(lockInfo);
            view.setPomMetadata(pomMetaData);
        }
        final Metadata kModuleMetaData = model.getKModuleMetaData();
        if (kModuleMetaData != null && lockInfo.getFile().equals(kModuleMetaData.getPath())) {
            kModuleMetaData.setLockInfo(lockInfo);
            view.setKModuleMetadata(kModuleMetaData);
        }
        final Metadata projectImportsMetaData = model.getProjectImportsMetaData();
        if (projectImportsMetaData != null && lockInfo.getFile().equals(projectImportsMetaData.getPath())) {
            projectImportsMetaData.setLockInfo(lockInfo);
            view.setImportsMetadata(projectImportsMetaData);
        }
    }
}

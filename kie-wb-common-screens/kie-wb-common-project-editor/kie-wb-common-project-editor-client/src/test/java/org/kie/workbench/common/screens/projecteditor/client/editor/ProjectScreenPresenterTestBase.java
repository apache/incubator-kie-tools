/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.google.gwtmockito.GwtMock;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.screens.projecteditor.client.build.BuildExecutor;
import org.kie.workbench.common.screens.projecteditor.client.editor.extension.BuildOptionExtension;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.impl.ObservablePathImpl;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.mockito.Mockito.*;

public abstract class ProjectScreenPresenterTestBase {

    protected ProjectScreenPresenter presenter;
    @Mock
    protected ProjectScreenView view;
    @Mock
    protected User user;
    @Mock
    protected CopyPopUpPresenter copyPopUpPresenter;
    @Mock
    protected SavePopUpPresenter savePopUpPresenter;
    @Mock
    protected ProjectScreenService projectScreenService;
    @Mock
    protected EventSourceMock<NotificationEvent> notificationEvent;
    @Mock
    protected ConflictingRepositoriesPopup conflictingRepositoriesPopup;
    @Spy
    protected WorkspaceProjectContext context = new WorkspaceProjectContext();
    @Spy
    protected MockInstance<LockManager> lockManagerInstanceProvider = new MockInstance();
    @Spy
    protected MockInstance<ResourceTypeDefinition> resourceTypeDefinitions = new MockInstance();
    @Mock
    protected KieModule module;
    @Mock
    protected Repository repository;
    @Mock
    protected Path pomPath;
    @Mock
    protected GAVPreferences gavPreferences;
    @Mock
    protected ProjectScopedResolutionStrategySupplier projectScopedResolutionStrategySupplier;
    @Mock
    protected BuildExecutor buildExecutor;
    protected ObservablePath observablePathToPomXML;
    @GwtMock
    @SuppressWarnings("unused")
    private ButtonGroup buildOptions;
    @GwtMock
    @SuppressWarnings("unused")
    private Button buildOptionsButton1;
    @GwtMock
    @SuppressWarnings("unused")
    private DropDownMenu buildOptionsMenu;
    @GwtMock
    @SuppressWarnings("unused")
    private AnchorListItem buildOptionsMenuButton1;

    protected void mockBuildOptions() {
        when(view.getBuildButtons()).thenReturn(buildOptions);
        when(buildOptions.getWidget(eq(0))).thenReturn(buildOptionsButton1);
        when(buildOptions.getWidget(eq(1))).thenReturn(buildOptionsMenu);
        when(buildOptionsMenu.getWidget(eq(0))).thenReturn(buildOptionsMenuButton1);
        when(buildOptionsMenu.getWidget(eq(1))).thenReturn(buildOptionsMenuButton1);
    }

    protected POM mockProjectScreenService(final ProjectScreenModel model) {
        final POM pom = new POM(new GAV("groupId",
                                        "artifactId",
                                        "version"));
        model.setPOM(pom);
        when(projectScreenService.load(any(Path.class))).thenReturn(model);
        return pom;
    }

    protected void mockLockManager(final ProjectScreenModel model) {
        final Path path = mock(Path.class);
        final Metadata pomMetadata = mock(Metadata.class);
        model.setPOMMetaData(pomMetadata);
        when(pomMetadata.getPath()).thenReturn(path);
        final Metadata kmoduleMetadata = mock(Metadata.class);
        model.setKModuleMetaData(kmoduleMetadata);
        when(kmoduleMetadata.getPath()).thenReturn(path);
        final Metadata importsMetadata = mock(Metadata.class);
        model.setProjectImportsMetaData(importsMetadata);
        when(importsMetadata.getPath()).thenReturn(path);
    }

    protected void mockWorkspaceProjectContext(final POM pom,
                                      final Repository repository,
                                      final KieModule module,
                                      final Path pomPath) {
        OrganizationalUnit ou = mock(OrganizationalUnit.class);
        Path rootPath = mock(Path.class);
        when(rootPath.toURI()).thenReturn("git://fake-space/fake-project/");
        when(context.getActiveWorkspaceProject()).thenReturn(Optional.of(new WorkspaceProject(ou,
                                                                                              repository,
                                                                                              new Branch("master",
                                                                                                         rootPath),
                                                                                              module)));
        when(context.getActiveOrganizationalUnit()).thenReturn(Optional.of(ou));
        when(context.getActivePackage()).thenReturn(Optional.empty());

        when(repository.getAlias()).thenReturn("repository");

        when(module.getModuleName()).thenReturn("module");
        when(module.getPomXMLPath()).thenReturn(pomPath);
        when(module.getPom()).thenReturn(pom);
        when(module.getRootPath()).thenReturn(mock(Path.class));
        when(pomPath.getFileName()).thenReturn("pom.xml");
        when(context.getActiveModule()).thenReturn(Optional.of(module));
    }

    protected void constructProjectScreenPresenter(final KieModule module) {
        doAnswer(invocationOnMock -> {
            ((ParameterizedCommand<GAVPreferences>) invocationOnMock.getArguments()[1]).execute(gavPreferences);
            return null;
        }).when(gavPreferences).load(any(PreferenceScopeResolutionStrategyInfo.class),
                                     any(ParameterizedCommand.class),
                                     any(ParameterizedCommand.class));

        presenter = new ProjectScreenPresenter(view,
                                               context,
                                               new CallerMock<>(projectScreenService),
                                               user,
                                               notificationEvent,
                                               mock(EventSourceMock.class),
                                               mock(EventSourceMock.class),
                                               mock(ProjectNameValidator.class),
                                               mock(PlaceManager.class),
                                               mock(BusyIndicatorView.class),
                                               new CallerMock<>(mock(ValidationService.class)),
                                               lockManagerInstanceProvider,
                                               mock(EventSourceMock.class),
                                               conflictingRepositoriesPopup,
                                               copyPopUpPresenter,
                                               savePopUpPresenter,
                                               gavPreferences,
                                               projectScopedResolutionStrategySupplier,
                                               resourceTypeDefinitions,
                                               buildExecutor) {

            @Override
            protected Pair<Collection<BuildOptionExtension>, Collection<BuildOptionExtension>> getBuildExtensions() {
                //Do nothing. This method makes direct use of IOC and fails to be mocked
                return new Pair<>(Collections.EMPTY_LIST,
                                                                                                    Collections.EMPTY_LIST);
            }

            @Override
            protected void destroyExtensions(final Collection<BuildOptionExtension> extensions) {
                //Do nothing. This method makes direct use of IOC and fails to be mocked
            }

            @Override
            protected void setupPathToPomXML() {
                //Stub the real implementation that makes direct use of IOC and fails to be mocked
                observablePathToPomXML = new ObservablePathImpl().wrap(module.getPomXMLPath());
                pathToPomXML = observablePathToPomXML;
            }
        };
    }
}

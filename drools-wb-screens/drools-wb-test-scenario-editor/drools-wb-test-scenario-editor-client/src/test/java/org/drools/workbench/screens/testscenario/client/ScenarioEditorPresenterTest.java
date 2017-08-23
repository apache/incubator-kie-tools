/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import java.lang.annotation.Annotation;
import java.util.Collections;
import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.project.datamodel.imports.HasImports;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.test.TestService;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioEditorPresenterTest {

    @Mock
    CommonConstants commonConstants;

    @Captor
    private ArgumentCaptor<Scenario> scenarioArgumentCaptor;

    @Mock
    private KieEditorWrapperView kieView;

    @Mock
    private ScenarioEditorView view;

    @Mock
    private VersionRecordManager versionRecordManager;

    @Mock
    private OverviewWidgetPresenter overviewWidget;

    @Mock
    private MultiPageEditor multiPage;

    @Mock
    private ImportsWidgetPresenter importsWidget;

    @Mock
    private User user;

    @Mock
    private ScenarioTestEditorService service;

    @Mock
    private TestService testService;

    @Mock
    private BasicFileMenuBuilder menuBuilder;

    @Spy
    @InjectMocks
    private FileMenuBuilderImpl fileMenuBuilder;

    @Mock
    private ProjectController projectController;

    @Mock
    private ProjectContext workbenchContext;

    private ScenarioEditorPresenter editor;
    private ScenarioEditorView.Presenter presenter;
    private Scenario scenario;
    private Overview overview;
    private Scenario scenarioRunResult = null;

    @Before
    public void setUp() throws Exception {

        final AsyncPackageDataModelOracleFactory modelOracleFactory = mock(AsyncPackageDataModelOracleFactory.class);

        editor = new ScenarioEditorPresenter(view,
                                             user,
                                             importsWidget,
                                             new CallerMock<>(service),
                                             new CallerMock<>(testService),
                                             new TestScenarioResourceType(),
                                             modelOracleFactory) {
            {
                kieView = ScenarioEditorPresenterTest.this.kieView;
                versionRecordManager = ScenarioEditorPresenterTest.this.versionRecordManager;
                overviewWidget = ScenarioEditorPresenterTest.this.overviewWidget;
                notification = makeNotificationEvent();
                fileMenuBuilder = ScenarioEditorPresenterTest.this.fileMenuBuilder;
                projectController = ScenarioEditorPresenterTest.this.projectController;
                workbenchContext = ScenarioEditorPresenterTest.this.workbenchContext;
                versionRecordManager = ScenarioEditorPresenterTest.this.versionRecordManager;
            }
        };
        presenter = editor;

        scenarioRunResult = new Scenario();
        scenario = new Scenario();
        overview = new Overview();

        when(user.getIdentifier()).thenReturn("userName");

        final TestScenarioModelContent testScenarioModelContent = new TestScenarioModelContent(scenario,
                                                                                               overview,
                                                                                               "org.test",
                                                                                               new PackageDataModelOracleBaselinePayload());

        when(service.loadContent(any(Path.class))).thenReturn(testScenarioModelContent);

        final TestScenarioResult result = new TestScenarioResult(scenarioRunResult,
                                                                 Collections.EMPTY_SET);
        when(service.runScenario(eq("userName"),
                                 any(Path.class),
                                 eq(scenario))).thenReturn(result);

        final AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
        when(modelOracleFactory.makeAsyncPackageDataModelOracle(any(Path.class),
                                                                any(HasImports.class),
                                                                any(PackageDataModelOracleBaselinePayload.class))
        ).thenReturn(dmo);
    }

    @Test
    public void testSimple() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testRunScenarioAndSave() throws Exception {

        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path,
                         placeRequest);

        reset(view);
        reset(importsWidget);

        presenter.onRunScenario();

        // Make sure imports are updated
        verify(view).initKSessionSelector(path,
                                          scenarioRunResult);
        verify(importsWidget).setContent(any(AsyncPackageDataModelOracle.class),
                                         eq(scenarioRunResult.getImports()),
                                         anyBoolean());

        editor.save("Commit message");

        verify(service).save(any(Path.class),
                             scenarioArgumentCaptor.capture(),
                             any(Metadata.class),
                             anyString());

        assertEquals(scenarioRunResult,
                     scenarioArgumentCaptor.getValue());
    }

    @Test
    public void testEmptyScenario() throws Exception {

        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path,
                         placeRequest);

        verify(view).renderFixtures(eq(path),
                                    any(AsyncPackageDataModelOracle.class),
                                    eq(scenario));
    }

    @Test
    public void testRunScenario() throws Exception {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path,
                         placeRequest);

        verify(view).initKSessionSelector(eq(path),
                                          any(Scenario.class));

        reset(view);

        presenter.onRunScenario();

        InOrder inOrder = inOrder(view);
        inOrder.verify(view)
                .showResults();
        inOrder.verify(view)
                .showAuditView(anySet());
        inOrder.verify(view)
                .initKSessionSelector(eq(path),
                                      any(Scenario.class));
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        editor.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(Command.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder).addRename(any(Path.class),
                                          any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder).addDelete(any(Path.class));
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(mock(Project.class)).when(workbenchContext).getActiveProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        editor.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(Command.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(DefaultFileNameValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class));
    }

    private Event<NotificationEvent> makeNotificationEvent() {
        return new Event<NotificationEvent>() {
            @Override
            public void fire(NotificationEvent notificationEvent) {
            }

            @Override
            public Event<NotificationEvent> select(Annotation... annotations) {
                return null;
            }

            @Override
            public <U extends NotificationEvent> Event<U> select(Class<U> aClass,
                                                                 Annotation... annotations) {
                return null;
            }
        };
    }
}

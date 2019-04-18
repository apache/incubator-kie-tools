/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.page.audit.AuditPage;
import org.drools.workbench.screens.testscenario.client.page.settings.SettingsPage;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.client.docks.DefaultEditorDock;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilderImpl;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.validation.AssetUpdateValidator;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.kie.workbench.common.workbench.client.test.TestReportingDocksHandler;
import org.kie.workbench.common.workbench.client.test.TestRunnerReportingPanel;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ScenarioEditorPresenterTest {

    @Mock
    protected AlertsButtonMenuItemBuilder alertsButtonMenuItemBuilder;
    @Mock
    protected MenuItem alertsButtonMenuItem;
    @Mock
    CommonConstants commonConstants;
    @Mock
    TestRunnerReportingPanel testRunnerReportingPanel;
    @Mock
    TestReportingDocksHandler testReportingDocksHandler;
    @Mock
    DefaultEditorDock docks;
    @Mock
    private PlaceManager placeManager;
    @Mock
    private EventSourceMock showTestPanelEvent;
    @Mock
    private EventSourceMock hideTestPanelEvent;
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
    private BasicFileMenuBuilder menuBuilder;
    @Spy
    @InjectMocks
    private FileMenuBuilderImpl fileMenuBuilder;
    @Mock
    private ProjectController projectController;
    @Mock
    private WorkspaceProjectContext workbenchContext;
    @Mock
    private SettingsPage settingsPage;
    @Mock
    private AuditPage auditPage;
    @Mock
    private PerspectiveManager perspectiveManager;
    private CallerMock<ScenarioTestEditorService> fakeService;
    private ScenarioEditorPresenter editor;
    private Scenario scenario;
    private Overview overview;
    private Scenario scenarioRunResult = null;

    @Before
    public void setUp() throws Exception {

        final AsyncPackageDataModelOracleFactory modelOracleFactory = mock(AsyncPackageDataModelOracleFactory.class);

        fakeService = new CallerMock<>(service);
        editor = spy(new ScenarioEditorPresenter(view,
                                                 user,
                                                 importsWidget,
                                                 fakeService,
                                                 new TestScenarioResourceType(new Decision()),
                                                 modelOracleFactory,
                                                 settingsPage,
                                                 auditPage,
                                                 testRunnerReportingPanel,
                                                 testReportingDocksHandler,
                                                 showTestPanelEvent,
                                                 hideTestPanelEvent) {
            {
                docks = ScenarioEditorPresenterTest.this.docks;
                kieView = ScenarioEditorPresenterTest.this.kieView;
                versionRecordManager = ScenarioEditorPresenterTest.this.versionRecordManager;
                overviewWidget = ScenarioEditorPresenterTest.this.overviewWidget;
                notification = makeNotificationEvent();
                fileMenuBuilder = ScenarioEditorPresenterTest.this.fileMenuBuilder;
                projectController = ScenarioEditorPresenterTest.this.projectController;
                workbenchContext = ScenarioEditorPresenterTest.this.workbenchContext;
                versionRecordManager = ScenarioEditorPresenterTest.this.versionRecordManager;
                alertsButtonMenuItemBuilder = ScenarioEditorPresenterTest.this.alertsButtonMenuItemBuilder;
                perspectiveManager = ScenarioEditorPresenterTest.this.perspectiveManager;
                placeManager = ScenarioEditorPresenterTest.this.placeManager;
            }

            @Override
            protected Command getSaveAndRename() {
                return mock(Command.class);
            }
        });

        doNothing().when(editor).addDownloadMenuItem(any());

        scenarioRunResult = new Scenario();
        scenario = new Scenario();
        overview = new Overview();

        when(user.getIdentifier()).thenReturn("userName");

        when(workbenchContext.getActiveOrganizationalUnit()).thenReturn(Optional.empty());
        when(workbenchContext.getActiveWorkspaceProject()).thenReturn(Optional.empty());

        final TestScenarioModelContent testScenarioModelContent = new TestScenarioModelContent(scenario,
                                                                                               overview,
                                                                                               "org.test",
                                                                                               new PackageDataModelOracleBaselinePayload());

        when(service.loadContent(any(Path.class))).thenReturn(testScenarioModelContent);

        final TestScenarioResult result = new TestScenarioResult(scenarioRunResult,
                                                                 Collections.EMPTY_SET,
                                                                 mock(TestResultMessage.class));
        when(service.runScenario(eq("userName"),
                                 any(Path.class),
                                 eq(scenario))).thenReturn(result);

        final AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
        when(modelOracleFactory.makeAsyncPackageDataModelOracle(any(Path.class),
                                                                any(HasImports.class),
                                                                any(PackageDataModelOracleBaselinePayload.class))
        ).thenReturn(dmo);

        when(alertsButtonMenuItemBuilder.build()).thenReturn(alertsButtonMenuItem);
        when(perspectiveManager.getCurrentPerspective()).thenReturn(mock(PerspectiveActivity.class));
    }

    @Test
    public void showDiagramEditorDocks() {
        DefaultPlaceRequest place = new DefaultPlaceRequest(ScenarioEditorPresenter.IDENTIFIER);

        editor.onStartup(mock(ObservablePath.class),
                         place);

        editor.onShowDiagramEditorDocks(new PlaceGainFocusEvent(place));

        verify(showTestPanelEvent).fire(any());
    }

    @Test
    public void showDiagramEditorDocksWrongPlaceName() {
        editor.onStartup(mock(ObservablePath.class),
                         new DefaultPlaceRequest(ScenarioEditorPresenter.IDENTIFIER));

        editor.onShowDiagramEditorDocks(new PlaceGainFocusEvent(new DefaultPlaceRequest("wrong name")));

        verify(showTestPanelEvent, never()).fire(any());
    }

    @Test
    public void testSimple() throws Exception {
        verify(view).setPresenter(any(ScenarioEditorPresenter.class));
    }

    @Test
    public void hideDiagramEditorDocks() {
        DefaultPlaceRequest place = new DefaultPlaceRequest(ScenarioEditorPresenter.IDENTIFIER);

        editor.onStartup(mock(ObservablePath.class),
                         place);
        verify(testRunnerReportingPanel).reset();

        editor.onHideDocks(new PlaceHiddenEvent(place));
        verify(hideTestPanelEvent).fire(any());
        verify(testRunnerReportingPanel, times(2)).reset();
    }

    @Test
    public void hideDiagramEditorDocksWrongPlaceName() {
        editor.onStartup(mock(ObservablePath.class),
                         new DefaultPlaceRequest(ScenarioEditorPresenter.IDENTIFIER));

        editor.onHideDocks(new PlaceHiddenEvent(new DefaultPlaceRequest("wrong name")));
        verify(hideTestPanelEvent, never()).fire(any());
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

        editor.onRunScenario();

        // Make sure imports are updated
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
    public void testKiePageRefreshAfterContentLoaded() throws Exception {
        final ObservablePath path = mock(ObservablePath.class);
        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.loadContent();

        verify(kieView).addPage(settingsPage);
        verify(kieView).addPage(auditPage);
        verify(settingsPage).refresh(view, path, scenario);
    }

    @Test
    public void testRunScenario() throws Exception {
        final ObservablePath path = mock(ObservablePath.class);
        final PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path,
                         placeRequest);

        reset(view);

        editor.onRunScenario();

        InOrder inOrder = inOrder(view);
        inOrder.verify(view).showBusyIndicator(TestScenarioConstants.INSTANCE.BuildingAndRunningScenario());
        inOrder.verify(view).showResults();
        inOrder.verify(view).hideBusyIndicator();

        verify(settingsPage).refresh(view, path, scenario);

        verify(auditPage).showFiredRulesAuditLog(Collections.emptySet());
        verify(auditPage).showFiredRules(notNull(ExecutionTrace.class));
    }

    @Test
    public void testRunScenarioFail() throws Exception {
        final TestRunFailedErrorCallback callback = mock(TestRunFailedErrorCallback.class);

        doReturn(true)
                .when(callback)
                .error(any(Message.class),
                       any(RuntimeException.class));
        doReturn(callback).when(editor).getTestRunFailedCallback();
        doThrow(new RuntimeException("some problem")).when(service).runScenario(anyString(),
                                                                                any(Path.class),
                                                                                any(Scenario.class));
        editor.onRunScenario();

        verify(callback).error(any(Message.class),
                               any(RuntimeException.class));
        verify(view).showBusyIndicator(TestScenarioConstants.INSTANCE.BuildingAndRunningScenario());
    }

    @Test
    public void testMakeMenuBar() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(true).when(projectController).canUpdateProject(any());

        editor.makeMenuBar();

        verify(fileMenuBuilder).addSave(any(Command.class));
        verify(fileMenuBuilder).addCopy(any(Path.class),
                                        any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addRename(any(Command.class));
        verify(fileMenuBuilder).addDelete(any(Path.class),
                                          any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
        verify(editor).addDownloadMenuItem(fileMenuBuilder);
    }

    @Test
    public void testMakeMenuBarWithoutUpdateProjectPermission() {
        doReturn(Optional.of(mock(WorkspaceProject.class))).when(workbenchContext).getActiveWorkspaceProject();
        doReturn(false).when(projectController).canUpdateProject(any());

        editor.makeMenuBar();

        verify(fileMenuBuilder,
               never()).addSave(any(Command.class));
        verify(fileMenuBuilder,
               never()).addCopy(any(Path.class),
                                any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addRename(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder,
               never()).addDelete(any(Path.class),
                                  any(AssetUpdateValidator.class));
        verify(fileMenuBuilder).addNewTopLevelMenu(alertsButtonMenuItem);
    }

    @Test
    public void testGetContentSupplier() throws Exception {

        final Scenario content = mock(Scenario.class);

        doReturn(content).when(editor).getScenario();

        final Supplier<Scenario> contentSupplier = editor.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() throws Exception {

        final Caller<? extends SupportsSaveAndRename<Scenario, Metadata>> serviceCaller = editor.getSaveAndRenameServiceCaller();

        assertEquals(fakeService, serviceCaller);
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

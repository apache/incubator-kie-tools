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

import java.util.HashSet;
import javax.enterprise.event.Event;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.type.TestScenarioResourceType;
import org.drools.workbench.screens.testscenario.model.TestScenarioModelContent;
import org.drools.workbench.screens.testscenario.model.TestScenarioResult;
import org.drools.workbench.screens.testscenario.service.ScenarioTestEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.guvnor.common.services.shared.test.TestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracleFactory;
import org.kie.workbench.common.widgets.configresource.client.widget.bound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.KieEditorWrapperView;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.multipage.MultiPageEditor;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioEditorPresenterTest {

    @Mock private KieEditorWrapperView    kieView;
    @Mock private ScenarioEditorView      view;
    @Mock private VersionRecordManager    versionRecordManager;
    @Mock private OverviewWidgetPresenter overviewWidget;
    @Mock private MultiPageEditor         multiPage;
    @Mock private ImportsWidgetPresenter importsWidget;

    private ScenarioTestEditorServiceCallerMock service;

    private ScenarioEditorPresenter      editor;
    private ScenarioEditorView.Presenter presenter;
    private Scenario                     scenario;

    private Overview overview;
    private Scenario scenarioRunResult = null;

    @Before
    public void setUp() throws Exception {

        AsyncPackageDataModelOracleFactory modelOracleFactory = mock(AsyncPackageDataModelOracleFactory.class);
        service = new ScenarioTestEditorServiceCallerMock();
        editor = new ScenarioEditorPresenter(view,
                                             importsWidget,
                                             service,
                                             new TestServiceCallerMock(),
                                             new TestScenarioResourceType(),
                                             modelOracleFactory) {
            {
                kieView = ScenarioEditorPresenterTest.this.kieView;
                versionRecordManager = ScenarioEditorPresenterTest.this.versionRecordManager;
                overviewWidget = ScenarioEditorPresenterTest.this.overviewWidget;

            }

            protected void makeMenuBar() {

            }
        };
        presenter = editor;

        scenarioRunResult = new Scenario();
        scenario = new Scenario();
        overview = new Overview();

        AsyncPackageDataModelOracle dmo = mock(AsyncPackageDataModelOracle.class);
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
        ObservablePath path = mock(ObservablePath.class);
        PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path, placeRequest);

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

        assertEquals(scenarioRunResult, service.savedScenario);

    }

    @Test
    public void testEmptyScenario() throws Exception {

        ObservablePath path = mock(ObservablePath.class);
        PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path, placeRequest);

        verify(view).renderFixtures(eq(path), any(AsyncPackageDataModelOracle.class), eq(scenario));

    }

    @Test
    public void testRunScenario() throws Exception {
        ObservablePath path = mock(ObservablePath.class);
        PlaceRequest placeRequest = mock(PlaceRequest.class);

        when(versionRecordManager.getCurrentPath()).thenReturn(path);

        editor.onStartup(path, placeRequest);

        verify(view).initKSessionSelector(eq(path), any(Scenario.class));

        reset(view);

        presenter.onRunScenario();

        verify(view).initKSessionSelector(eq(path), any(Scenario.class));
        verify(view).showAuditView(anySet());
    }

    class ScenarioTestEditorServiceCallerMock
            implements Caller<ScenarioTestEditorService> {

        RemoteCallback remoteCallback;

        ScenarioTestEditorService service = new ScenarioTestEditorServiceMock();

        Scenario savedScenario = null;

        @Override public ScenarioTestEditorService call() {
            return service;
        }

        @Override public ScenarioTestEditorService call(RemoteCallback<?> remoteCallback) {
            return call(remoteCallback, null);
        }

        @Override public ScenarioTestEditorService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            this.remoteCallback = remoteCallback;
            return service;
        }

        private class ScenarioTestEditorServiceMock implements ScenarioTestEditorService {

            @Override public TestScenarioModelContent loadContent(Path path) {
                TestScenarioModelContent testScenarioModelContent = new TestScenarioModelContent(scenario, overview, "org.test", new PackageDataModelOracleBaselinePayload());
                remoteCallback.callback(testScenarioModelContent);
                return testScenarioModelContent;
            }

            @Override public TestScenarioResult runScenario(Path path, Scenario scenario) {
                TestScenarioResult result = new TestScenarioResult("user",
                                                                   scenarioRunResult,
                                                                   new HashSet<String>());
                remoteCallback.callback(result);
                return null;
            }

            @Override public Path copy(Path path, String s, String s1) {
                return null;
            }

            @Override public Path create(Path path, String s, Scenario scenario, String s1) {
                return null;
            }

            @Override public void delete(Path path, String s) {

            }

            @Override public Scenario load(Path path) {
                return null;
            }

            @Override public Path rename(Path path, String s, String s1) {
                return null;
            }

            @Override public Path save(Path path, Scenario content, Metadata metadata, String comment) {

                savedScenario = content;

                return null;
            }
        }
    }

    class TestServiceCallerMock
            implements Caller<TestService> {

        RemoteCallback remoteCallback;

        TestServiceMock service = new TestServiceMock();

        @Override public TestService call() {
            return null;
        }

        @Override public TestService call(RemoteCallback<?> remoteCallback) {
            return call(remoteCallback, null);
        }

        @Override public TestService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            this.remoteCallback = remoteCallback;
            return service;
        }

        private class TestServiceMock implements TestService {

            @Override public void runAllTests(Path path) {
            }

            @Override public void runAllTests(Path path, Event<TestResultMessage> customTestResultEvent) {

            }
        }
    }
}

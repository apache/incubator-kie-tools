/*
 * Copyright 2015 JBoss Inc
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

import java.util.List;
import java.util.Set;

import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.workingset.client.model.WorkingSetSettings;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class KSessionSelectorTest {

    private KSessionSelector               selector;
    private KSessionSelectorView           view;
    private KSessionSelectorView.Presenter presenter;
    private Path                           path;
    private KieProject                     kieProject;
    private KModuleModel                   kModule;

    @Before
    public void setUp() throws Exception {

        path = mock(Path.class);

        kieProject = spy(new KieProject());
        when(kieProject.getKModuleXMLPath()).thenReturn(path);

        kModule = new KModuleModel();

        kModule.getKBases().put("kbase1", getKBase("kbase1", "ksession1"));
        kModule.getKBases().put("kbase2", getKBase("kbase2", "ksession1", "ksession2"));
        kModule.getKBases().put("kbase3", getKBase("kbase3"));

        view = mock(KSessionSelectorView.class);

        selector = new KSessionSelector(
                view,
                new KieProjectServiceCallerMock(),
                new KModuleServiceCallerMock());

        presenter = selector;
    }


    @Test
    public void testSimple() throws Exception {
        view.setPresenter(presenter);
    }

    // TODO: Change needs to clean the list in scenario

    @Test
    public void testSetKBaseAndKSession() throws Exception {
        Scenario scenario = new Scenario();
        scenario.getKSessions().add("ksession2");
        selector.init(path, scenario);

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());
        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(2, ksessionNamesList.size());

        verify(view).setSelected("kbase2", "ksession2");
    }

    @Test
    public void testKBaseAndKSessionNotPreviouslySet() throws Exception {
        Scenario scenario = new Scenario();
        selector.init(path, scenario);

        verify(view).addKBase("kbase1");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());
        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1, ksessionNamesList.size());

        verify(view).setSelected("kbase1", "ksession1");

        assertEquals("ksession1", scenario.getKSessions().iterator().next());

    }

    @Test
    public void testEmpty() throws Exception {
        // No kbases or ksessions defined in the kmodule.xml
        this.kModule = new KModuleModel();

        Scenario scenario = new Scenario();
        selector.init(path, scenario);

        verify(view).addKBase("defaultKieBase");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());

        verify(view).setSelected(eq("defaultKieBase"), eq("defaultKieSession"));

        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1, ksessionNamesList.size());

        assertEquals("defaultKieSession", ksessionNamesList.iterator().next());

        // Model needs to be updated
        assertEquals(1, scenario.getKSessions().size());
        assertEquals("defaultKieSession", scenario.getKSessions().iterator().next());
    }

    @Test
    public void testKSessionDefinedInScenarioNoLongerExists() throws Exception {

        Scenario scenario = new Scenario();
        scenario.getKSessions().add("ksessionThatHasBeenRemovedFromKModuleXML");
        selector.init(path, scenario);

        verify(view).addKBase("kbase1");
        verify(view).addKBase("kbase2");
        verify(view).addKBase("kbase3");
        verify(view).addKBase("---");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());

        verify(view).setSelected(eq("---"), eq("ksessionThatHasBeenRemovedFromKModuleXML"));

        verify(view).showWarningSelectedKSessionDoesNotExist();

        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1, ksessionNamesList.size());

        assertEquals("ksessionThatHasBeenRemovedFromKModuleXML", ksessionNamesList.get(0));

        // Model needs to be updated
        assertEquals(1, scenario.getKSessions().size());
        assertEquals("ksessionThatHasBeenRemovedFromKModuleXML", scenario.getKSessions().iterator().next());

    }

    @Test
    public void testKSessionDefinedInScenarioNoLongerExistsAndKModuleIsEmpty() throws Exception {
        // No kbases or ksessions defined in the kmodule.xml
        this.kModule = new KModuleModel();

        Scenario scenario = new Scenario();
        scenario.getKSessions().add("ksessionThatHasBeenRemovedFromKModuleXML");
        selector.init(path, scenario);

        verify(view).addKBase("defaultKieBase");
        verify(view).addKBase("---");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());

        verify(view).setSelected(eq("---"), eq("ksessionThatHasBeenRemovedFromKModuleXML"));

        verify(view).showWarningSelectedKSessionDoesNotExist();

        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1, ksessionNamesList.size());

        assertEquals("ksessionThatHasBeenRemovedFromKModuleXML", ksessionNamesList.get(0));

        // Model needs to be updated
        assertEquals(1, scenario.getKSessions().size());
        assertEquals("ksessionThatHasBeenRemovedFromKModuleXML", scenario.getKSessions().iterator().next());

    }

    @Test
    public void testChangeSelection() throws Exception {

        Scenario scenario = new Scenario();
        scenario.getKSessions().add("ksessionThatHasBeenRemovedFromKModuleXML");
        selector.init(path, scenario);

        presenter.onKBaseSelected("kbase2");

        // Model needs to be updated
        assertEquals(1, scenario.getKSessions().size());
        assertEquals("ksession1", scenario.getKSessions().iterator().next());

    }

    private KBaseModel getKBase(final String kbaseName,
                                final String... ksessionNames) {
        return new KBaseModel() {{
            setName(kbaseName);
            for (final String ksessionName : ksessionNames) {
                getKSessions().add(new KSessionModel() {{
                    setName(ksessionName);
                }});
            }
        }};
    }

    class KieProjectServiceCallerMock
            implements Caller<KieProjectService> {

        private KieProjectService kieProjectService = new KieProjectServiceMock();

        RemoteCallback remoteCallback;

        @Override public KieProjectService call() {
            return kieProjectService;
        }

        @Override public KieProjectService call(RemoteCallback<?> remoteCallback) {
            return call(remoteCallback, null);
        }

        @Override public KieProjectService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            this.remoteCallback = remoteCallback;
            return kieProjectService;
        }

        class KieProjectServiceMock
                implements KieProjectService {

            @Override public WorkingSetSettings loadWorkingSetConfig(Path project) {
                return null;
            }

            @Override public KieProject resolveProject(Path resource) {
                remoteCallback.callback(kieProject);
                return null;
            }

            @Override public Project resolveParentProject(Path resource) {
                return null;
            }

            @Override public Project resolveToParentProject(Path resource) {
                return null;
            }

            @Override public Set<Project> getProjects(Repository repository, String branch) {
                return null;
            }

            @Override public org.guvnor.common.services.project.model.Package resolvePackage(Path resource) {
                return null;
            }

            @Override public Set<org.guvnor.common.services.project.model.Package> resolvePackages(Project project) {
                return null;
            }

            @Override public Set<Package> resolvePackages(Package pkg) {
                return null;
            }

            @Override public Package resolveDefaultPackage(Project project) {
                return null;
            }

            @Override public Package resolveParentPackage(Package pkg) {
                return null;
            }

            @Override public boolean isPom(Path resource) {
                return false;
            }

            @Override public KieProject newProject(Repository repository, String name, POM pom, String baseURL) {
                return null;
            }

            @Override public Package newPackage(Package pkg, String packageName) {
                return null;
            }

            @Override public void addRole(Project project, String role) {

            }

            @Override public void removeRole(Project project, String role) {

            }

            @Override public Path rename(Path pathToPomXML, String newName, String comment) {
                return null;
            }

            @Override public void delete(Path pathToPomXML, String comment) {

            }

            @Override public void copy(Path pathToPomXML, String newName, String comment) {

            }
        }
    }

    class KModuleServiceCallerMock
            implements Caller<KModuleService> {

        private KModuleService kModuleService = new KModuleServiceMock();

        RemoteCallback remoteCallback;

        @Override public KModuleService call() {
            return kModuleService;
        }

        @Override public KModuleService call(RemoteCallback<?> remoteCallback) {
            return call(remoteCallback, null);
        }

        @Override public KModuleService call(RemoteCallback<?> remoteCallback, ErrorCallback<?> errorCallback) {
            this.remoteCallback = remoteCallback;
            return kModuleService;
        }

        class KModuleServiceMock
                implements KModuleService {

            @Override public boolean isKModule(Path resource) {
                return false;
            }

            @Override public Path setUpKModuleStructure(Path projectRoot) {
                return null;
            }

            @Override public KModuleModel load(Path path) {
                remoteCallback.callback(kModule);
                return null;
            }

            @Override public Path save(Path path, KModuleModel content, Metadata metadata, String comment) {
                return null;
            }
        }
    }
}
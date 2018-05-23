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

package org.kie.workbench.common.widgets.client.widget;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KSessionSelectorTest {

    @Mock
    private Path path;

    @Mock
    private Path kmodulePath;

    private KieModule kieModule;

    @Mock
    private KieModuleService kieModuleService;

    @Mock
    private KModuleService kModuleService;

    private KSessionSelector selector;
    private KSessionSelectorView view;
    private KModuleModel kModule;

    @Before
    public void setUp() throws Exception {

        kieModule = spy(new KieModule());

        when(kieModule.getKModuleXMLPath()).thenReturn(kmodulePath);

        kModule = new KModuleModel();

        kModule.getKBases().put("kbase1",
                                getKBase("kbase1",
                                         "ksession1"));
        kModule.getKBases().put("kbase2",
                                getKBase("kbase2",
                                         "ksession2",
                                         "ksession3"));
        kModule.getKBases().put("kbase3",
                                getKBase("kbase3"));

        view = mock(KSessionSelectorView.class);

        when(kieModuleService.resolveModule(path)).thenReturn(kieModule);

        when(kModuleService.load(kmodulePath)).thenReturn(kModule);

        selector = new KSessionSelector(
                view,
                new CallerMock<>(kieModuleService),
                new CallerMock<>(kModuleService));
    }

    @Test
    public void testSetPresenter() throws Exception {
        view.setPresenter(selector);
    }

    @Test
    public void testSetKBaseAndKSession() throws Exception {

        selector.init(path,
                      "ksession2");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());
        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(2,
                     ksessionNamesList.size());

        verify(view).setSelected("kbase2",
                                 "ksession2");
    }

    @Test
    public void testKBaseAndKSessionNotPreviouslySet() throws Exception {
        selector.init(path,
                      null);
        verify(view).addKBases("kbase1",
                               "kbase2",
                               "kbase3");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());
        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1,
                     ksessionNamesList.size());

        verify(view).setSelected("kbase1",
                                 "ksession1");
    }

    @Test
    public void testEmpty() throws Exception {
        // No kbases or ksessions defined in the kmodule.xml
        when(kModuleService.load(kmodulePath)).thenReturn(new KModuleModel());

        selector.init(path,
                      null);

        verify(view).addKBases("defaultKieBase");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());

        verify(view).setSelected(eq("defaultKieBase"),
                                 eq("defaultKieSession"));

        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1,
                     ksessionNamesList.size());

        assertEquals("defaultKieSession",
                     ksessionNamesList.iterator().next());
    }

    @Test
    public void testKSessionDefinedInScenarioNoLongerExists() throws Exception {

        selector.init(path,
                      "ksessionThatHasBeenRemovedFromKModuleXML");

        verify(view).addKBases("kbase1", "kbase2", "kbase3", "---");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());

        verify(view).setSelected(eq("---"),
                                 eq("ksessionThatHasBeenRemovedFromKModuleXML"));

        verify(view).showWarningSelectedKSessionDoesNotExist();

        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1,
                     ksessionNamesList.size());

        assertEquals("ksessionThatHasBeenRemovedFromKModuleXML",
                     ksessionNamesList.get(0));
    }

    @Test
    public void testKSessionDefinedInScenarioNoLongerExistsAndKModuleIsEmpty() throws Exception {
        // No kbases or ksessions defined in the kmodule.xml
        when(kModuleService.load(kmodulePath)).thenReturn(new KModuleModel());

        selector.init(path,
                      "ksessionThatHasBeenRemovedFromKModuleXML");

        verify(view).addKBases("defaultKieBase", "---");

        ArgumentCaptor<List> listArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).setKSessions(listArgumentCaptor.capture());

        verify(view).setSelected(eq("---"),
                                 eq("ksessionThatHasBeenRemovedFromKModuleXML"));

        verify(view).showWarningSelectedKSessionDoesNotExist();

        List ksessionNamesList = listArgumentCaptor.getValue();
        assertEquals(1,
                     ksessionNamesList.size());

        assertEquals("ksessionThatHasBeenRemovedFromKModuleXML",
                     ksessionNamesList.get(0));
    }

    @Test
    public void testChangeSelection() throws Exception {

        selector.init(path,
                      "ksessionThatHasBeenRemovedFromKModuleXML");

        reset(view);

        selector.onKBaseSelected("kbase2");

        verify(view).setSelected("kbase2",
                                 "ksession2");
    }

    @Test
    public void onSelectionChange() {
        Command command = mock(Command.class);

        selector.setSelectionChangeHandler(command);

        selector.onSelectionChange();

        verify(command,
               times(1)).execute();
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
}
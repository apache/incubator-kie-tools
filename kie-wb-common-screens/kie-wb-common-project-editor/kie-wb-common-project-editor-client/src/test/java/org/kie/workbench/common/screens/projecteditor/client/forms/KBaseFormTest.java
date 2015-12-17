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

package org.kie.workbench.common.screens.projecteditor.client.forms;

public class KBaseFormTest {


//    private KBaseForm form;
//    private KBaseFormView view;
//    private KBaseFormView.Presenter presenter;
//
//    @Before
//    public void setUp() throws Exception {
//        view = mock(KBaseFormView.class);
//        form = new KBaseForm(view);
//        presenter = form;
//    }
//
//    @Test
//    public void testCleanUp() throws Exception {
//        verify(view).setPresenter(presenter);
//        form.setModel(new KBaseModel());
//        verify(view).setName(null);
//
//        ArgumentCaptor<List> statelessSessionModelArgumentCaptor = ArgumentCaptor.forClass(List.class);
//        ArgumentCaptor<List> statefulModelArgumentCaptor = ArgumentCaptor.forClass(List.class);
//
//        verify(view).setStatefulSessions(statefulModelArgumentCaptor.capture());
//
//        verify(view).setEventProcessingModeStream();
//        verify(view, never()).setEventProcessingModeCloud();
//        verify(view).setEqualsBehaviorIdentity();
//        verify(view, never()).setEqualsBehaviorEquality();
//
//        assertEquals(0, statelessSessionModelArgumentCaptor.getValue().size());
//        assertEquals(0, statefulModelArgumentCaptor.getValue().size());
//    }
//
//    @Test
//    public void testShowSimpleData() throws Exception {
//        KBaseModel config = new KBaseModel();
//        config.setName("Name");
//
//        config.setEqualsBehavior( AssertBehaviorOption.EQUALITY);
//        config.setEventProcessingMode( EventProcessingOption.CLOUD);
//
//        config.getKSessions().add(createStatelessKSession("1"));
//        config.getKSessions().add(createStatelessKSession("2"));
//        config.getKSessions().add(createStatelessKSession("3"));
//
//        config.getKSessions().add(createStatefulKSession("4"));
//        config.getKSessions().add(createStatefulKSession("5"));
//
//        form.setModel(config);
//        verify(view).setName("Name");
//
//        ArgumentCaptor<List> statelessSessionModelArgumentCaptor = ArgumentCaptor.forClass(List.class);
//        ArgumentCaptor<List> statefulModelArgumentCaptor = ArgumentCaptor.forClass(List.class);
//
//        verify(view).setStatefulSessions(statefulModelArgumentCaptor.capture());
//
//        verify(view, never()).setEventProcessingModeStream();
//        verify(view).setEventProcessingModeCloud();
//        verify(view, never()).setEqualsBehaviorIdentity();
//        verify(view).setEqualsBehaviorEquality();
//
//        assertEquals(3, statelessSessionModelArgumentCaptor.getValue().size());
//        assertEquals(2, statefulModelArgumentCaptor.getValue().size());
//    }
//
//    @Test
//    public void testEqualsBehaviorChange() throws Exception {
//        KBaseModel config = new KBaseModel();
//
//        form.setModel(config);
//
//        // Default
//        assertEquals(AssertBehaviorOption.IDENTITY, config.getEqualsBehavior());
//
//        // Toggle
//        presenter.onEqualsBehaviorEqualitySelect();
//        assertEquals(AssertBehaviorOption.EQUALITY, config.getEqualsBehavior());
//
//        presenter.onEqualsBehaviorIdentitySelect();
//        assertEquals(AssertBehaviorOption.IDENTITY, config.getEqualsBehavior());
//    }
//
//    @Test
//    public void testEventProcessingModeStreamChange() throws Exception {
//        KBaseModel config = new KBaseModel();
//
//        form.setModel(config);
//
//        // Default
//        assertEquals(EventProcessingOption.STREAM, config.getEventProcessingMode());
//
//        // Toggle
//        presenter.onEventProcessingModeCloudSelect();
//        assertEquals(EventProcessingOption.CLOUD, config.getEventProcessingMode());
//
//        presenter.onEventProcessingModeStreamSelect();
//        assertEquals(EventProcessingOption.STREAM, config.getEventProcessingMode());
//    }
//
//    private KSessionModel createStatefulKSession(String fullName) {
//        KSessionModel kSessionModel = new KSessionModel();
//        kSessionModel.setName(fullName);
//        kSessionModel.setType("stateful");
//        return kSessionModel;
//    }
//
//    private KSessionModel createStatelessKSession(String fullName) {
//        KSessionModel kSessionModel = new KSessionModel();
//        kSessionModel.setName(fullName);
//        kSessionModel.setType("stateless");
//        return kSessionModel;
//    }

}

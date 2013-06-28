/*
 * Copyright 2012 JBoss Inc
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

import java.util.Map;

import org.guvnor.common.services.project.model.AssertBehaviorOption;
import org.guvnor.common.services.project.model.EventProcessingOption;
import org.guvnor.common.services.project.model.KBaseModel;
import org.guvnor.common.services.project.model.KSessionModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class KBaseFormTest {


    private KBaseForm form;
    private KBaseFormView view;
    private KBaseFormView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        view = mock(KBaseFormView.class);
        form = new KBaseForm(view);
        presenter = form;
    }

    @Test
    public void testCleanUp() throws Exception {
        verify(view).setPresenter(presenter);
        form.setModel(new KBaseModel());
        verify(view).setName(null);

        ArgumentCaptor<Map> statelessSessionModelArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> statefulModelArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        verify(view).setStatefulSessions(statefulModelArgumentCaptor.capture());
        verify(view).setStatelessSessions(statelessSessionModelArgumentCaptor.capture());

        verify(view).setEventProcessingModeStream();
        verify(view, never()).setEventProcessingModeCloud();
        verify(view).setEqualsBehaviorIdentity();
        verify(view, never()).setEqualsBehaviorEquality();

        assertEquals(0, statelessSessionModelArgumentCaptor.getValue().size());
        assertEquals(0, statefulModelArgumentCaptor.getValue().size());
    }

    @Test
    public void testShowSimpleData() throws Exception {
        KBaseModel config = new KBaseModel();
        config.setName("Name");

        config.setEqualsBehavior( AssertBehaviorOption.EQUALITY);
        config.setEventProcessingMode( EventProcessingOption.CLOUD);

        config.getStatelessSessions().put("1", createStatelessKSession("1"));
        config.getStatelessSessions().put("2", createStatelessKSession("2"));
        config.getStatelessSessions().put("3", createStatelessKSession("3"));

        config.getStatefulSessions().put("4,", createStatefulKSession("4"));
        config.getStatefulSessions().put("5,", createStatefulKSession("5"));

        form.setModel(config);
        verify(view).setName("Name");

        ArgumentCaptor<Map> statelessSessionModelArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> statefulModelArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        verify(view).setStatefulSessions(statefulModelArgumentCaptor.capture());
        verify(view).setStatelessSessions(statelessSessionModelArgumentCaptor.capture());

        verify(view, never()).setEventProcessingModeStream();
        verify(view).setEventProcessingModeCloud();
        verify(view, never()).setEqualsBehaviorIdentity();
        verify(view).setEqualsBehaviorEquality();

        assertEquals(3, statelessSessionModelArgumentCaptor.getValue().size());
        assertEquals(2, statefulModelArgumentCaptor.getValue().size());
    }

    @Test
    public void testEqualsBehaviorChange() throws Exception {
        KBaseModel config = new KBaseModel();

        form.setModel(config);

        // Default
        assertEquals(AssertBehaviorOption.IDENTITY, config.getEqualsBehavior());

        // Toggle
        presenter.onEqualsBehaviorEqualitySelect();
        assertEquals(AssertBehaviorOption.EQUALITY, config.getEqualsBehavior());

        presenter.onEqualsBehaviorIdentitySelect();
        assertEquals(AssertBehaviorOption.IDENTITY, config.getEqualsBehavior());
    }

    @Test
    public void testEventProcessingModeStreamChange() throws Exception {
        KBaseModel config = new KBaseModel();

        form.setModel(config);

        // Default
        assertEquals(EventProcessingOption.STREAM, config.getEventProcessingMode());

        // Toggle
        presenter.onEventProcessingModeCloudSelect();
        assertEquals(EventProcessingOption.CLOUD, config.getEventProcessingMode());

        presenter.onEventProcessingModeStreamSelect();
        assertEquals(EventProcessingOption.STREAM, config.getEventProcessingMode());
    }

    private KSessionModel createStatefulKSession(String fullName) {
        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setName(fullName);
        kSessionModel.setType("stateful");
        return kSessionModel;
    }

    private KSessionModel createStatelessKSession(String fullName) {
        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setName(fullName);
        kSessionModel.setType("stateless");
        return kSessionModel;
    }

}

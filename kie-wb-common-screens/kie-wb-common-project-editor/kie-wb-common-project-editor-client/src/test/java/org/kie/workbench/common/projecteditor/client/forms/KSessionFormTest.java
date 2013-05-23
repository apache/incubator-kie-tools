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

package org.kie.workbench.common.projecteditor.client.forms;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.projecteditor.client.forms.KSessionForm;
import org.kie.workbench.common.projecteditor.client.forms.KSessionFormView;
import org.kie.workbench.common.services.project.service.model.ClockTypeOption;
import org.kie.workbench.common.services.project.service.model.KSessionModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class KSessionFormTest {

    private KSessionFormView view;
    private KSessionForm form;
    private KSessionFormView.Presenter presenter;

    @Before
    public void setUp() throws Exception {
        view = mock(KSessionFormView.class);
        form = new KSessionForm(view);
        presenter = form;
    }

    @Test
    public void testSimpleSetUp() throws Exception {
        KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setName("Test");

        form.setModel(kSessionModel);

        verify(view).setPresenter(presenter);
        verify(view).setName("Test");
    }

    @Test
    public void testClockTypeChange() throws Exception {
        KSessionModel model = new KSessionModel();

        form.setModel(model);

        // Check default
        verify(view).selectRealtime();
        assertEquals(ClockTypeOption.REALTIME, model.getClockType());

        presenter.onPseudoSelect();
        assertEquals(ClockTypeOption.PSEUDO, model.getClockType());

        presenter.onRealtimeSelect();
        assertEquals(ClockTypeOption.REALTIME, model.getClockType());
    }
}

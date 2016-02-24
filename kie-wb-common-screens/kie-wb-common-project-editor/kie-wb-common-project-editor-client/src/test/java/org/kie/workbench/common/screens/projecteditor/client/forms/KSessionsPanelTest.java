/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.projecteditor.client.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

@RunWith(GwtMockitoTestRunner.class)
public class KSessionsPanelTest {

    @Mock
    private KSessionsPanelView view;
    @Mock
    private TextBoxFormPopup namePopup;

    @InjectMocks
    private KSessionsPanel kSessionsPanel;

    @Before
    public void setUp() {
        kSessionsPanel = new KSessionsPanel(view, namePopup, mock( EventSourceMock.class ));
    }

    /**
     * BZ 983540 - Add new ksession dialog will not disappear on OK click.
     */
    @Test
    public void testOnAdd() {
        ArgumentCaptor<PopupSetFieldCommand> captor = ArgumentCaptor.forClass(PopupSetFieldCommand.class);
        kSessionsPanel.setItems(new ArrayList<KSessionModel>());

        kSessionsPanel.onAdd();

        verify(namePopup).show(captor.capture());
        captor.getValue().setName("validKSessionName");
        verify(namePopup).hide();
    }

    /**
     * BZ 983528 - Removal of ksession does not work.
     */
    @Test
    public void testOnDelete() {
        ArgumentCaptor<PopupSetFieldCommand> commandCaptor = ArgumentCaptor.forClass(PopupSetFieldCommand.class);
        ArgumentCaptor<ArrayList> listCaptor = ArgumentCaptor.forClass(ArrayList.class);
        KSessionModel model = new KSessionModel();
        model.setName("myKSession");
        kSessionsPanel.setItems(new ArrayList<KSessionModel>());

        kSessionsPanel.onAdd();
        verify(namePopup).show(commandCaptor.capture());
        commandCaptor.getValue().setName(model.getName());
        verify(view, atLeastOnce()).setItemList(listCaptor.capture());

        assertEquals(1, listCaptor.getValue().size());
        assertTrue(listCaptor.getValue().contains(model));

        kSessionsPanel.onDelete(model);

        verify(view, atLeastOnce()).setItemList(listCaptor.capture());
        assertEquals(0, listCaptor.getValue().size());
    }
}

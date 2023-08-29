/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.widgets.toolbar;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CopyToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.CutToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.DeleteSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToJpgToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPdfToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ExportToPngToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.PasteToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.RedoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SaveToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.UndoToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ValidateToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.VisitGraphToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNEditorToolbarTest {

    @Test
    public void testInit() {

        final ManagedToolbar<EditorSession> toolbar = mock(ManagedToolbar.class);
        when(toolbar.register(any())).thenReturn(toolbar);
        final DMNEditorToolbar tested = new DMNEditorToolbar(toolbar);
        tested.init();

        verify(toolbar).register(VisitGraphToolbarCommand.class);

        verify(toolbar).register(ClearToolbarCommand.class);
        verify(toolbar).register(DeleteSelectionToolbarCommand.class);
        verify(toolbar).register(SwitchGridToolbarCommand.class);
        verify(toolbar).register(UndoToolbarCommand.class);
        verify(toolbar).register(RedoToolbarCommand.class);
        verify(toolbar).register(ValidateToolbarCommand.class);
        verify(toolbar).register(ExportToPngToolbarCommand.class);
        verify(toolbar).register(ExportToJpgToolbarCommand.class);
        verify(toolbar).register(ExportToPdfToolbarCommand.class);
        verify(toolbar).register(CopyToolbarCommand.class);
        verify(toolbar).register(CutToolbarCommand.class);
        verify(toolbar).register(PasteToolbarCommand.class);
        verify(toolbar).register(SaveToolbarCommand.class);
        verify(toolbar).register(DMNPerformAutomaticLayoutToolbarCommand.class);
    }
}
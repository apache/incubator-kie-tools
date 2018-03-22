/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.widgets.toolbar.command;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToBpmnSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SessionCommandFactory;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExportToBpmnToolbarCommandTest
        extends AbstractToolbarCommandTest {

    private static final String CAPTION = "CAPTION";

    @Mock
    private SessionCommandFactory sessionCommandFactory;

    @Mock
    private ExportToBpmnSessionCommand sessionCommand;

    @Test
    public void testExport() {
        when(sessionCommandFactory.newExportToBpmnSessionCommand()).thenReturn(sessionCommand);
        when(translationService.getValue(CoreTranslationMessages.EXPORT_BPMN)).thenReturn(CAPTION);
        final ExportToBpmnToolbarCommand tested = new ExportToBpmnToolbarCommand(sessionCommandFactory,
                                                                                 translationService);
        verify(sessionCommandFactory,
               times(1)).newExportToBpmnSessionCommand();
        assertEquals(IconType.FILE_TEXT_O,
                     tested.getIcon());
        assertEquals(CAPTION,
                     tested.getCaption());
        assertEquals(CAPTION,
                     tested.getTooltip());
    }
}
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ExportToSvgSessionCommand;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExportToSvgToolbarCommandTest extends AbstractToolbarCommandTest {

    private static final String TEXT = "Export SVG";

    private ExportToSvgToolbarCommand command;

    @Mock
    private ExportToSvgSessionCommand sessionCommand;

    @Before
    public void setUp() throws Exception {
        when(sessionCommandFactory.newExportToSvgSessionCommand()).thenReturn(sessionCommand);

        when(translationService.getValue(CoreTranslationMessages.EXPORT_SVG)).thenReturn(TEXT);
        command = new ExportToSvgToolbarCommand(sessionCommandFactory, translationService);
    }

    @Test
    public void testInstance() {
        verify(sessionCommandFactory, times(1)).newExportToSvgSessionCommand();
        assertEquals(IconType.FILE_IMAGE_O, command.getIcon());
        assertFalse(command.requiresConfirm());
        assertEquals(TEXT, command.getCaption());
        assertEquals(TEXT, command.getTooltip());
    }
}
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

package org.kie.workbench.common.stunner.forms.client.components.toolbox;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.view.event.MouseClickEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.resources.i18n.FormsClientConstants;
import org.kie.workbench.common.stunner.forms.service.FormGenerationService;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormGenerationToolboxActionTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private ClientFormGenerationManager formGenerationManager;

    @Mock
    private FormGenerationService formGenerationService;

    private FormGenerationToolboxAction tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        doAnswer(invocationOnMock -> {
            final Consumer<FormGenerationService> consumer =
                    (Consumer<FormGenerationService>) invocationOnMock.getArguments()[0];
            consumer.accept(formGenerationService);
            return null;
        }).when(formGenerationManager).call(any(Consumer.class));
        when(translationService.getValue(eq(FormsClientConstants.FormsGenerateTaskForm)))
                .thenReturn("generateTitle");
        tested = new FormGenerationToolboxAction(translationService,
                                                 formGenerationManager);
    }

    @Test
    public void testGetTitle() {
        assertEquals("generateTitle", tested.getTitle(canvasHandler,
                                                      "uuid"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnClick() {
        final MouseClickEvent event = mock(MouseClickEvent.class);
        tested.onMouseClick(canvasHandler,
                            "uuid",
                            event);
        verify(formGenerationManager, times(1)).call(any(Consumer.class));
        verify(formGenerationService, times(1)).generateSelectedForms(eq(diagram),
                                                                      eq(new String[]{"uuid"}));
    }
}

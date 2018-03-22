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

package org.kie.workbench.common.stunner.forms.client.session.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.processing.index.Index;
import org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier;
import org.kie.workbench.common.stunner.forms.client.resources.i18n.FormsClientConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GenerateSelectedFormsSessionCommandTest
        extends AbstractFormsSessionCommandTest {

    private static final String ID1 = "id1";
    private static final String ID2 = "id2";
    private static final Collection<String> SELECTED_ITEMS = Arrays.asList(ID1, ID2);

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private FormGenerationNotifier formGenerationNotifier;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private Predicate<Element> acceptor;

    @Mock
    private Index index;

    @Mock
    private Element element1;

    @Mock
    private Element element2;

    private GenerateSelectedFormsSessionCommand tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        when(canvasHandler.getGraphIndex()).thenReturn(index);
        when(element1.getUUID()).thenReturn(ID1);
        when(element2.getUUID()).thenReturn(ID2);
        when(index.get(eq(ID1))).thenReturn(element1);
        when(index.get(eq(ID2))).thenReturn(element2);
        when(session.getSelectionControl()).thenReturn(selectionControl);
        when(selectionControl.getSelectedItems()).thenReturn(SELECTED_ITEMS);
        when(acceptor.test(any(Element.class))).thenReturn(true);
        tested = new GenerateSelectedFormsSessionCommand(formGenerationManager, formGenerationNotifier, translationService);
        tested.setElementAcceptor(acceptor);
        tested.bind(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateNoElements() {
        when(selectionControl.getSelectedItems()).thenReturn(Collections.emptyList());
        final ClientSessionCommand.Callback callback = mock(ClientSessionCommand.Callback.class);
        tested.execute(callback);
        verify(formGenerationService, never()).generateSelectedForms(eq(diagram),
                                                                     anyObject());
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onError(anyObject());
        verify(acceptor, never()).test(any(Element.class));
        verify(translationService).getValue(FormsClientConstants.FormsNoItemsSelectedForGeneration);
        verify(formGenerationNotifier).showNotification(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateSelected() {
        final ClientSessionCommand.Callback callback = mock(ClientSessionCommand.Callback.class);
        tested.execute(callback);
        ArgumentCaptor<String[]> idsCaptor = ArgumentCaptor.forClass(String[].class);
        verify(formGenerationService, times(1)).generateSelectedForms(eq(diagram),
                                                                      idsCaptor.capture());
        final String[] ids = idsCaptor.getValue();
        assertTrue(Arrays.asList(ids).contains(ID1));
        assertTrue(Arrays.asList(ids).contains(ID2));
        verify(acceptor, times(1)).test(eq(element1));
        verify(acceptor, times(1)).test(eq(element2));
        verify(callback, times(1)).onSuccess();
        verify(callback, never()).onError(anyObject());
        verify(translationService, never()).getValue(FormsClientConstants.FormsNoItemsSelectedForGeneration);
        verify(formGenerationNotifier, never()).showNotification(anyString());
    }
}

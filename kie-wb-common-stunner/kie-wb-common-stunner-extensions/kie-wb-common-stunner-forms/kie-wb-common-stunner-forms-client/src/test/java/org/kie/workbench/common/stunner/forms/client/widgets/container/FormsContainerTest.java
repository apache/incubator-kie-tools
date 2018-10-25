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

package org.kie.workbench.common.stunner.forms.client.widgets.container;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayer;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormsContainerTest {

    private static final String GRAPH_UID = "graphUid";

    private static final String FIRST_ELEMENT_UID = "first_uid";
    private static final String SECOND_ELEMENT_UID = "second_uid";

    @Mock
    private Path path;

    @Mock
    private FieldChangeHandler fieldChangeHandler;

    @Mock
    private FormsContainerView view;

    @Mock
    private ManagedInstance<FormDisplayer> displayersInstance;

    private FormsContainer formsContainer;

    private List<FormDisplayer> activeDisplayers = new ArrayList<>();

    @Mock
    private EventSourceMock<FormFieldChanged> formFieldChangedEvent;

    @Mock
    private DynamicFormRenderer renderer;

    @Before
    public void init() {
        when(displayersInstance.get()).thenAnswer((Answer<FormDisplayer>) invocation -> createDisplayer());

        formsContainer = new FormsContainer(view, displayersInstance, formFieldChangedEvent);

        formsContainer.getElement();

        verify(view, times(1)).getElement();
    }

    @Test
    public void testFirstRender() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        testRender(getNode(FIRST_ELEMENT_UID), 1, 1, renderMode);
    }

    @Test
    public void testSecondRender() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        testRender(getNode(FIRST_ELEMENT_UID), 1, 1, renderMode);

        testRender(getNode(SECOND_ELEMENT_UID), 2, 1, renderMode);
    }

    @Test
    public void testRenderExistingNode() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        NodeImpl<Definition<?>> firstNode = getNode(FIRST_ELEMENT_UID);

        FormDisplayer firstDisplayer = testRender(firstNode, 1, 1, renderMode);

        NodeImpl secondNode = getNode(SECOND_ELEMENT_UID);

        FormDisplayer secondDisplayer = testRender(secondNode, 2, 1, renderMode);

        formsContainer.render(GRAPH_UID, firstNode.getUUID(), firstNode.getContent().getDefinition(), path, fieldChangeHandler, renderMode);

        verify(displayersInstance, times(2)).get();

        verify(secondDisplayer, times(2)).hide();

        verify(firstDisplayer, times(2)).show();
        verify(firstDisplayer, times(2)).render(firstNode.getUUID(), firstNode.getContent().getDefinition(), path, fieldChangeHandler, renderMode);
    }

    @Test
    public void testDestroyDiagramDisplayers() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        FormDisplayer firstDisplayer = testRender(getNode(FIRST_ELEMENT_UID), 1, 1, renderMode);

        FormDisplayer secondDisplayer = testRender(getNode(SECOND_ELEMENT_UID), 2, 1, renderMode);

        formsContainer.clearDiagramDisplayers(GRAPH_UID);

        verify(firstDisplayer, times(3)).hide();
        verify(view, times(1)).removeDisplayer(firstDisplayer);
        verify(displayersInstance, times(1)).destroy(firstDisplayer);

        verify(secondDisplayer, times(2)).hide();
        verify(view, times(1)).removeDisplayer(secondDisplayer);
        verify(displayersInstance, times(1)).destroy(secondDisplayer);
    }

    @Test
    public void testDestroyOneDisplayer() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        NodeImpl firstNode = getNode(FIRST_ELEMENT_UID);

        FormDisplayer firstDisplayer = testRender(firstNode, 1, 1, renderMode);

        NodeImpl secondNode = getNode(SECOND_ELEMENT_UID);

        FormDisplayer secondDisplayer = testRender(secondNode, 2, 1, renderMode);

        formsContainer.clearFormDisplayer(GRAPH_UID, FIRST_ELEMENT_UID);

        verify(firstDisplayer, times(3)).hide();
        verify(view, times(1)).removeDisplayer(firstDisplayer);
        verify(displayersInstance, times(1)).destroy(firstDisplayer);

        verify(secondDisplayer, times(1)).hide();
        verify(view, never()).removeDisplayer(secondDisplayer);
        verify(displayersInstance, never()).destroy(secondDisplayer);

        formsContainer.clearFormDisplayer(GRAPH_UID, SECOND_ELEMENT_UID);

        verify(secondDisplayer, times(2)).hide();
        verify(view, times(1)).removeDisplayer(secondDisplayer);
        verify(displayersInstance, times(1)).destroy(secondDisplayer);
    }

    @Test
    public void testDestroyAllDisplayers() {
        //arbitrary render mode
        RenderMode renderMode = RenderMode.EDIT_MODE;
        testRender(getNode(FIRST_ELEMENT_UID), 1, 1, renderMode);

        testRender(getNode(SECOND_ELEMENT_UID), 2, 1, renderMode);

        formsContainer.destroyAll();

        verify(view, times(1)).clear();
        verify(displayersInstance, times(1)).destroyAll();
    }

    private FormDisplayer testRender(NodeImpl<Definition<?>> node, int expectedDisplayers, int currentDisplayerRender, RenderMode renderMode) {
        //clear mocks
        reset(renderer);
        reset(formFieldChangedEvent);

        formsContainer.render(GRAPH_UID, node.getUUID(), node.getContent().getDefinition(), path, fieldChangeHandler, renderMode);

        verify(displayersInstance, times(expectedDisplayers)).get();

        assertThat(activeDisplayers).isNotEmpty().hasSize(expectedDisplayers);

        FormDisplayer displayer = activeDisplayers.get(expectedDisplayers - 1);

        verify(displayer, times(currentDisplayerRender)).render(node.getUUID(), node.getContent().getDefinition(), path, fieldChangeHandler, renderMode);

        verify(displayer, times(currentDisplayerRender)).hide();
        verify(view, times(currentDisplayerRender)).addDisplayer(displayer);
        verify(displayer).show();

        //test fire FormFieldChanged event
        final ArgumentCaptor<FieldChangeHandler> fieldChangeHandlerArgumentCaptor = ArgumentCaptor.forClass(FieldChangeHandler.class);
        verify(renderer).addFieldChangeHandler(fieldChangeHandlerArgumentCaptor.capture());
        fieldChangeHandlerArgumentCaptor.getValue().onFieldChange("field", "value");
        final ArgumentCaptor<FormFieldChanged> formFieldChangedArgumentCaptor = ArgumentCaptor.forClass(FormFieldChanged.class);
        verify(formFieldChangedEvent).fire(formFieldChangedArgumentCaptor.capture());
        final FormFieldChanged formFieldChanged = formFieldChangedArgumentCaptor.getValue();
        assertThat(formFieldChanged.getName()).isEqualTo("field");
        assertThat(formFieldChanged.getValue()).isEqualTo("value");
        assertThat(formFieldChanged.getUuid()).isEqualTo(node.getUUID());

        return displayer;
    }

    @SuppressWarnings("unchecked")
    protected NodeImpl<Definition<?>> getNode(final String uuid) {
        NodeImpl<Definition<?>> node = mock(NodeImpl.class);

        when(node.getUUID()).thenReturn(uuid);
        when(node.getContent()).thenReturn(mock(Definition.class));

        return node;
    }

    protected FormDisplayer createDisplayer() {
        FormDisplayer displayer = mock(FormDisplayer.class);
        when(displayer.getRenderer()).thenReturn(renderer);

        activeDisplayers.add(displayer);

        return displayer;
    }
}

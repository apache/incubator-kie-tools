/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.bpmn.client.editor;

import java.util.function.Supplier;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.bpmn.api.model.impl.nodes.ProcessNode;
import org.uberfire.ext.wires.bpmn.api.service.BpmnService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class BpmnEditorPresenterTest {

    @Mock
    private BpmnEditorView view;

    @Mock
    private ProcessNode processNode;

    @Mock
    private Caller<BpmnService> service;

    @InjectMocks
    private BpmnEditorPresenter presenter = spy(new BpmnEditorPresenter(view));

    @Test
    public void testGetContentSupplier() {

        doReturn(processNode).when(presenter).getContent();

        final Supplier<ProcessNode> contentSupplier = presenter.getContentSupplier();

        assertEquals(processNode, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        assertEquals(service, presenter.getSaveAndRenameServiceCaller());
    }
}

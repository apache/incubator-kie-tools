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

package org.kie.workbench.common.stunner.forms.backend.service;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.forms.service.FormGeneratedEvent;
import org.kie.workbench.common.stunner.forms.service.FormGenerationFailureEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormGenerationServiceImplTest {

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private Graph graph;

    @Mock
    private Event<FormGeneratedEvent> formGeneratedEvent;

    @Mock
    private Event<FormGenerationFailureEvent> formGenerationFailureEvent;

    @Mock
    private FormDefinitionGenerator formDefinitionGenerator;

    private FormGenerationServiceImpl generationService;

    @Before
    public void init() {
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);
        when(diagram.getGraph()).thenReturn(graph);

        generationService = new FormGenerationServiceImpl(formGeneratedEvent, formGenerationFailureEvent, formDefinitionGenerator);
    }

    @Test
    public void testGenerateProcessForm() {
        testGenerateProcessForm(false);
    }

    @Test
    public void testGenerateProcessFormFailure() {
        testGenerateProcessForm(true);
    }

    @Test
    public void testGenerateSelectedForms() {
        testGenerateSelectedForms(false);
    }

    @Test
    public void testGenerateSelectedFormsFailure() {
        testGenerateSelectedForms(true);
    }

    @Test
    public void testGenerateAllForms() {
        testGenerateAllForms(false);
    }

    @Test
    public void testGenerateAllFormsFailure() {
        testGenerateAllForms(true);
    }

    private void testGenerateProcessForm(boolean failure) {
        if (failure) {
            doThrow(new RuntimeException())
                    .when(formDefinitionGenerator).generateProcessForm(diagram);
        }

        generationService.generateProcessForm(diagram);

        verify(formDefinitionGenerator).generateProcessForm(diagram);

        if (failure) {
            verify(formGenerationFailureEvent).fire(any());
        } else {
            verify(formGeneratedEvent).fire(any());
        }
    }

    private void testGenerateSelectedForms(boolean failure) {
        if (failure) {
            doThrow(new RuntimeException())
                    .when(formDefinitionGenerator).generateSelectedForms(any(), any());
        }

        generationService.generateSelectedForms(diagram, new String[] {""});

        verify(formDefinitionGenerator).generateSelectedForms(any(), any());

        if (failure) {
            verify(formGenerationFailureEvent).fire(any());
        } else {
            verify(formGeneratedEvent).fire(any());
        }
    }

    private void testGenerateAllForms(boolean failure) {
        if (failure) {
            doThrow(new RuntimeException())
                    .when(formDefinitionGenerator).generateAllForms(any());
        }

        generationService.generateAllForms(diagram);

        verify(formDefinitionGenerator).generateAllForms(any());

        if (failure) {
            verify(formGenerationFailureEvent).fire(any());
        } else {
            verify(formGeneratedEvent).fire(any());
        }
    }
}

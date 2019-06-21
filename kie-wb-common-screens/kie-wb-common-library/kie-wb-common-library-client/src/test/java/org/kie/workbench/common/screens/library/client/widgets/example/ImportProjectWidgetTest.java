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
 *
 */

package org.kie.workbench.common.screens.library.client.widgets.example;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.library.client.widgets.example.branchselector.BranchSelectorPopUpPresenter;
import org.kie.workbench.common.screens.library.client.widgets.example.errors.ExampleProjectErrorPresenter;
import org.kie.workbench.common.screens.library.client.widgets.example.errors.ExampleProjectOkPresenter;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImportProjectWidgetTest {

    public static final String EXAMPLE_PROJECT_NAME = "Test Example Project";
    public static final String EXAMPLE_PROJECT_DESCRIPTION = "Example Project Description";

    @Mock
    private ExampleProjectWidget.View view;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExampleProjectOkPresenter exampleProjectOkPresenter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ExampleProjectErrorPresenter exampleProjectErrorPresenter;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BranchSelectorPopUpPresenter branchSelectorPopUpPresenter;

    @Mock
    private ImportProject importProject;

    private ExampleProjectWidget widget;
    private List<ExampleProjectError> errors;

    @Before
    public void setUp() {
        this.widget = new ExampleProjectWidget(this.view,
                                               this.exampleProjectOkPresenter,
                                               this.exampleProjectErrorPresenter,
                                               this.branchSelectorPopUpPresenter);
        when(importProject.getName()).thenReturn(EXAMPLE_PROJECT_NAME);
        when(importProject.getDescription()).thenReturn(EXAMPLE_PROJECT_DESCRIPTION);

        errors = Arrays.asList(new ExampleProjectError("AnId",
                                                       "An Error Description"));
    }

    @Test
    public void testProjectContainErrors() {
        when(this.importProject.getErrors()).thenReturn(this.errors);
        this.widget.init(this.importProject,
                         mock(ExampleProjectWidgetContainer.class));

        verify(this.exampleProjectOkPresenter,
               never()).getView();

        verify(this.exampleProjectErrorPresenter)
                .initialize(eq(this.errors));

        verify(this.view)
                .setDisabled();

        verify(this.view)
                .setup(eq(EXAMPLE_PROJECT_NAME),
                       eq(EXAMPLE_PROJECT_DESCRIPTION),
                       any(),
                       eq(false));
    }

    @Test
    public void testProjectNotContainsErrors() {
        when(this.importProject.getErrors()).thenReturn(Collections.emptyList());
        this.widget.init(this.importProject,
                         mock(ExampleProjectWidgetContainer.class));

        verify(this.exampleProjectErrorPresenter,
               never())
                .initialize(any());

        verify(this.exampleProjectOkPresenter)
                .getView();

        verify(this.view)
                .setup(eq(EXAMPLE_PROJECT_NAME),
                       eq(EXAMPLE_PROJECT_DESCRIPTION),
                       any(),
                       eq(false));
    }

    @Test
    public void testClickOkProject() {
        when(this.importProject.getErrors()).thenReturn(Collections.emptyList());
        this.widget.init(this.importProject,
                         mock(ExampleProjectWidgetContainer.class));

        this.widget.select();

        verify(this.view)
                .setActive();

        assertTrue(this.widget.isSelected());

        this.widget.click();

        verify(this.view)
                .unsetActive();

        assertFalse(this.widget.isSelected());
    }

    @Test
    public void testSelectErrorProject() {
        when(this.importProject.getErrors()).thenReturn(errors);
        this.widget.init(this.importProject,
                         mock(ExampleProjectWidgetContainer.class));

        this.widget.select();

        verify(this.view,
               never())
                .setActive();

        verify(this.view,
               never())
                .unsetActive();

        assertFalse(this.widget.isSelected());
    }
}
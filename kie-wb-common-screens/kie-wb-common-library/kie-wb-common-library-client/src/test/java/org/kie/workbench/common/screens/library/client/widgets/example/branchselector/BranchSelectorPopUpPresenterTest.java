/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.example.branchselector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElement;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BranchSelectorPopUpPresenterTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private BranchSelectorPopUpPresenter.View view;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private KieMultipleSelectElement branchSelector;

    private BranchSelectorPopUpPresenter presenter;

    @Before
    public void setUp() {
        presenter = new BranchSelectorPopUpPresenter(view,
                                                     branchSelector);
    }

    @Test
    public void setupTest() {
        final ImportProject importProject = mock(ImportProject.class);

        final List<String> branches = Arrays.asList("master",
                                                    "branch1");
        doReturn(branches).when(importProject).getSelectedBranches();

        final HTMLElement branchSelectorContainer = mock(HTMLElement.class);
        doReturn(branchSelectorContainer).when(view).getBranchSelectorContainer();

        presenter.setup(importProject,
                        b -> {});

        verify(view).init(presenter);
        verify(branchSelector).setup(same(branchSelectorContainer),
                                     any(),
                                     eq(Arrays.asList("branch1",
                                                      "master")),
                                     any());
        verify(view).show();
    }

    @Test
    public void setupWithoutMasterSelectedTest() {
        final ImportProject importProject = mock(ImportProject.class);

        final List<String> branches = new ArrayList<>();
        branches.add("branch1");
        doReturn(branches).when(importProject).getSelectedBranches();

        final HTMLElement branchSelectorContainer = mock(HTMLElement.class);
        doReturn(branchSelectorContainer).when(view).getBranchSelectorContainer();

        presenter.setup(importProject,
                        mock(Consumer.class));

        verify(view).init(presenter);
        verify(branchSelector).setup(same(branchSelectorContainer),
                                     any(),
                                     eq(Arrays.asList("branch1",
                                                      "master")),
                                     any());
        verify(view).show();
        verify(presenter.branchesSelectedCallback, never()).accept(anyList());
    }

    @Test
    public void defineBranchesEmptyTest() {
        presenter.importProject = mock(ImportProject.class);
        presenter.branches = Collections.emptyList();
        presenter.branchesSelectedCallback = mock(Consumer.class);

        presenter.defineBranches();

        verify(view).getAtLeastMasterIsRequiredMessage();
        verify(view, never()).getMasterIsRequiredMessage();
        verify(view).showError(any());
        verify(presenter.importProject, never()).setSelectedBranches(presenter.branches);
        verify(view, never()).hide();
        verify(presenter.branchesSelectedCallback, never()).accept(anyList());
    }

    @Test
    public void defineBranchesWithoutMasterTest() {
        presenter.importProject = mock(ImportProject.class);
        presenter.branches = Arrays.asList("branch1");
        presenter.branchesSelectedCallback = mock(Consumer.class);

        presenter.defineBranches();

        verify(view, never()).getAtLeastMasterIsRequiredMessage();
        verify(view).getMasterIsRequiredMessage();
        verify(view).showError(any());
        verify(presenter.importProject, never()).setSelectedBranches(presenter.branches);
        verify(view, never()).hide();
        verify(presenter.branchesSelectedCallback, never()).accept(anyList());
    }

    @Test
    public void defineBranchesWithMasterTest() {
        presenter.importProject = mock(ImportProject.class);
        presenter.branches = Arrays.asList("master", "branch1");
        presenter.branchesSelectedCallback = mock(Consumer.class);

        presenter.defineBranches();

        verify(view, never()).getAtLeastMasterIsRequiredMessage();
        verify(view, never()).getMasterIsRequiredMessage();
        verify(view, never()).showError(any());
        verify(presenter.importProject).setSelectedBranches(presenter.branches);
        verify(view).hide();
        verify(presenter.branchesSelectedCallback).accept(presenter.branches);
    }

    @Test
    public void cancelTest() {
        presenter.cancel();

        verify(view).hide();
    }
}

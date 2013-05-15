/*
 * Copyright 2013 JBoss Inc
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

package org.kie.workbench.projecteditor.client.forms;

import org.junit.Before;
import org.junit.Test;
import org.kie.guvnor.project.model.Dependency;
import org.kie.workbench.projecteditor.client.forms.DependencyGrid;
import org.kie.workbench.projecteditor.client.forms.DependencyGridView;
import org.kie.workbench.projecteditor.client.forms.DependencySelectorPopup;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DependencyGridTest {

    private DependencyGrid grid;
    private DependencyGridView view;
    private DependencyGridView.Presenter presenter;
    private DependencySelectorPopup dependencySelectorPopup;


    @Before
    public void setUp() throws Exception {
        view = mock(DependencyGridView.class);
        dependencySelectorPopup = mock(DependencySelectorPopup.class);
        grid = new DependencyGrid(dependencySelectorPopup, view);
        presenter = grid;
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testFillList() throws Exception {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        Dependency dependency = new Dependency();
        dependencies.add(dependency);

        grid.fillList(dependencies);

        verify(view).setList(dependencies);

    }


    @Test
    public void testAddFromRepository() throws Exception {

        presenter.onAddDependencyFromRepositoryButton();

    }
}

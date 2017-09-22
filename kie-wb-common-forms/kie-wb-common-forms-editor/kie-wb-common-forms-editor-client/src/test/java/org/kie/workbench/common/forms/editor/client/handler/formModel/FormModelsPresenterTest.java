/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.handler.formModel.container.FormModelCreationContainer;
import org.kie.workbench.common.forms.editor.client.handler.formModel.container.FormModelCreationContainerView;
import org.kie.workbench.common.forms.model.FormModel;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormModelsPresenterTest {

    @Mock
    protected FormModelsView view;

    @Mock
    protected Path path;

    protected FormModelsPresenter presenter;

    protected List<FormModelCreationContainer> testContainers = new ArrayList<>();

    protected FormModelCreationContainer container;

    protected FormModelCreationContainerView containerView;

    @Mock
    protected ManagedInstance<FormModelCreationContainer> containerInstance;

    @Mock
    protected ManagedInstance<FormModelCreationViewManager> modelManagerInstance;

    @Before
    public void setup() {

        for (int i = 0; i < 5; i++) {
            FormModelCreationContainerView containerView = mock(FormModelCreationContainerView.class);

            FormModelCreationContainer container = new FormModelCreationContainer(containerView);

            FormModelCreationViewManager creationManager = mock(FormModelCreationViewManager.class);

            when(creationManager.getFormModel()).thenReturn(mock(FormModel.class));
            when(creationManager.getLabel()).thenReturn("Container: " + i);
            when(creationManager.getPriority()).thenReturn(i);
            when(creationManager.isValid()).thenReturn(i % 2 == 0);

            container.setup(creationManager,
                            aContainer -> doSelect(aContainer));

            testContainers.add(container);

            if (i == 0) {
                this.container = container;
                this.containerView = containerView;
            }
        }

        presenter = new FormModelsPresenter(view,
                                            containerInstance,
                                            modelManagerInstance) {
            @Override
            protected List<FormModelCreationContainer> getRegisteredCreationManagers() {
                return testContainers;
            }
        };
    }

    protected void doSelect(FormModelCreationContainer container) {
        presenter.selectContainer(container);
    }

    @Test
    public void testFunctionallityWithoutValidationFailure() {

        presenter.init();

        verify(view).setCreationViews(testContainers);

        presenter.initialize(path);

        testContainers.forEach(container -> {
            verify(container.getCreationViewManager(),
                   atLeastOnce()).getPriority();
            verify(container.getCreationViewManager(),
                   atLeastOnce()).init(path);
        });

        presenter.selectContainer(container);

        verify(containerView).select();

        presenter.selectContainer(container);

        verify(containerView,
               times(3)).showCreationView();

        presenter.isValid();

        verify(container.getCreationViewManager()).isValid();

        presenter.getFormModel();

        verify(container.getCreationViewManager()).getFormModel();

        presenter.asWidget();
        verify(view).asWidget();
    }
}

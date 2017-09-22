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
package org.kie.workbench.common.forms.editor.client.handler.formModel.container;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelCreationViewManager;
import org.kie.workbench.common.forms.editor.client.handler.formModel.SelectModelCreatorManagerCallback;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FormModelCreationContainerTest {

    @Mock
    private FormModelCreationContainerView view;

    @Mock
    private FormModelCreationViewManager creationViewManager;

    @Mock
    private SelectModelCreatorManagerCallback callback;

    @Mock
    private Path path;

    private FormModelCreationContainer container;

    @Before
    public void init() {
        container = new FormModelCreationContainer(view);
    }

    @Test
    public void testGeneralFunctionallity() {
        container.setup(creationViewManager,
                        callback);

        verify(view).init(container);

        container.initData(path);
        verify(creationViewManager).init(path);

        container.getFormModelLabel();
        verify(creationViewManager).getLabel();

        container.getCreationView();
        verify(creationViewManager).getView();

        container.selectManager();

        verify(view).select();
        verify(callback).selectContainerCallback(container);

        container.showCreationView();
        verify(view).showCreationView();

        container.hideCreationView();
        verify(view).hideCreationView();
    }
}

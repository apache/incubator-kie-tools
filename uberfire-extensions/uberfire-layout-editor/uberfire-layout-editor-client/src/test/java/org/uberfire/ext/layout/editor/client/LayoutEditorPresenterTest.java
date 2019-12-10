/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayoutEditorPresenterTest {

    public static final String LAYOUT_NAME = "test layout";
    public static final String EMPTY_TITLE_TEXT = "Empty title text";
    public static final String EMPTY_SUB_TITLE_TEXT = "Empty SubTitle text";

    @Mock
    private Container container;

    @Mock
    private LayoutGenerator layoutGenerator;

    @Mock
    private LayoutEditorPresenter.View view;

    @Mock
    private LayoutDragComponentGroupPresenter.View dragComponentGroupView;

    private LayoutTemplate testTemplate = new LayoutTemplate(LAYOUT_NAME);


    private LayoutEditorPresenter presenter;

    @Before
    public void initialize() {
        presenter = new LayoutEditorPresenter(view, container, layoutGenerator);
    }

    @Test
    public void testInitialization() {

        verify(view).init(presenter);

        presenter.initNew();

        verify(container).getView();
        verify(view).setupDesign(any());
    }

    @Test
    public void testLoadLayout() {
        presenter.loadLayout(testTemplate,
                             EMPTY_TITLE_TEXT,
                             EMPTY_SUB_TITLE_TEXT);
        verify(container).load(testTemplate,
                               EMPTY_TITLE_TEXT,
                               EMPTY_SUB_TITLE_TEXT);
    }

    @Test
    public void testLoadEmptyLayout() {
        presenter.loadEmptyLayout(LAYOUT_NAME,
                                  EMPTY_TITLE_TEXT,
                                  EMPTY_SUB_TITLE_TEXT);
        verify(container).loadEmptyLayout(LAYOUT_NAME,
                                          LayoutTemplate.Style.FLUID,
                                          EMPTY_TITLE_TEXT,
                                          EMPTY_SUB_TITLE_TEXT);
    }

    @Test
    public void testSetup() {
        Supplier<Boolean> lockSupplier = () -> false;
        presenter.setup(lockSupplier);
        verify(container).setLockSupplier(lockSupplier);
    }

    public void testLayoutEditorClear() {
        presenter.clear();

        verify(container).reset();
    }
}

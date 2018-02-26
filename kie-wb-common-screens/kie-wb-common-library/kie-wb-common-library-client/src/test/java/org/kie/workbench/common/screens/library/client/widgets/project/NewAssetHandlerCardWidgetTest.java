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
 *
 */

package org.kie.workbench.common.screens.library.client.widgets.project;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Model;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NewAssetHandlerCardWidgetTest {

    public static final String TITLE = "Title";
    public static final String MODEL = "Model";
    private NewAssetHandlerCardWidget widget;

    @Mock
    private NewResourcePresenter newResourcePresenter;

    @Mock
    private NewAssetHandlerCardWidget.View view;

    @Mock
    private NewResourceHandler newResourceHandler;

    @Mock
    private TranslationService ts;

    @Before
    public void setUp() {
        this.widget = new NewAssetHandlerCardWidget(this.view,
                                                    this.newResourcePresenter,
                                                    this.ts);

        when(ts.getTranslation(eq(new Model().getName()))).thenReturn(MODEL);
        when(newResourceHandler.getResourceType()).thenReturn(new JavaResourceTypeDefinition(new Model()));
        when(newResourceHandler.getDescription()).thenReturn(TITLE);
    }

    @Test
    public void testInitialize() {
        this.widget.initialize(newResourceHandler);

        verify(view,
               times(1)).setDescription(eq(MODEL));
        verify(view,
               times(1)).setTitle(eq(TITLE));
        verify(view,
               times(1)).setCommand(any());
    }
}
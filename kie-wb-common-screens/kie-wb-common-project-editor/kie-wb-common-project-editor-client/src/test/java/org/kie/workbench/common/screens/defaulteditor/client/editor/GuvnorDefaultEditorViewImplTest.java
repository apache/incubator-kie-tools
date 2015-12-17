/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorPresenter;
import org.uberfire.ext.widgets.core.client.editors.defaulteditor.DefaultFileEditorView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GuvnorDefaultEditorViewImplTest {

    @GwtMock
    DefaultFileEditorView defaultFileEditorView;

    @Spy
    DefaultFileEditorPresenter presenter = new DefaultFileEditorPresenter();

    private int howManyTimesTheWidgetIsSet;

    private GuvnorDefaultEditorViewImpl guvnorDefaultEditorView;

    @Before
    public void setUp() throws Exception {
        howManyTimesTheWidgetIsSet = 0;

        presenter.view = defaultFileEditorView;

        guvnorDefaultEditorView = new GuvnorDefaultEditorViewImpl( presenter ) {
            @Override
            protected void initWidget( Widget widget ) {
                howManyTimesTheWidgetIsSet++;
            }
        };
    }

    @Test
    public void testPostConstructSetsTheView() throws Exception {

        guvnorDefaultEditorView.init();

        assertEquals( "Widget needs to be set on init.",
                      1, howManyTimesTheWidgetIsSet );
    }

    @Test
    public void testStartUp() throws Exception {

        ObservablePath path1 = mock( ObservablePath.class );
        guvnorDefaultEditorView.onStartup( path1 );
        ObservablePath path2 = mock( ObservablePath.class );
        guvnorDefaultEditorView.onStartup( path2 );

        verify( presenter ).onStartup( path1 );
        verify( presenter ).onStartup( path2 );

        assertEquals( "Start up should not init the widget. No matter how many times it is called.",
                      0, howManyTimesTheWidgetIsSet );
    }
}
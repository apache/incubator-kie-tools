/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.model.FormModel;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FormModelsPresenterTest {

    @Mock
    protected FormModelsView view;

    @Mock
    protected Path path;

    protected FormModelsPresenter presenter;

    protected List<FormModelCreationView> creationViews = new ArrayList<>();

    @Before
    public void setup() {
        for ( int i = 0; i < 5; i ++ ) {
            FormModelCreationView creationView = mock( FormModelCreationView.class );
            when( creationView.getFormModel() ).thenReturn( mock( FormModel.class ) );
            when( creationView.getLabel() ).thenReturn( "View: " + i );
            when( creationView.getPriority() ).thenReturn( i );
            when( creationView.isValid() ).thenReturn( i % 2 == 0 );
            creationViews.add( creationView );
        }

        presenter = new TestFormModelsPresenter( view, creationViews );
    }

    @Test
    public void testFunctionallity() {

        presenter.init();

        verify( view ).setCreationViews( creationViews );

        presenter.initialize( path );

        for ( FormModelCreationView view : creationViews ) {
            verify( view, atLeastOnce() ).getPriority();
            verify( view ).init( path );
        }

        presenter.isValid();

        verify( view ).isValid();

        presenter.getFormModel();

        verify( view ).getFormModel();

        presenter.asWidget();
        verify( view ).asWidget();
    }
}

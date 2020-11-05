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

package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class NewDependencyPopupTest {

    @GwtMock
    ProjectEditorResources resources;

    @GwtMock
    ProjectEditorConstants constants;

    @Mock
    private NewDependencyPopupView view;

    @Mock
    private Callback<Dependency> callback;

    @Captor
    private ArgumentCaptor<Dependency> dependencyArgumentCaptor;

    private NewDependencyPopup newDependencyPopup;

    @Before
    public void setUp() throws Exception {
        newDependencyPopup = new NewDependencyPopup( view );
        newDependencyPopup.show( callback );
    }

    @Test
    public void testClearOldValues() throws Exception {

        verify( view ).clean();

        newDependencyPopup.show( callback );

        verify( view, times( 2 ) ).clean();
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify( view ).setPresenter( newDependencyPopup );
    }

    @Test
    public void testShowView() throws Exception {
        verify( view ).show();
    }

    @Test
    public void testFillValidDependency() throws Exception {
        fillInValidGAV();

        newDependencyPopup.onOkClicked();

        verify( callback ).callback( any( Dependency.class ) );
        verify( view ).hide();
    }

    @Test
    public void testCloseIfDependencyValid() throws Exception {
        fillInValidGAV();

        newDependencyPopup.onOkClicked();

        verify( view ).hide();
    }

    @Test
    public void testValuesAreClearedWhenReopening() throws Exception {
        fillInValidGAV();

        newDependencyPopup.onArtifactIdChange( "artifactId" );

        // reopen
        newDependencyPopup.show( callback );

        newDependencyPopup.onOkClicked();
        verify( callback, never() ).callback( any( Dependency.class ) );
    }

    private void fillInValidGAV() {
        newDependencyPopup.onGroupIdChange( "groupID" );
        newDependencyPopup.onArtifactIdChange( "myArtifactID" );
        newDependencyPopup.onVersionChange( "1.0" );
    }
}
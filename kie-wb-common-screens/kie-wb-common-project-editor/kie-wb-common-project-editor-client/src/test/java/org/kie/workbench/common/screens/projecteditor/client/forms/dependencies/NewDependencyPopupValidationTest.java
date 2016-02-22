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
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class NewDependencyPopupValidationTest {

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
    public void testFillValidDependency() throws Exception {
        fillInValidGAV();

        final Dependency dependency = userClicksOk();

        assertEquals( "groupID", dependency.getGroupId() );
        assertEquals( "myArtifactID", dependency.getArtifactId() );
        assertEquals( "1.0", dependency.getVersion() );
    }

    @Test
    public void testCloseIfDependencyValid() throws Exception {
        fillInValidGAV();

        newDependencyPopup.onOkClicked();

        verify( view ).hide();
    }

    @Test
    public void testFillInValidDependency() throws Exception {
        fillInValidGAV();
        newDependencyPopup.onGroupIdChange( "" );

        newDependencyPopup.onOkClicked();

        verify( callback, never() ).callback( any( Dependency.class ) );
        verify( view, never() ).hide();
    }

    @Test
    public void testFillInValidGroupIdDependency() throws Exception {
        newDependencyPopup.onArtifactIdChange( "test" );
        newDependencyPopup.onVersionChange( "1.0" );

        newDependencyPopup.onOkClicked();

        verify( view ).setGroupIdValidationState( ValidationState.ERROR );
    }

    @Test
    public void testFillInValidArtifactIdDependency() throws Exception {
        newDependencyPopup.onGroupIdChange( "org.test" );
        newDependencyPopup.onVersionChange( "1.0" );

        newDependencyPopup.onOkClicked();

        verify( view ).setArtifactIdValidationState( ValidationState.ERROR );
    }

    @Test
    public void testFillInValidVersionDependency() throws Exception {
        newDependencyPopup.onGroupIdChange( "org.test" );
        newDependencyPopup.onArtifactIdChange( "test" );

        newDependencyPopup.onOkClicked();

        verify( view ).setVersionValidationState( ValidationState.ERROR );
    }

    @Test
    public void testInValidGroupId() throws Exception {
        newDependencyPopup.onGroupIdChange( "" );

        verify( view ).invalidGroupId( "DependencyIsMissingAGroupId" );
        verify( view ).setGroupIdValidationState( ValidationState.ERROR );
    }

    @Test
    public void testValidGroupId() throws Exception {
        newDependencyPopup.onGroupIdChange( "org.test" );

        verify( view ).invalidGroupId( "" );
        verify( view ).setGroupIdValidationState( ValidationState.SUCCESS );
    }

    @Test
    public void testInValidArtifactId() throws Exception {
        newDependencyPopup.onArtifactIdChange( "" );

        verify( view ).invalidArtifactId( "DependencyIsMissingAnArtifactId" );
        verify( view ).setArtifactIdValidationState( ValidationState.ERROR );
    }

    @Test
    public void testValidArtifactId() throws Exception {
        newDependencyPopup.onArtifactIdChange( "artifact" );

        verify( view ).invalidArtifactId( "" );
        verify( view ).setArtifactIdValidationState( ValidationState.SUCCESS );
    }

    @Test
    public void testInValidVersion() throws Exception {
        newDependencyPopup.onVersionChange( "" );

        verify( view ).invalidVersion( "DependencyIsMissingAVersion" );
        verify( view ).setVersionValidationState( ValidationState.ERROR );
    }

    @Test
    public void testValidVersion() throws Exception {
        newDependencyPopup.onVersionChange( "1.0" );

        verify( view ).invalidVersion( "" );
        verify( view ).setVersionValidationState( ValidationState.SUCCESS );
    }


    private Dependency userClicksOk() {
        newDependencyPopup.onOkClicked();
        verify( callback ).callback( dependencyArgumentCaptor.capture() );
        return dependencyArgumentCaptor.getValue();
    }

    private void fillInValidGAV() {
        newDependencyPopup.onGroupIdChange( "groupID" );
        newDependencyPopup.onArtifactIdChange( "myArtifactID" );
        newDependencyPopup.onVersionChange( "1.0" );
    }
}
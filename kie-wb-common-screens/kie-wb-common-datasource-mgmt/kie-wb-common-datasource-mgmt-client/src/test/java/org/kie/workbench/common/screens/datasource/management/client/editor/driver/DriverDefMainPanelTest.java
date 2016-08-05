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

package org.kie.workbench.common.screens.datasource.management.client.editor.driver;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datasource.management.client.util.DataSourceManagementTestConstants;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DriverDefMainPanelTest
        implements DataSourceManagementTestConstants {

    @Mock
    private DriverDefMainPanelView view;

    private DriverDefMainPanel mainPanel;

    private String name;

    private String groupId;

    private String artifactId;

    private String version;

    private String driverClass;

    @Before
    public void setup() {
        mainPanel = new DriverDefMainPanel( view );
        name = null;
        groupId = null;
        artifactId = null;
        version = null;
        driverClass = null;
        mainPanel.setHandler( new DriverDefMainPanelView.Handler() {
            @Override
            public void onNameChange() {
                name = view.getName();
            }

            @Override
            public void onDriverClassChange() {
                driverClass = view.getDriverClass();
            }

            @Override
            public void onGroupIdChange() {
                groupId = view.getGroupId();
            }

            @Override
            public void onArtifactIdChange() {
                artifactId = view.getArtifactId();
            }

            @Override
            public void onVersionChange() {
                version = view.getVersion();
            }
        } );
    }

    @Test
    public void testSetName() {
        mainPanel.setName( NAME );
        verify( view, times( 1 ) ).setName( NAME );
    }

    @Test
    public void testGetName() {
        when ( view.getName() ).thenReturn( NAME );
        assertEquals( NAME, mainPanel.getName() );
    }

    @Test
    public void testSetNameError() {
         mainPanel.setNameErrorMessage( ERROR );
        verify( view, times( 1 ) ).setNameErrorMessage( ERROR );
    }

    @Test
    public void clearNameError() {
        mainPanel.clearNameErrorMessage();
        verify( view, times( 1 ) ).clearNameErrorMessage();
    }

    @Test
    public void testSetGroupId() {
        mainPanel.setGroupId( GROUP_ID );
        verify( view, times( 1 ) ).setGroupId( GROUP_ID );
    }

    @Test
    public void testGetGroupId() {
        when ( view.getGroupId() ).thenReturn( GROUP_ID );
        assertEquals( GROUP_ID, mainPanel.getGroupId() );
    }

    @Test
    public void testSetGroupIdError() {
        mainPanel.setGroupIdErrorMessage( ERROR );
        verify( view, times( 1 ) ).setGroupIdErrorMessage( ERROR );
    }

    @Test
    public void clearGroupIdError() {
        mainPanel.clearGroupIdErrorMessage();
        verify( view, times( 1 ) ).clearGroupIdErrorMessage();
    }

    @Test
    public void testSetArtifactId() {
        mainPanel.setArtifactId( ARTIFACT_ID );
        verify( view, times( 1 ) ).setArtifactId( ARTIFACT_ID );
    }

    @Test
    public void testGetArtifactId() {
        when ( view.getArtifactId() ).thenReturn( ARTIFACT_ID );
        assertEquals( ARTIFACT_ID, mainPanel.getArtifactId() );
    }

    @Test
    public void testSetArtifactIdError() {
        mainPanel.setArtifactIdErrorMessage( ERROR );
        verify( view, times( 1 ) ).setArtifactIdErrorMessage( ERROR );
    }

    @Test
    public void clearArtifactIdError() {
        mainPanel.clearArtifactIdErrorMessage();
        verify( view, times( 1 ) ).clearArtifactIdErrorMessage();
    }

    @Test
    public void testSetVersion() {
        mainPanel.setVersion( VERSION );
        verify( view, times( 1 ) ).setVersion( VERSION );
    }

    @Test
    public void testGetVersion() {
        when ( view.getVersion() ).thenReturn( VERSION );
        assertEquals( VERSION, mainPanel.getVersion() );
    }

    @Test
    public void testSetVersionError() {
        mainPanel.setVersionErrorMessage( ERROR );
        verify( view, times( 1 ) ).setVersionErrorMessage( ERROR );
    }

    @Test
    public void clearVersionError() {
        mainPanel.clearVersionErrorMessage();
        verify( view, times( 1 ) ).clearVersionErrorMessage();
    }

    @Test
    public void testSetDriverClass() {
        mainPanel.setDriverClass( DRIVER_CLASS );
        verify( view, times( 1 ) ).setDriverClass( DRIVER_CLASS );
    }

    @Test
    public void testGetDriverClass() {
        when( view.getDriverClass() ).thenReturn( DRIVER_CLASS );
        assertEquals( DRIVER_CLASS, mainPanel.getDriverClass() );
    }

    @Test
    public void testSetDriverClassError() {
        mainPanel.setDriverClassErrorMessage( ERROR );
        verify( view, times( 1 ) ).setDriverClassErrorMessage( ERROR );
    }

    @Test
    public void clearDriverClassError() {
        mainPanel.clearDriverClassErrorMessage();
        verify( view, times( 1 ) ).clearDriverClassErrorMessage();
    }

    @Test
    public void testNameChange() {
        when ( view.getName() ).thenReturn( NAME );
        //emulates the presenter method executed from the UI.
        mainPanel.onNameChange();
        verify( view, times( 1 ) ).getName();
        //the handler should have been invoked and collected the expected value.
        assertEquals( NAME, name );
    }

    @Test
    public void testGroupIdChange() {
        when( view.getGroupId() ).thenReturn( GROUP_ID );
        //emulates the presenter method executed from the UI.
        mainPanel.onGroupIdChange();
        verify( view, times( 1 ) ).getGroupId();
        //the handler should have been invoked and collected the expected value.
        assertEquals( GROUP_ID, groupId );
    }

    @Test
    public void testArtifactIdChange() {
        when( view.getArtifactId() ).thenReturn( ARTIFACT_ID );
        //emulates the presenter method executed from the UI.
        mainPanel.onArtifactIdChange();
        verify( view, times( 1 ) ).getArtifactId();
        //the handler should have been invoked and collected the expected value.
        assertEquals( ARTIFACT_ID, artifactId );
    }

    @Test
    public void testVersionChange() {
        when( view.getVersion() ).thenReturn( VERSION );
        //emulates the presenter method executed from the UI.
        mainPanel.onVersionChange();
        verify( view, times( 1 ) ).getVersion();
        //the handler should have been invoked and collected the expected value.
        assertEquals( VERSION, version );
    }

    @Test
    public void testDriverClassChange() {
        when( view.getDriverClass() ).thenReturn( DRIVER_CLASS );
        //emulates the presenter method executed from the UI.
        mainPanel.onDriverClassChange();
        verify( view, times( 1 ) ).getDriverClass();
        //the handler should have been invoked and collected the expected value.
        assertEquals( DRIVER_CLASS, driverClass );
    }

}

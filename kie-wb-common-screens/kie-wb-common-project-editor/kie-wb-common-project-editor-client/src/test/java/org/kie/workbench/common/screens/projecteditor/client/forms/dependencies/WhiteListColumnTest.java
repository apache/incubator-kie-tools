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

import java.util.Arrays;
import java.util.HashSet;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;
import org.kie.workbench.common.services.shared.dependencies.NormalEnhancedDependency;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class WhiteListColumnTest {

    @GwtMock
    ProjectEditorResources projectEditorResources;


    @Mock
    DependencyGrid grid;

    private WhiteListColumn whiteListColumn;
    private WhiteList       whiteList;
    private String shownMessage;

    @Before
    public void setUp() throws Exception {
        whiteListColumn = new WhiteListColumn() {
            @Override
            protected void showMessage( final String message ) {
                shownMessage = message;
            }
        };
        whiteList = new WhiteList();

        whiteListColumn.init( grid,
                              whiteList );
    }

    @Test
    public void testEmptyWhiteListEmptyDependency() throws Exception {
        assertEquals( "AllPackagesIncluded",
                      whiteListColumn.getValue( getDependency() ) );
    }

    @Test
    public void testEmptyDependency() throws Exception {
        whiteList.add( "org.hello" );
        assertEquals( "PackagesNotIncluded",
                      whiteListColumn.getValue( getDependency() ) );
    }

    @Test
    public void testWhiteListEmpty() throws
                                     Exception {
        assertEquals( "AllPackagesIncluded",
                      whiteListColumn.getValue( getDependency( "org.hello" ) ) );
    }

    @Test
    public void testNotWhiteListed() throws Exception {
        whiteList.add( "org.something.else" );

        assertEquals( "PackagesNotIncluded", whiteListColumn.getValue( getDependency( "org.hello" ) ) );
    }

    @Test
    public void testWhiteListed() throws Exception {
        whiteList.add( "org.hello" );

        assertEquals( "AllPackagesIncluded", whiteListColumn.getValue( getDependency( "org.hello" ) ) );
    }

    @Test
    public void testSomeWhiteListed() throws Exception {
        whiteList.add( "org.hello" );

        assertEquals( "SomePackagesIncluded", whiteListColumn.getValue( getDependency( "org.hello",
                                                                                       "org.bye" ) ) );
    }

    @Test
    public void testOnAddAll() throws Exception {
        final EnhancedDependency dependency = getDependency( "org.test" );
        dependency.getDependency().setGroupId( "groupId" );
        dependency.getDependency().setArtifactId( "artifactId" );
        dependency.getDependency().setVersion( "1.0" );

        whiteListColumn.getFieldUpdater().update( 1, dependency, WhiteListCell.ADD_ALL );

        verify( grid ).onAddAll( dependency.getPackages() );
    }

    @Test
    public void testOnRemoveAll() throws Exception {
        final EnhancedDependency dependency = getDependency( "org.test" );
        dependency.getDependency().setGroupId( "groupId" );
        dependency.getDependency().setArtifactId( "artifactId" );
        dependency.getDependency().setVersion( "1.0" );

        whiteListColumn.getFieldUpdater().update( 1, dependency, WhiteListCell.ADD_NONE );

        verify( grid ).onRemoveAll( dependency.getPackages() );
    }

    @Test
    public void testOnToggleInvalidDependency() throws Exception {
        final EnhancedDependency dependency = getDependency( "org.test" );

        whiteListColumn.getFieldUpdater().update( 1, dependency, "test" );

        assertEquals( "DependencyIsMissingAGroupId", shownMessage );

        verify( grid, never() ).onAddAll( anySet() );
    }

    private EnhancedDependency getDependency( final String... packages ) {
        return new NormalEnhancedDependency( new Dependency(),
                                             new HashSet<>( Arrays.asList( packages ) ) );
    }
}
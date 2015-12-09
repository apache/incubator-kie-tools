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

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Dependency;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
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
    private SafeHtmlBuilder safeHtmlBuilder;
    private WhiteList       whiteList;

    @Before
    public void setUp() throws Exception {
        whiteListColumn = new WhiteListColumn();
        safeHtmlBuilder = new SafeHtmlBuilder();
        whiteList = new WhiteList();

        whiteListColumn.init( grid,
                              whiteList );
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals( "AllPackagesIncluded", whiteListColumn.getValue( getDependency() ) );
    }

    @Test
    public void testNotWhiteListed() throws Exception {
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
    public void testOnToggle() throws Exception {
        final Dependency dependency = getDependency( "org.test" );

        whiteListColumn.getFieldUpdater().update( 1, dependency, "test" );

        verify( grid ).onTogglePackagesToWhiteList( dependency.getPackages() );
    }

    private Dependency getDependency( final String... packages ) {
        final Dependency dependency = new Dependency();
        for ( String aPackage : packages ) {
            dependency.getPackages().add( aPackage );
        }

        return dependency;
    }
}
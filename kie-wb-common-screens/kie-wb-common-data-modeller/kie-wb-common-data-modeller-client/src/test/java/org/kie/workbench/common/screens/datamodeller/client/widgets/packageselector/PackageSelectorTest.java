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

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.widgets.DataModelerEditorsTestHelper;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class PackageSelectorTest {

    @Mock
    private PackageSelectorView view;

    @Test
    public void loadEditorTest() {

        DataModelerContext context = createContext();
        List<Pair<String, String>> expectedPackageList = expectedPackageList();
        PackageSelector packageSelector = new PackageSelector( view );
        packageSelector.setContext( context );

        verify( view, times( 1 ) ).initPackageList( expectedPackageList, null, true );
    }

    @Test
    public void selectedPackageChange() {

        DataModelerContext context = createContext();
        List<Pair<String, String>> expectedPackageList = expectedPackageList();
        PackageSelector packageSelector = new PackageSelector( view );
        packageSelector.setContext( context );

        verify( view, times( 1 ) ).initPackageList( expectedPackageList, null, true );

        packageSelector.setCurrentPackage( "package2" );
        verify( view, times( 1 ) ).initPackageList( expectedPackageList , "package2", false );
    }

    private DataModelerContext createContext() {
        DataModelerContext context = DataModelerEditorsTestHelper.createTestContext();
        context.appendPackage( "package2" );
        context.appendPackage( "package1" );
        context.appendPackage( "package3" );
        return context;
    }

    private List<Pair<String, String>> expectedPackageList() {
        //expected options list should be sorted
        List<Pair<String, String>> expectedList = new ArrayList<Pair<String, String>>();
        expectedList.add( new Pair<String, String>( "package1", "package1" ) );
        expectedList.add( new Pair<String, String>( "package2", "package2" ) );
        expectedList.add( new Pair<String, String>( "package3", "package3" ) );
        return expectedList;
    }
}

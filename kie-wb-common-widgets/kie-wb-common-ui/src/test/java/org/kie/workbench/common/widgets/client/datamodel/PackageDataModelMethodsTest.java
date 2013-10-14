/*
* Copyright 2010 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.kie.workbench.common.widgets.client.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ProjectDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.widgets.client.callbacks.Callback;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PackageDataModelMethodsTest {

    @Test
    public void testMethodsOnJavaClass_TreeMap() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( TreeMap.class )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "java.util" ).setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller();
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setMethodInformation( packageLoader.getProjectMethodInformation() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        oracle.getFieldCompletions( TreeMap.class.getSimpleName(),
                                    FieldAccessorsAndMutators.ACCESSOR,
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] getters ) {
                                            assertEquals( 17,
                                                          getters.length );
                                            assertEquals( "this",
                                                          getters[ 0 ].getName() );
                                            assertEquals( "clone",
                                                          getters[ 1 ].getName() );
                                            assertEquals( "comparator",
                                                          getters[ 2 ].getName() );
                                            assertEquals( "descendingKeySet",
                                                          getters[ 3 ].getName() );
                                            assertEquals( "descendingMap",
                                                          getters[ 4 ].getName() );
                                            assertEquals( "empty",
                                                          getters[ 5 ].getName() );
                                            assertEquals( "entrySet",
                                                          getters[ 6 ].getName() );
                                            assertEquals( "firstEntry",
                                                          getters[ 7 ].getName() );
                                            assertEquals( "firstKey",
                                                          getters[ 8 ].getName() );
                                            assertEquals( "keySet",
                                                          getters[ 9 ].getName() );
                                            assertEquals( "lastEntry",
                                                          getters[ 10 ].getName() );
                                            assertEquals( "lastKey",
                                                          getters[ 11 ].getName() );
                                            assertEquals( "navigableKeySet",
                                                          getters[ 12 ].getName() );
                                            assertEquals( "pollFirstEntry",
                                                          getters[ 13 ].getName() );
                                            assertEquals( "pollLastEntry",
                                                          getters[ 14 ].getName() );
                                            assertEquals( "size",
                                                          getters[ 15 ].getName() );
                                            assertEquals( "values",
                                                          getters[ 16 ].getName() );
                                        }
                                    } );

        oracle.getFieldCompletions( TreeMap.class.getSimpleName(),
                                    FieldAccessorsAndMutators.MUTATOR,
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] setters ) {
                                            assertEquals( 0,
                                                          setters.length );
                                        }
                                    } );
    }

    @Test
    public void testMethodsOnJavaClass_ArrayList() throws Exception {
        final ProjectDataModelOracle projectLoader = ProjectDataModelOracleBuilder.newProjectOracleBuilder()
                .addClass( ArrayList.class )
                .build();

        final PackageDataModelOracle packageLoader = PackageDataModelOracleBuilder.newPackageOracleBuilder( "java.util" ).setProjectOracle( projectLoader ).build();

        //Emulate server-to-client conversions
        final MockAsyncPackageDataModelOracleImpl oracle = new MockAsyncPackageDataModelOracleImpl();
        final Caller<IncrementalDataModelService> service = new MockIncrementalDataModelServiceCaller();
        oracle.setService( service );

        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
        dataModel.setPackageName( packageLoader.getPackageName() );
        dataModel.setModelFields( packageLoader.getProjectModelFields() );
        dataModel.setMethodInformation( packageLoader.getProjectMethodInformation() );
        PackageDataModelOracleTestUtils.populateDataModelOracle( mock( Path.class ),
                                                                 new MockHasImports(),
                                                                 oracle,
                                                                 dataModel );

        oracle.getMethodNames( ArrayList.class.getSimpleName(),
                               new Callback<List<String>>() {
                                   @Override
                                   public void callback( final List<String> methodNames ) {
                                       assertNotNull( methodNames );
                                       assertFalse( methodNames.isEmpty() );
                                       for ( final String methodName : methodNames ) {
                                           assertFalse( "Method " + methodName + " is not allowed.",
                                                        allowedMethod( methodName ) );
                                       }
                                   }
                               } );
    }

    private boolean allowedMethod( final String methodName ) {
        return ( "hashCode".equals( methodName )
                || "equals".equals( methodName )
                || "listIterator".equals( methodName )
                || "lastIndexOf".equals( methodName )
                || "indexOf".equals( methodName )
                || "subList".equals( methodName )
                || "get".equals( methodName )
                || "isEmpty".equals( methodName )
                || "containsKey".equals( methodName )
                || "values".equals( methodName )
                || "entrySet".equals( methodName )
                || "containsValue".equals( methodName )
                || "keySet".equals( methodName )
                || "size".equals( methodName )
                || "toArray".equals( methodName )
                || "iterator".equals( methodName )
                || "contains".equals( methodName )
                || "isEmpty".equals( methodName )
                || "containsAll".equals( methodName )
                || "size".equals( methodName ) );
    }

}

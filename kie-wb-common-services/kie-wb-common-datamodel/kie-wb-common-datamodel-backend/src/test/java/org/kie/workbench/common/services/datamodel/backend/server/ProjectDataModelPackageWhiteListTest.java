/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URL;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.drools.workbench.models.datamodel.oracle.ProjectDataModelOracle;
import org.junit.Test;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodel.backend.server.ProjectDataModelOracleTestUtils.*;

/**
 * Tests for DataModelService
 */
public class ProjectDataModelPackageWhiteListTest extends AbstractDataModelWeldTest {

    @Test
    public void testPackageNameWhiteList_EmptyWhiteList() throws Exception {
        final DataModelService dataModelService = getDataModelService();

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t7p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t7p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t7p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t7p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t7p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t7p2.Bean2" ) );
    }

    @Test
    public void testPackageNameWhiteList_IncludeOnePackage() throws Exception {
        final DataModelService dataModelService = getDataModelService();

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest2" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 1,
                      oracle.getProjectModelFields().size() );
        assertContains( "t8p1.Bean1",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t8p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t8p1.Bean1" ) );
    }

    @Test
    public void testPackageNameWhiteList_IncludeAllPackages() throws Exception {
        final DataModelService dataModelService = getDataModelService();

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest3" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t9p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t9p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t9p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t9p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t9p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t9p2.Bean2" ) );
    }

    @Test
    public void testPackageNameWhiteList_NoWhiteList() throws Exception {
        final DataModelService dataModelService = getDataModelService();

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest4" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t10p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t10p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t10p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t10p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t10p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t10p2.Bean2" ) );
    }

    @Test
    public void testPackageNameWhiteList_Wildcards() throws Exception {
        final DataModelService dataModelService = getDataModelService();

        final URL packageUrl = this.getClass().getResource( "/DataModelPackageWhiteListTest5" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        final ProjectDataModelOracle oracle = dataModelService.getProjectDataModel( packagePath );

        assertNotNull( oracle );

        assertEquals( 2,
                      oracle.getProjectModelFields().size() );
        assertContains( "t11.p1.Bean1",
                        oracle.getProjectModelFields().keySet() );
        assertContains( "t11.p2.Bean2",
                        oracle.getProjectModelFields().keySet() );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t11.p1.Bean1" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t11.p1.Bean1" ) );

        assertEquals( 1,
                      oracle.getProjectModelFields().get( "t11.p2.Bean2" ).length );
        assertContains( "this",
                        oracle.getProjectModelFields().get( "t11.p2.Bean2" ) );
    }

    private DataModelService getDataModelService() {
        final Bean dataModelServiceBean = (Bean) beanManager.getBeans( DataModelService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( dataModelServiceBean );
        return (DataModelService) beanManager.getReference( dataModelServiceBean,
                                                            DataModelService.class,
                                                            cc );
    }

}

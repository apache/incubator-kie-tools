package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URL;

import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;

public class DataModelerInnerTypesTest extends DataModelerServiceBaseTest {

    /**
     * This test checks that class fields that of enum types or inner class types should be skipped.
     *
     */
    @Test
    public void testDataModelerService() throws Exception {

        try {
            final URL packageUrl = this.getClass().getResource( "/TestInnerTypes" );
            final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
            final Path packagePath = paths.convert( nioPackagePath );

            KieProject project = projectService.resolveProject( packagePath );

            DataModel dataModel = dataModelService.loadModel( project );
            DataObject dataObject = dataModel.getDataObject( "test.Outer" );
            assertNotNull( "DataObject test.Outer was not loaded", dataObject );
            assertEquals( "DataObject test.Outer should not have readed properties", 0, dataObject.getProperties().size() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Test failed dued to the following exception: " + e.getMessage() );
        }
    }

}

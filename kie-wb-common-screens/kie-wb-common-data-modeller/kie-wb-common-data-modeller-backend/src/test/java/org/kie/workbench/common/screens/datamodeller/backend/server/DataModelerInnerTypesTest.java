package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.*;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.backend.vfs.Path;

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

            DataModelTO dataModel = dataModelService.loadModel( project );
            DataObjectTO dataObject = dataModel.getDataObjectByClassName("test.Outer");
            assertNotNull( "DataObject test.Outer was not loaded", dataObject );
            assertEquals( "DataObject test.Outer should not have readed properties", 0, dataObject.getProperties().size() );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Test failed dued to the following exception: " + e.getMessage() );
        }
    }

}

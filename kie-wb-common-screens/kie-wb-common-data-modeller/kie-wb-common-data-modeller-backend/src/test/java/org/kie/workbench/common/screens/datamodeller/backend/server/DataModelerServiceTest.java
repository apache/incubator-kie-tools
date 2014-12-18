package org.kie.workbench.common.screens.datamodeller.backend.server;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;

/**
 * Tests for DataModelService
 */
public class DataModelerServiceTest extends DataModelerServiceBaseTest {

    @Test
    public void testDataModelerService() throws Exception {

        final URL packageUrl = this.getClass().getResource( "/DataModelerTest1" );
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath( packageUrl.toURI() );
        final Path packagePath = paths.convert( nioPackagePath );

        KieProject project = projectService.resolveProject( packagePath );

        DataModelTO dataModelOriginalTO = createModel();
        DataModelTO dataModelTO = dataModelService.loadModel( project );
        Map<String, DataObjectTO> objectsMap = new HashMap<String, DataObjectTO>();

        assertNotNull( dataModelTO );

        assertEquals( dataModelOriginalTO.getDataObjects().size(), dataModelTO.getDataObjects().size() );

        for ( DataObjectTO dataObjectTO : dataModelTO.getDataObjects() ) {
            objectsMap.put( dataObjectTO.getClassName(), dataObjectTO );
        }

        for ( DataObjectTO dataObjectTO : dataModelOriginalTO.getDataObjects() ) {
            DataModelerAssert.assertEqualsDataObject( dataObjectTO, objectsMap.get( dataObjectTO.getClassName() ) );
        }

    }

    private DataModelTO createModel() {
        DataModelTO dataModelTO = new DataModelTO();

        DataObjectTO pojo1 = createDataObject( "Pojo1", "t1p1", "t1p2.Pojo2" );

        AnnotationTO annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION, "value", "true" );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.ROLE_ANNOTATION, "value", "EVENT" );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.LABEL_ANNOTATION, "value", "Pojo1Label" );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, "value", "Pojo1Description" );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.DURATION_ANNOTATION, "value", "duration" );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.TIMESTAMP_ANNOTATION, "value", "timestamp" );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION, null, null );
        pojo1.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.EXPIRES_ANNOTATION, "value", "1h25m" );
        pojo1.addAnnotation( annotationTO );

        ObjectPropertyTO propertyTO = addProperty( pojo1, "field1", "java.lang.Character", true, false, null );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.POSITION_ANNOTATION, "value", "0" );
        propertyTO.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.KEY_ANNOTATION, null, null );
        propertyTO.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.LABEL_ANNOTATION, "value", "field1Label" );
        propertyTO.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, "value", "field1Description" );
        propertyTO.addAnnotation( annotationTO );
        pojo1.getProperties().add( propertyTO );

        propertyTO = addProperty( pojo1, "duration", "java.lang.Integer", true, false, null );
        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.POSITION_ANNOTATION, "value", "1" );
        propertyTO.addAnnotation( annotationTO );
        pojo1.getProperties().add( propertyTO );

        propertyTO = addProperty( pojo1, "timestamp", "java.util.Date", true, false, null );
        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.POSITION_ANNOTATION, "value", "2" );
        propertyTO.addAnnotation( annotationTO );
        pojo1.getProperties().add( propertyTO );

        propertyTO = addProperty( pojo1, "field2", "char", true, false, null );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.POSITION_ANNOTATION, "value", "3" );
        propertyTO.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.KEY_ANNOTATION, null, null );
        propertyTO.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.LABEL_ANNOTATION, "value", "field2Label" );
        propertyTO.addAnnotation( annotationTO );

        annotationTO = createAnnotation( systemAnnotations, null, AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, "value", "field2Description" );
        propertyTO.addAnnotation( annotationTO );
        pojo1.getProperties().add( propertyTO );

        propertyTO = addProperty( pojo1, "serialVersionUID", "long", true, false, null );
        pojo1.getProperties().add( propertyTO );

        dataModelTO.getDataObjects().add( pojo1 );

        DataObjectTO pojo2 = createDataObject( "Pojo2", "t1p2", null );
        dataModelTO.getDataObjects().add( pojo2 );

        return dataModelTO;

    }

}

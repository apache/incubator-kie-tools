package org.kie.workbench.common.services.datamodeller.codegen;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.kie.workbench.common.services.datamodeller.core.*;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleModelDriver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GenerationEngineTest {

    private GenerationEngine engine;
    private DataModelOracleModelDriver dataModelOracleDriver;
    private Map<String, AnnotationDefinition> annotationDefinitions;

    Properties results = new Properties();

    @Before
    public void setup() {
        try {
            InputStream in = this.getClass().getResourceAsStream( "GenerationTestResults.properties" );
            results.load(in);
            in.close();

            engine = GenerationEngine.getInstance();
            dataModelOracleDriver = DataModelOracleModelDriver.getInstance();

            annotationDefinitions = new HashMap<String, AnnotationDefinition>(5);
            List<AnnotationDefinition> configuredAnnotations = dataModelOracleDriver.getConfiguredAnnotations();
            for ( AnnotationDefinition annotationDefinition : configuredAnnotations ) {
                annotationDefinitions.put( annotationDefinition.getClassName(), annotationDefinition );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClassAnnotationStringGeneration() {

        Annotation classReactive = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.ClassReactive.class.getName() ) );

        Annotation propReactive = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.PropertyReactive.class.getName() ) );

        Annotation role = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Role.class.getName() ) );
        role.setValue( "value", org.kie.api.definition.type.Role.Type.EVENT.name() );

        GenerationContext generationContext = new GenerationContext( dataModelOracleDriver.createModel() );

        try {
            String result = engine.generateAnnotationString(generationContext, classReactive);
            assertEquals( results.getProperty( "testClassAnnotationStringGeneration1" ), result );

            result = engine.generateAnnotationString(generationContext, propReactive);
            assertEquals( results.getProperty( "testClassAnnotationStringGeneration2" ), result );

            result = engine.generateAnnotationString(generationContext, role);
            assertEquals( results.getProperty( "testClassAnnotationStringGeneration3" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDefaultConstructorStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object = dataModel.addDataObject("com.test.Object1");

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateDefaultConstructorString(generationContext, object);
            assertEquals( results.getProperty( "testDefaultConstructorStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllFieldsConstructorStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        DataObject object2 = dataModel.addDataObject("com.test.sub.Object2");

        object1.addProperty("attribute1", "java.lang.String");
        object1.addProperty("attribute2", "java.lang.Boolean");
        object1.addProperty("attribute3", object2.getClassName());

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateAllFieldsConstructorString(generationContext, object1);
            assertEquals( results.getProperty( "testAllFieldsConstructorStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeyFieldsConstructorStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        DataObject object2 = dataModel.addDataObject("com.test.sub.Object2");

        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );
        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );

        position.setValue( "value", "0" );
        prop3.addAnnotation( key );
        prop3.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "1" );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "2" );
        prop2.addAnnotation( key );
        prop2.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue("value", "3");
        prop4.addAnnotation( position );

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateKeyFieldsConstructorString(generationContext, object1);
            assertEquals( results.getProperty( "testKeyFieldsConstructorStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllConstructorsStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        DataObject object2 = dataModel.addDataObject("com.test.sub.Object2");

        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );
        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );

        position.setValue( "value", "0" );
        prop3.addAnnotation( key );
        prop3.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "1" );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "2" );
        prop2.addAnnotation( key );
        prop2.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue("value", "3");
        prop4.addAnnotation( position );

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateAllConstructorsString(generationContext, object1);
            assertEquals( results.getProperty( "testAllConstructorsStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFieldAnnotationStringGeneration() {

        Annotation label = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Label.class.getName() ) );
        label.setValue( "value", "Attribute 1" );

        Annotation desc = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Description.class.getName() ) );
        desc.setValue( "value", "Description for Attribute 1" );

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );

        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue("value", "0");

        GenerationContext generationContext = new GenerationContext( dataModelOracleDriver.createModel() );

        try {
            String result = engine.generateAnnotationString(generationContext, label);
            assertEquals( results.getProperty( "testFieldAnnotationStringGeneration1" ), result );
            result = engine.generateAnnotationString(generationContext, desc);
            assertEquals( results.getProperty( "testFieldAnnotationStringGeneration2" ), result );
            result = engine.generateAnnotationString(generationContext, key);
            assertEquals( results.getProperty( "testFieldAnnotationStringGeneration3" ), result );
            result = engine.generateAnnotationString(generationContext, position);
            assertEquals( results.getProperty( "testFieldAnnotationStringGeneration4" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAllFieldAnnotationsStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");

        Annotation label = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Label.class.getName() ) );
        label.setValue( "value", "Attribute 1" );

        Annotation desc = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Description.class.getName() ) );
        desc.setValue( "value", "Description for Attribute 1" );

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );

        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "0" );

        prop1.addAnnotation( label );
        prop1.addAnnotation( desc );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        GenerationContext generationContext = new GenerationContext( dataModelOracleDriver.createModel() );

        try {
            String result = engine.generateAllAnnotationsString(generationContext, prop1);
            assertEquals( results.getProperty( "testAllFieldAnnotationsStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFieldStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object = dataModel.addDataObject("com.test.Object1");
        ObjectProperty property = object.addProperty("attribute1", "java.lang.String");

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateFieldString(generationContext, property);
            assertEquals( results.getProperty( "testFieldStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    // Return field definition + annotations
    public void testCompleteFieldStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");

        Annotation label = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Label.class.getName() ) );
        label.setValue( "value", "Attribute 1" );

        Annotation desc = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Description.class.getName() ) );
        desc.setValue( "value", "Description for Attribute 1" );

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );

        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "0" );

        prop1.addAnnotation( label );
        prop1.addAnnotation( desc );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        GenerationContext generationContext = new GenerationContext( dataModelOracleDriver.createModel() );

        try {
            String result = engine.generateCompleteFieldString(generationContext, prop1);
            assertEquals( results.getProperty( "testCompleteFieldStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFieldAccessorStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object = dataModel.addDataObject("com.test.Object1");
        ObjectProperty property = object.addProperty("attribute1", "java.lang.String");

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateFieldGetterString(generationContext, property);
            assertEquals( results.getProperty( "testFieldAccessorStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFieldMutatorStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object = dataModel.addDataObject("com.test.Object1");
        ObjectProperty property = object.addProperty("attribute1", "java.lang.String");

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateFieldSetterString(generationContext, property);
            assertEquals( results.getProperty( "testFieldMutatorStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFieldAccessorMutatorStringGeneration() {
        testFieldAccessorStringGeneration();
        testFieldMutatorStringGeneration();
    }

    @Test
    public void testEqualsStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        DataObject object2 = dataModel.addDataObject("com.test.sub.Object2");

        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );
        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );

        position.setValue( "value", "0" );
        prop4.addAnnotation( key );
        prop4.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "1" );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "2" );
        prop2.addAnnotation( key );
        prop2.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue("value", "3");
        prop3.addAnnotation( position );

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateEqualsString(generationContext, object1);
            assertEquals( results.getProperty( "testEqualsStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHashCodeStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        DataObject object2 = dataModel.addDataObject("com.test.sub.Object2");

        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );
        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );

        position.setValue( "value", "0" );
        prop4.addAnnotation( key );
        prop4.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "1" );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "2" );
        prop2.addAnnotation( key );
        prop2.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue("value", "3");
        prop3.addAnnotation( position );

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateHashCodeString(generationContext, object1);
            assertEquals( results.getProperty( "testHashCodeStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJavaClassStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.Object1");
        DataObject object2 = dataModel.addDataObject("com.test.sub.Object2");

        ObjectProperty prop1 = object1.addProperty("attribute1", "java.lang.String");
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );
        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );

        position.setValue( "value", "0" );
        prop4.addAnnotation( key );
        prop4.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "1" );
        prop1.addAnnotation( key );
        prop1.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue( "value", "2" );
        prop2.addAnnotation( key );
        prop2.addAnnotation( position );

        position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );
        position.setValue("value", "3");
        prop3.addAnnotation(position);

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateJavaClassString(generationContext, object1);
            System.out.println( result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}

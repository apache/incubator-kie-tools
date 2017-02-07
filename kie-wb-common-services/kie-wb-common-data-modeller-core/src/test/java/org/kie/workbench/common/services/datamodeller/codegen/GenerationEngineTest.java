
/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.codegen;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Parameter;
import org.kie.workbench.common.services.datamodeller.core.Type;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.MethodImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ParameterImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.TypeImpl;
import org.kie.workbench.common.services.datamodeller.driver.impl.DataModelOracleModelDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class GenerationEngineTest {

    private static final Logger logger = LoggerFactory.getLogger(GenerationEngineTest.class);

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
            Set<String> propertyNames = results.stringPropertyNames();
            for (String property : propertyNames) {
                String newProperty = results.getProperty(property).replaceAll("\n",
                                                                              System.getProperty("line.separator"));
                results.setProperty(property,
                                    newProperty);
            }

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
        (( ObjectPropertyImpl ) prop1 ).setFileOrder( 1 );
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        (( ObjectPropertyImpl ) prop2 ).setFileOrder( 2 );
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        (( ObjectPropertyImpl ) prop3 ).setFileOrder( 0);
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");
        (( ObjectPropertyImpl ) prop4 ).setFileOrder( 3 );

        Annotation key = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Key.class.getName() ) );
        Annotation position = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Position.class.getName() ) );

        prop3.addAnnotation( key );
        prop1.addAnnotation( key );

        prop2.addAnnotation( key );

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
        (( ObjectPropertyImpl ) prop1 ).setFileOrder( 0 );
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        (( ObjectPropertyImpl ) prop2 ).setFileOrder( 1 );
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        (( ObjectPropertyImpl ) prop3 ).setFileOrder( 2 );
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");
        (( ObjectPropertyImpl ) prop4 ).setFileOrder( 3 );

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
    public void testMaxFieldsForConstructorsStringGeneration1() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.MaxFieldsForConstructor1");

        ObjectProperty prop1;
        //The constructor for this data object should be generated, since we don't reach the limit.
        for (int i = 0; i < GenerationTools.MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR-1; i++) {
            prop1 = object1.addProperty("attribute"+normalize(i), "java.lang.String");
            ((ObjectPropertyImpl)prop1).setFileOrder( i );
        }

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateAllConstructorsString(generationContext, object1);
            assertEquals( results.getProperty( "testMaxFieldsForConstructorsStringGeneration1" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMaxFieldsForConstructorsStringGeneration2() {

        DataModel dataModel = dataModelOracleDriver.createModel();
        DataObject object1 = dataModel.addDataObject("com.test.MaxFieldsForConstructor2");

        ObjectProperty prop1;
        //The constructor for this data object should be generated, since we don't reach the limit.
        for (int i = 0; i < GenerationTools.MAX_FIELDS_FOR_DEFAULT_CONSTRUCTOR; i++) {
            prop1 = object1.addProperty("attribute"+normalize(i), "java.lang.String");
        }

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateAllConstructorsString(generationContext, object1);
            assertEquals( results.getProperty( "testMaxFieldsForConstructorsStringGeneration2" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private String normalize(int i) {
        if ( i < 10 ) return "000"+i;
        if ( i < 100 ) return "00"+i;
        if ( i < 1000 ) return "0"+i;
        return i+"";
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
    public void testMethodStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();

        Parameter parameter1 = new ParameterImpl( new TypeImpl( "com.test.Object1" ), "o1" );
        Parameter parameter2 = new ParameterImpl( new TypeImpl( "com.test.Object1" ), "o2" );

        Type returnType = new TypeImpl( "com.test.Object1" );

        Method method = new MethodImpl( "test", Arrays.asList( parameter1, parameter2 ), "return o1;", returnType, Visibility.PUBLIC );

        DataObject object = dataModel.addDataObject( "com.test.Object1" );
        object.addMethod( method );

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateMethodString( generationContext, method );
            assertEquals( results.getProperty( "testMethodStringGeneration" ), results.getProperty( "testMethodStringGeneration" ), result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void testClassWithNestedClassStringGeneration() {

        DataModel dataModel = dataModelOracleDriver.createModel();

        DataObject object = dataModel.addDataObject("com.test.Object1");

        GenerationContext generationContext = new GenerationContext( dataModel );

        try {
            String result = engine.generateNestedClassString(generationContext, object);
            assertEquals( results.getProperty( "testNestedClassStringGeneration" ), results.getProperty( "testNestedClassStringGeneration" ), result );
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

        ObjectPropertyImpl prop1 = (ObjectPropertyImpl) object1.addProperty("attribute1", "java.lang.String");
        prop1.setFileOrder( 0 );
        ObjectPropertyImpl prop2 = (ObjectPropertyImpl) object1.addProperty("attribute2", "java.lang.Boolean");
        prop2.setFileOrder( 1 );
        ObjectPropertyImpl prop3 = (ObjectPropertyImpl) object1.addProperty("attribute3", object2.getClassName());
        prop3.setFileOrder( 2 );
        ObjectPropertyImpl prop4 = (ObjectPropertyImpl) object1.addProperty("attribute4", "long");
        prop4.setFileOrder( 3 );

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
        (( ObjectPropertyImpl ) prop1 ).setFileOrder( 0 );
        ObjectProperty prop2 = object1.addProperty("attribute2", "java.lang.Boolean");
        (( ObjectPropertyImpl ) prop2 ).setFileOrder( 1 );
        ObjectProperty prop3 = object1.addProperty("attribute3", object2.getClassName());
        (( ObjectPropertyImpl ) prop3 ).setFileOrder( 2 );
        ObjectProperty prop4 = object1.addProperty("attribute4", "long");
        (( ObjectPropertyImpl ) prop4 ).setFileOrder( 3 );

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

        Annotation label = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Label.class.getName() ) );
        label.setValue( "value", "Object1 Label" );
        object1.addAnnotation( label );

        Annotation classReactive = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.ClassReactive.class.getName() ) );
        object1.addAnnotation( classReactive );

        Annotation propReactive = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.PropertyReactive.class.getName() ) );
        object1.addAnnotation( propReactive );

        Annotation role = new AnnotationImpl( annotationDefinitions.get( org.kie.api.definition.type.Role.class.getName() ) );
        role.setValue( "value", org.kie.api.definition.type.Role.Type.EVENT.name() );
        object1.addAnnotation( role );


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
            logger.debug( result );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}

/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.datamodeller.parser;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.parser.descr.AnnotationDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.FieldDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.ModifierDescr;
import org.kie.workbench.common.services.datamodeller.parser.descr.VariableDeclarationDescr;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodeller.parser.ParserAssert.*;

public class AnnotationParsing1Test extends JavaParserBaseTest {

    List<FieldDescr> expectedFields = new ArrayList<FieldDescr>(  );

    public AnnotationParsing1Test( ) {
        super( "AnnotationParsing1.java" );
        init( );
    }

    @Test
    public void testAnnotations( ) {
        try {

            assertClass( );

            List<AnnotationDescr> classAnnotations = parser.getFileDescr().getClassDescr().getModifiers().getAnnotations();
            assertEquals(4, classAnnotations.size() );

            assertEqualsAnnotation( null, ParserTestUtil.createAnnotation( "TestAnnotation", null, null ), classAnnotations.get( 0 ) );
            assertEqualsAnnotation( null, ParserTestUtil.createAnnotation( "TestAnnotation1", "\"value\"", null ), classAnnotations.get( 1 ) );
            assertEqualsAnnotation( null, ParserTestUtil.createAnnotation( "TestAnnotation2", null, new String[][] {{ "method1", "\"param1\""}, { "method2", "\"param2\""}} ), classAnnotations.get( 2 ) );
            assertEqualsAnnotation( null, ParserTestUtil.createAnnotation( "TestAnnotation3", null, new String[][] {{ "value", "\"value\""}, { "method1", "\"param1\""}, { "method2", "\"param2\""}} ), classAnnotations.get( 3 ) );


            List<FieldDescr> fields = parser.getFileDescr( ).getClassDescr( ).getFields( );
            assertEquals( expectedFields.size( ), fields.size( ) );

            for ( int i = 0; i < expectedFields.size( ); i++ ) {
                assertEqualsFieldDeclaration( null,  expectedFields.get( i ), fields.get( i ) );
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage( ) );
        }
    }


    private void init() {

        FieldDescr fieldDeclaration;
        VariableDeclarationDescr variableDecl;
        AnnotationDescr annotationDescr;

        //@TestAnnotation()
        //public int field1;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field1", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation", null, new String[][]{} ) );
        fieldDeclaration.addModifier( new ModifierDescr( "public", -1, -1, -1, -1, "public" ) );
        expectedFields.add( fieldDeclaration );

        //@TestAnnotation
        //private int field2;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field2", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation", null, null ));
        fieldDeclaration.addModifier( new ModifierDescr( "private", -1, -1, -1, -1, "private" ) );
        expectedFields.add( fieldDeclaration );

        //public
        //@TestAnnotation1
        //static int field3;
        fieldDeclaration = ParserTestUtil.createField( new String[]{"public"}, "field3", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation1", null, null ));
        fieldDeclaration.addModifier( new ModifierDescr( "static", -1, -1, -1, -1, "static" ) );
        expectedFields.add( fieldDeclaration );

        //@TestAnnotation1("value")
        //int field4;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field4", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation1", "\"value\"", null ) );
        expectedFields.add( fieldDeclaration );

        //protected
        //@TestAnnotation1( value = "value")
        //int field5;
        fieldDeclaration = ParserTestUtil.createField( new String[]{"protected"}, "field5", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation1", null, new String[][]{ { "value", "\"value\""} } ));
        expectedFields.add( fieldDeclaration );

        //@TestAnnotation2( method1 = "param1", method2 = "param2")
        //int field6;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field6", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation2", null, new String[][]{ { "method1", "\"param1\""}, { "method2", "\"param2\"" } } ));
        expectedFields.add( fieldDeclaration );


        //@TestAnnotation2(method2 = "param2")
        //int field7;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field7", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation2", null, new String[][]{ { "method2", "\"param2\"" } } ));
        expectedFields.add( fieldDeclaration );

        //@TestAnnotation3( value = "value", method1 = "param1", method2 = "param2")
        //int field8;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field8", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation3", null, new String[][]{ { "value", "\"value\""}, {"method1", "\"param1\""}, { "method2", "\"param2\"" } } ));
        expectedFields.add( fieldDeclaration );

        //@TestAnnotation
        //@TestAnnotation1("value")
        //@TestAnnotation2( method1 = "param1", method2 = "param2")
        //@TestAnnotation3( value = "value", method1 = "param1", method2 = "param2" )
        //int field9;
        fieldDeclaration = ParserTestUtil.createField( new String[]{}, "field9", "int", null );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation", null, null ) );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation1", "\"value\"", null ) );
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation2", null, new String[][]{ {"method1", "\"param1\""}, { "method2", "\"param2\"" } } ));
        fieldDeclaration.addAnnotation( ParserTestUtil.createAnnotation( "TestAnnotation3", null, new String[][]{ { "value", "\"value\""}, {"method1", "\"param1\""}, { "method2", "\"param2\"" } } ));
        expectedFields.add( fieldDeclaration );

    }

}

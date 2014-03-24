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

import org.junit.Ignore;
import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.parser.descr.*;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.kie.workbench.common.services.datamodeller.parser.ParserAssert.assertEqualsFieldDeclaration;
import static org.kie.workbench.common.services.datamodeller.parser.ParserAssert.assertEqualsMethodDeclaration;
import static org.kie.workbench.common.services.datamodeller.parser.ParserAssert.assertEqualsParameterList;

public class MehodParsing1Test extends JavaParserBaseTest {

    private List<MethodDescr> expectedMethods;

    private List<String> methodSentences;

    public MehodParsing1Test( ) {
        super( "MethodParsing1.java" );
        init( );
    }

    @Test
    public void testMethodsSentencesReading( ) {
        try {
            assertClass( );
            List<MethodDescr> methods = parser.getFileDescr( ).getClassDescr( ).getMethods( );
            int i = 0;
            for ( String methodSentence : methodSentences ) {
                assertEquals( methodSentences.get( i ), ParserUtil.readElement( buffer, methods.get( i ) ) );
                i++;
            }
        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage( ) );
        }
    }

    @Test
    public void testClassMethods( ) {

        try {
            assertClass( );

            List<MethodDescr> methods = parser.getFileDescr( ).getClassDescr( ).getMethods( );
            assertEquals( expectedMethods.size( ), methods.size( ) );
            for ( int i = 0; i < expectedMethods.size( ); i++ ) {
                assertEqualsMethodDeclaration( buffer, expectedMethods.get( i ), methods.get( i ) );
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage( ) );
        }

    }

    @Test
    public void testMethodParsingMode( ) {
        try {
            JavaParser parser;
            int i = 0;
            for ( String methodSentence : methodSentences ) {
                parser = JavaParserFactory.newParser( methodSentence, JavaParserBase.ParserMode.PARSE_METHOD );
                parser.methodDeclaration( );
                assertNotNull( parser.getMethodDescr( ) );
                assertEqualsMethodDeclaration( new StringBuffer( methodSentence ), expectedMethods.get( i ), parser.getMethodDescr( ) );
                i++;
            }
        } catch ( Exception e ) {
            e.printStackTrace( );
            fail( "Test failed: " + e.getMessage( ) );
        }
    }

    private void init( ) {

        methodSentences = new ArrayList<String>( );

        methodSentences.add( "public MethodParsing1() { this(null); }" );
        methodSentences.add( "public MethodParsing1(String field1) { this.field1 = field1; }" );
        methodSentences.add( "public MethodParsing1(int a, int b) { this(a, b, (byte)1); }" );
        methodSentences.add( "public MethodParsing1(int a, int b, byte c) { super(); }" );

        methodSentences.add( "public String getField1() { return field1; }" );
        methodSentences.add( "public void setField1(String field1) { this.field1 = field1; }" );
        methodSentences.add( "private int method1() { return -1; }" );
        methodSentences.add( "private void method2() {}" );
        methodSentences.add( "public static java.lang.String method3() { return null; }" );
        methodSentences.add( "public static final Integer method4() { return null; }" );
        methodSentences.add( "public void method5(java.lang.Integer param1, int param2) {}" );
        methodSentences.add( "java.util.List<java.lang.String> method6() { return null;    }" );
        methodSentences.add( "protected   java.util.AbstractList<String>    method7  ( final int   param1 ,  java.lang.Integer   param2  ,   java.util.List<java.lang.Integer>      param3      ) {    return  null  ;    }" );
        methodSentences.add( "int method8  ( final int   param1 ,  java.lang.Integer   param2   ) [  ]   [    ] { return null; }" );
        methodSentences.add( "int method9 ( final Object ...  param1) { return -1;}" );
        methodSentences.add( "private java.util.AbstractList<Object> method10  (  final java.lang.String param1,  int param2 , List<java.util.List<String>>...param3) { return null; }" );

        expectedMethods = new ArrayList<MethodDescr>( );

        MethodDescr method;
        NormalParameterDescr param1;
        NormalParameterDescr param2;
        NormalParameterDescr param3;
        EllipsisParameterDescr ellipsisParam;
        ParameterListDescr parmsList;

        //public MethodParsing1() { this(null); }
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setIdentifier( new IdentifierDescr( "MethodParsing1", -1, -1, -1, -1 ) );
        expectedMethods.add( method );

        //public MethodParsing1(String field1) { this.field1 = field1; }
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setIdentifier( new IdentifierDescr( "MethodParsing1", -1, -1, -1, -1 ) );

        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "String", -1, -1 ) );
        param1.setIdentifier( new IdentifierDescr( "field1", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //public MethodParsing1(int a, int b) { this(a, b, (byte)1); }
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setIdentifier( new IdentifierDescr( "MethodParsing1", -1, -1, -1, -1 ) );

        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param1.setIdentifier( new IdentifierDescr( "a", -1, -1, -1, -1 ) );

        param2 = new NormalParameterDescr( );
        param2.setModifiers( new ModifierListDescr( ) );
        param2.setType( new TypeDescr( null, -1, -1 ) );
        param2.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param2.setIdentifier( new IdentifierDescr( "b", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        parmsList.addParameter( param2 );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //public MethodParsing1(int a, int b, byte c) { super(); }
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setIdentifier( new IdentifierDescr( "MethodParsing1", -1, -1, -1, -1 ) );

        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param1.setIdentifier( new IdentifierDescr( "a", -1, -1, -1, -1 ) );

        param2 = new NormalParameterDescr( );
        param2.setModifiers( new ModifierListDescr( ) );
        param2.setType( new TypeDescr( null, -1, -1 ) );
        param2.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param2.setIdentifier( new IdentifierDescr( "b", -1, -1, -1, -1 ) );

        param3 = new NormalParameterDescr( );
        param3.setModifiers( new ModifierListDescr( ) );
        param3.setType( new TypeDescr( null, -1, -1 ) );
        param3.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "byte" ) );
        param3.setIdentifier( new IdentifierDescr( "c", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        parmsList.addParameter( param2 );
        parmsList.addParameter( param3 );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //"public String getField1() { return field1; }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "String", -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "getField1", -1, -1, -1, -1 ) );
        expectedMethods.add( method );

        //"public void setField1(String field1) { this.field1 = field1; }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setType( new TypeDescr( ) );
        method.getType( ).setVoidType( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_VOID, "void", -1, -1, -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "getField1", -1, -1, -1, -1 ) );
        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "String", -1, -1 ) );
        param1.setIdentifier( new IdentifierDescr( "field1", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //"private int method1() { return -1; }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "private" ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        method.setIdentifier( new IdentifierDescr( "method1", -1, -1, -1, 1 ) );
        expectedMethods.add( method );

        //"private void method2() {}"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "private" ) );
        method.setType( new TypeDescr( ) );
        method.getType( ).setVoidType( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_VOID, "void", -1, -1, -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "method2", -1, -1, -1, -1 ) );
        expectedMethods.add( method );

        //"public static java.lang.String method3() { return null; }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "static" ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.lang.String", -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "method3", -1, -1, -1, -1 ) );
        expectedMethods.add( method );

        //"public static final Integer method4() { return null; }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "static" ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "final" ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "Integer", -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "method4", -1, -1, -1, -1 ) );
        expectedMethods.add( method );

        //"public void method5(java.lang.Integer param1, int param2) {}"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "public" ) );
        method.setType( new TypeDescr( ) );
        method.getType( ).setVoidType( new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_VOID, "void", -1, -1, -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "method5", -1, -1, -1, -1 ) );
        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.lang.Integer", -1, -1 ) );
        param1.setIdentifier( new IdentifierDescr( "param1", -1, -1, -1, -1 ) );

        param2 = new NormalParameterDescr( );
        param2.setModifiers( new ModifierListDescr( ) );
        param2.setType( new TypeDescr( null, -1, -1 ) );
        param2.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param2.setIdentifier( new IdentifierDescr( "param2", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        parmsList.addParameter( param2 );
        method.setParamsList( parmsList );

        expectedMethods.add( method );

        //"java.util.List<java.lang.String> method6() { return null;    }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.util.List<java.lang.String>", -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "method6", -1, -1, -1, -1 ) );
        expectedMethods.add( method );

        //"protected   java.util.AbstractList<String>    method7  ( final int   param1 ,  java.lang.Integer   param2  ,   java.util.List<java.lang.Integer>      param3      ) {    return  null  ;    }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "protected" ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.util.AbstractList<String>", -1, -1 ) );
        method.setIdentifier( new IdentifierDescr( "method7", -1, -1, -1, -1 ) );

        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.addModifier( new ModifierDescr( null, -1, -1, "final" ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param1.setIdentifier( new IdentifierDescr( "param1", -1, -1, -1, -1 ) );

        param2 = new NormalParameterDescr( );
        param2.setModifiers( new ModifierListDescr( ) );
        param2.setType( new TypeDescr( null, -1, -1 ) );
        param2.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.lang.Integer", -1, -1 ) );
        param2.setIdentifier( new IdentifierDescr( "param2", -1, -1, -1, -1 ) );

        param3 = new NormalParameterDescr( );
        param3.setModifiers( new ModifierListDescr( ) );
        param3.setType( new TypeDescr( null, -1, -1 ) );
        param3.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.util.List<java.lang.Integer>", -1, -1 ) );
        param3.setIdentifier( new IdentifierDescr( "param3", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        parmsList.addParameter( param2 );
        parmsList.addParameter( param3 );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //"int method8  ( final int   param1 ,  java.lang.Integer   param2)[][] { return null; }"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        method.setIdentifier( new IdentifierDescr( "method8", -1, -1, -1, -1 ) );

        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param1.addModifier( new ModifierDescr( null, -1, -1, "final" ) );
        param1.setIdentifier( new IdentifierDescr( "param1", -1, -1, -1, -1 ) );

        param2 = new NormalParameterDescr( );
        param2.setModifiers( new ModifierListDescr( ) );
        param2.setType( new TypeDescr( null, -1, -1 ) );
        param2.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.lang.Integer", -1, -1 ) );
        param2.setIdentifier( new IdentifierDescr( "param2", -1, -1, -1, -1 ) );
        method.addDimension( new DimensionDescr( "", -1, -1, -1, -1, new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_LBRACKET, "[", -1, -1, -1, -1 ), new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_RBRACKET, "]", -1, -1, -1, -1 ) ) );
        method.addDimension( new DimensionDescr( "", -1, -1, -1, -1, new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_LBRACKET, "[", -1, -1, -1, -1 ), new JavaTokenDescr( ElementDescriptor.ElementType.JAVA_RBRACKET, "]", -1, -1, -1, -1 ) ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        parmsList.addParameter( param2 );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //"int method9 ( final Object ...  param1) { return -1;}"
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        method.setIdentifier( new IdentifierDescr( "method9", -1, -1, -1, -1 ) );

        ellipsisParam = new EllipsisParameterDescr( );
        ellipsisParam.setModifiers( new ModifierListDescr( ) );
        ellipsisParam.setType( new TypeDescr( null, -1, -1 ) );
        ellipsisParam.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "Object", -1, -1 ) );
        ellipsisParam.addModifier( new ModifierDescr( null, -1, -1, "final" ) );
        ellipsisParam.setIdentifier( new IdentifierDescr( "param1", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( ellipsisParam );
        method.setParamsList( parmsList );
        expectedMethods.add( method );

        //private java.util.AbstractList<Object> method10  (  final java.lang.String param1,  int param2 , List<java.util.List<String>>...param3) { return null; }
        method = new MethodDescr( null, -1, -1 );
        method.setModifiers( new ModifierListDescr( ) );
        method.setType( new TypeDescr( null, -1, -1 ) );
        method.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.util.AbstractList<Object>", -1, -1 ) );
        method.addModifier( new ModifierDescr( null, -1, -1, "private" ) );
        method.setIdentifier( new IdentifierDescr( "method10", -1, -1, -1, -1 ) );

        param1 = new NormalParameterDescr( );
        param1.setModifiers( new ModifierListDescr( ) );
        param1.addModifier( new ModifierDescr( null, -1, -1, "final" ) );
        param1.setType( new TypeDescr( null, -1, -1 ) );
        param1.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "java.lang.String", -1, -1 ) );
        param1.setIdentifier( new IdentifierDescr( "param1", -1, -1, -1, -1 ) );

        param2 = new NormalParameterDescr( );
        param2.setModifiers( new ModifierListDescr( ) );
        param2.setType( new TypeDescr( null, -1, -1 ) );
        param2.getType( ).setPrimitiveType( new PrimitiveTypeDescr( null, -1, -1, "int" ) );
        param2.setIdentifier( new IdentifierDescr( "param2", -1, -1, -1, -1 ) );

        ellipsisParam = new EllipsisParameterDescr( );
        ellipsisParam.setModifiers( new ModifierListDescr( ) );
        ellipsisParam.setType( new TypeDescr( null, -1, -1 ) );
        ellipsisParam.getType( ).setClassOrInterfaceType( new ClassOrInterfaceTypeDescr( "List<java.util.List<String>>", -1, -1 ) );
        ellipsisParam.setIdentifier( new IdentifierDescr( "param3", -1, -1, -1, -1 ) );

        parmsList = new ParameterListDescr( );
        parmsList.addParameter( param1 );
        parmsList.addParameter( param2 );
        parmsList.addParameter( ellipsisParam );
        method.setParamsList( parmsList );

        expectedMethods.add( method );

    }
}

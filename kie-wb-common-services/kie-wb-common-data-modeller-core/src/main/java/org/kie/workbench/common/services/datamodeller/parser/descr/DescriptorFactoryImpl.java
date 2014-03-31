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

package org.kie.workbench.common.services.datamodeller.parser.descr;

import org.kie.workbench.common.services.datamodeller.parser.JavaParser;
import org.kie.workbench.common.services.datamodeller.parser.JavaParserBase.ParserMode;
import org.kie.workbench.common.services.datamodeller.parser.JavaParserFactory;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

public class DescriptorFactoryImpl implements DescriptorFactory {

    public static DescriptorFactory getInstance( ) {
        return new DescriptorFactoryImpl( );
    }

    @Override
    public MethodDescr createMethodDescr( String source ) throws Exception {
        JavaParser parser = JavaParserFactory.newParser( source, ParserMode.PARSE_METHOD );
        parser.methodDeclaration( );
        MethodDescr methodDescr = parser.getMethodDescr( );
        //TODO the parser should set the source for the elements
        ParserUtil.setSourceBufferTMP( methodDescr, parser.getSourceBuffer( ) );
        ParserUtil.populateUnManagedElements( methodDescr );
        ParserUtil.setSourceBufferTMP( methodDescr, parser.getSourceBuffer( ) );
        return methodDescr;
    }

    @Override
    public FieldDescr createFieldDescr( String source ) throws Exception {
        JavaParser parser = JavaParserFactory.newParser( source, ParserMode.PARSE_FIELD );
        parser.fieldDeclaration( );
        FieldDescr fieldDescr = parser.getFieldDescr( );
        //TODO the parser should set the source for his children
        ParserUtil.setSourceBufferTMP( fieldDescr, parser.getSourceBuffer( ) );
        ParserUtil.populateUnManagedElements( fieldDescr );
        ParserUtil.setSourceBufferTMP( fieldDescr, parser.getSourceBuffer( ) );
        return fieldDescr;
    }

    @Override
    public IdentifierDescr createIdentifierDescr( String source ) throws Exception {
        IdentifierDescr identifierDescr = new IdentifierDescr( source, 0, source.length()-1, 1, 0 );
        identifierDescr.setSourceBuffer( new StringBuilder( source ) );
        return identifierDescr;
    }

    @Override
    public TypeDescr createTypeDescr( String source ) throws Exception {
        JavaParser parser = JavaParserFactory.newParser( source, ParserMode.PARSE_TYPE );
        TypeDescr result = parser.type().typeDescr;
        ParserUtil.setSourceBufferTMP( result, parser.getSourceBuffer() );
        return result;
    }

    @Override
    public PackageDescr createPackageDescr( String source ) throws Exception {
        JavaParser parser = JavaParserFactory.newParser( source, ParserMode.PARSE_PACKAGE );
        PackageDescr result = parser.packageDeclaration().packageDec;
        ParserUtil.setSourceBufferTMP( result, parser.getSourceBuffer() );
        return result;
    }

    @Override
    public QualifiedNameDescr createQualifiedNameDescr( String source ) throws Exception {
        JavaParser parser = JavaParserFactory.newParser( source, ParserMode.PARSE_QUALIFIED_NAME);
        QualifiedNameDescr result = parser.qualifiedName().qnameDec;
        ParserUtil.setSourceBufferTMP( result, parser.getSourceBuffer() );
        return result;
    }

    @Override
    public JavaTokenDescr createJavaTokenDescr(  ElementDescriptor.ElementType tokenType,  String source ) {
        JavaTokenDescr javaTokenDescr = new JavaTokenDescr( tokenType, source, 0, source.length()-1, 1, 0 );
        javaTokenDescr.setSourceBuffer( new StringBuilder( source ) );
        return javaTokenDescr;
    }

    @Override
    public JavaTokenDescr createExtendsTokenDescr() {
        return createJavaTokenDescr( ElementDescriptor.ElementType.JAVA_EXTENDS, "extends" );
    }

    @Override
    public TextTokenElementDescr createTextTokenDescr( String text ) {
        TextTokenElementDescr textTokenDescr = new TextTokenElementDescr( text, 0, text.length()-1, 1, 0 );
        textTokenDescr.setSourceBuffer( new StringBuilder( text ) );
        return textTokenDescr;
    }
}

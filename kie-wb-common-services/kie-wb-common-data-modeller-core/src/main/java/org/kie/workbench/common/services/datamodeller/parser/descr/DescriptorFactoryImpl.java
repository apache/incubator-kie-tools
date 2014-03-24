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
        //TODO the parser should set the source the his children
        ParserUtil.setSourceBufferTMP( fieldDescr, parser.getSourceBuffer( ) );
        ParserUtil.populateUnManagedElements( fieldDescr );
        ParserUtil.setSourceBufferTMP( fieldDescr, parser.getSourceBuffer( ) );
        return fieldDescr;
    }
}

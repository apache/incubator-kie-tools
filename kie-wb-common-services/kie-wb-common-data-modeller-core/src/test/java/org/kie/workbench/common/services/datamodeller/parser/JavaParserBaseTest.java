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

import org.junit.Before;

import static org.junit.Assert.*;

import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import java.io.InputStream;

public class JavaParserBaseTest {

    JavaParser parser;

    String fileName;

    StringBuffer buffer;

    public JavaParserBaseTest( String fileName ) {
        this.fileName = fileName;
    }

    @Before
    public void preTest( ) throws Exception {
        InputStream inputStream = this.getClass( ).getResourceAsStream( fileName );
        parser = JavaParserFactory.newParser( inputStream );
        buffer = new StringBuffer( ParserUtil.readString( this.getClass( ).getResourceAsStream( fileName ) ) );

        parser.compilationUnit( );
    }

    protected void assertClass( ) {
        assertNotNull( parser.getFileDescr( ) );
        assertNotNull( parser.getFileDescr( ).getClassDescr( ) );
    }

}

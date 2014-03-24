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

import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

public class JavaFileHandlerBaseTest {

    String fileName;

    String originalFileContent;

    protected JavaFileHandlerImpl fileHandler;

    public JavaFileHandlerBaseTest( String fileName ) {
        this.fileName = fileName;
    }

    @Before
    public void preTest( ) throws Exception {

        InputStream inputStream = this.getClass( ).getResourceAsStream( fileName );
        fileHandler = new JavaFileHandlerImpl( inputStream );
        inputStream.close( );

        inputStream = this.getClass( ).getResourceAsStream( fileName );
        originalFileContent = ParserUtil.readString( inputStream );
        inputStream.close( );

        //initial tests
        //after reading handler original content should be the same as file content.
        assertEquals( originalFileContent, fileHandler.getOriginalContent( ) );

        //if we invoke the build method without modifications
        //the generated file should be the same as originalFileContent.
        assertEquals( originalFileContent, fileHandler.buildResult( ) );

    }

}

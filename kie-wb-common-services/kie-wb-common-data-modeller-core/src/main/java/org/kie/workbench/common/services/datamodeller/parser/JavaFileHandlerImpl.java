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

import java.io.InputStream;

import org.kie.workbench.common.services.datamodeller.parser.descr.FileDescr;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

public class JavaFileHandlerImpl implements JavaFileHandler {

    private StringBuilder source = null;

    private JavaParser parser;

    private FileDescr fileDescr;

    public JavaFileHandlerImpl( InputStream inputStream ) throws Exception {
        //TODO implement better exceptions handling
        source = new StringBuilder( ParserUtil.readString( inputStream ) );
        parseSource( );
    }

    public JavaFileHandlerImpl (String sourceString) throws Exception {
        source = new StringBuilder( sourceString );
        parseSource();
    }

    public FileDescr getFileDescr( ) {
        return fileDescr;
    }

    public String getOriginalContent( ) {
        return source.toString( );
    }

    private void parseSource( ) throws Exception {
        parser = JavaParserFactory.newParser( source.toString( ) );
        parser.compilationUnit( );
        fileDescr = parser.getFileDescr( );
        ParserUtil.setSourceBufferTMP( fileDescr, parser.getSourceBuffer( ) );
        ParserUtil.populateUnManagedElements( fileDescr );
    }

    public String buildResult( ) {
        return ParserUtil.printTree( fileDescr );
    }
}
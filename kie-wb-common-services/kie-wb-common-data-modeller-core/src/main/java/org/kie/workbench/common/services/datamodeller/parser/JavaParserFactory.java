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

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.kie.workbench.common.services.datamodeller.parser.util.ParserUtil;

public class JavaParserFactory {

    public static JavaParser newParser( final InputStream inputStream ) throws Exception {
        StringBuilder source = ParserUtil.readStringBuilder( inputStream );
        return newParser( source.toString( ), JavaParserBase.ParserMode.PARSE_CLASS );
    }

    public static JavaParser newParser( final String source, final JavaParserBase.ParserMode mode ) {
        final CharStream charStream = new ANTLRStringStream( source );
        return newParser( charStream, new StringBuilder( source ), mode );
    }

    public static JavaParser newParser( final String source ) {
        return newParser( source, JavaParserBase.ParserMode.PARSE_CLASS );
    }

    private static JavaParser newParser( final CharStream charStream, StringBuilder source, final JavaParserBase.ParserMode mode ) {
        final JavaLexer lexer = new JavaLexer( charStream );
        final TokenStream tokenStream = new CommonTokenStream( lexer );
        final JavaParser parser = new JavaParser( tokenStream, source, mode );
        return parser;
    }
}

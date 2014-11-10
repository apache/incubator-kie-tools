/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.uberfire.metadata.backend.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

public class LowerCaseTokenizer extends CharTokenizer {

    public LowerCaseTokenizer( Version matchVersion,
                               Reader input ) {
        super( matchVersion, input );
    }

    public LowerCaseTokenizer( Version matchVersion,
                               AttributeSource source,
                               Reader input ) {
        super( matchVersion, source, input );
    }

    public LowerCaseTokenizer( Version matchVersion,
                               AttributeFactory factory,
                               Reader input ) {
        super( matchVersion, factory, input );
    }

    @Override
    protected boolean isTokenChar( int c ) {
        return true;
    }

    /**
     * Converts char to lower case
     * {@link Character#toLowerCase(int)}.
     */
    @Override
    protected int normalize( int c ) {
        try {
            return Character.toLowerCase( c );
        } catch ( Exception ex ) {
            return c;
        }
    }

}

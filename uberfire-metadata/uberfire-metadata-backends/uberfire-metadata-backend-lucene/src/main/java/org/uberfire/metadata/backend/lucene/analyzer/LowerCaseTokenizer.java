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

package org.uberfire.metadata.backend.lucene.analyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.util.Version;

public class FilenameAnalyzer extends Analyzer {

    private final Version matchVersion;

    public FilenameAnalyzer( final Version matchVersion ) {
        super();
        this.matchVersion = matchVersion;
    }

    @Override
    protected TokenStreamComponents createComponents( String fieldName,
                                                      Reader reader ) {
        final LowerCaseTokenizer src = new LowerCaseTokenizer( matchVersion, reader );
        final TokenStream tok = new LowerCaseFilter( matchVersion, src );

        return new TokenStreamComponents( src, tok );
    }
}

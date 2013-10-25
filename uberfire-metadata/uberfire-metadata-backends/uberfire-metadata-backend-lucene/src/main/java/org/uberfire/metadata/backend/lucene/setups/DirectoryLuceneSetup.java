/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.metadata.backend.lucene.setups;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40Codec;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.uberfire.metadata.backend.lucene.analyzer.FilenameAnalyzer;

import static org.apache.lucene.util.Version.*;
import static org.uberfire.commons.validation.Preconditions.*;

/**
 *
 */
public class DirectoryLuceneSetup extends BaseLuceneSetup {

    public static final String REPOSITORIES_ROOT_DIR = ".index";

    private final IndexWriter writer;
    private final Analyzer analyzer;
    private final Directory directory;
    private final boolean freshIndex;

    public DirectoryLuceneSetup( final Directory directory,
                                 final boolean freshIndex ) {
        try {
            this.freshIndex = freshIndex;
            this.directory = checkNotNull( "directory", directory );

            this.analyzer = new PerFieldAnalyzerWrapper( new StandardAnalyzer( LUCENE_40 ), new HashMap<String, Analyzer>() {{
                put( CUSTOM_FIELD_FILENAME, new FilenameAnalyzer( LUCENE_40 ) );
            }} );

            final IndexWriterConfig config = new IndexWriterConfig( LUCENE_40, getAnalyzer() );

            final Codec codec = new Lucene40Codec() {
                @Override
                public PostingsFormat getPostingsFormatForField( String field ) {
                    if ( field.equals( "id" ) ) {
                        return PostingsFormat.forName( "Memory" );
                    } else {
                        return PostingsFormat.forName( "Lucene40" );
                    }
                }
            };
            config.setCodec( codec );

            this.writer = new IndexWriter( directory, config );
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public IndexWriter writer() {
        return writer;
    }

    @Override
    public IndexSearcher nrtSearcher() {
        try {
            return new SearcherFactory().newSearcher( DirectoryReader.open( writer, true ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void nrtRelease( final IndexSearcher searcher ) {
        try {
            searcher.getIndexReader().close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    @Override
    public void dispose() {
        try {
            writer.commit();
            writer.close();
            analyzer.close();
            directory.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean freshIndex() {
        return freshIndex;
    }

    @Override
    public void commit() {
        try {
            writer.commit();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    protected static boolean freshIndex( final File file ) {
        return !file.exists();
    }

    protected static File defaultFile() {
        final String value = System.getProperty( "org.uberfire.metadata.index.dir" );
        if ( value == null || value.trim().isEmpty() ) {
            return new File( REPOSITORIES_ROOT_DIR );
        } else {
            return new File( value.trim(), REPOSITORIES_ROOT_DIR );
        }
    }

}

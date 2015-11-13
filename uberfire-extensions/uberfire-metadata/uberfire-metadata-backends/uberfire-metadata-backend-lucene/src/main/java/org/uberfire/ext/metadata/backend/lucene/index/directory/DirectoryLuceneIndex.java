/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.backend.lucene.index.directory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.uberfire.ext.metadata.backend.lucene.index.BaseLuceneIndex;
import org.uberfire.ext.metadata.model.KCluster;

import static org.uberfire.commons.validation.Preconditions.*;

/**
 *
 */
public class DirectoryLuceneIndex extends BaseLuceneIndex {

    private final KCluster cluster;
    private final IndexWriter writer;
    private final Directory directory;
    private AtomicBoolean freshIndex;
    private AtomicBoolean isDisposed = new AtomicBoolean( false );

    public DirectoryLuceneIndex( final KCluster cluster,
                                 final Directory directory,
                                 final IndexWriterConfig config ) {
        try {
            this.cluster = checkNotNull( "cluster", cluster );
            this.directory = checkNotNull( "directory", directory );
            this.writer = new IndexWriter( directory.getDirectory(), config );
            this.freshIndex = new AtomicBoolean( directory.freshIndex() );
        } catch ( final Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public IndexWriter writer() {
        return writer;
    }

    @Override
    public KCluster getCluster() {
        return cluster;
    }

    @Override
    public IndexReader nrtReader() {
        try {
            return DirectoryReader.open( writer, true );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void nrtRelease( final IndexReader reader ) {
        try {
            reader.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public IndexSearcher nrtSearcher() {
        try {
            return new SearcherFactory().newSearcher( nrtReader() );
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
    public void dispose() {
        if ( isDisposed.get() ) {
            return;
        }
        closeWriter();
        directory.close();
        isDisposed.set( true );
    }

    private void closeWriter() {
        try {
            writer.commit();
            writer.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public boolean freshIndex() {
        return freshIndex.get();
    }

    @Override
    public void commit() {
        try {
            writer.commit();
            freshIndex.set( false );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void delete() {
        closeWriter();
        directory.delete();
        isDisposed.set( true );
    }
}

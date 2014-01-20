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

package org.uberfire.metadata.backend.lucene.index.directory;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.lucene40.Lucene40Codec;
import org.apache.lucene.index.IndexWriterConfig;
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.metadata.backend.lucene.index.LuceneIndexFactory;
import org.uberfire.metadata.backend.lucene.model.KClusterImpl;
import org.uberfire.metadata.model.KCluster;

import static org.apache.lucene.util.Version.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

public class DirectoryFactory implements LuceneIndexFactory {

    private static final String REPOSITORIES_ROOT_DIR = ".index";

    private final Map<KCluster, LuceneIndex> clusters = new ConcurrentHashMap<KCluster, LuceneIndex>();
    private final DirectoryType type;
    private final Analyzer analyzer;

    public DirectoryFactory( final DirectoryType type,
                             final Analyzer analyzer ) {
        this.analyzer = analyzer;
        this.type = type;
        final File[] files = defaultHostingDir().listFiles();
        if ( files != null && files.length > 0 ) {
            for ( final File file : files ) {
                if ( file.isDirectory() ) {
                    final KCluster cluster = new KClusterImpl( file.getName() );
                    clusters.put( cluster, type.newIndex( cluster, newConfig( analyzer ) ) );
                }
            }
        }
    }

    private IndexWriterConfig newConfig( final Analyzer analyzer ) {
        final IndexWriterConfig config = new IndexWriterConfig( LUCENE_40, analyzer );
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

        return config;
    }

    @Override
    public LuceneIndex newCluster( final KCluster kcluster ) {
        checkCondition( "Cluster already exists", !clusters.containsKey( checkNotNull( "kcluster", kcluster ) ) );

        final LuceneIndex newIndex = type.newIndex( kcluster, newConfig( analyzer ) );
        clusters.put( kcluster, newIndex );

        return newIndex;
    }

    @Override
    public Map<? extends KCluster, ? extends LuceneIndex> getIndexes() {
        return Collections.unmodifiableMap( clusters );
    }

    @Override
    public synchronized void dispose() {
        for ( final LuceneIndex luceneIndex : clusters.values() ) {
            luceneIndex.dispose();
        }
    }

    public static File defaultHostingDir() {
        final String value = System.getProperty( "org.uberfire.metadata.index.dir" );
        if ( value == null || value.trim().isEmpty() ) {
            return new File( REPOSITORIES_ROOT_DIR );
        } else {
            return new File( value.trim(), REPOSITORIES_ROOT_DIR );
        }
    }
}

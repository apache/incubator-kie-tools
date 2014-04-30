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

package org.uberfire.metadata.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.metadata.engine.Index;

import static org.junit.Assert.*;
import static org.uberfire.metadata.io.KObjectUtil.*;

/**
 *
 */
public class BatchIndexTest {

    private static IOService ioService = null;
    private static LuceneConfig config;

    public static IOService ioService() throws InterruptedException {
        if ( ioService == null ) {
            config = new LuceneConfigBuilder().withInMemoryMetaModelStore().useDirectoryBasedIndex().useInMemoryDirectory().build();
            ioService = new IOServiceDotFileImpl();
        }
        return ioService;
    }

    @BeforeClass
    public static void setup() throws IOException {
        final String path = createTempDirectory().getAbsolutePath();
        System.setProperty( "org.uberfire.nio.git.dir", path );
        System.out.println( ".niogit: " + path );

        final URI newRepo = URI.create( "git://temp-repo-test" );

        try {
            ioService().newFileSystem( newRepo, new HashMap<String, Object>() );
        } catch ( final Exception ex ) {
        }
    }

    @Test
    public void testIndex() throws IOException, InterruptedException {
        {
            final Path file = ioService().get( "git://temp-repo-test/path/to/file.txt" );
            ioService().write( file, "some content here", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.author";
                                   }

                                   @Override
                                   public Object value() {
                                       return "My User Name Here";
                                   }
                               }, new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.lastModification";
                                   }

                                   @Override
                                   public Object value() {
                                       return new Date();
                                   }
                               }, new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.comment";
                                   }

                                   @Override
                                   public Object value() {
                                       return "initial document version, should be revised later.";
                                   }
                               }
                             );
        }
        {
            final Path file = ioService().get( "git://temp-repo-test/path/to/some/complex/file.txt" );
            ioService().write( file, "some other content here", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.author";
                                   }

                                   @Override
                                   public Object value() {
                                       return "My Second User Name";
                                   }
                               }, new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.lastModification";
                                   }

                                   @Override
                                   public Object value() {
                                       return new Date();
                                   }
                               }, new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.comment";
                                   }

                                   @Override
                                   public Object value() {
                                       return "important document, should be used right now.";
                                   }
                               }
                             );
        }
        {
            final Path file = ioService().get( "git://temp-repo-test/simple.doc" );
            ioService().write( file, "some doc content here", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.author";
                                   }

                                   @Override
                                   public Object value() {
                                       return "My Original User";
                                   }
                               }, new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.lastModification";
                                   }

                                   @Override
                                   public Object value() {
                                       return new Date();
                                   }
                               }, new FileAttribute<Object>() {
                                   @Override
                                   public String name() {
                                       return "dcore.comment";
                                   }

                                   @Override
                                   public Object value() {
                                       return "unlock document updated, should be checked by boss.";
                                   }
                               }
                             );
        }

        {
            final Path file = ioService().get( "git://temp-repo-test/xxx/simple.xls" );
            ioService().write( file, "plans!?" );
        }

        new BatchIndex( config.getIndexEngine(),
                        config.getIndexers(),
                        ioService(),
                        DublinCoreView.class ).run( ioService().get( "git://temp-repo-test/" ), new Runnable() {
            @Override
            public void run() {
                try {
                    final Index index = config.getIndexManager().get( toKCluster( ioService().get( "git://temp-repo-test/" ).getFileSystem() ) );

                    final IndexSearcher searcher = ( (LuceneIndex) index ).nrtSearcher();
                    {
                        final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

                        searcher.search( new MatchAllDocsQuery(), collector );

                        final ScoreDoc[] hits = collector.topDocs().scoreDocs;

                        assertEquals( 4, hits.length );
                    }

                    {
                        final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

                        searcher.search( new TermQuery( new Term( "dcore.author", "name" ) ), collector );

                        final ScoreDoc[] hits = collector.topDocs().scoreDocs;

                        assertEquals( 2, hits.length );
                    }

                    {
                        final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

                        searcher.search( new TermQuery( new Term( "dcore.author", "second" ) ), collector );

                        final ScoreDoc[] hits = collector.topDocs().scoreDocs;

                        assertEquals( 1, hits.length );
                    }

                    ( (LuceneIndex) index ).nrtRelease( searcher );

                } catch ( Exception ex ) {
                    ex.printStackTrace();
                    fail();
                }
            }
        } );

    }

    public static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        return temp;
    }

}

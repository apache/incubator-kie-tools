package org.uberfire.metadata.backend.lucene;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;
import org.uberfire.metadata.backend.lucene.FieldFactory;
import org.uberfire.metadata.backend.lucene.LuceneIndexEngine;
import org.uberfire.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.metadata.backend.lucene.setups.BaseLuceneSetup;
import org.uberfire.metadata.engine.MetaIndexEngine;
import org.uberfire.metadata.engine.MetaModelStore;
import org.uberfire.metadata.model.KObject;
import org.uberfire.metadata.model.KObjectKey;
import org.uberfire.metadata.model.KProperty;
import org.uberfire.metadata.model.schema.MetaType;

import static org.junit.Assert.*;

/**
 *
 */
public abstract class BaseIndexEngineSimpleTest {

    @Test
    public void testSimpleIndex() throws IOException {

        final FieldFactory factory = new SimpleFieldFactory();
        final MetaIndexEngine engine = new LuceneIndexEngine( getMetaModelStore(), getLuceneSetup(), factory );

        engine.index( new KObject() {
            @Override
            public String getId() {
                return "unique.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "Path";
                    }
                };
            }

            @Override
            public String getClusterId() {
                return "cluster.id.here";
            }

            @Override
            public String getSegmentId() {
                return "/";
            }

            @Override
            public String getKey() {
                return "some.key.here";
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new HashSet<KProperty<?>>() {{
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.author";
                        }

                        @Override
                        public String getValue() {
                            return "Some Author name here.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<String>() {
                        @Override
                        public String getName() {
                            return "dcore.comment";
                        }

                        @Override
                        public String getValue() {
                            return "My comment file.txt here that has some content that is important to my users.";
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Integer>() {
                        @Override
                        public String getName() {
                            return "dcore.review";
                        }

                        @Override
                        public Integer getValue() {
                            return 10;
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );
                    add( new KProperty<Date>() {
                        @Override
                        public String getName() {
                            return "dcore.lastModifiedTime";
                        }

                        @Override
                        public Date getValue() {
                            return new Date();
                        }

                        @Override
                        public boolean isSearchable() {
                            return true;
                        }
                    } );

                }};
            }
        } );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ) );

        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.author" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.comment" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.review" ) );
        assertNotNull( getMetaModelStore().getMetaObject( "Path" ).getProperty( "dcore.lastModifiedTime" ) );

        final IndexSearcher searcher = getLuceneSetup().nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "dcore.author", "some" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "dcore.comment", "users" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            final NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange( "dcore.review", 1, 15, true, true );

            searcher.search( rangeQuery, collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            final NumericRangeQuery<Long> rangeQuery = NumericRangeQuery.newLongRange( "dcore.lastModifiedTime", 0L, new Date().getTime(), true, true );

            searcher.search( rangeQuery, collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "id", "unique.id.here" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            searcher.search( new TermQuery( new Term( "type", "Path" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        getLuceneSetup().nrtRelease( searcher );

        engine.rename( new KObjectKey() {
                           @Override
                           public String getId() {
                               return "unique.id.here";
                           }

                           @Override
                           public MetaType getType() {
                               return new MetaType() {
                                   @Override
                                   public String getName() {
                                       return "Path";
                                   }
                               };
                           }

                           @Override
                           public String getClusterId() {
                               return "cluster.id.here";
                           }

                           @Override
                           public String getSegmentId() {
                               return "/";
                           }

                           @Override
                           public String getKey() {
                               return "some.key.here";
                           }
                       }, new KObjectKey() {
                           @Override
                           public String getId() {
                               return "other.id.here";
                           }

                           @Override
                           public MetaType getType() {
                               return new MetaType() {
                                   @Override
                                   public String getName() {
                                       return "Path";
                                   }
                               };
                           }

                           @Override
                           public String getClusterId() {
                               return "other.cluster.id.here";
                           }

                           @Override
                           public String getSegmentId() {
                               return "/";
                           }

                           @Override
                           public String getKey() {
                               return "some.key.here";
                           }
                       }
                     );

        final IndexSearcher updatedSearcher = getLuceneSetup().nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            updatedSearcher.search( new MatchAllDocsQuery(), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            updatedSearcher.search( new TermQuery( new Term( "id", "other.id.here" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            updatedSearcher.search( new TermQuery( new Term( "id", "unique.id.here" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 0, hits.length );
        }

        engine.delete( new KObjectKey() {
            @Override
            public String getId() {
                return "other.id.here";
            }

            @Override
            public MetaType getType() {
                return new MetaType() {
                    @Override
                    public String getName() {
                        return "Path";
                    }
                };
            }

            @Override
            public String getClusterId() {
                return "other.cluster.id.here";
            }

            @Override
            public String getSegmentId() {
                return "/";
            }

            @Override
            public String getKey() {
                return "some.key.here";
            }
        } );

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            updatedSearcher.search( new TermQuery( new Term( "id", "other.id.here" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1, hits.length );
        }

        getLuceneSetup().nrtRelease( updatedSearcher );

        final IndexSearcher deletedSearcher = getLuceneSetup().nrtSearcher();

        {
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10, true );

            deletedSearcher.search( new TermQuery( new Term( "id", "other.id.here" ) ), collector );

            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 0, hits.length );
        }

        getLuceneSetup().nrtRelease( deletedSearcher );

    }

    protected abstract BaseLuceneSetup getLuceneSetup();

    protected abstract MetaModelStore getMetaModelStore();

}

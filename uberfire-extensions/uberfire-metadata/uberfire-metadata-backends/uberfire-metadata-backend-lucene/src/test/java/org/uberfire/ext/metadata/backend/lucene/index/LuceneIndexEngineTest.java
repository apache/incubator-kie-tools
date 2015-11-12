package org.uberfire.ext.metadata.backend.lucene.index;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.engine.MetaModelStore;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LuceneIndexEngineTest {

    @Test
    public void testDisposableRegistry() {
        final LuceneConfig config = new LuceneConfig( mock( MetaModelStore.class ),
                                                      mock( FieldFactory.class ),
                                                      mock( LuceneIndexFactory.class ),
                                                      mock( Analyzer.class ) );

        assertTrue( PriorityDisposableRegistry.getDisposables().contains( config ) );
    }

}

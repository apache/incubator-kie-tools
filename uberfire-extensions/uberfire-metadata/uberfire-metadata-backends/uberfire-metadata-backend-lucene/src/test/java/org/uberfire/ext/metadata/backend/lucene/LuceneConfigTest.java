package org.uberfire.ext.metadata.backend.lucene;

import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexEngine;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.engine.MetaModelStore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LuceneConfigTest {

    @Test
    public void testDisposableRegistry() {
        final LuceneIndexEngine indexEngine = new LuceneIndexEngine( mock( FieldFactory.class ),
                                                                     mock( MetaModelStore.class ),
                                                                     mock( LuceneIndexManager.class ) );

        assertTrue( PriorityDisposableRegistry.getDisposables().contains( indexEngine ) );
    }

}

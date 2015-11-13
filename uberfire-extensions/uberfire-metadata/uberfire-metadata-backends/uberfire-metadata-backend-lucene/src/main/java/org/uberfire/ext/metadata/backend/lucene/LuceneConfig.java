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

package org.uberfire.ext.metadata.backend.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexEngine;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.backend.lucene.search.LuceneSearchIndex;
import org.uberfire.ext.metadata.engine.IndexManager;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.search.SearchIndex;

public class LuceneConfig implements MetadataConfig {

    private final MetaModelStore metaModelStore;
    private final FieldFactory fieldFactory;
    private final LuceneIndexManager indexManager;
    private final Analyzer analyzer;
    private final LuceneSearchIndex searchIndex;
    private final LuceneIndexEngine indexEngine;

    public LuceneConfig( final MetaModelStore metaModelStore,
                         final FieldFactory fieldFactory,
                         final LuceneIndexFactory indexFactory,
                         final Analyzer analyzer ) {
        this.metaModelStore = metaModelStore;
        this.fieldFactory = fieldFactory;
        this.indexManager = new LuceneIndexManager( indexFactory );
        this.analyzer = analyzer;
        this.searchIndex = new LuceneSearchIndex( this.indexManager,
                                                  this.analyzer );
        this.indexEngine = new LuceneIndexEngine( this.fieldFactory,
                                                  this.metaModelStore,
                                                  this.indexManager );
        PriorityDisposableRegistry.register( this );
    }

    @Override
    public SearchIndex getSearchIndex() {
        return searchIndex;
    }

    @Override
    public MetaIndexEngine getIndexEngine() {
        return indexEngine;
    }

    @Override
    public IndexManager getIndexManager() {
        return indexManager;
    }

    @Override
    public MetaModelStore getMetaModelStore() {
        return metaModelStore;
    }

    @Override
    public void dispose() {
        indexEngine.dispose();
        indexManager.dispose();
        metaModelStore.dispose();
        analyzer.close();
    }

    @Override
    public int priority() {
        return -20;
    }
}

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata;

import org.apache.lucene.analysis.Analyzer;
import org.uberfire.ext.metadata.backend.elastic.search.ElasticSearchSearchIndex;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.ext.metadata.search.SearchIndex;

import static com.google.common.base.Preconditions.checkNotNull;

public class ElasticSearchConfig implements MetadataConfig {

    private final SearchIndex searchIndex;
    private final MetaIndexEngine metaIndexEngine;
    private final MetaModelStore metaModelStore;
    private final IndexProvider indexProvider;

    public ElasticSearchConfig(MetaIndexEngine metaIndexEngine,
                               MetaModelStore metaModelStore,
                               IndexProvider indexProvider,
                               Analyzer analyzer) {
        this.metaModelStore = checkNotNull(metaModelStore,
                                           "metaModelStore");
        this.indexProvider = checkNotNull(indexProvider,
                                          "indexProvider");
        this.searchIndex = new ElasticSearchSearchIndex(indexProvider,
                                                        analyzer);
        this.metaIndexEngine = metaIndexEngine;
    }

    @Override
    public SearchIndex getSearchIndex() {
        return this.searchIndex;
    }

    @Override
    public MetaIndexEngine getIndexEngine() {
        return this.metaIndexEngine;
    }

    @Override
    public MetaModelStore getMetaModelStore() {
        return this.metaModelStore;
    }

    @Override
    public int priority() {
        return -20;
    }

    @Override
    public void dispose() {
    }

    public IndexProvider getIndexProvider() {
        return indexProvider;
    }
}

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

package org.uberfire.ext.metadata.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.uberfire.ext.metadata.ElasticSearchConfig;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.backend.elastic.index.ElasticSearchIndexProvider;
import org.uberfire.ext.metadata.backend.elastic.provider.ElasticSearchContext;
import org.uberfire.ext.metadata.backend.infinispan.InfinispanSearchConfig;
import org.uberfire.ext.metadata.backend.infinispan.provider.InfinispanContext;
import org.uberfire.ext.metadata.backend.infinispan.provider.InfinispanIndexProvider;
import org.uberfire.ext.metadata.backend.infinispan.provider.MappingProvider;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.ext.metadata.backend.lucene.index.CustomAnalyzerWrapperFactory;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndexManager;
import org.uberfire.ext.metadata.backend.lucene.index.directory.DirectoryFactory;
import org.uberfire.ext.metadata.backend.lucene.index.directory.DirectoryType;
import org.uberfire.ext.metadata.backend.lucene.provider.LuceneIndexProvider;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.event.IndexEvent;
import org.uberfire.ext.metadata.io.analyzer.KiePerFieldAnalyzerWrapper;
import org.uberfire.ext.metadata.io.index.MetadataIndexEngine;
import org.uberfire.ext.metadata.metamodel.InMemoryMetaModelStore;
import org.uberfire.ext.metadata.metamodel.NullMetaModelStore;

public class MetadataConfigBuilder {

    private static final String LUCENE = "lucene";
    private static final String ELASTIC = "elastic";
    private static final String ISPN = "infinispan";

    public static final String ORG_UBERFIRE_EXT_METADATA_INDEX = "org.appformer.ext.metadata.index";
    private static final Consumer<List<IndexEvent>> NOP_OBSERVER = o -> {
    };

    private MetaModelStore metaModelStore;
    private FieldFactory fieldFactory;
    private DirectoryType type;
    private Analyzer analyzer;
    private CustomAnalyzerWrapperFactory customAnalyzerWrapperFactory;
    private Map<String, Analyzer> analyzers;
    private final String metadataIndex;

    public MetadataConfigBuilder() {
        this(System.getProperty(ORG_UBERFIRE_EXT_METADATA_INDEX,
                                LUCENE));
    }

    public MetadataConfigBuilder(String metadataIndex) {
        this.metadataIndex = metadataIndex;
    }

    public MetadataConfigBuilder withInMemoryMetaModelStore() {
        this.metaModelStore = new InMemoryMetaModelStore();
        return this;
    }

    public MetadataConfigBuilder withoutMemoryMetaModel() {
        this.metaModelStore = new NullMetaModelStore();
        return this;
    }

    public MetadataConfigBuilder withDefaultFieldFactory() {
        this.fieldFactory = new SimpleFieldFactory();
        return this;
    }

    public MetadataConfigBuilder usingAnalyzers(final Map<String, Analyzer> analyzers) {
        this.analyzers = analyzers;
        return this;
    }

    public MetadataConfigBuilder usingAnalyzerWrapperFactory(CustomAnalyzerWrapperFactory analyzerWrapper) {
        this.customAnalyzerWrapperFactory = analyzerWrapper;
        return this;
    }

    public MetadataConfigBuilder usingFieldFactory(final FieldFactory fieldFactory) {
        this.fieldFactory = fieldFactory;
        return this;
    }

    public MetadataConfigBuilder useDirectoryBasedIndex() {
        return this;
    }

    public MetadataConfigBuilder useInMemoryDirectory() {
        this.type = DirectoryType.INMEMORY;
        return this;
    }

    public MetadataConfigBuilder useMMapDirectory() {
        this.type = DirectoryType.MMAP;
        return this;
    }

    public MetadataConfigBuilder useNIODirectory() {
        this.type = DirectoryType.NIO;
        return this;
    }

    public MetadataConfig build() {
        if (metaModelStore == null) {
            withoutMemoryMetaModel();
        }
        if (fieldFactory == null) {
            withDefaultFieldFactory();
        }
        if (type == null) {
            withDefaultDirectory();
        }
        if (analyzers == null) {
            withDefaultAnalyzers();
        }
        if (analyzer == null) {
            withDefaultAnalyzer();
        }

        if (this.metadataIndex.toLowerCase().equals(ELASTIC)) {
            ElasticSearchIndexProvider indexProvider = new ElasticSearchIndexProvider(this.metaModelStore,
                                                                                      ElasticSearchContext.getInstance(),
                                                                                      analyzer);
            return new ElasticSearchConfig(new MetadataIndexEngine(indexProvider,
                                                                   metaModelStore),
                                           metaModelStore,
                                           indexProvider,
                                           analyzer);
        } else if (this.metadataIndex.toLowerCase().equals(ISPN)) {
            InfinispanContext context = InfinispanContext.getInstance();
            MappingProvider mappingProvider = new MappingProvider(this.analyzer);
            InfinispanIndexProvider infinispanIndexProvider = new InfinispanIndexProvider(context,
                                                                                          mappingProvider);
            return new InfinispanSearchConfig(new MetadataIndexEngine(infinispanIndexProvider,
                                                                      this.metaModelStore),
                                              infinispanIndexProvider,
                                              this.metaModelStore,
                                              this.analyzer);
        } else {
            DirectoryFactory indexFactory = new DirectoryFactory(type,
                                                                 analyzer);
            LuceneIndexManager indexManager = new LuceneIndexManager(indexFactory);
            LuceneIndexProvider indexProvider = new LuceneIndexProvider(indexManager,
                                                                        fieldFactory);
            return new LuceneConfig(metaModelStore,
                                    fieldFactory,
                                    indexManager,
                                    new MetadataIndexEngine(indexProvider,
                                                            metaModelStore),
                                    analyzer);
        }
    }

    public void withDefaultDirectory() {
        useNIODirectory();
    }

    public void withDefaultAnalyzers() {
        this.analyzers = new HashMap<>();
        analyzers.put(LuceneIndex.CUSTOM_FIELD_FILENAME,
                      new FilenameAnalyzer());
    }

    public void withDefaultAnalyzer() {
        if (this.customAnalyzerWrapperFactory == null) {
            this.analyzer = new KiePerFieldAnalyzerWrapper(new StandardAnalyzer(CharArraySet.EMPTY_SET),
                                                           new HashMap<String, Analyzer>() {{
                                                               putAll(analyzers);
                                                           }});
        } else {
            this.analyzer = this.customAnalyzerWrapperFactory.getAnalyzerWrapper(new StandardAnalyzer(CharArraySet.EMPTY_SET),
                                                                                 analyzers);
        }
    }
}

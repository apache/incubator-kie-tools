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

package org.uberfire.metadata.backend.lucene;

import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.uberfire.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.metadata.backend.lucene.fields.SimpleFieldFactory;
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.metadata.backend.lucene.index.directory.DirectoryFactory;
import org.uberfire.metadata.backend.lucene.index.directory.DirectoryType;
import org.uberfire.metadata.backend.lucene.metamodel.InMemoryMetaModelStore;
import org.uberfire.metadata.backend.lucene.metamodel.NullMetaModelStore;
import org.uberfire.metadata.engine.MetaModelStore;

import static org.apache.lucene.util.Version.*;

public final class LuceneConfigBuilder {

    private MetaModelStore metaModelStore;
    private FieldFactory fieldFactory;
    private DirectoryType type;
    private Analyzer analyzer;

    public LuceneConfigBuilder() {
    }

    public LuceneConfigBuilder withInMemoryMetaModelStore() {
        this.metaModelStore = new InMemoryMetaModelStore();
        return this;
    }

    public LuceneConfigBuilder withoutMemoryMetaModel() {
        this.metaModelStore = new NullMetaModelStore();
        return this;
    }

    public LuceneConfigBuilder withDefaultFieldFactory() {
        this.fieldFactory = new SimpleFieldFactory();
        return this;
    }

    public LuceneConfigBuilder usingFieldFactory( final FieldFactory fieldFactory ) {
        this.fieldFactory = fieldFactory;
        return this;
    }

    public LuceneConfigBuilder useDirectoryBasedIndex() {
        return this;
    }

    public LuceneConfigBuilder useInMemoryDirectory() {
        this.type = DirectoryType.INMEMORY;
        return this;
    }

    public LuceneConfigBuilder useMMapDirectory() {
        this.type = DirectoryType.MMAP;
        return this;
    }

    public LuceneConfigBuilder useNIODirectory() {
        this.type = DirectoryType.NIO;
        return this;
    }

    public LuceneConfig build() {
        if ( metaModelStore == null ) {
            withoutMemoryMetaModel();
        }
        if ( fieldFactory == null ) {
            withDefaultFieldFactory();
        }
        if ( type == null ) {
            withDefaultDirectory();
        }
        if ( analyzer == null ) {
            withDefaultAnalyzer();
        }

        return new LuceneConfig( metaModelStore, fieldFactory, new DirectoryFactory( type, analyzer ), analyzer );
    }

    public void withDefaultDirectory() {
        useNIODirectory();
    }

    public void withDefaultAnalyzer() {
        this.analyzer = new PerFieldAnalyzerWrapper( new StandardAnalyzer( LUCENE_40 ), new HashMap<String, Analyzer>() {{
            put( LuceneIndex.CUSTOM_FIELD_FILENAME, new FilenameAnalyzer( LUCENE_40 ) );
        }} );
    }

}

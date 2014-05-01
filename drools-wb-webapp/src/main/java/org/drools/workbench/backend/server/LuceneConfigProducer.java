/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.backend.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.lucene.analysis.Analyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.uberfire.metadata.backend.lucene.LuceneConfig;
import org.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.metadata.engine.Indexer;

import static org.apache.lucene.util.Version.*;

@ApplicationScoped
public class LuceneConfigProducer {

    @Inject
    @Any
    private Instance<Indexer> indexers;

    private LuceneConfig config;

    @PostConstruct
    public void setup() {
        final Set<Indexer> indexers = getIndexers();
        final Map<String, Analyzer> analyzers = getAnalyzers();
        this.config = new LuceneConfigBuilder().withInMemoryMetaModelStore()
                .usingIndexers( indexers )
                .usingAnalyzers( analyzers )
                .useDirectoryBasedIndex()
                .useNIODirectory()
                .build();
    }

    @Produces
    @Named("luceneConfig")
    public LuceneConfig configProducer() {
        return this.config;
    }

    private Set<Indexer> getIndexers() {
        if ( indexers == null ) {
            return Collections.emptySet();
        }
        final Set<Indexer> result = new HashSet<Indexer>();
        for ( Indexer indexer : indexers ) {
            result.add( indexer );
        }
        return result;
    }

    private Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer( LUCENE_40 ) );
        }};
    }

}

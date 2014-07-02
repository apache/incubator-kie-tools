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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.lucene.analysis.Analyzer;
import org.kie.uberfire.metadata.backend.lucene.LuceneConfig;
import org.kie.uberfire.metadata.backend.lucene.LuceneConfigBuilder;
import org.kie.uberfire.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;

import static org.apache.lucene.util.Version.*;

@ApplicationScoped
public class LuceneConfigProducer {

    private LuceneConfig config;

    @PostConstruct
    public void setup() {
        final Map<String, Analyzer> analyzers = getAnalyzers();
        this.config = new LuceneConfigBuilder().withInMemoryMetaModelStore()
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

    private Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer( LUCENE_40 ) );
            put( ProjectRootPathIndexTerm.TERM,
                 new FilenameAnalyzer( LUCENE_40 ) );
        }};
    }

}

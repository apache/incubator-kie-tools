/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.screens.workbench.backend.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.lucene.analysis.Analyzer;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.FieldTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeInterfaceIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeNameIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeParentIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.FullyQualifiedClassNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.TypeIndexTerm;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;

/**
 * This class contains the default Lucene configuration, and can be
 * replaced by using CDI alternatives.
 */
@ApplicationScoped
public class DefaultLuceneConfigProducer {

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
                 new RuleAttributeNameAnalyzer() );
            put( RuleAttributeIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer() );
            put( RuleAttributeValueIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer() );

            put( ProjectRootPathIndexTerm.TERM,
                 new FilenameAnalyzer() );

            put( PackageNameIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
            put( FieldTypeIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
            put( JavaTypeIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
            put( JavaTypeInterfaceIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
            put( JavaTypeNameIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
            put( JavaTypeParentIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
            put( TypeIndexTerm.TERM,
                 new FullyQualifiedClassNameAnalyzer() );
        }};
    }
}

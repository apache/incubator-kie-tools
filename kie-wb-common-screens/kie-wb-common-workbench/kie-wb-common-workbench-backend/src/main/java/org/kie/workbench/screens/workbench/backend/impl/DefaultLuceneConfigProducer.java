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
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryFileNameIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.LibraryRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.ImpactAnalysisAnalyzerWrapperFactory;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.LowerCaseOnlyAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;

/**
 * This class contains the default Lucene configuration, and can be
 * replaced by using CDI alternatives.
 */
@ApplicationScoped
public class DefaultLuceneConfigProducer {

    private MetadataConfig config;

    @PostConstruct
    public void setup() {
        final Map<String, Analyzer> analyzers = getAnalyzers();
        this.config = new MetadataConfigBuilder().withInMemoryMetaModelStore()
                .usingAnalyzers(analyzers)
                .usingAnalyzerWrapperFactory(ImpactAnalysisAnalyzerWrapperFactory.getInstance())
                .useDirectoryBasedIndex()
                .useNIODirectory()
                .build();
    }

    @Produces
    @Named("luceneConfig")
    public MetadataConfig configProducer() {
        return this.config;
    }

    Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put(LibraryFileNameIndexTerm.TERM,
                new FilenameAnalyzer());
            put(LibraryRepositoryRootIndexTerm.TERM,
                new FilenameAnalyzer());
            put(ModuleRootPathIndexTerm.TERM,
                new FilenameAnalyzer());
            put(PackageNameIndexTerm.TERM,
                new LowerCaseOnlyAnalyzer());
            put(LuceneIndex.CUSTOM_FIELD_FILENAME,
                new FilenameAnalyzer());

            // all of the (resource, part, shared, etc) references and resource or part terms
            // are taken care of via the ImpactAnalysisAnalyzerWrapper
        }};
    }
}

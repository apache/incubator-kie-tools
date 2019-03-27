/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.indexing;

import java.util.Collections;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DelegatingAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.SharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.analyzer.ElasticSearchAnalyzer;
import org.uberfire.ext.metadata.analyzer.ElasticSearchAnalyzerWrapper;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;

/**
 * This analyzer is based on the {@link PerFieldAnalyzerWrapper} class, which
 * was build for cases when fields require different analysis techniques.
 * <p>
 * <p>A {@link ImpactAnalysisAnalyzerWrapper} can be used like any other analyzer, for both indexing
 * and query parsing.
 */
public final class ImpactAnalysisAnalyzerWrapper extends DelegatingAnalyzerWrapper implements ElasticSearchAnalyzerWrapper {

    private final LowerCaseOnlyAnalyzer lowerCaseOnlyAnalyzer = new LowerCaseOnlyAnalyzer();

    private final Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> fieldAnalyzers;

    /**
     * Constructs with default analyzer and a map of analyzers to use for
     * specific fields.
     * <p>
     * Any fields not specifically defined to use a different analyzer will use the {@link StandardAnalyzer}.
     */
    public ImpactAnalysisAnalyzerWrapper() {
        this(new StandardAnalyzer(CharArraySet.EMPTY_SET),
             Collections.EMPTY_MAP);
    }

    /**
     * Constructs with default analyzer and a map of analyzers to use for
     * specific fields.
     * @param defaultAnalyzer Any fields not specifically
     * defined to use a different analyzer will use the one provided here.
     * @param fieldAnalyzers a Map (String field name to the Analyzer) to be
     * used for those fields
     */
    public ImpactAnalysisAnalyzerWrapper(Analyzer defaultAnalyzer,
                                         Map<String, Analyzer> fieldAnalyzers) {
        super(PER_FIELD_REUSE_STRATEGY);
        this.defaultAnalyzer = defaultAnalyzer;
        this.fieldAnalyzers = (fieldAnalyzers != null) ? fieldAnalyzers : Collections.<String, Analyzer>emptyMap();
    }

    private static final String RESOURCE_REF_FIELD_NAME_BEGIN = ReferenceIndexTerm.TERM + ":";
    private static final String SHARED_PART_REF_FIELD_NAME_BEGIN = SharedPartIndexTerm.TERM + ":";
    private static final String PACKAGE_NAME_FIELD_NAME = PackageNameIndexTerm.TERM;
    private static final String PROJECT_NAME_FIELD_NAME = ModuleNameIndexTerm.TERM;
    private static final String PROJECT_ROOT_PATH_FIELD_NAME = ModuleRootPathIndexTerm.TERM;
    private static final String[] PART_FIELD_NAME_BEGINS;

    static {
        PartType[] partTypes = PartType.values();
        PART_FIELD_NAME_BEGINS = new String[partTypes.length];
        for (int i = 0; i < partTypes.length; ++i) {
            PART_FIELD_NAME_BEGINS[i] = partTypes[i].toString() + ":";
        }
    }

    private static final String[] RESOURCE_FIELD_NAME_BEGINS;

    static {
        ResourceType[] resTypes = ResourceType.values();
        RESOURCE_FIELD_NAME_BEGINS = new String[resTypes.length];
        for (int i = 0; i < resTypes.length; ++i) {
            RESOURCE_FIELD_NAME_BEGINS[i] = resTypes[i].toString() + ":";
        }
    }

    @Override
    protected Analyzer getWrappedAnalyzer(String fieldName) {
        Analyzer analyzer = fieldAnalyzers.get(fieldName);

        if (analyzer == null) {
            // referenced resources and referenced parts
            if (fieldName.startsWith(RESOURCE_REF_FIELD_NAME_BEGIN)) {
                analyzer = lowerCaseOnlyAnalyzer;
                // shared parts
            } else if (fieldName.startsWith(SHARED_PART_REF_FIELD_NAME_BEGIN)) {
                analyzer = lowerCaseOnlyAnalyzer;
                // package name
            } else if (fieldName.startsWith(PACKAGE_NAME_FIELD_NAME)) {
                analyzer = lowerCaseOnlyAnalyzer;
                // project name
            } else if (fieldName.startsWith(PROJECT_NAME_FIELD_NAME)) {
                analyzer = lowerCaseOnlyAnalyzer;
                // project root path URI
            } else if (fieldName.startsWith(PROJECT_ROOT_PATH_FIELD_NAME)) {
                analyzer = lowerCaseOnlyAnalyzer;
                // resources and parts
            } else {
                boolean found = false;
                for (String typeFieldNameStart : RESOURCE_FIELD_NAME_BEGINS) {
                    if (fieldName.startsWith(typeFieldNameStart)) {
                        analyzer = lowerCaseOnlyAnalyzer;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (String typeFieldNameStart : PART_FIELD_NAME_BEGINS) {
                        if (fieldName.startsWith(typeFieldNameStart)) {
                            analyzer = lowerCaseOnlyAnalyzer;
                            break;
                        }
                    }
                }
            }
        }

        return (analyzer != null) ? analyzer : defaultAnalyzer;
    }

    @Override
    public String getFieldAnalyzer(String fieldName) {
        Analyzer analyzer = this.getWrappedAnalyzer(fieldName);
        Class<?> analyzerClass = analyzer.getClass();
        if (analyzerClass.equals(LowerCaseOnlyAnalyzer.class) ||
                analyzerClass.equals(FilenameAnalyzer.class)) {
            return ElasticSearchAnalyzer.FILENAME.toString();
        } else {
            return ElasticSearchAnalyzer.STANDARD.toString();
        }
    }

    @Override
    public String toString() {
        return "ImpactAnalysisAnalyzerWrapper(" + fieldAnalyzers + ", default=" + defaultAnalyzer + ")";
    }
}

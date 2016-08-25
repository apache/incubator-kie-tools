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

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.uberfire.ext.metadata.backend.lucene.index.CustomAnalyzerWrapperFactory;


/**
 * Factory class for the {@link ImpactAnalysisAnalyzerWrapper}. This construct is necessary in order to be able to use the
 * {@link ImpactAnalysisAnalyzerWrapper} class with the uberfire code, in particular the Lucene config builder class(es).
 */
public class ImpactAnalysisAnalyzerWrapperFactory implements CustomAnalyzerWrapperFactory {

    private static final ImpactAnalysisAnalyzerWrapperFactory _instance = new ImpactAnalysisAnalyzerWrapperFactory();

    private ImpactAnalysisAnalyzerWrapperFactory() {
        // private
    }

    public static ImpactAnalysisAnalyzerWrapperFactory getInstance() {
        return _instance;
    }

    /* (non-Javadoc)
     * @see org.uberfire.ext.metadata.backend.lucene.index.CustomAnalyzerWrapper#getAnalyzer()
     */
    public Analyzer getAnalyzerWrapper(Analyzer defaultAnalyzer, Map<String, Analyzer> fieldAnalyzers ) {
        return new ImpactAnalysisAnalyzerWrapper(defaultAnalyzer, fieldAnalyzers);
    }

}

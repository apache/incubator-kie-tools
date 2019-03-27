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

package org.uberfire.ext.metadata.io.analyzer;

import java.util.Collections;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DelegatingAnalyzerWrapper;
import org.uberfire.ext.metadata.analyzer.ElasticSearchAnalyzer;
import org.uberfire.ext.metadata.analyzer.ElasticSearchAnalyzerWrapper;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;

public class KiePerFieldAnalyzerWrapper extends DelegatingAnalyzerWrapper implements ElasticSearchAnalyzerWrapper {

    private final Analyzer defaultAnalyzer;
    private final Map<String, Analyzer> fieldAnalyzers;

    public KiePerFieldAnalyzerWrapper(Analyzer defaultAnalyzer) {
        this(defaultAnalyzer,
             (Map) null);
    }

    public KiePerFieldAnalyzerWrapper(Analyzer defaultAnalyzer,
                                      Map<String, Analyzer> fieldAnalyzers) {
        super(PER_FIELD_REUSE_STRATEGY);
        this.defaultAnalyzer = defaultAnalyzer;
        this.fieldAnalyzers = fieldAnalyzers != null ? fieldAnalyzers : Collections.emptyMap();
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        Analyzer analyzer = this.fieldAnalyzers.get(fieldName);
        return analyzer != null ? analyzer : this.defaultAnalyzer;
    }

    public String toString() {
        return "KiePerFieldAnalyzerWrapper(" + this.fieldAnalyzers + ", default=" + this.defaultAnalyzer + ")";
    }

    @Override
    public String getFieldAnalyzer(String fieldName) {
        Analyzer analyzer = this.getWrappedAnalyzer(fieldName);
        Class<?> analyzerClass = analyzer.getClass();
        if (analyzerClass.equals(FilenameAnalyzer.class)) {
            return ElasticSearchAnalyzer.FILENAME.toString();
        } else {
            return ElasticSearchAnalyzer.STANDARD.toString();
        }
    }
}

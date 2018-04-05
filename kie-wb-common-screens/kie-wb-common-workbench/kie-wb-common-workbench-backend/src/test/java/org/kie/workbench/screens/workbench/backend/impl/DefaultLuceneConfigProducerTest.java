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

package org.kie.workbench.screens.workbench.backend.impl;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.library.api.index.LibraryFileNameIndexTerm;
import org.kie.workbench.common.screens.library.api.index.LibraryRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.LowerCaseOnlyAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefaultLuceneConfigProducerTest {

    private DefaultLuceneConfigProducer producer;

    @Before
    public void setup() {
        this.producer = new DefaultLuceneConfigProducer();
    }

    @Test
    public void checkDefaultAnalyzers() {
        final Map<String, Analyzer> analyzers = producer.getAnalyzers();

        assertEquals(5,
                     analyzers.size());
        assertTrue(analyzers.get(LibraryFileNameIndexTerm.TERM) instanceof FilenameAnalyzer);
        assertTrue(analyzers.get(LibraryRepositoryRootIndexTerm.TERM) instanceof FilenameAnalyzer);
        assertTrue(analyzers.get(ModuleRootPathIndexTerm.TERM) instanceof FilenameAnalyzer);
        assertTrue(analyzers.get(PackageNameIndexTerm.TERM) instanceof LowerCaseOnlyAnalyzer);
        assertTrue(analyzers.get(LuceneIndex.CUSTOM_FIELD_FILENAME) instanceof FilenameAnalyzer);
    }
}

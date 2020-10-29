/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.drltext.backend.server.indexing;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.drools.workbench.screens.drltext.type.DRLResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IndexRuleInvalidDrlTest extends BaseIndexingTest<DRLResourceTypeDefinition> {

    @Test
    public void testIndexRuleInvalidDrl() throws IOException, InterruptedException {
        //Setup logging
        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        final Appender<ILoggingEvent> mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");
        root.addAppender(mockAppender);

        //Add test files
        final Path path = basePath.resolve("bz1269366.drl");
        final String drl = loadText("bz1269366.drl");
        ioService().write(path,
                          drl);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath).getClusterId());

        {
            final Query query = new SingleTermQueryBuilder(new ValueReferenceIndexTerm("org.drools.workbench.screens.drltext.backend.server.indexing.classes.Applicant", ResourceType.JAVA))
                    .build();

            // should be 1, but parsing fails (see verify line below) so that 0 is returned..
            searchFor(index, query, 0);

            verify(mockAppender).doAppend(argThat(argument -> argument.getMessage().startsWith("Unable to parse DRL")));
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {
            {
                put(ModuleRootPathIndexTerm.TERM, new FilenameAnalyzer());
            }
        };
    }

    @Override
    protected DRLResourceTypeDefinition getResourceTypeDefinition() {
        return new DRLResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }
}

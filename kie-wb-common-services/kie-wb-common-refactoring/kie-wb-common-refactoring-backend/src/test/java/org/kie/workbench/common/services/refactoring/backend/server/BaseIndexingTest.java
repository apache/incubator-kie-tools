/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.refactoring.backend.server;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.enterprise.inject.Instance;

import org.apache.lucene.analysis.Analyzer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQueries;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringRuleNamePageRow.RuleName;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseIndexingTest<T extends ResourceTypeDefinition> extends IndexingTest<T> {

    protected int seed = new Random(10L).nextInt();

    protected boolean created = false;
    protected Path basePath;

    protected RefactoringQueryServiceImpl service;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void setup() throws IOException {
        if (!created) {
            final String repositoryName = getRepositoryName();
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty("org.uberfire.nio.git.dir",
                               path);
            System.setProperty("org.uberfire.nio.git.daemon.enabled",
                               "false");
            System.setProperty("org.uberfire.nio.git.ssh.enabled",
                               "false");
            System.setProperty("org.uberfire.sys.repo.monitor.disabled",
                               "true");
            logger.debug(".niogit: " + path);

            final URI newRepo = URI.create("git://" + repositoryName);

            try {
                IOService ioService = ioService();
                ioService.newFileSystem(newRepo,
                                        new HashMap<String, Object>());

                // Don't ask, but we need to write a single file first in order for indexing to work
                basePath = getDirectoryPath().resolveSibling("someNewOtherPath");
                ioService().write(basePath.resolve("dummy"),
                                  "<none>");
            } catch (final Exception e) {
                e.printStackTrace();
                logger.warn("Failed to initialize IOService instance: " + e.getClass().getSimpleName() + ": " + e.getMessage(),
                            e);
            } finally {
                created = true;
            }

            final Instance<NamedQuery> namedQueriesProducer = mock(Instance.class);
            when(namedQueriesProducer.iterator()).thenReturn(getQueries().iterator());

            service = new RefactoringQueryServiceImpl(getConfig(),
                                                      new NamedQueries(namedQueriesProducer));
            service.init();
        }
    }

    protected Set<NamedQuery> getQueries() {
        // overrride me if using the RefactoringQueryServiceImpl!
        return Collections.emptySet();
    }

    @After
    public void dispose() {
        super.dispose();
        created = false;
        System.clearProperty("org.uberfire.nio.git.ssh.enabled");
        System.clearProperty("org.uberfire.nio.git.daemon.enabled");
    }

    protected abstract String getRepositoryName();

    protected Path getDirectoryPath() {
        final String repositoryName = getRepositoryName();
        final Path dir = ioService().get(URI.create("git://" + repositoryName + "/_someDir" + seed));
        ioService().deleteIfExists(dir);
        return dir;
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put(ModuleRootPathIndexTerm.TERM,
                new FilenameAnalyzer());
        }};
    }

    protected void assertResponseContains(final List<RefactoringPageRow> rows,
                                          final Path path) {
        for (RefactoringPageRow row : rows) {
            final String rowFileName = ((org.uberfire.backend.vfs.Path) row.getValue()).getFileName();
            final String fileName = path.getFileName().toString();
            if (rowFileName.endsWith(fileName)) {
                return;
            }
        }
        fail("Response does not contain expected Path '" + path.toUri().toString() + "'.");
    }

    protected void assertResponseContains(final List<RefactoringPageRow> rows,
                                          final String ruleName) {
        for (RefactoringPageRow row : rows) {
            final String rowRuleName = ((String) row.getValue());
            if (rowRuleName.equals(ruleName)) {
                return;
            }
        }
        fail("Response does not contain expected Rule Name '" + ruleName + "'.");
    }

    protected void assertResponseContains(final List<RefactoringPageRow> rows,
                                          final String simpleRuleName,
                                          final String packageName) {
        for (RefactoringPageRow row : rows) {
            final RuleName r = (RuleName) row.getValue();
            final String rowRuleName = r.getSimpleRuleName();
            final String rowPackageName = r.getPackageName();

            if (rowRuleName.equals(simpleRuleName) && rowPackageName.equals(packageName)) {
                return;
            }
        }
        fail("Response does not contain expected Rule Name '" + simpleRuleName + "' in package '" + packageName + "'.");
    }

    protected void addTestFile(final String projectName,
                               final String pathToFile) throws IOException {
        final Path path = basePath.resolve(projectName + "/" + pathToFile);
        final String text = loadText(pathToFile);
        ioService().write(path,
                          text);
    }
}

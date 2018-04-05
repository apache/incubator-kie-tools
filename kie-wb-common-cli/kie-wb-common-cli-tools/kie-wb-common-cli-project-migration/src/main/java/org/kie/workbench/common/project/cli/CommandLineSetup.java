/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.project.cli;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.lucene.analysis.Analyzer;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.commons.util.RawMVELEvaluator;
import org.kie.workbench.common.screens.library.api.index.LibraryFileNameIndexTerm;
import org.kie.workbench.common.screens.library.api.index.LibraryRepositoryRootIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.ImpactAnalysisAnalyzerWrapperFactory;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.LowerCaseOnlyAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.uberfire.backend.server.IOWatchServiceAllImpl;
import org.uberfire.ext.metadata.MetadataConfig;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceNio2WrapperImpl;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;
import org.uberfire.server.cdi.UberFireGeneralFactory;

@ApplicationScoped
@Specializes
public class CommandLineSetup extends UberFireGeneralFactory {

    @Produces
    @Singleton
    public User user() {
        return CommandLineAuthService.PLACEHOLDER;
    }

    @Produces
    @Singleton
    public MVELEvaluator mvelEvaluator() {
        return new RawMVELEvaluator();
    }

    @Produces
    @Singleton
    @Named("luceneConfig")
    public MetadataConfig luceneConfig() {
        return new MetadataConfigBuilder().withInMemoryMetaModelStore()
                                          .usingAnalyzers(getAnalyzers())
                                          .usingAnalyzerWrapperFactory(ImpactAnalysisAnalyzerWrapperFactory.getInstance())
                                          .useDirectoryBasedIndex()
                                          .useNIODirectory()
                                          .build();
    }

    @Produces
    @Named("ioStrategy")
    @Singleton
    public IOService ioService(IOWatchServiceAllImpl watchService) {
        return new IOServiceNio2WrapperImpl("1", watchService);
    }

    /*
     * This method must be specialized because the super-type producer is @RequestScoped, which cannot work outside a web server.
     */
    @Override
    @Produces
    @Default
    @ApplicationScoped
    public SessionInfo getSessionInfo(AuthenticationService authenticationService) {
        return new SessionInfoImpl("dummy-id", authenticationService.getUser());
    }

    @SuppressWarnings("serial")
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

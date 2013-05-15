/*
 * Copyright 2012 JBoss Inc
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

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.kie.commons.io.IOSearchService;
import org.kie.commons.io.IOService;
import org.kie.commons.io.attribute.DublinCoreView;
import org.kie.commons.java.nio.base.version.VersionAttributeView;
import org.kie.workbench.common.services.backend.metadata.attribute.OtherMetaView;
import org.kie.kieora.backend.lucene.LuceneIndexEngine;
import org.kie.kieora.backend.lucene.LuceneSearchIndex;
import org.kie.kieora.backend.lucene.LuceneSetup;
import org.kie.kieora.backend.lucene.fields.SimpleFieldFactory;
import org.kie.kieora.backend.lucene.metamodels.InMemoryMetaModelStore;
import org.kie.kieora.backend.lucene.setups.NIOLuceneSetup;
import org.kie.kieora.engine.MetaIndexEngine;
import org.kie.kieora.engine.MetaModelStore;
import org.kie.kieora.io.IOSearchIndex;
import org.kie.kieora.io.IOServiceIndexedImpl;
import org.kie.kieora.search.SearchIndex;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.repositories.DefaultSystemRepository;

@ApplicationScoped
public class ApplicationScopedProducer {

    private final IOService ioService;
    private final IOSearchService ioSearchService;

    private final LuceneSetup luceneSetup = new NIOLuceneSetup();
    private final DefaultSystemRepository systemRepository = new DefaultSystemRepository();

    public ApplicationScopedProducer() {
        final MetaModelStore metaModelStore = new InMemoryMetaModelStore();
        final MetaIndexEngine indexEngine = new LuceneIndexEngine( metaModelStore,
                                                                   luceneSetup,
                                                                   new SimpleFieldFactory() );
        final SearchIndex searchIndex = new LuceneSearchIndex( luceneSetup );
        this.ioService = new IOServiceIndexedImpl( indexEngine,
                                                   DublinCoreView.class,
                                                   VersionAttributeView.class,
                                                   OtherMetaView.class );
        this.ioSearchService = new IOSearchIndex( searchIndex,
                                                  this.ioService );
    }

    @PreDestroy
    private void cleanup() {
        luceneSetup.dispose();
    }

    @Produces
    @Named("ioStrategy")
    public IOService ioService() {
        return ioService;
    }

    @Produces
    @Named("system")
    public Repository systemRepository() {
        return systemRepository;
    }

    @Produces
    @Named("ioSearchStrategy")
    public IOSearchService ioSearchService() {
        return ioSearchService;
    }

}

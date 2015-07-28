/*
 * Copyright 2014 JBoss Inc
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
package org.kie.workbench.common.backend.server.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;

@Startup(value = StartupType.BOOTSTRAP, priority = -1)
@ApplicationScoped
public class IndexersBootstrap {

    @Inject
    @Any
    private Instance<Indexer> indexers;

    @PostConstruct
    public void setup() {
        for ( Indexer indexer : getIndexers() ) {
            IndexersFactory.addIndexer( indexer );
        }
    }

    private Set<Indexer> getIndexers() {
        if ( indexers == null ) {
            return Collections.emptySet();
        }
        final Set<Indexer> result = new HashSet<Indexer>();
        for ( Indexer indexer : indexers ) {
            result.add( indexer );
        }
        return result;
    }

}

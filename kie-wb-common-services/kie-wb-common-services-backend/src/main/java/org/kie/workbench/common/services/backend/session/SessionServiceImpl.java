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
package org.kie.workbench.common.services.backend.session;

import javax.inject.Inject;

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.guvnor.common.services.builder.Builder;
import org.guvnor.common.services.builder.LRUBuilderCache;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class SessionServiceImpl
        implements SessionService {

    private LRUBuilderCache cache;

    public SessionServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public SessionServiceImpl( final LRUBuilderCache cache ) {
        this.cache = cache;
    }

    @Override
    public KieSession newKieSession( final Project project ) {

        final Builder builder = cache.assertBuilder( project );

        KieContainer kieContainer = null;

        try {
            kieContainer = builder.getKieContainer();

            //If a KieContainer could not be built there is a build error somewhere; so return null to be handled elsewhere
            if ( kieContainer == null ) {
                return null;
            }

            //We always need a pseudo clock
            final SessionConfiguration conf = new SessionConfiguration();
            conf.setClockType( ClockType.PSEUDO_CLOCK );
            return kieContainer.newKieSession( conf );

        } catch ( RuntimeException e ) {
            throw new GenericPortableException( e.getMessage() );
        }

    }

}

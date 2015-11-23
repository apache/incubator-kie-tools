/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@ApplicationScoped
public class DomainHandlerRegistry {

    private List<DomainHandler> domainHandlers = new ArrayList<DomainHandler>(  );

    private SyncBeanManager iocBeanManager;

    @Inject
    public DomainHandlerRegistry( SyncBeanManager iocBeanManager ) {
        this.iocBeanManager = iocBeanManager;
    }

    @PostConstruct
    private void init() {

        final Collection<SyncBeanDef<DomainHandler>> handlerBeans = iocBeanManager.lookupBeans( DomainHandler.class );
        if ( handlerBeans != null && handlerBeans.size() > 0 ) {
            for ( SyncBeanDef<DomainHandler> beanDef : handlerBeans ) {
                domainHandlers.add( beanDef.getInstance() );
            }
        }
        Collections.sort( domainHandlers, new Comparator<DomainHandler>() {
            @Override public int compare( DomainHandler handler1, DomainHandler handler2 ) {
                Integer key1 = handler1.getPriority();
                Integer key2 = handler2.getPriority();
                return key1.compareTo( key2 );
            }
        } );

    }

    public List<DomainHandler> getDomainHandlers( String excludeDomain ) {
        List<DomainHandler> result = new ArrayList<DomainHandler>(  );
        for ( DomainHandler handler : domainHandlers ) {
            if ( !excludeDomain.equals( handler.getName() ) ) {
                result.add( handler );
            }
        }
        return result;
    }

    public List<DomainHandler> getDomainHandlers( List<String> includedDomains ) {
        List<DomainHandler> result = new ArrayList<DomainHandler>(  );
        for ( DomainHandler handler : domainHandlers ) {
            if ( includedDomains.contains( handler.getName() ) ) {
                result.add( handler );
            }
        }
        return result;
    }

    public List<DomainHandler> getDomainHandlers() {
        return Collections.unmodifiableList( domainHandlers );
    }

}

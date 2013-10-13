/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.uberfire.commons.services.cdi;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

public class StartupBeanExtension implements Extension {

    private final List<OrderedBean> startupEagerBeans = new LinkedList<OrderedBean>();
    private final List<OrderedBean> startupBootstrapBeans = new LinkedList<OrderedBean>();

    private final Comparator<OrderedBean> priorityComparator = new Comparator<OrderedBean>() {
        @Override
        public int compare( final OrderedBean o1,
                            final OrderedBean o2 ) {
            return o1.priority - o2.priority;
        }
    };

    public <X> void processBean( @Observes final ProcessBean<X> event ) {
        if ( event.getAnnotated().isAnnotationPresent( Startup.class ) && (event.getAnnotated().isAnnotationPresent( ApplicationScoped.class ) 
		|| event.getAnnotated().isAnnotationPresent( Singleton.class))) {
            final Startup startupAnnotation = event.getAnnotated().getAnnotation( Startup.class );
            final StartupType type = startupAnnotation.value();
            final int priority = startupAnnotation.priority();
            final Bean<?> bean = event.getBean();
            switch ( type ) {
                case EAGER:
                    startupEagerBeans.add( new OrderedBean( bean,
                                                            priority ) );
                    break;
                case BOOTSTRAP:
                    startupBootstrapBeans.add( new OrderedBean( bean,
                                                                priority ) );
                    break;
            }
        }
    }

    public void afterDeploymentValidation( final @Observes AfterDeploymentValidation event,
                                           final BeanManager manager ) {
        //Force execution of Bootstrap bean's @PostConstruct methods first
        runPostConstruct( manager,
                          startupBootstrapBeans );

        //Followed by execution of remaining Eager bean's @PostConstruct methods
        runPostConstruct( manager,
                          startupEagerBeans );
    }

    private void runPostConstruct( final BeanManager manager,
                                   final List<OrderedBean> orderedBeans ) {
        //Sort first, by priority
        Collections.sort( orderedBeans,
                          priorityComparator );
        for ( OrderedBean ob : orderedBeans ) {
            // the call to toString() is a cheat to force the bean to be initialized
            final Bean<?> bean = ob.bean;
            manager.getReference( bean,
                                  bean.getBeanClass(),
                                  manager.createCreationalContext( bean ) ).toString();
        }
    }

    private class OrderedBean {

        Bean<?> bean;
        int priority;

        private OrderedBean( final Bean<?> bean,
                             final int priority ) {
            this.bean = bean;
            this.priority = priority;
        }
    }

}

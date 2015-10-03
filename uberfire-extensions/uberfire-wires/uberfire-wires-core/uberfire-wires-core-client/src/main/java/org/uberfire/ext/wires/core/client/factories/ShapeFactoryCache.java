/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.client.factories;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;

/**
 * A cache of Factories
 */
@ApplicationScoped
public class ShapeFactoryCache {

    @Inject
    private SyncBeanManager iocManager;

    private Set<ShapeFactory> factories = new HashSet<ShapeFactory>();

    @PostConstruct
    private void setup() {
        this.factories = getAvailableFactories();
    }

    public Set<ShapeFactory> getShapeFactories() {
        return Collections.unmodifiableSet( factories );
    }

    public void addShapeFactory( final ShapeFactory factory ) {
        factories.add( PortablePreconditions.checkNotNull( "factory",
                                                           factory ) );
    }

    private Set<ShapeFactory> getAvailableFactories() {
        final Set<ShapeFactory> factories = new HashSet<ShapeFactory>();
        final Collection<SyncBeanDef<ShapeFactory>> factoryBeans = iocManager.lookupBeans( ShapeFactory.class );
        for ( SyncBeanDef<ShapeFactory> factoryBean : factoryBeans ) {
            factories.add( factoryBean.getInstance() );
        }
        return factories;
    }

}

/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;

@ApplicationScoped
public class IOCUtil {

    private final SyncBeanManager beanManager;
    private final Map<Object, Collection<Object>> objectMap = new HashMap<Object, Collection<Object>>();

    @Inject
    public IOCUtil( final SyncBeanManager beanManager ) {
        this.beanManager = beanManager;
    }

    public <T> T newInstance( final Object context,
                              final Class<T> clazz ) {
        final T object = beanManager.lookupBean( clazz ).newInstance();
        if ( !objectMap.containsKey( context ) ) {
            objectMap.put( context, new ArrayList<Object>() );
        }
        objectMap.get( context ).add( object );
        return object;
    }

    public void cleanup( final Object context ) {
        final Collection<Object> objects = objectMap.get( context );
        if ( objects != null ) {
            for ( Object object : objects ) {
                beanManager.destroyBean( object );
            }
        }
    }
}

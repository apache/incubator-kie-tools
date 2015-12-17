/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.social.hp.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Utility to get file type icon
 */
@Dependent
public class IconLocator {

    private List<ClientResourceType> resourceTypes = new ArrayList<ClientResourceType>();

    @Inject
    private SyncBeanManager iocManager;

    @PostConstruct
    public void init() {
        //@Any doesn't work client side, so lookup instances using Errai's BeanManager
        final Collection<IOCBeanDef<ClientResourceType>> availableResourceTypes = iocManager.lookupBeans( ClientResourceType.class );
        for ( final IOCBeanDef<ClientResourceType> resourceTypeBean : availableResourceTypes ) {
            final ClientResourceType resourceType = resourceTypeBean.getInstance();
            resourceTypes.add( resourceType );
        }

        //Sort ResourceTypes so those with highest priority match first
        Collections.sort( resourceTypes,
                          new Comparator<ClientResourceType>() {

                              @Override
                              public int compare( final ClientResourceType o1,
                                                  final ClientResourceType o2 ) {
                                  int priority1 = o1.getPriority();
                                  int priority2 = o2.getPriority();
                                  if ( priority1 == priority2 ) {
                                      return 0;
                                  }
                                  if ( priority1 < priority2 ) {
                                      return 1;
                                  }
                                  return -1;
                              }
                          } );
    }


    public List<ClientResourceType> getResourceTypes() {
        return resourceTypes;
    }

}

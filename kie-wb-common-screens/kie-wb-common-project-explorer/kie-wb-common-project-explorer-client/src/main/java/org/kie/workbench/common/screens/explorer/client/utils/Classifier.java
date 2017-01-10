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
package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Utility to group Items
 */
@ApplicationScoped
public class Classifier {

    private List<ClientResourceType> resourceTypes = new ArrayList<ClientResourceType>();

    @Inject
    private SyncBeanManager iocManager;

    @PostConstruct
    public void init() {
        //@Any doesn't work client side, so lookup instances using Errai's BeanManager
        final Collection<SyncBeanDef<ClientResourceType>> availableResourceTypes = iocManager.lookupBeans( ClientResourceType.class );
        for ( final SyncBeanDef<ClientResourceType> resourceTypeBean : availableResourceTypes ) {
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

    public Map<ClientResourceType, Collection<FolderItem>> group( final Collection<FolderItem> folderItems ) {
        final Map<ClientResourceType, Collection<FolderItem>> groups = new HashMap<ClientResourceType, Collection<FolderItem>>();
        for ( FolderItem folderItem : folderItems ) {
            final ClientResourceType resourceType = ( findResourceType( folderItem ) );
            Collection<FolderItem> itemsForGroup = groups.get( resourceType );
            if ( itemsForGroup == null ) {
                itemsForGroup = new ArrayList<FolderItem>();
                groups.put( resourceType,
                            itemsForGroup );
            }
            itemsForGroup.add( folderItem );
        }
        return groups;
    }

    public ClientResourceType findResourceType( final FolderItem folderItem ) {
        if ( folderItem.getItem() instanceof Path ) {
            return findResourceType( (Path) folderItem.getItem() );
        }
        throw new IllegalArgumentException( "Invalid FolderItem type." );
    }

    public ClientResourceType findResourceType( final Path path ) {
        for ( ClientResourceType resourceType : resourceTypes ) {
            if ( resourceType.accept( path ) ) {
                return resourceType;
            }
        }
        throw new IllegalArgumentException( "Unable to find ResourceType for " + path.toURI() + ". Is AnyResourceType on the classpath?" );
    }
}

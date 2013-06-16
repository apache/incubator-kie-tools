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

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.uberfire.client.workbench.type.ClientResourceType;

/**
 * Utility to group Items
 */
@ApplicationScoped
public class Classifier {

    private static final String MISCELLANEOUS = "Miscellaneous";

    private List<ClientResourceType> resourceTypes = new ArrayList<ClientResourceType>();

    @Inject
    private IOCBeanManager iocManager;

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
                                  if ( priority1 > priority2 ) {
                                      return 1;
                                  }
                                  return -1;
                              }
                          } );
    }

    public Map<String, Collection<Item>> group( final Collection<Item> items ) {
        final Map<String, Collection<Item>> groups = new HashMap<String, Collection<Item>>();
        for ( Item item : items ) {
            final String description = ( findDescription( item ) );
            Collection<Item> itemsForDescription = groups.get( description );
            if ( itemsForDescription == null ) {
                itemsForDescription = new ArrayList<Item>();
                groups.put( description,
                            itemsForDescription );
            }
            itemsForDescription.add( item );
        }
        return groups;
    }

    private String findDescription( final Item item ) {
        for ( ClientResourceType resourceType : resourceTypes ) {
            if ( resourceType.accept( item.getPath() ) ) {
                final String description = resourceType.getDescription();
                return ( ( description == null || description.isEmpty() ) ? MISCELLANEOUS : description );
            }
        }
        return MISCELLANEOUS;
    }

}

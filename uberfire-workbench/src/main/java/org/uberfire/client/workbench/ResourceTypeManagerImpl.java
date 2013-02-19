package org.uberfire.client.workbench;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.file.ResourceTypeManager;
import org.uberfire.client.workbench.file.ResourceType;

import static java.util.Collections.*;

@ApplicationScoped
public class ResourceTypeManagerImpl implements ResourceTypeManager {

    private final List<ResourceType> resourceTypes = new ArrayList<ResourceType>();
    private final IOCBeanManager iocManager;

    @Inject
    public ResourceTypeManagerImpl( final IOCBeanManager iocManager ) {
        this.iocManager = iocManager;
    }

    @PostConstruct
    public void init() {
        final Collection<IOCBeanDef<ResourceType>> availableTypes = iocManager.lookupBeans( ResourceType.class );

        for ( final IOCBeanDef<ResourceType> availableType : availableTypes ) {
            resourceTypes.add( availableType.getInstance() );
        }

        sort( resourceTypes, new Comparator<ResourceType>() {
            @Override
            public int compare( final ResourceType o1,
                                final ResourceType o2 ) {
                if ( o1.getPriority() < o2.getPriority() ) {
                    return 1;
                } else if ( o1.getPriority() > o2.getPriority() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } );

    }

    @Override
    public ResourceType resolve( final Path path ) {
        for ( final ResourceType resourceType : resourceTypes ) {
            if ( resourceType.accept( path ) ) {
                return resourceType;
            }
        }
        return null;
    }
}

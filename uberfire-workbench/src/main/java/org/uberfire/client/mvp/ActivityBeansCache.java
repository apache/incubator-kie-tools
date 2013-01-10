package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.client.workbench.annotations.ResourceType;

import static java.util.Collections.*;

/**
 *
 */
@ApplicationScoped
public class ActivityBeansCache {

    @Inject
    private IOCBeanManager iocManager;

    private final Map<String, IOCBeanDef<Activity>> cachedActivitiesById      = new HashMap<String, IOCBeanDef<Activity>>();
    private final Map<String, IOCBeanDef<Activity>> cachedActivitiesByPattern = new LinkedHashMap<String, IOCBeanDef<Activity>>();
    private       IOCBeanDef<Activity>              defaultActivity           = null;

    @PostConstruct
    public void init() {
        final Collection<IOCBeanDef<Activity>> activities = iocManager.lookupBeans( Activity.class );

        final Set<String> ids = new HashSet<String>( activities.size() );

        final Map<String, IOCBeanDef<Activity>> tempTypes = new LinkedHashMap<String, IOCBeanDef<Activity>>();
        for ( final IOCBeanDef<Activity> activityBean : activities ) {
            final String id = getIdentifier( activityBean );

            if ( ids.contains( id ) ) {
                throw new RuntimeException( "Conflic detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
            }

            ids.add( id );
            cachedActivitiesById.put( id, activityBean );
            if ( id.equals( "TextEditor" ) ) {
                defaultActivity = activityBean;
            }

            final String type = getResourceType( activityBean );
            if ( type != null ) {
                tempTypes.put( type, activityBean );
            }
        }

        //TODO: {porcelli} for an unknow reason... TreeMap doesn't work.. so I have to workaround with the following code
        final List<String> result = new ArrayList<String>( tempTypes.keySet() );
        sort( result, new Comparator<String>() {
            @Override
            public int compare( final String o1,
                                final String o2 ) {
                if ( o1.length() < o2.length() ) {
                    return 1;
                } else if ( o1.length() > o2.length() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } );

        for ( final String key : result ) {
            cachedActivitiesByPattern.put( key, tempTypes.get( key ) );
        }
    }

    /**
     * Given a bean definition return it's @Identifier value
     * @param beanDefinition
     * @return List of possible identifier, empty if none
     */
    private String getIdentifier( final IOCBeanDef beanDefinition ) {
        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        for ( Annotation a : annotations ) {
            if ( a instanceof Identifier ) {
                final Identifier identifier = (Identifier) a;
                return identifier.value();
            }
        }
        throw new RuntimeException( "Invalid Activity, missing @Identifier " + beanDefinition.getBeanClass().getName() );
    }

    private String getResourceType( final IOCBeanDef beanDefinition ) {
        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        for ( Annotation a : annotations ) {
            if ( a instanceof ResourceType ) {
                final ResourceType resourceType = (ResourceType) a;
                if ( resourceType.value().trim().length() > 0 ) {
                    return resourceType.value();
                }
            }
        }
        return null;
    }

    public IOCBeanDef<Activity> getDefaultActivity() {
        return defaultActivity;
    }

    public IOCBeanDef<Activity> getActivityById( final String id ) {
        return cachedActivitiesById.get( id );
    }

    public IOCBeanDef<Activity> cachedActivitiesByPattern( final String type ) {
        return cachedActivitiesByPattern.get( type );
    }

    public Set<String> getPatterns() {
        return cachedActivitiesByPattern.keySet();
    }
}

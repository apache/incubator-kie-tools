package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.kie.commons.data.Pair;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.ClientResourceType;

import static java.util.Collections.*;

/**
 *
 */
@ApplicationScoped
public class ActivityBeansCache {

    private final Map<String, IOCBeanDef<Activity>> activitiesById;
    private final List<ActivityAndMetaInfo> activities;
    private final IOCBeanManager iocManager;

    @Inject
    public ActivityBeansCache( final IOCBeanManager iocManager ) {
        this.iocManager = iocManager;
        this.activitiesById = new HashMap<String, IOCBeanDef<Activity>>();
        this.activities = new ArrayList<ActivityAndMetaInfo>();
    }

    @PostConstruct
    public void init() {
        final Collection<IOCBeanDef<Activity>> availableActivities = iocManager.lookupBeans( Activity.class );

        for ( final IOCBeanDef<Activity> activityBean : availableActivities ) {
            final String id = getIdentifier( activityBean );

            if ( activitiesById.keySet().contains( id ) ) {
                throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
            }

            activitiesById.put( id, activityBean );

            final Pair<Integer, List<Class<? extends ClientResourceType>>> metaInfo = getActivityMetaInfo( activityBean );
            if ( metaInfo != null ) {
                activities.add( new ActivityAndMetaInfo( activityBean, metaInfo.getK1(), metaInfo.getK2() ) );
            }
        }

        sort( activities, new Comparator<ActivityAndMetaInfo>() {
            @Override
            public int compare( final ActivityAndMetaInfo o1,
                                final ActivityAndMetaInfo o2 ) {

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

    private Pair<Integer, List<Class<? extends ClientResourceType>>> getActivityMetaInfo( final IOCBeanDef beanDefinition ) {
        AssociatedResources associatedResources = null;
        Priority priority = null;

        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        for ( Annotation a : annotations ) {
            if ( a instanceof AssociatedResources ) {
                associatedResources = (AssociatedResources) a;
                continue;
            }
            if ( a instanceof Priority ) {
                priority = (Priority) a;
                continue;
            }
        }

        if ( associatedResources == null ) {
            return null;
        }

        final int priorityValue;
        if ( priority == null ) {
            priorityValue = 0;
        } else {
            priorityValue = priority.value();
        }

        final List<Class<? extends ClientResourceType>> types = new ArrayList<Class<? extends ClientResourceType>>();
        for ( Class<? extends ClientResourceType> type : associatedResources.value() ) {
            types.add( type );
        }

        return Pair.newPair( priorityValue, types );
    }

    public IOCBeanDef<Activity> getActivity( final String id ) {
        return activitiesById.get( id );
    }

    public IOCBeanDef<Activity> getActivity( final Path path ) {

        for ( final ActivityAndMetaInfo currentActivity : activities ) {
            for ( final ClientResourceType resourceType : currentActivity.getResourceTypes() ) {
                if ( resourceType.accept( path ) ) {
                    return currentActivity.getActivityBean();
                }
            }
        }

        return null;
    }

    private class ActivityAndMetaInfo {

        private final IOCBeanDef<Activity> activityBean;
        private final int priority;
        private final ClientResourceType[] resourceTypes;

        private ActivityAndMetaInfo( final IOCBeanDef<Activity> activityBean,
                                     final int priority,
                                     final List<Class<? extends ClientResourceType>> resourceTypes ) {
            this.activityBean = activityBean;
            this.priority = priority;
            this.resourceTypes = new ClientResourceType[ resourceTypes.size() ];

            for ( int i = 0; i < resourceTypes.size(); i++ ) {
                this.resourceTypes[ i ] = iocManager.lookupBean( resourceTypes.get( i ) ).getInstance();
            }
        }

        public IOCBeanDef<Activity> getActivityBean() {
            return activityBean;
        }

        public int getPriority() {
            return priority;
        }

        public ClientResourceType[] getResourceTypes() {
            return resourceTypes;
        }
    }
}

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
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.events.NewPerspectiveEvent;
import org.uberfire.workbench.events.NewWorkbenchScreenEvent;

import static java.util.Collections.*;

/**
 *
 */
@ApplicationScoped
public class ActivityBeansCache {

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;

    @Inject
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent;

    private final Map<String, IOCBeanDef<Activity>> activitiesById = new HashMap<String, IOCBeanDef<Activity>>();
    private final List<ActivityAndMetaInfo> activities = new ArrayList<ActivityAndMetaInfo>();
    private final List<SplashScreenActivity> splashScreens = new ArrayList<SplashScreenActivity>();

    @PostConstruct
    public void init() {
        final Collection<IOCBeanDef<Activity>> availableActivities = iocManager.lookupBeans( Activity.class );

        for ( final IOCBeanDef<Activity> baseBean : availableActivities ) {
            //{porcelli} TODO workaround an Errai bug - it doesn't attach bean names when you lookup by a type that's not the concrete bean type
            final IOCBeanDef<Activity> activityBean = (IOCBeanDef<Activity>) iocManager.lookupBean( baseBean.getBeanClass() );

            final String id = activityBean.getName();

            if ( activitiesById.keySet().contains( id ) ) {
                throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
            }

            activitiesById.put( id, activityBean );

            if ( isSplashScreen( activityBean.getQualifiers() ) ) {
                splashScreens.add( (SplashScreenActivity) activityBean.getInstance() );
            } else {
                final Pair<Integer, List<Class<? extends ClientResourceType>>> metaInfo = getActivityMetaInfo( activityBean );
                if ( metaInfo != null ) {
                    activities.add( new ActivityAndMetaInfo( activityBean, metaInfo.getK1(), metaInfo.getK2() ) );
                }
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

    private boolean isSplashScreen( final Set<Annotation> qualifiers ) {
        for ( final Annotation qualifier : qualifiers ) {
            if ( qualifier instanceof IsSplashScreen ) {
                return true;
            }
        }
        return false;
    }

    //Used for runtime plugins
    public void addNewScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        newWorkbenchScreenEventEvent.fire( new NewWorkbenchScreenEvent( id ) );
    }

    //Used for runtime plugins
    public void addNewPerspectiveActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        newPerspectiveEventEvent.fire( new NewPerspectiveEvent( id ) );
    }

    public void addNewSplashScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        splashScreens.add( (SplashScreenActivity) activityBean.getInstance() );
//        newSplashScreenEvent.fire( new NewSplashScreenEvent( id ) );
    }

    public List<SplashScreenActivity> getSplashScreens() {
        return splashScreens;
    }

    private Pair<Integer, List<Class<? extends ClientResourceType>>> getActivityMetaInfo( final IOCBeanDef<?> beanDefinition ) {
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

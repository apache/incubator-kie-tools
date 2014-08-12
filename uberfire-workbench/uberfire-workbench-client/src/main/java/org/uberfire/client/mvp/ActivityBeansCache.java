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
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

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
        final Collection<IOCBeanDef<Activity>> availableActivities = getAvailableActivities();

        for ( final IOCBeanDef<Activity> baseBean : availableActivities ) {
            //{porcelli} TODO workaround an Errai bug - it doesn't attach bean names when you lookup by a type that's not the concrete bean type
            final IOCBeanDef<Activity> activityBean = reLookupBean( baseBean );

            final String id = activityBean.getName();

            if ( activitiesById.keySet().contains( id ) ) {
                throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
            }

            activitiesById.put( id, activityBean );

            if ( isSplashScreen( activityBean.getQualifiers() ) ) {
                splashScreens.add( (SplashScreenActivity) activityBean.getInstance() );
            } else {
                final Pair<Integer, List<Class<? extends ClientResourceType>>> metaInfo = generateActivityMetaInfo( activityBean );
                if ( metaInfo != null ) {
                    getActivities().add( new ActivityAndMetaInfo( activityBean, metaInfo.getK1(), metaInfo.getK2() ) );
                }
            }
        }

        sortActivitiesByPriority();
    }

    List<ActivityAndMetaInfo> getActivities() {
        return activities;
    }

    void sortActivitiesByPriority() {
        sort( getActivities(), new Comparator<ActivityAndMetaInfo>() {
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

    IOCBeanDef<Activity> reLookupBean( IOCBeanDef<Activity> baseBean ) {
        return (IOCBeanDef<Activity>) iocManager.lookupBean( baseBean.getBeanClass() );
    }

    Collection<IOCBeanDef<Activity>> getAvailableActivities() {
        return iocManager.lookupBeans( Activity.class );
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

    public void addNewEditorActivity( final IOCBeanDef<Activity> activityBean,
                                      Class<? extends ClientResourceType> resourceTypeClass ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        final List<Class<? extends ClientResourceType>> resourceTypes = new ArrayList<Class<? extends ClientResourceType>>();
        resourceTypes.add( resourceTypeClass );
        activities.add( new ActivityAndMetaInfo( activityBean, 0, resourceTypes ) );
    }

    public void addNewSplashScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        splashScreens.add( (SplashScreenActivity) activityBean.getInstance() );
    }

    public List<SplashScreenActivity> getSplashScreens() {
        return splashScreens;
    }

    public IOCBeanDef<Activity> getActivity( final String id ) {
        return activitiesById.get( id );
    }

    public IOCBeanDef<Activity> getActivity( final Path path ) {

        for ( final ActivityAndMetaInfo currentActivity : getActivities() ) {
            for ( final ClientResourceType resourceType : currentActivity.getResourceTypes() ) {
                if ( resourceType.accept( path ) ) {
                    return currentActivity.getActivityBean();
                }
            }
        }

        throw new EditorResourceTypeNotFound();
    }

    Pair<Integer, List<Class<? extends ClientResourceType>>> generateActivityMetaInfo( IOCBeanDef<Activity> activityBean ) {
        return ActivityMetaInfo.generate( activityBean );
    }

    public List<String> getActivitiesById() {
      return new ArrayList<String>(activitiesById.keySet());
    }

    class ActivityAndMetaInfo {

        private final IOCBeanDef<Activity> activityBean;
        private final int priority;
        private final ClientResourceType[] resourceTypes;

        ActivityAndMetaInfo( final IOCBeanDef<Activity> activityBean,
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

    private class EditorResourceTypeNotFound extends RuntimeException {

    }
}

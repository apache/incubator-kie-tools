package org.uberfire.client.mvp;

import static java.util.Collections.*;

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
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

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

    /**
     * All active activity beans mapped by their CDI bean name (names are mandatory for activity beans).
     */
    private final Map<String, IOCBeanDef<Activity>> activitiesById = new HashMap<String, IOCBeanDef<Activity>>();

    /**
     * All active Activities that have an {@link AssociatedResources} annotation and are not splash screens.
     */
    private final List<ActivityAndMetaInfo> resourceActivities = new ArrayList<ActivityAndMetaInfo>();

    /**
     * All active activities that are splash screens.
     */
    private final List<SplashScreenActivity> splashActivities = new ArrayList<SplashScreenActivity>();

    @PostConstruct
    void init() {
        final Collection<IOCBeanDef<Activity>> availableActivities = getAvailableActivities();

        for ( final IOCBeanDef<Activity> baseBean : availableActivities ) {
            final IOCBeanDef<Activity> activityBean = reLookupBean( baseBean );

            final String id = activityBean.getName();

            if ( activitiesById.keySet().contains( id ) ) {
                throw new RuntimeException( "Conflict detected. Activity '" + id + "' already exists. " + activityBean.getBeanClass().toString() );
            }

            activitiesById.put( id, activityBean );

            if ( isSplashScreen( activityBean.getQualifiers() ) ) {
                splashActivities.add( (SplashScreenActivity) activityBean.getInstance() );
            } else {
                final Pair<Integer, List<Class<? extends ClientResourceType>>> metaInfo = generateActivityMetaInfo( activityBean );
                if ( metaInfo != null ) {
                    getResourceActivities().add( new ActivityAndMetaInfo( activityBean, metaInfo.getK1(), metaInfo.getK2() ) );
                }
            }
        }

        sortResourceActivitiesByPriority();
    }

    /**
     * Returns all activities in this cache that have an associated resource type.
     */
    List<ActivityAndMetaInfo> getResourceActivities() {
        return resourceActivities;
    }

    void sortResourceActivitiesByPriority() {
        sort( getResourceActivities(), new Comparator<ActivityAndMetaInfo>() {
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
     * {porcelli} TODO workaround an Errai bug - it doesn't attach bean names when you lookup by a type that's not the concrete bean type.
     */
    IOCBeanDef<Activity> reLookupBean( IOCBeanDef<Activity> baseBean ) {
        return (IOCBeanDef<Activity>) iocManager.lookupBean( baseBean.getBeanClass() );
    }

    Collection<IOCBeanDef<Activity>> getAvailableActivities() {
        Collection<IOCBeanDef<Activity>> activeBeans = new ArrayList<IOCBeanDef<Activity>>();
        for ( IOCBeanDef<Activity> bean : iocManager.lookupBeans( Activity.class ) ) {
            if ( bean.isActivated() ) {
                activeBeans.add( bean );
            }
        }
        return activeBeans;
    }

    private boolean isSplashScreen( final Set<Annotation> qualifiers ) {
        for ( final Annotation qualifier : qualifiers ) {
            if ( qualifier instanceof IsSplashScreen ) {
                return true;
            }
        }
        return false;
    }

    public void removeActivity( String id ) {
        activitiesById.remove(id);
    }

    /** Used for runtime plugins. */
    public void addNewScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        newWorkbenchScreenEventEvent.fire( new NewWorkbenchScreenEvent( id ) );
    }

    /** Used for runtime plugins. */
    public void addNewPerspectiveActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        newPerspectiveEventEvent.fire( new NewPerspectiveEvent( id ) );
    }

    /** Used for runtime plugins. */
    public void addNewEditorActivity( final IOCBeanDef<Activity> activityBean,
                                      Class<? extends ClientResourceType> resourceTypeClass ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        final List<Class<? extends ClientResourceType>> resourceTypes = new ArrayList<Class<? extends ClientResourceType>>();
        resourceTypes.add( resourceTypeClass );
        resourceActivities.add( new ActivityAndMetaInfo( activityBean, 0, resourceTypes ) );
    }

    public void addNewSplashScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        splashActivities.add( (SplashScreenActivity) activityBean.getInstance() );
    }

    /**
     * Returns all active splash screen activities in this cache.
     */
    public List<SplashScreenActivity> getSplashScreens() {
        return splashActivities;
    }

    /**
     * Returns the activity with the given CDI bean name from this cache, or null if there is no such activity or the
     * activity with the given name is not an activated bean.
     *
     * @param id
     *            the CDI name of the bean (see {@link Named}), or in the case of runtime plugins, the name the activity
     *            was registered under.
     */
    public IOCBeanDef<Activity> getActivity( final String id ) {
        return activitiesById.get( id );
    }

    /**
     * Returns the activated activity with the highest priority that can handle the given file. Returns null if no
     * activated activity can handle the path.
     *
     * @param path
     *            the file to find a path-based activity for (probably a {@link WorkbenchEditorActivity}, but this cache
     *            makes no guarantees).
     */
    public IOCBeanDef<Activity> getActivity( final Path path ) {

        for ( final ActivityAndMetaInfo currentActivity : getResourceActivities() ) {
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

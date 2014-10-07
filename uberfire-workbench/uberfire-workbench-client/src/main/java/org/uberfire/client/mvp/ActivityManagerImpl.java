/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.mvp;

import static java.util.Collections.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;

@ApplicationScoped
public class ActivityManagerImpl implements ActivityManager {

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private AuthorizationManager authzManager;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private User identity;

    /**
     * Activities in this set have had their {@link Activity#onStartup(PlaceRequest)} method called and have not been
     * shut down yet. This set tracks objects by identity, so it is possible that it could have multiple activities of
     * the same type within it (for example, multiple editors of the same type for different files.)
     */
    private final Map<Activity, PlaceRequest> startedActivities = new IdentityHashMap<Activity, PlaceRequest>();

    private final Map<Object, Boolean> containsCache = new HashMap<Object, Boolean>();

    @Override
    public <T extends Activity> Set<T> getActivities( final Class<T> clazz ) {
        // not calling onStartup. See UF-105.
        return secure( iocManager.lookupBeans( clazz ) );
    }

    @Override
    public SplashScreenActivity getSplashScreenInterceptor( final PlaceRequest placeRequest ) {

        SplashScreenActivity resultBean = null;
        for ( SplashScreenActivity splashScreen : activityBeansCache.getSplashScreens() ) {
            if ( splashScreen.intercept( placeRequest ) ) {
                resultBean = splashScreen;
                break;
            }
        }

        return startIfNecessary( secure( resultBean ), placeRequest );
    }

    @Override
    public Set<Activity> getActivities( final PlaceRequest placeRequest ) {

        final Collection<IOCBeanDef<Activity>> beans;
        if ( placeRequest instanceof PathPlaceRequest ) {
            beans = resolveByPath( (PathPlaceRequest) placeRequest );
        } else {
            beans = resolveById( placeRequest.getIdentifier() );
        }

        return startIfNecessary( secure( beans ), placeRequest );
    }

    @Override
    public boolean containsActivity( final PlaceRequest placeRequest ) {
        if ( containsCache.containsKey( placeRequest.getIdentifier() ) ) {
            return containsCache.get( placeRequest.getIdentifier() );
        }

        Path path = null;
        if ( placeRequest instanceof PathPlaceRequest ) {
            path = ( (PathPlaceRequest) placeRequest ).getPath();
            if ( containsCache.containsKey( path ) ) {
                return containsCache.get( path );
            }
        }

        final Activity result = getActivity( placeRequest );
        containsCache.put( placeRequest.getIdentifier(), result != null );
        if ( path != null ) {
            containsCache.put( path, result != null );
        }

        return result != null;
    }

    @Override
    public Activity getActivity( final PlaceRequest placeRequest ) {
        return getActivity( Activity.class, placeRequest );
    }

    @Override
    public <T extends Activity> T getActivity( final Class<T> clazz,
                                               final PlaceRequest placeRequest ) {
        final Set<Activity> activities = getActivities( placeRequest );
        if ( activities.size() == 0 ) {
            return null;
        }

        final Activity activity = activities.iterator().next();

        return (T) activity;
    }

    @Override
    public void destroyActivity( final Activity activity ) {
        if ( startedActivities.remove( activity ) != null ) {
            boolean isDependentScope = getBeanScope( activity ) == Dependent.class;
            activity.onShutdown();
            if ( isDependentScope ) {
                iocManager.destroyBean( activity );
            }
        } else {
            throw new IllegalStateException( "Activity " + activity + " is not currently in the started state" );
        }
    }

    @Override
    public boolean isStarted( final Activity activity ) {
        return startedActivities.containsKey( activity );
    }

    /**
     * Returns the scope of the given activity bean, first in the Errai bean manager and then falling back on checking
     * with the activity cache (the only way to look up the BeanDef for a runtime plugin activity). Beans that are not
     * started (or were started but have been shut down) will cause an NPE if the fallback to the activity beans cache
     * happens.
     *
     * @param startedActivity
     *            an activity that is in the <i>started</i> or <i>open</i> state.
     */
    private Class<?> getBeanScope( Activity startedActivity ) {

//        // splash screens are tracked separately
//        if ( startedActivity instanceof SplashScreenActivity ) {
//            // FIXME this is an assumption based on convention. should modify bean cache to keep bean defs for splash screens too.
//            return ApplicationScoped.class;
//        }
//
//        final IOCBeanDef<?> beanDef = activityBeansCache.getActivity( startedActivity.getPlace().getIdentifier() );
//        if ( beanDef == null ) {
//            return Dependent.class;
//        }
//        return beanDef.getScope();
        IOCBeanDef<?> beanDef = iocManager.lookupBean( startedActivity.getClass() );
        if ( beanDef == null ) {
            beanDef = activityBeansCache.getActivity( startedActivity.getPlace().getIdentifier() );
        }
        return beanDef.getScope();
    }

    private <T extends Activity> Set<T> secure( final Collection<IOCBeanDef<T>> activityBeans ) {
        final Set<T> activities = new HashSet<T>( activityBeans.size() );

        for ( final IOCBeanDef<T> activityBean : activityBeans ) {
            if ( !activityBean.isActivated() ) continue;
            final T instance = activityBean.getInstance();
            if ( authzManager.authorize( instance, identity ) ) {
                activities.add( instance );
            } else {
                // Since user does not have permission, destroy bean to avoid memory leak
                if ( activityBean.getScope().equals( Dependent.class ) ) {
                    iocManager.destroyBean( instance );
                }
            }
        }

        return activities;
    }

    private SplashScreenActivity secure( final SplashScreenActivity bean ) {
        if ( bean == null ) {
            return null;
        }

        if ( authzManager.authorize( bean, identity ) ) {
            return bean;
        }

        return null;
    }

    private <T extends Activity> T startIfNecessary( T activity, PlaceRequest place ) {
        if (activity == null) return null;
        if ( !startedActivities.containsKey( activity ) ) {
            activity.onStartup( place );
            startedActivities.put( activity, place );
        }
        return activity;
    }

    private Set<Activity> startIfNecessary( Set<Activity> activities, PlaceRequest place ) {
        for ( Activity activity : activities ) {
            startIfNecessary( activity, place );
        }
        return activities;
    }

    /**
     * Gets the bean definition of the activity associated with the given place ID, if one exists.
     *
     * @param identifier
     *            the place ID. Null is permitted, but always resolves to an empty collection.
     * @return an unmodifiable collection with zero or one item, depending on if the resolution was successful.
     */
    private Collection<IOCBeanDef<Activity>> resolveById( final String identifier ) {
        if ( identifier == null ) {
            return emptyList();
        }

        IOCBeanDef<Activity> beanDefActivity = activityBeansCache.getActivity(identifier);
        if (beanDefActivity == null) {
            return emptyList();
        }
        return singletonList(beanDefActivity);
    }

    private Set<IOCBeanDef<Activity>> resolveByPath( final PathPlaceRequest place ) {
        if ( place == null ) {
            return emptySet();
        }
        final IOCBeanDef<Activity> result = activityBeansCache.getActivity( place.getIdentifier() );

        if ( result != null ) {
            return singleton( result );
        }

        return asSet( activityBeansCache.getActivity( place.getPath() ) );
    }

    private Set<IOCBeanDef<Activity>> asSet( final IOCBeanDef<Activity> activity ) {
        if ( activity == null ) {
            return emptySet();
        }

        return singleton( activity );
    }

}

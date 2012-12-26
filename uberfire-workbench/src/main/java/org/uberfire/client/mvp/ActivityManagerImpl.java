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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.regexp.shared.RegExp;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.workbench.annotations.Identifier;
import org.uberfire.client.workbench.annotations.ResourceType;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import static java.util.Collections.*;

@ApplicationScoped
public class ActivityManagerImpl
        implements
        ActivityManager {

    private final Map<PlaceRequest, Activity> activeActivities = new HashMap<PlaceRequest, Activity>();

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private RuntimeAuthorizationManager authzManager;

    @Inject
    private DefaultPlaceResolver defaultPlaceResolver;

    @Inject
    private Identity identity;

    private final Map<String, IOCBeanDef<Activity>> cachedActivitiesById   = new HashMap<String, IOCBeanDef<Activity>>();
    private final Map<String, IOCBeanDef<Activity>> cachedActivitiesByType = new LinkedHashMap<String, IOCBeanDef<Activity>>();

    private IOCBeanDef<Activity> defaultActivity = null;

    @PostConstruct
    public void init() {
        final Collection<IOCBeanDef<Activity>> activities = iocManager.lookupBeans( Activity.class );

        final Map<String, IOCBeanDef<Activity>> tempTypes = new LinkedHashMap<String, IOCBeanDef<Activity>>();
        for ( final IOCBeanDef<Activity> activityBean : activities ) {
            final String id = getIdentifier( activityBean );
            if ( id != null ) {
                cachedActivitiesById.put( id, activityBean );
                if ( id.equals( "TextEditor" ) ) {
                    defaultActivity = activityBean;
                }
            }

            final String type = getResourceType( activityBean );
            if ( type != null ) {
                tempTypes.put( type, activityBean );
            }
        }

        //for an unknow reason... TreeMap doesn't work.. so I have to workaround with the following code
        final List<String> result = new ArrayList<String>( tempTypes.keySet() );
        Collections.sort( result, new Comparator<String>() {
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
            cachedActivitiesByType.put( key, tempTypes.get( key ) );
        }
    }

    @Override
    public Activity getActivity( final PlaceRequest placeRequest ) {
        //Check and return any existing Activity for the PlaceRequest
        if ( activeActivities.containsKey( placeRequest ) ) {
            return activeActivities.get( placeRequest );
        }

        //Lookup an Activity for the PlaceRequest
        Activity instance = null;

        final String identifier = placeRequest.getIdentifier();
        final Set<IOCBeanDef<Activity>> activityBeans;

        if ( identifier.equals( DefaultPlaceRequest.PATH_ID ) ) {
            activityBeans = resolveByPath( placeRequest.getParameterString( "path:name", null ) );
        } else {
            activityBeans = resolveById( identifier );
        }

        switch ( activityBeans.size() ) {
            case 0:
                //No activities found. Show an error to the user.
                final PlaceRequest notFoundPopup = new DefaultPlaceRequest( "workbench.activity.notfound" );
                notFoundPopup.addParameter( "requestedPlaceIdentifier", identifier );
                placeManager.goTo( notFoundPopup );
                break;
            case 1:
                instance = getFirstActivity( activityBeans );
                if ( instance == null ) {
                    placeManager.goTo( DefaultPlaceRequest.NOWHERE );
                }
                //Only WorkbenchActivities can be re-visited
                if ( instance instanceof WorkbenchActivity ) {
                    activeActivities.put( placeRequest,
                                          instance );
                }
                return instance;
            default:
                // Check if there is a default
                final String editorId = defaultPlaceResolver.getEditorId( identifier );
                if ( editorId == null ) {
                    goToMultipleActivitiesPlace( identifier );
                } else {
                    for ( Activity activity : getActivities( placeRequest ) ) {
                        if ( activity.getSignatureId().equals( editorId ) ) {
                            return activity;
                        }
                    }
                    goToMultipleActivitiesPlace( identifier );
                }
        }

        return null;
    }

    private void goToMultipleActivitiesPlace( String identifier ) {
        final PlaceRequest multiplePopup = new DefaultPlaceRequest( "workbench.activities.multiple" );
        multiplePopup.addParameter( "requestedPlaceIdentifier",
                                    identifier );
        placeManager.goTo( multiplePopup );
    }

    @Override
    public <T extends Activity> Set<T> getActivities( final Class<T> clazz ) {

        final Collection<IOCBeanDef<T>> activityBeans = iocManager.lookupBeans( clazz );

        final Set<T> activities = new HashSet<T>( activityBeans.size() );

        for ( final IOCBeanDef<T> activityBean : activityBeans ) {
            final T instance = activityBean.getInstance();
            if ( authzManager.authorize( instance,
                                         identity ) ) {
                activities.add( instance );
            } else {
                //If user does not have permission destroy bean to avoid memory leak
                if ( activityBean.getScope().equals( Dependent.class ) ) {
                    iocManager.destroyBean( instance );
                }
            }
        }

        return activities;
    }

    @Override
    public Set<Activity> getActivities( final PlaceRequest placeRequest ) {
        final Set<Activity> activities = new HashSet<Activity>();
        for ( IOCBeanDef<Activity> bean : resolveById( placeRequest.getIdentifier() ) ) {
            activities.add( bean.getInstance() );
        }
        return activities;
    }

    private Activity getFirstActivity( final Set<IOCBeanDef<Activity>> activityBeans ) {
        if ( activityBeans == null || activityBeans.size() == 0 ) {
            return null;
        }
        final IOCBeanDef<Activity> activityBean = activityBeans.iterator().next();
        final Activity instance = activityBean.getInstance();
        if ( !authzManager.authorize( instance,
                                      identity ) ) {
            //If user does not have permission destroy bean to avoid memory leak
            if ( activityBean.getScope().equals( Dependent.class ) ) {
                iocManager.destroyBean( instance );
            }
            return null;
        }
        return instance;
    }

    @Override
    public void removeActivity( final PlaceRequest placeRequest ) {
        final Activity activity = activeActivities.remove( placeRequest );
        iocManager.destroyBean( activity );
    }

    ///FROM IDUTILS!

    /**
     * Get a set of Bean definitions that can handle the @Identifier
     * @param identifier
     * @return
     */
    //Don't return actual Activity instances as we'd need to release them later
    private Set<IOCBeanDef<Activity>> resolveById( final String identifier ) {
        if ( identifier == null ) {
            return emptySet();
        }

        final IOCBeanDef<Activity> result = cachedActivitiesById.get( identifier );
        if ( result == null ) {
            return emptySet();
        }

        return unmodifiableSet( new HashSet<IOCBeanDef<Activity>>( 1 ) {{
            add( result );
        }} );
    }

    private Set<IOCBeanDef<Activity>> resolveByPath( final String fileName ) {
        if ( fileName == null ) {
            return Collections.emptySet();
        }

        final Set<IOCBeanDef<Activity>> matchingActivityBeans = new HashSet<IOCBeanDef<Activity>>();
        for ( final String pattern : cachedActivitiesByType.keySet() ) {
            if ( RegExp.compile( pattern ).test( fileName ) ) {
                matchingActivityBeans.add( cachedActivitiesByType.get( pattern ) );
                break;
            }

        }

        if ( matchingActivityBeans.isEmpty() ) {
            matchingActivityBeans.add( defaultActivity );
        }

        return unmodifiableSet( matchingActivityBeans );
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
        return null;
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
}

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

package org.drools.guvnor.client.mvp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.screens.activities.multiple.MultipleActivitiesFoundPlace;
import org.jboss.errai.ioc.client.container.IOCBeanDef;

import com.google.gwt.user.client.Window;

@Dependent
public class GuvnorNGActivityMapperImpl
    implements
    ActivityMapper {

    private final Map<IPlaceRequest, Activity> activeActivities = new HashMap<IPlaceRequest, Activity>();

    @Inject
    private IdentifierUtils                    idUtils;

    @Inject
    private PlaceManager                       placeManager;

    public Activity getActivity(final IPlaceRequest placeRequest) {
        //Check and return any existing Activity for the PlaceRequest
        if ( activeActivities.containsKey( placeRequest ) ) {
            return activeActivities.get( placeRequest );
        }

        //Lookup an Activity for the PlaceRequest
        Activity instance = null;
        final String identifier = placeRequest.getIdentifier();
        Set<IOCBeanDef< ? >> activityBeans = idUtils.getActivities( identifier );
        switch ( activityBeans.size() ) {
            case 0 :
                //TODO {manstis} No activities found. Show an error to the user.
                Window.alert( "No Activity found to handle: [" + identifier + "]" );
                break;
            case 1 :
                instance = getFirstActivity( activityBeans );
                activeActivities.put( placeRequest,
                                      instance );
                return instance;
            default :
                placeManager.goTo( new MultipleActivitiesFoundPlace( identifier ) );
                //TODO {manstis} Multiple activities found. Show a selector to the user.
//                Window.alert( "Multiple Activities found to handle: [" + identifier + "]. Using the first..." );
//                instance = getFirstActivity( activityBeans );
//                activeActivities.put( placeRequest,
//                                      instance );
//                return instance;
        }

        return null;
    }

    private Activity getFirstActivity(final Set<IOCBeanDef< ? >> activityBeans) {
        if ( activityBeans == null || activityBeans.size() == 0 ) {
            return null;
        }
        final IOCBeanDef< ? > activityBean = activityBeans.iterator().next();
        final Activity instance = (Activity) activityBean.getInstance();
        return instance;
    }

    public void removeActivity(final PlaceRequest placeRequest) {
        final Activity activity = activeActivities.remove( placeRequest );
        if ( activity instanceof WorkbenchActivity ) {
            final WorkbenchActivity wbActivity = (WorkbenchActivity) activity;
            wbActivity.onStop();
        }
    }

}

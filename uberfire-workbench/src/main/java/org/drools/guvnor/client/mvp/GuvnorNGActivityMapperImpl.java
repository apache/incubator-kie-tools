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

import org.drools.guvnor.shared.mvp.PlaceRequest;
import org.jboss.errai.ioc.client.container.IOCBeanDef;

@Dependent
public class GuvnorNGActivityMapperImpl
    implements
    ActivityMapper {

    private final Map<PlaceRequest, Activity> activeActivities = new HashMap<PlaceRequest, Activity>();

    @Inject
    private IdentifierUtils                    idUtils;

    @Inject
    private PlaceManager                       placeManager;

    public Activity getActivity(final PlaceRequest placeRequest) {
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
                //No activities found. Show an error to the user.
            	final PlaceRequest notFoundPopup = new PlaceRequest( "workbench.activity.notfound" );
            	notFoundPopup.addParameter("requestedPlaceIdentifier", identifier);
                placeManager.goTo( notFoundPopup );
                break;
            case 1 :
                instance = getFirstActivity( activityBeans );
                activeActivities.put( placeRequest,
                                      instance );
                return instance;
            default :
                //TODO {manstis} Multiple activities found. Show a selector to the user.
            	final PlaceRequest multiplePopup = new PlaceRequest( "workbench.activities.multiple" );
            	multiplePopup.addParameter("requestedPlaceIdentifier", identifier);
                placeManager.goTo( multiplePopup );
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

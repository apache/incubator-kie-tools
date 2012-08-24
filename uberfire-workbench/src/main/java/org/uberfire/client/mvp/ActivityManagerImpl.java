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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.security.authz.AccessDecisionManager;
import org.uberfire.shared.mvp.PlaceRequest;

import static org.uberfire.shared.mvp.PlaceRequest.NOWHERE;

@Dependent
public class ActivityManagerImpl
        implements
        ActivityManager {

    private final Map<PlaceRequest, Activity> activeActivities = new HashMap<PlaceRequest, Activity>();

    @Inject
    private IdentifierUtils idUtils;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private AccessDecisionManager accessDecisionManager;

    public Activity getActivity(final PlaceRequest placeRequest) {
        //Check and return any existing Activity for the PlaceRequest
        if (activeActivities.containsKey(placeRequest)) {
            return activeActivities.get(placeRequest);
        }

        //Lookup an Activity for the PlaceRequest
        Activity instance = null;
        final String identifier = placeRequest.getIdentifier();
        Set<IOCBeanDef<Activity>> activityBeans = idUtils.getActivities(identifier);
        switch (activityBeans.size()) {
            case 0:
                //No activities found. Show an error to the user.
                final PlaceRequest notFoundPopup = new PlaceRequest("workbench.activity.notfound");
                notFoundPopup.addParameter("requestedPlaceIdentifier", identifier);
                placeManager.goTo(notFoundPopup);
                break;
            case 1:
                instance = getFirstActivity(activityBeans);
                if (instance == null){
                    placeManager.goTo(NOWHERE);
                }
                activeActivities.put(placeRequest, instance);
                return instance;
            default:
                //TODO {manstis} Multiple activities found. Show a selector to the user.
                final PlaceRequest multiplePopup = new PlaceRequest("workbench.activities.multiple");
                multiplePopup.addParameter("requestedPlaceIdentifier", identifier);
                placeManager.goTo(multiplePopup);
        }

        return null;
    }

    @Override
    public <T extends Activity> Set<T> getActivities(Class<T> clazz) {

        final Collection<IOCBeanDef<T>> activityBeans = iocManager.lookupBeans(clazz);

        final Set<T> activities = new HashSet<T>(activityBeans.size());

        for (final IOCBeanDef<T> activityBean : activityBeans) {
            final T instance = activityBean.getInstance();
            if (accessDecisionManager.accessGranted(instance)) {
                activities.add(instance);
            }
        }

        return activities;
    }

    private Activity getFirstActivity(final Set<IOCBeanDef<Activity>> activityBeans) {
        if (activityBeans == null || activityBeans.size() == 0) {
            return null;
        }
        final IOCBeanDef<Activity> activityBean = activityBeans.iterator().next();
        final Activity instance = activityBean.getInstance();
        if (accessDecisionManager.accessDenied(instance)) {
            return null;
        }
        return instance;
    }

    public void removeActivity(final PlaceRequest placeRequest) {
        final Activity activity = activeActivities.remove(placeRequest);
        if (activity instanceof WorkbenchEditorActivity) {
            final WorkbenchEditorActivity wbActivity = (WorkbenchEditorActivity) activity;
            wbActivity.onStop();
        }
    }

}

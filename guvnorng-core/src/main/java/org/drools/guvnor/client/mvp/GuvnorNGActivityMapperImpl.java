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


import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.ActivityMapper;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Dependent
public class GuvnorNGActivityMapperImpl implements ActivityMapper {
    private final Map<PlaceRequest, Activity> activeActivities = new HashMap<PlaceRequest, Activity>();

    @Inject
    private IOCBeanManager manager;

    public Activity getActivity(final PlaceRequest placeRequest) {

        Collection<IOCBeanDef> beans = manager.lookupBeans(Activity.class);

        // check to see if the bean exists
        for (IOCBeanDef activityBean : beans) {
            // get the instance of the activity
            Activity activity = (Activity) activityBean.getInstance();
            if (activity.getNameToken().equals(placeRequest.getNameToken())) {
                return activity;
            }
        }

        return null;
    }
    
    //Not working yet with Errai
    public Activity getActivity1(final PlaceRequest placeRequest) {
        if (activeActivities.containsKey(placeRequest)) {
            return activeActivities.get(placeRequest);
        }

        NameToken qual = new NameToken() {
            public Class annotationType() {
                return NameToken.class;
            }

            @Override
            public String value() {
                return placeRequest.getNameToken();
            }
        };

        IOCBeanDef<Activity> bean = manager.lookupBean(Activity.class, qual);
        Activity activity = (Activity) bean.getInstance();
        activeActivities.put(placeRequest, activity);
        return activity;
    }
    
    public void removeActivity(final PlaceRequest placeRequest) {
        Activity activity = activeActivities.remove(placeRequest);
        activity.onStop();
    }
        
    
}

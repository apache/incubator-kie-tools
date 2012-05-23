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

package org.drools.guvnor.client.editor;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.ActivityMapper;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;


@Dependent
//@ApplicationScoped
public class GuvnorNGActivityMapperImpl implements ActivityMapper {
/*
    Map<String, Activity> activites = new HashMap<String, Activity>();

    @Inject 
    void initServices(@Any Instance<Activity> activities) { 
      for (Activity activity: activities) {
        activites.put(activity.getNameToken(), activity);
      }
    }

    public Activity getActivity( PlaceRequest placeRequest ){
      return activites.get(placeRequest.getNameToken());
    }
    
*/
    @PostConstruct
    public void lookupBean() {
        Collection<IOCBeanDef> beans = manager.lookupBeans(Activity.class);     

        // check to see if the bean exists
        for (IOCBeanDef activityBean : beans) {
            // get the instance of the bean
          Activity bean = (Activity) activityBean.getInstance();
        }
      }
    
    @Inject private IOCBeanManager manager;

    private Activity actual = null;

    public Activity getActivity(final PlaceRequest placeRequest) {
        if ("AdminArea".equalsIgnoreCase(placeRequest.getNameToken())) {
            actual = manager.lookupBean(MyAdminAreaActivity.class).getInstance();
        } else if ("AdminArea2".equalsIgnoreCase(placeRequest.getNameToken())) {
            actual = manager.lookupBean(MyAdminAreaActivity2.class).getInstance();
        }
        
        return actual;
    }
}

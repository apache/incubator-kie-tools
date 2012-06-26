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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
public class GuvnorNGActivityMapperImpl
    implements
    ActivityMapper {
    private final Map<IPlaceRequest, Activity> activeActivities = new HashMap<IPlaceRequest, Activity>();

    @Inject
    private IOCBeanManager                     manager;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Activity getActivity(final IPlaceRequest placeRequest) {
        if ( activeActivities.containsKey( placeRequest ) ) {
            return activeActivities.get( placeRequest );
        }

        final String nameToken = placeRequest.getNameToken();

        Collection<IOCBeanDef> beans = manager.lookupBeans( Activity.class );

        //Lookup Activity by NameToken
        //See https://community.jboss.org/thread/200255 as to why we can't use look lookupBean(Class, Qualifiers)
        for ( IOCBeanDef activityBean : beans ) {
            Set<Annotation> qualifiers = activityBean.getQualifiers();
            for ( Annotation q : qualifiers ) {
                if ( q instanceof NameToken ) {
                    final NameToken token = (NameToken) q;
                    if ( token.value().equalsIgnoreCase( nameToken ) ) {
                        Activity activity = (Activity) activityBean.getInstance();
                        activeActivities.put( placeRequest,
                                              activity );
                        return activity;
                    }
                }
            }
        }

        return null;
    }

    public void removeActivity(final PlaceRequest placeRequest) {
        Activity activity = activeActivities.remove( placeRequest );
        activity.onStop();
    }

}

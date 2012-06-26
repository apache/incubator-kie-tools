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
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.jboss.errai.ioc.client.container.IOCResolutionException;

@Dependent
public class GuvnorNGActivityMapperImpl
    implements
    ActivityMapper {
    private final Map<IPlaceRequest, Activity> activeActivities = new HashMap<IPlaceRequest, Activity>();

    @Inject
    private IOCBeanManager                     iocManager;

    public Activity getActivity(final IPlaceRequest placeRequest) {
        if ( activeActivities.containsKey( placeRequest ) ) {
            return activeActivities.get( placeRequest );
        }

        final String nameToken = placeRequest.getNameToken();

        //Lookup Activity by NameToken
        final Annotation qualifier = new NameToken() {

            @Override
            public Class< ? extends Annotation> annotationType() {
                return NameToken.class;
            }

            @Override
            public String value() {
                return nameToken;
            }

        };
        try {
            final IOCBeanDef<Activity> activity = iocManager.lookupBean( Activity.class,
                                                                         qualifier );
            final Activity instance = (Activity) activity.getInstance();
            activeActivities.put( placeRequest,
                                  instance );
            return instance;

        } catch ( IOCResolutionException ioce ) {
            //Could not find a bean to handle the NameToken (or we found multiple!)
            //TODO {manstis} We could present the user with a list of choices
        }

        return null;
    }

    public void removeActivity(final PlaceRequest placeRequest) {
        Activity activity = activeActivities.remove( placeRequest );
        activity.onStop();
    }

}

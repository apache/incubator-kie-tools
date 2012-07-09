/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.mvp;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

/**
 * Utilities for Identifiers
 */
@ApplicationScoped
public class IdentifierUtils {

    @Inject
    private IOCBeanManager iocManager;

    /**
     * Given a bean definition return it's @Identifier value
     * 
     * @param beanDefinition
     * @return Identifier or null if none found
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String getIdentifier(final IOCBeanDef beanDefinition) {
        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        for ( Annotation a : annotations ) {
            if ( a instanceof Identifier ) {
                final Identifier identifier = (Identifier) a;
                return identifier.value();
            }
        }
        return null;
    }

    /**
     * Get a set of Activities that can handle the @Identifier
     * 
     * @param identifier
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Set<Activity> getActivities(final String identifier) {
        final Collection<IOCBeanDef> activityBeans = iocManager.lookupBeans( Activity.class );
        final Set<Activity> activities = new HashSet<Activity>();
        for ( IOCBeanDef activityBean : activityBeans ) {
            final String activityIdentifier = getIdentifier( activityBean );
            if ( identifier.equalsIgnoreCase( activityIdentifier ) ) {
                final Activity instance = (Activity) activityBean.getInstance();
                activities.add( instance );
            }
        }
        return activities;
    }

}

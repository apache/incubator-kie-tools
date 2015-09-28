/*
 * Copyright 2015 JBoss Inc
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

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@ApplicationScoped
public class ActivityBeansInfo {

    public List<String> getAvailableWorkbenchScreensIds() {
        return lookupBeansId( WorkbenchScreenActivity.class );
    }

    public List<String> getAvailablePerspectivesIds() {
        return lookupBeansId( PerspectiveActivity.class );
    }

    public List<String> getAvailableSplashScreensIds() {
        return lookupBeansId( SplashScreenActivity.class );
    }

    public List<String> getAvailableWorkbenchEditorsIds() {
        return lookupBeansId( WorkbenchEditorActivity.class );
    }

    private List<String> lookupBeansId( Class<?> activityClass ) {
        final Collection<? extends IOCBeanDef<?>> screens = getBeanManager().lookupBeans( activityClass );
        List<String> result = new ArrayList<String>();
        for (final IOCBeanDef<?> beanDef : screens) {
            result.add( getId( beanDef ) );
        }
        Collections.sort( result );
        return result;
    }

    SyncBeanManager getBeanManager() {
        return IOC.getBeanManager();
    }


    private String getId( final IOCBeanDef<?> beanDef ) {
        for (final Annotation annotation : beanDef.getQualifiers()) {
            if (isNamed( annotation )) {
                return ((Named) annotation).value();
            }
        }
        if (hasBeanName( beanDef )) {
            return beanDef.getName();
        }
        return "";
    }

    boolean isNamed( Annotation annotation ) {
        return annotation instanceof Named;
    }

    private boolean hasBeanName( IOCBeanDef<?> beanDef ) {
        return beanDef.getName() != null && !beanDef.getName().isEmpty();
    }
}

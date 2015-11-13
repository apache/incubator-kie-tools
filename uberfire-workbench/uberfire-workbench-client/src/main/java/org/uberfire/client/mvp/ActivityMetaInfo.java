/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

public class ActivityMetaInfo {

    static Pair<Integer, List<Class<? extends ClientResourceType>>> generate(final IOCBeanDef<?> beanDefinition){

        AssociatedResources associatedResources = null;
        Priority priority = null;

        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        for ( Annotation a : annotations ) {
            if ( a instanceof AssociatedResources ) {
                associatedResources = (AssociatedResources) a;
                continue;
            }
            if ( a instanceof Priority ) {
                priority = (Priority) a;
                continue;
            }
        }

        if ( associatedResources == null ) {
            return null;
        }

        final int priorityValue;
        if ( priority == null ) {
            priorityValue = 0;
        } else {
            priorityValue = priority.value();
        }

        final List<Class<? extends ClientResourceType>> types = new ArrayList<Class<? extends ClientResourceType>>();
        for ( Class<? extends ClientResourceType> type : associatedResources.value() ) {
            types.add( type );
        }

        return Pair.newPair( priorityValue, types );
    }

}

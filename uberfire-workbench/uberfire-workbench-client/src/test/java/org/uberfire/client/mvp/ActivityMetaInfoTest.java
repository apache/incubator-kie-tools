/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.junit.Test;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

public class ActivityMetaInfoTest {

    @Test
    public void generateNotGenerateActivityMetaInfo() {
        IOCBeanDef<?> beanDefinition = mock( IOCBeanDef.class );
        when( beanDefinition.getQualifiers() ).thenReturn( Collections.<Annotation>emptySet() );

        Pair<Integer, List<Class<? extends ClientResourceType>>> nullGenerated = ActivityMetaInfo.generate( beanDefinition );

        assertNull( nullGenerated );

    }

    @Test
    public void generateActivityMetaInfo() {

        IOCBeanDef<?> beanDefinition = mock( IOCBeanDef.class );
        Priority priority = mock( Priority.class );
        Integer priorityValue = 1;
        when(priority.value()).thenReturn( priorityValue );

        Set<Annotation> qualifiers = new HashSet<Annotation>();
        AssociatedResources associatedResources = mock( AssociatedResources.class );

        final List<Class<? extends ClientResourceType>> typesList = new ArrayList<Class<? extends ClientResourceType>>();
        typesList.add(ClientResourceType.class);

        Class<? extends ClientResourceType>[] array = toArray( typesList );
        when(associatedResources.value()).thenReturn( array );

        qualifiers.add( associatedResources );
        qualifiers.add( priority );

        when( beanDefinition.getQualifiers() ).thenReturn( qualifiers );

        Pair<Integer, List<Class<? extends ClientResourceType>>> generated = ActivityMetaInfo.generate( beanDefinition );

        assertEquals( priorityValue, generated.getK1() );
        assertTrue(generated.getK2().contains(ClientResourceType.class  ));
    }

    private static <T> T[] toArray(List<T> list) {
        T[] toR = (T[]) java.lang.reflect.Array.newInstance(list.get(0)
                                                                    .getClass(), list.size());
        for (int i = 0; i < list.size(); i++) {
            toR[i] = list.get(i);
        }
        return toR;
    }
}

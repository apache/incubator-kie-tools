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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.DynamicAnnotation;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.junit.Test;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ActivityMetaInfoTest {

    @Test
    public void generateNotGenerateActivityMetaInfo() {
        IOCBeanDef<?> beanDefinition = mock(IOCBeanDef.class);
        when(beanDefinition.getQualifiers()).thenReturn(Collections.<Annotation>emptySet());

        Pair<Integer, List<String>> nullGenerated = ActivityMetaInfo.generate(beanDefinition);

        assertNull(nullGenerated);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void generateActivityMetaInfo() {
        IOCBeanDef<?> beanDefinition = mock(IOCBeanDef.class);
        Priority priority = mock(Priority.class);
        Integer priorityValue = 1;
        when(priority.value()).thenReturn(priorityValue);

        Set<Annotation> qualifiers = new HashSet<Annotation>();
        AssociatedResources associatedResources = mock(AssociatedResources.class);

        final List<Class<? extends ClientResourceType>> typesList = new ArrayList<Class<? extends ClientResourceType>>();
        typesList.add(ClientResourceType.class);

        Class<? extends ClientResourceType>[] array = typesList.toArray(new Class[typesList.size()]);
        when(associatedResources.value()).thenReturn(array);

        qualifiers.add(associatedResources);
        qualifiers.add(priority);

        when(beanDefinition.getQualifiers()).thenReturn(qualifiers);

        Pair<Integer, List<String>> generated = ActivityMetaInfo.generate(beanDefinition);

        assertEquals(priorityValue,
                     generated.getK1());
        assertTrue(generated.getK2().contains(ClientResourceType.class.getName()));
    }

    @Test
    public void generateActivityMetaInfoForDynamicActivity() {
        final String otherResourceType = "org.uberfire.OtherResourceType";
        IOCBeanDef<?> beanDefinition = mock(IOCBeanDef.class);

        DynamicAnnotation priority = mock(DynamicAnnotation.class);
        when(priority.getName()).thenReturn(Priority.class.getName());
        when(priority.getMember("value")).thenReturn("1");

        Set<Annotation> qualifiers = new HashSet<Annotation>();
        DynamicAnnotation associatedResources = mock(DynamicAnnotation.class);
        when(associatedResources.getName()).thenReturn(AssociatedResources.class.getName());
        when(associatedResources.getMember("value")).thenReturn("[" + ClientResourceType.class.getName() + "," + otherResourceType + "]");

        qualifiers.add(associatedResources);
        qualifiers.add(priority);

        when(beanDefinition.isDynamic()).thenReturn(true);
        when(beanDefinition.getQualifiers()).thenReturn(qualifiers);

        Pair<Integer, List<String>> generated = ActivityMetaInfo.generate(beanDefinition);

        assertEquals(Integer.valueOf(1),
                     generated.getK1());
        assertTrue(generated.getK2().contains(ClientResourceType.class.getName()));
        assertTrue(generated.getK2().contains("org.uberfire.OtherResourceType"));
    }
}

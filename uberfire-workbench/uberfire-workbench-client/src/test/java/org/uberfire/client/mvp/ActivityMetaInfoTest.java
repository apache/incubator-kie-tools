package org.uberfire.client.mvp;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.DotResourceType;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.type.TextResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
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
    @Ignore
    public void generateActivityMetaInfo() {

//        IOCBeanDef<?> beanDefinition = mock( IOCBeanDef.class );
//        Priority priority = mock( Priority.class );
//
//        Set<Annotation> qualifiers = new HashSet<Annotation>();
//        AssociatedResources associatedResources = mock( AssociatedResources.class );
//
//        Class<? extends ClientResourceType>[] values = new Class<AnyResourceType>[ 0 ];
//        when(associatedResources.value()).thenReturn( values );
//
//       // DotResourceType
//
//        qualifiers.add( associatedResources );
//        qualifiers.add( priority );
//
//        when( beanDefinition.getQualifiers() ).thenReturn( qualifiers );
//
//        Pair<Integer, List<Class<? extends ClientResourceType>>> generated = ActivityMetaInfo.generate( beanDefinition );

    }

}

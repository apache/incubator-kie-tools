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

import org.jboss.errai.ioc.client.container.CreationalContext;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActivityBeansInfoTest {

    private SyncBeanManager syncBeanManager;

    private ActivityBeansInfo activityBeansInfo;

    @Before
    public void setup(){
        syncBeanManager = mock( SyncBeanManager.class );
        activityBeansInfo = new ActivityBeansInfo(){
            @Override
            SyncBeanManager getBeanManager() {
                return syncBeanManager;
            }
        };
    }

    @Test
    public void getAvaliableWorkbenchScreensIdsTest(){
        when( syncBeanManager.lookupBeans( WorkbenchScreenActivity.class ) )
                .thenReturn( generateBeansList() );

        assertEquals( 2 , activityBeansInfo.getAvailableWorkbenchScreensIds().size() );
        //assert bean order
        assertEquals( "A" , activityBeansInfo.getAvailableWorkbenchScreensIds().get( 0 ) );
        assertEquals( "Z" , activityBeansInfo.getAvailableWorkbenchScreensIds().get( 1 ) );

    }

    private Collection<IOCBeanDef<WorkbenchScreenActivity>> generateBeansList() {
        Collection<IOCBeanDef<WorkbenchScreenActivity>> beans = new ArrayList<IOCBeanDef<WorkbenchScreenActivity>>(  );

        beans.add( generateBeanDef( "Z", true ) );
        beans.add( generateBeanDef( "A", false ) );

        return beans;
    }

    private IOCBeanDef<WorkbenchScreenActivity> generateBeanDef(String beanName, final boolean hasAnnotations) {
        return new IOCBeanDef<WorkbenchScreenActivity>() {
            @Override
            public Class<WorkbenchScreenActivity> getType() {
                return null;
            }

            @Override
            public Class<?> getBeanClass() {
                return null;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return null;
            }

            @Override
            public WorkbenchScreenActivity getInstance() {
                return null;
            }

            @Override
            public WorkbenchScreenActivity getInstance( CreationalContext context ) {
                return null;
            }

            @Override
            public WorkbenchScreenActivity newInstance() {
                return null;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                final HashSet<Annotation> annotations = new HashSet<Annotation>();
                if( hasAnnotations ){
                    annotations.add( new Named(){

                        @Override
                        public Class<? extends Annotation> annotationType() {
                            return null;
                        }

                        @Override
                        public String value() {
                            return "Z";
                        }
                    } );
                }
                return annotations;
            }

            @Override
            public boolean matches( Set<Annotation> annotations ) {
                return false;
            }

            @Override
            public String getName() {
                return "A";
            }

            @Override
            public boolean isConcrete() {
                return false;
            }

            @Override
            public boolean isActivated() {
                return false;
            }
        };
    }


}
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

import org.junit.Test;

public class ActivityBeansCacheTest {

    @Test
    public void initShouldCacheSplashScreen() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();

        cache.init();

        assertEquals( cache.getMockDef(), cache.getActivity( cache.getIdMock() ) );
        assertTrue( cache.getSplashScreens().contains( cache.getSplashScreenActivity() ) );
    }

    @Test
    public void initShouldCacheActivityById() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();

        cache.init();

        assertEquals( cache.getMockDef(), cache.getActivity( cache.getIdMock() ) );
    }

    @Test(expected = RuntimeException.class)
    public void initShouldNotAllowTwoIdenticalsActivities() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();
        cache.duplicateActivity();

        cache.init();
    }

    @Test
    public void initShouldCacheOtherActivities() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();
        cache.mockActivityBehaviour();
        cache.init();

        assertFalse(cache.getResourceActivities().isEmpty());
    }

    @Test
    public void initShouldOrderActivityByPriority() throws Exception {

        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();

        int priorityActivityOne = Integer.MIN_VALUE;
        int priorityActivityTwo = Integer.MAX_VALUE;

        cache.createActivitiesAndMetaInfo( priorityActivityOne, priorityActivityTwo );

        ActivityBeansCache.ActivityAndMetaInfo firstActivityOnList = cache.getResourceActivities().get( 0 );
        ActivityBeansCache.ActivityAndMetaInfo secondActivityOnList = cache.getResourceActivities().get( 1 );

        assertEquals( priorityActivityOne, firstActivityOnList.getPriority() );
        assertEquals( priorityActivityTwo, secondActivityOnList.getPriority() );

        cache.sortResourceActivitiesByPriority();

        firstActivityOnList = cache.getResourceActivities().get( 0 );
        secondActivityOnList = cache.getResourceActivities().get( 1 );

        assertEquals( priorityActivityTwo, firstActivityOnList.getPriority() );
        assertEquals( priorityActivityOne, secondActivityOnList.getPriority() );

    }

}

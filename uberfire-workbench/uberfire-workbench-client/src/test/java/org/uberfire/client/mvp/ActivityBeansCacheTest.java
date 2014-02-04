package org.uberfire.client.mvp;

import org.junit.Test;

import static org.junit.Assert.*;

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

        assertFalse(cache.getActivities().isEmpty());
    }

    @Test
    public void initShouldOrderActivityByPriority() throws Exception {

        ActivityBeansCacheUnitTestWrapper cache = new ActivityBeansCacheUnitTestWrapper();

        int priorityActivityOne = Integer.MIN_VALUE;
        int priorityActivityTwo = Integer.MAX_VALUE;

        cache.createActivitiesAndMetaInfo( priorityActivityOne, priorityActivityTwo );

        ActivityBeansCache.ActivityAndMetaInfo firstActivityOnList = cache.getActivities().get( 0 );
        ActivityBeansCache.ActivityAndMetaInfo secondActivityOnList = cache.getActivities().get( 1 );

        assertEquals( priorityActivityOne, firstActivityOnList.getPriority() );
        assertEquals( priorityActivityTwo, secondActivityOnList.getPriority() );

        cache.sortActivitiesByPriority();

        firstActivityOnList = cache.getActivities().get( 0 );
        secondActivityOnList = cache.getActivities().get( 1 );

        assertEquals( priorityActivityTwo, firstActivityOnList.getPriority() );
        assertEquals( priorityActivityOne, secondActivityOnList.getPriority() );

    }

}

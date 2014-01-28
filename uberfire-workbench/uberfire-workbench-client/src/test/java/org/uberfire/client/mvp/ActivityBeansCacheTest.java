package org.uberfire.client.mvp;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActivityBeansCacheTest {


    @Test
    public void initShouldCacheSplashScreen() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache  = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();

        cache.init();

        assertEquals(cache.getMockDef(), cache.getActivity( cache.getIdMock()));
        assertTrue( cache.getSplashScreens().contains( cache.getSplashScreenActivity() ) );
    }

    @Test
    public void initShouldCacheActivityById() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache  = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();

        cache.init();

        assertEquals( cache.getMockDef(), cache.getActivity( cache.getIdMock() ) );
    }

    @Test(expected = RuntimeException.class)
    public void initShouldNotAllowTwoIdenticalsActivities() throws Exception {
        ActivityBeansCacheUnitTestWrapper cache  = new ActivityBeansCacheUnitTestWrapper();
        cache.mockSplashScreenBehaviour();
        cache.duplicateActivity();

        cache.init();
    }


    @Test
    @Ignore
    public void initShouldCacheOtherActivities() throws Exception {

        fail("not yet implemented");
    }

    @Test
    @Ignore
    public void initShouldOrderActivityByPriority() throws Exception {

        fail("not yet implemented");

    }

}

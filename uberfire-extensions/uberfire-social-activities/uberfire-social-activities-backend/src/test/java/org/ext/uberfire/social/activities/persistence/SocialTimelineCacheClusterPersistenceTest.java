package org.ext.uberfire.social.activities.persistence;

import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;

import static org.junit.Assert.*;

public class SocialTimelineCacheClusterPersistenceTest {

    @Test
    public void testDisposableRegistry() {
        final SocialTimelineCacheClusterPersistence object = new SocialTimelineCacheClusterPersistence( null, null, null, null, null, null, null, null, null );
        assertTrue( PriorityDisposableRegistry.getDisposables().contains( object ) );
    }

}

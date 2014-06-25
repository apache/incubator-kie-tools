package org.kie.uberfire.social.activities.service;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface StressTestAPI {

    int stress( int numberOf10Events,
                int totalSleepBetweenEvents );
}

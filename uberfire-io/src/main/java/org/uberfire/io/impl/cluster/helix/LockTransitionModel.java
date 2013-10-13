package org.uberfire.io.impl.cluster.helix;

import org.apache.helix.NotificationContext;
import org.apache.helix.model.Message;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;

@StateModelInfo(initialState = "OFFLINE", states = { "LEADER", "STANDBY" })
public class LockTransitionModel extends StateModel {

    private final String lockName;
    private final SimpleLock lock;

    public LockTransitionModel( final String lockName,
                                final SimpleLock lock ) {
        this.lockName = lockName;
        this.lock = lock;
    }

    @Transition(from = "STANDBY", to = "LEADER")
    public void lock( final Message m,
                      final NotificationContext context ) {
        lock.lock();
    }

    @Transition(from = "LEADER", to = "STANDBY")
    public void release( final Message m,
                         final NotificationContext context ) {
        lock.unlock();
    }

    @Transition(from = "STANDBY", to = "OFFLINE")
    public void toOffLine( final Message m,
                           final NotificationContext context ) {
    }

    @Transition(from = "OFFLINE", to = "STANDBY")
    public void toStandBy( final Message m,
                           final NotificationContext context ) {
    }

    @Transition(from = "OFFLINE", to = "DROPPED")
    public void dropped( final Message m,
                         final NotificationContext context ) {
    }

}

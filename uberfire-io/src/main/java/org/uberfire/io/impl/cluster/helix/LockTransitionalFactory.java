package org.uberfire.io.impl.cluster.helix;

import org.apache.helix.participant.statemachine.StateModelFactory;

public class LockTransitionalFactory extends StateModelFactory<LockTransitionModel> {

    private final SimpleLock lock;

    LockTransitionalFactory( final SimpleLock lock ) {
        this.lock = lock;
    }

    @Override
    public LockTransitionModel createNewStateModel( final String lockName ) {
        return new LockTransitionModel( lockName, lock );
    }
}

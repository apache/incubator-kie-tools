package org.uberfire.io.impl.cluster.helix;

import org.apache.helix.participant.statemachine.StateModelFactory;

public class LockTransitionalFactory extends StateModelFactory<LockTransitionModel> {

    @Override
    public LockTransitionModel createNewStateModel( final String lockName ) {
        return new LockTransitionModel( lockName );
    }
}

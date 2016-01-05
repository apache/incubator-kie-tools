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

package org.uberfire.io.impl.cluster.helix;

import org.apache.helix.NotificationContext;
import org.apache.helix.model.Message;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;

@StateModelInfo(initialState = "OFFLINE", states = { "LEADER", "STANDBY" })
public class LockTransitionModel extends StateModel {

    private final String lockName;

    public LockTransitionModel( final String lockName ) {
        this.lockName = lockName;
    }

    @Transition(from = "STANDBY", to = "LEADER")
    public void lock( final Message m,
                      final NotificationContext context ) {
    }

    @Transition(from = "LEADER", to = "STANDBY")
    public void release( final Message m,
                         final NotificationContext context ) {
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

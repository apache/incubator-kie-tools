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

package org.uberfire.security.authz;

import org.uberfire.security.impl.authz.AffirmativeBasedVoter;
import org.uberfire.security.impl.authz.ConsensusBasedVoter;
import org.uberfire.security.impl.authz.UnanimousBasedVoter;

/**
 * Voting algorithms are used by the {@link PermissionManager} in order to determine what is the
 * winning result when the user is assigned with more than one role or group.
 * <p>
 * <p>See:</p>
 * <ul>
 * <li>{@link UnanimousBasedVoter}</li>
 * <li>{@link ConsensusBasedVoter}</li>
 * <li>{@link AffirmativeBasedVoter}</li>
 * </ul>
 */
public interface VotingAlgorithm {

    /**
     * It get a list of results as input and votes for a winning result.
     */
    AuthorizationResult vote(Iterable<AuthorizationResult> results);
}

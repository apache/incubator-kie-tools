/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

/**
 * A voting strategy establishes what to do when different authorization results are produced during the
 * evaluation of a permission over a protected resource. This is the case when the user is assigned with
 * more than one user and/or group. In such case, different strategies can be applied in order to resolve
 * what is the winning result.
 */
public enum VotingStrategy {

    /**
     * It is the most lenient strategy. Only a single positive vote is required.
     */
    AFFIRMATIVE,

    /**
     * It is based on general agreement. It requires a majority of positive votes.
     */
    CONSENSUS,

    /**
     * It is the less lenient strategy. It requires a 100% of positive votes.
     */
    UNANIMOUS,

    /**
     * It is based on role/group priorities. The highest priority result wins.
     */
    PRIORITY;
}

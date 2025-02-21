/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.acme.candidate;

import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskInstance;
import jakarta.enterprise.inject.Specializes;
import org.kie.kogito.usertask.impl.BasicUserTaskAssignmentStrategy;

@Specializes
@ApplicationScoped
public class CustomUserTaskAssignmentStrategyConfig extends BasicUserTaskAssignmentStrategy {

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Optional<String> computeAssignment(UserTaskInstance userTaskInstance, IdentityProvider identityProvider) {
        System.out.println("Computing assignment using custom User Task assignment strategy.");
        // Your custom logic goes here. For example:
        if ("hr_interview".equals(userTaskInstance.getTaskName())) {
            return Optional.of("recruiter");
        } else if ("it_interview".equals(userTaskInstance.getTaskName())) {
            return Optional.of("developer");
        } else {
            return Optional.empty();
        }
    }
}

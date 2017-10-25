/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.builder.service;

import org.guvnor.common.services.project.builder.model.BuildResults;

/**
 * Provides hook for build and deploy operation to be notified when both build and deploy have been executed.
 * Allows to take action after build and deploy to maven and report back its status by adding messages to
 * <code>buildResults</code>
 */
public interface PostBuildHandler {

    /**
     * Process custom logic and all errors should be reported via <code>buildResults.addBuildMessage()</code>
     * @param buildResults
     */
    void process(BuildResults buildResults);
}

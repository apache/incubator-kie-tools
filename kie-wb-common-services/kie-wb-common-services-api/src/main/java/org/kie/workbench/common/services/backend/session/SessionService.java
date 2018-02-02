/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.backend.session;

import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.shared.project.KieModule;

/**
 * Provides a KSession for given project
 */
public interface SessionService {

    /**
     * Returns the default KSession for a Project with the clock hard set to pseudo clock.
     */
    KieSession newDefaultKieSessionWithPseudoClock(final KieModule project);

    KieSession newKieSession(KieModule project, String ksessionName);
}

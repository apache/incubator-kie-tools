/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */
package org.guvnor.m2repo.preferences;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArtifactRepositoryPreferenceTest {

    @Test
    public void defaultValue() {
        ArtifactRepositoryPreference artifactRepositoryPreference = new ArtifactRepositoryPreference();
        artifactRepositoryPreference = artifactRepositoryPreference.defaultValue(artifactRepositoryPreference);

        assertEquals(artifactRepositoryPreference.getGlobalM2RepoDir(), "repositories/kie/global");
        assertEquals(artifactRepositoryPreference.getWorkspaceM2RepoDir(), "repositories/kie/workspaces");
        assertTrue(artifactRepositoryPreference.isGlobalM2RepoDirEnabled());
        assertFalse(artifactRepositoryPreference.isWorkspaceM2RepoDirEnabled());
        assertTrue(artifactRepositoryPreference.isDistributionManagementM2RepoDirEnabled());
    }
}
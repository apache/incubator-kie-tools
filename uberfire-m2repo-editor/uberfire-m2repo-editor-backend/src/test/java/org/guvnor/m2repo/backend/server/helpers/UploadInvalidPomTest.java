/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.m2repo.backend.server.helpers;

import java.io.InputStream;

import org.apache.maven.project.ProjectBuildingException;
import org.junit.Test;

public class UploadInvalidPomTest {

    @Test(expected = ProjectBuildingException.class)
    public void testBrokenPom() throws Exception {
        resolvePom("org/guvnor/m2repo/backend/server/helpers/broken-pom.xml");
    }

    @Test(expected = ProjectBuildingException.class)
    public void testNonExistingParentGavInPom() throws Exception {
        resolvePom("org/guvnor/m2repo/backend/server/helpers/non-existing-parent-gav-pom.xml");
    }

    private void resolvePom(String path) throws Exception {
        try (InputStream pomInputStream = this.getClass().getClassLoader().getResourceAsStream(path)) {
            PomModelResolver.resolveFromPom(pomInputStream);
        } catch (Exception e) {
            throw e;
        }
    }
}

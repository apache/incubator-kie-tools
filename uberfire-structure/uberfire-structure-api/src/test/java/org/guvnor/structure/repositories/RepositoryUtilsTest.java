/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUtilsTest {

    @Test
    public void cleanUpCredentialsFromEnvMapWhenNotNeededTest() {
        final Map<String, Object> envMap = createEnvMap(false);

        final List<String> result = RepositoryUtils.cleanUpCredentialsFromEnvMap(envMap);

        assertThat(result).isEmpty();
    }

    @Test
    public void cleanUpCredentialsFromEnvMapWhenNeededTest() {
        final Map<String, Object> envMap = createEnvMap(true);

        final List<String> result = RepositoryUtils.cleanUpCredentialsFromEnvMap(envMap);

        assertThat(result).isNotEmpty();
        assertThat(result).contains(EnvironmentParameters.USER_NAME,
                                    EnvironmentParameters.PASSWORD,
                                    EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD);
        assertThat(envMap).doesNotContainKeys(EnvironmentParameters.USER_NAME,
                                              EnvironmentParameters.PASSWORD,
                                              EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD);
    }

    private Map<String, Object> createEnvMap(final boolean includeCredentials) {
        final Map<String, Object> envMap = new HashMap<>();
        envMap.put("foo", true);
        envMap.put("bar", 1);

        if (includeCredentials) {
            envMap.put(EnvironmentParameters.USER_NAME, "user");
            envMap.put(EnvironmentParameters.PASSWORD, "pw");
            envMap.put(EnvironmentParameters.SECURE_PREFIX + EnvironmentParameters.PASSWORD, "spw");
        }

        return envMap;
    }
}

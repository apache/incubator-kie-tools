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
 */

package org.guvnor.ala.wildfly.executor.tests;

import java.util.HashMap;
import java.util.Map;

import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.wildfly.config.WildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.config.impl.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.junit.Test;
import org.uberfire.java.nio.file.Path;

import static java.util.Collections.singletonMap;
import static org.guvnor.ala.util.VariableInterpolation.interpolate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContextAwareWildflyRuntimeExecConfigTest {

    public static final String WAR_PATH_DEFAULT_EXPRESSION = "${input." + WildflyRuntimeExecConfig.WAR_PATH + "}";

    public static final String REDEPLOY_STRATEGY_DEFAULT_EXPRESSION = "${input." + WildflyRuntimeExecConfig.REDEPLOY_STRATEGY + "}";

    public static final String RUNTIME_NAME_DEFAULT_EXPRESSION = "${input." + WildflyRuntimeExecConfig.RUNTIME_NAME + "}";

    public static final String FILE_PATH = "/path/to/file.war";

    public static final String REDEPLOY_OPTION = "none";

    public static final String RUNTIME_NAME = "runtimeNameValue";

    @Test
    public void testDefaultExpression() {
        assertEquals(WAR_PATH_DEFAULT_EXPRESSION,
                     new ContextAwareWildflyRuntimeExecConfig().getWarPath());
        assertEquals(REDEPLOY_STRATEGY_DEFAULT_EXPRESSION,
                     new ContextAwareWildflyRuntimeExecConfig().getRedeployStrategy());
        assertEquals(RUNTIME_NAME_DEFAULT_EXPRESSION,
                     new ContextAwareWildflyRuntimeExecConfig().getRuntimeName());
    }

    @Test
    public void testContextUsingMavenBinary() {
        final ContextAwareWildflyRuntimeExecConfig config = new ContextAwareWildflyRuntimeExecConfig();
        final Map<String, Object> context = new HashMap<>();
        final WildflyProvider provider = mock(WildflyProvider.class);
        context.put("wildfly-provider",
                    provider);
        final MavenBinary binary = mock(MavenBinary.class);
        final Path path = mock(Path.class);
        when(binary.getPath()).thenReturn(path);
        when(path.toString()).thenReturn(FILE_PATH);
        context.put("binary",
                    binary);

        config.setContext(context);

        assertEquals(provider,
                     config.getProviderId());
        assertEquals(FILE_PATH,
                     config.getWarPath());

        final WildflyRuntimeExecConfig configClone = config.asNewClone(config);
        assertEquals(provider,
                     configClone.getProviderId());
        assertEquals(FILE_PATH,
                     configClone.getWarPath());
    }

    @Test
    public void testContextUsingPath() {
        final ContextAwareWildflyRuntimeExecConfig config = new ContextAwareWildflyRuntimeExecConfig();
        final WildflyProvider provider = mock(WildflyProvider.class);
        final Map<String, Object> context = singletonMap("wildfly-provider",
                                                         provider);

        config.setContext(context);

        assertEquals(provider,
                     config.getProviderId());
        assertEquals(WAR_PATH_DEFAULT_EXPRESSION,
                     config.getWarPath());

        final WildflyRuntimeExecConfig configClone = config.asNewClone(config);
        assertEquals(provider,
                     configClone.getProviderId());
        assertEquals(WAR_PATH_DEFAULT_EXPRESSION,
                     configClone.getWarPath());
    }

    @Test
    public void testVariablesResolution() {
        Map<String, String> values = new HashMap<>();
        values.put(ContextAwareWildflyRuntimeExecConfig.WAR_PATH,
                   FILE_PATH);
        values.put(ContextAwareWildflyRuntimeExecConfig.REDEPLOY_STRATEGY,
                   REDEPLOY_OPTION);
        values.put(ContextAwareWildflyRuntimeExecConfig.RUNTIME_NAME,
                   RUNTIME_NAME);

        final ContextAwareWildflyRuntimeExecConfig config = new ContextAwareWildflyRuntimeExecConfig();
        final ContextAwareWildflyRuntimeExecConfig varConfig = interpolate(singletonMap("input",
                                                                                        values),
                                                                           config);
        assertEquals(FILE_PATH,
                     varConfig.getWarPath());
        assertEquals(REDEPLOY_OPTION,
                     varConfig.getRedeployStrategy());
        assertEquals(RUNTIME_NAME,
                     varConfig.getRuntimeName());
    }
}
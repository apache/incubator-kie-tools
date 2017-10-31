/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.core.checks.base;

import org.drools.workbench.services.verifier.api.client.configuration.RunnerType;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CheckRunnerFactoryTest {


    @Test(expected = IllegalArgumentException.class)
    public void passInNull() throws
                             Exception {
        CheckRunnerFactory.make( null );
    }

    @Test
    public void makeGWTRunner() throws
                                Exception {
        assertTrue( CheckRunnerFactory.make( RunnerType.GWT ) instanceof GWTCheckRunner );
    }

    @Test
    public void makeJavaRunner() throws
                                 Exception {
        assertTrue( CheckRunnerFactory.make( RunnerType.JAVA ) instanceof JavaCheckRunner );
    }

    @Test
    public void allEnumValuesReturnARunner() throws
                                             Exception {
        for ( final RunnerType runnerType : RunnerType.values() ) {
            assertNotNull( CheckRunnerFactory.make( runnerType ) );
        }
    }
}
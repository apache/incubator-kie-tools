/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.scenariosimulation.kogito.marshaller;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ScesimGwtJsonixTest {

    private static final String MODEL_PATH =
            "target/classes/org/drools/workbench/scenariosimulation/kogito/marshaller/js/model/";

    @Test
    public void testModelJsInteropClassesWereGenerated() throws Exception {
        Assertions.assertThat(Files.list(Paths.get(MODEL_PATH)).count())
                .as("Number of generated classes should be equal to number of those in scesim.xsd + SCESIM.class")
                .isEqualTo(23L);
    }
}

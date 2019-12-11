/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.enums.backend.server.indexing;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssert;
import org.guvnor.test.CDITestSetup;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class EnumIndexVisitorCDITest extends CDITestSetup {

    private EnumFileIndexer indexer;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        indexer = getReference(EnumFileIndexer.class);
    }

    @After
    public void tearDown() throws Exception {
        super.cleanup();
    }

    @Test
    public void testDependentEnumIndexing() throws Exception {
        final String carFQN = "com.myteam.repro.Car";
        final Path testedPath = Paths.get(getClass().getResource("cars/src/main/resources/com/myteam/repro/cars.enumeration").toURI());
        final Set<KProperty<?>> properties = indexer.fillIndexBuilder(testedPath).build();
        final ModuleDataModelOracle oracle = indexer.getModuleDataModelOracle(testedPath);
        Assertions.assertThat(oracle.getModuleModelFields().keySet()).contains(carFQN);
        final IterableAssert carFields = Assertions.assertThat(properties).filteredOn("name", "ref:field:" + carFQN);
        carFields.filteredOn("value", "price").hasSize(1);
        carFields.filteredOn("value", "color").hasSize(1);
        final IterableAssert javaClasses = Assertions.assertThat(properties).filteredOn("name", "ref:java");
        javaClasses.filteredOn("value", carFQN).hasSize(1);
    }
}
